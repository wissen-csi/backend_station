const SERIES_CONFIG = {
    temp: { label: "Temperatura del aire", unit: "°C", type: "line", color: "#4cd7f6", decimals: 1 },
    hum: { label: "Humedad relativa", unit: "%", type: "line", color: "#89ceff", decimals: 0 },
    press: { label: "Presión atmosférica", unit: "hPa", type: "line", color: "#4edea3", decimals: 1 },
    rain: { label: "Precipitación acumulada", unit: "mm", type: "bar", color: "#06b6d4", decimals: 1 },
    ph: { label: "pH del agua (Calidad de agua)", unit: "pH", type: "line", color: "#1bbd85", decimals: 2 }
};

// --- ESTADO GLOBAL ---
const DashboardState = {
    currentTimeframe: '24h',
    currentSeries: 'temp',
    currentPage: 1,
    rowsPerPage: 6,
    searchQuery: '',
    customStart: '2025-05-18',
    customEnd: '2025-05-18',
    isCustomEmpty: false
};

// Obtiene los datos correspondientes al período activo
// Ajusta esta URL al endpoint GET que publique tu backend.
const MEDICIONES_URL = "http://localhost:8080/api/mediciones";
let lecturas = [];
let consultando = false;
let ultimaConsulta = "";

function normalizarLectura(fila) {
    const instante = typeof fila?.timestamp === "string" ? Date.parse(fila.timestamp.replace(" ", "T")) : NaN;
    if (!Number.isFinite(instante)) throw new Error("Fecha de medición inválida");
    const fecha = new Date(instante);
    const dos = valor => String(valor).padStart(2, "0");
    const dia = [fecha.getFullYear(), dos(fecha.getMonth()+1), dos(fecha.getDate())].join("-");
    const hora = [fecha.getHours(), fecha.getMinutes(), fecha.getSeconds()].map(dos).join(":");
    const lectura = { instante, timestamp: dia + " " + hora };
    for (const campo of ["temp","hum","press","windSpeed","windDeg","rain","rainRate","ph","cond","waterTemp"]) {
        const valor = fila[campo];
        const numero = typeof valor === "string" && valor.trim() !== "" ? Number(valor) : valor;
        lectura[campo] = Number.isFinite(numero) ? numero : null;
    }
    lectura.windDir = /^[A-Z]{1,4}$/.test(fila.windDir ?? "") ? fila.windDir : "—";
    lectura.status = typeof fila.status === "string" && /^[\p{L}\d ._-]{1,60}$/u.test(fila.status) ? fila.status : "Sin estado";
    return lectura;
}

async function consultarMediciones() {
    if (consultando) return;
    consultando = true;
    const etiqueta = document.getElementById("sync-time-label");
    etiqueta.textContent = "Consultando…";
    try {
        const respuesta = await fetch(MEDICIONES_URL, {cache:"no-store", signal: AbortSignal.timeout(10000)});
        if (!respuesta.ok) throw new Error("HTTP " + respuesta.status);
        const filas = await respuesta.json();
        if (!Array.isArray(filas)) throw new Error("Se esperaba una lista JSON");
        lecturas = filas.map(normalizarLectura).sort((a,b) => b.instante - a.instante);
        updateView();
        ultimaConsulta = new Date().toLocaleTimeString();
        etiqueta.textContent = "Actualizado a las " + ultimaConsulta;
    } catch (error) {
        etiqueta.textContent = "Error al consultar la API" + (ultimaConsulta ? " · Última consulta " + ultimaConsulta : "");
        console.error("AeroSense:", error.message);
    } finally {
        consultando = false;
    }
}

function getActiveDataset() {
    if (DashboardState.currentTimeframe === "custom") {
        return lecturas.filter(r => r.timestamp.slice(0,10) >= DashboardState.customStart &&
            r.timestamp.slice(0,10) <= DashboardState.customEnd && !DashboardState.isCustomEmpty);
    }
    const dias = {"24h":1,"7d":7,"30d":30}[DashboardState.currentTimeframe];
    const fin = Date.now();
    return lecturas.filter(r => r.instante >= fin - dias * 86400000 && r.instante <= fin);
}


function updateCards(dataset) {
    if (!dataset || dataset.length === 0) {
        document.getElementById('card-temp-val').textContent = "--";
        document.getElementById('card-hum-val').textContent = "--";
        document.getElementById('card-press-val').textContent = "--";
        document.getElementById('card-wind-val').textContent = "--";
        document.getElementById('card-rain-val').textContent = "--";
        document.getElementById('card-water-val').textContent = "--";
        return;
    }

    // Último registro válido en el tiempo (el primero en el array cronológico inverso)
    const latest = dataset[0];
    const latestTemp = dataset.find(r => r.temp !== null && r.temp !== undefined);
    const latestHum = dataset.find(r => r.hum !== null && r.hum !== undefined);
    const latestPress = dataset.find(r => r.press !== null && r.press !== undefined);
    const latestWind = dataset.find(r => r.windSpeed !== null && r.windSpeed !== undefined);
    const latestRain = dataset.find(r => r.rain !== null && r.rain !== undefined);
    const latestWater = dataset.find(r => r.ph !== null && r.ph !== undefined);

    // 1. Temp
    if (latestTemp) {
        document.getElementById('card-temp-val').textContent = latestTemp.temp.toFixed(1);
        document.getElementById('card-temp-time').textContent = latestTemp.timestamp;
        const validTemps = dataset.filter(r => r.temp !== null).map(r => r.temp);
        const minT = Math.min(...validTemps);
        const maxT = Math.max(...validTemps);
        document.getElementById('card-temp-minmax').innerHTML = `Mín: <strong class="text-on-surface font-mono">${minT.toFixed(1)}°</strong> | Máx: <strong class="text-on-surface font-mono">${maxT.toFixed(1)}°</strong>`;
    }

    // 2. Humedad
    if (latestHum) {
        document.getElementById('card-hum-val').textContent = latestHum.hum;
        document.getElementById('card-hum-time').textContent = latestHum.timestamp;
    }

    // 3. Presión
    if (latestPress) {
        document.getElementById('card-press-val').textContent = latestPress.press.toFixed(1);
        document.getElementById('card-press-time').textContent = latestPress.timestamp;
    }

    // 4. Viento
    if (latestWind) {
        document.getElementById('card-wind-val').textContent = latestWind.windSpeed.toFixed(1);
        document.getElementById('card-wind-dir').textContent = `${latestWind.windDir} (${latestWind.windDeg ?? '—'}°)`;
        document.getElementById('card-wind-time').textContent = latestWind.timestamp;
    }

    // 5. Precipitación
    if (latestRain) {
        // Suma acumulada en el período
        const totalRain = dataset.reduce((acc, r) => acc + (r.rain !== null ? r.rain : 0), 0);
        document.getElementById('card-rain-val').textContent = totalRain.toFixed(1);
        document.getElementById('card-rain-time').textContent = latestRain.timestamp;
        document.getElementById('card-rain-rate').textContent = `${(latestRain.rainRate?.toFixed(1) ?? '—')} mm/h`;
    }

    // 6. Sonda Río
    if (latestWater) {
        document.getElementById('card-water-val').textContent = latestWater.ph.toFixed(2);
        document.getElementById('card-water-cond').textContent = `${latestWater.cond ?? '—'} µS/cm`;
        document.getElementById('card-water-time').textContent = latestWater.timestamp;
        document.getElementById('card-water-temp').textContent = `${(latestWater.waterTemp?.toFixed(1) ?? '—')} °C`;
    }
}

// --- GENERACIÓN DINÁMICA DE LA GRÁFICA SVG ---
function renderChart(dataset, seriesKey) {
    const svg = document.getElementById('telemetry-chart-svg');
    const badge = document.getElementById('chart-points-badge');
    const config = SERIES_CONFIG[seriesKey] || SERIES_CONFIG.temp;
    const totalRecords = dataset.length;

    // Filtrar puntos válidos para esta variable (CUIDADO: 0 es un valor numérico válido, ej. 0.0 mm de lluvia!)
    const validData = [];
    dataset.forEach((row, originalIndex) => {
        const val = row[seriesKey];
        if (val !== null && val !== undefined && !isNaN(val)) {
            validData.push({
                index: originalIndex,
                timestamp: row.timestamp,
                value: val,
                status: row.status
            });
        }
    });

    const validCount = validData.length;

    // Actualizar contador junto a la gráfica
    if (badge) {
        if (totalRecords === 0) {
            badge.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-error inline-block mr-1"></span>0 puntos válidos graficados (0 registros)`;
            badge.className = "px-space-xs py-space-2xs rounded-full bg-error/20 text-error font-label-sm font-mono font-medium";
        } else if (validCount < totalRecords) {
            const missing = totalRecords - validCount;
            badge.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-secondary-fixed inline-block mr-1"></span>${validCount} de ${totalRecords} puntos graficados (${missing} faltante${missing > 1 ? 's' : ''})`;
            badge.className = "px-space-xs py-space-2xs rounded-full bg-secondary/20 text-secondary-fixed font-label-sm font-mono font-medium";
        } else {
            badge.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-tertiary inline-block mr-1"></span>${validCount} de ${totalRecords} puntos válidos graficados`;
            badge.className = "px-space-xs py-space-2xs rounded-full bg-tertiary/20 text-tertiary font-label-sm font-mono font-medium";
        }
    }

    // Actualizar métricas resumen inferiores (Promedio, Máx, Mín)
    updateSummaryMetrics(validData, config);

    // Dimensiones del lienzo SVG
    const width = 1000;
    const height = 320;
    const padLeft = 65;
    const padRight = 35;
    const padTop = 30;
    const padBottom = 265;
    const plotWidth = width - padLeft - padRight;
    const plotHeight = padBottom - padTop;

    // Si no hay datos, renderizar estado vacío
    if (validCount === 0) {
        svg.innerHTML = `
            <text x="${width/2}" y="${height/2 - 10}" fill="#869397" font-family="Geist" font-size="14" text-anchor="middle">
                Sin datos disponibles para el período o rango seleccionado
            </text>
            <text x="${width/2}" y="${height/2 + 15}" fill="#3d494c" font-family="Geist" font-size="12" text-anchor="middle">
                Ajuste los filtros de fecha o seleccione otra variable
            </text>
        `;
        renderTimelineLabels([]);
        return;
    }

    // Calcular límites Y con margen
    const values = validData.map(d => d.value);
    let minVal = Math.min(...values);
    let maxVal = Math.max(...values);

    if (config.type === 'bar') {
        minVal = 0; // La lluvia siempre parte desde 0 mm
        if (maxVal === 0) maxVal = 2.0; // Espacio visual si todo es 0
        maxVal = maxVal * 1.25;
    } else {
        if (maxVal === minVal) {
            maxVal += 1.0;
            minVal -= 1.0;
        } else {
            const margin = (maxVal - minVal) * 0.15;
            maxVal += margin;
            minVal -= margin;
        }
    }

    const getY = (val) => padBottom - ((val - minVal) / (maxVal - minVal)) * plotHeight;
    const getX = (idx, total) => {
        if (total <= 1) return padLeft + plotWidth / 2;
        // Invertir orden cronológico para que el tiempo avance de izquierda a derecha
        return padLeft + ((total - 1 - idx) / (total - 1)) * plotWidth;
    };

    // Construcción del contenido SVG
    let svgContent = `
        <defs>
            <linearGradient id="cyanGradient" x1="0" x2="0" y1="0" y2="1">
                <stop offset="0%" stop-color="${config.color}" stop-opacity="0.35"></stop>
                <stop offset="70%" stop-color="#1bbd85" stop-opacity="0.08"></stop>
                <stop offset="100%" stop-color="#0b1326" stop-opacity="0"></stop>
            </linearGradient>
            <linearGradient id="lineStroke" x1="0" x2="1" y1="0" y2="0">
                <stop offset="0%" stop-color="#06b6d4"></stop>
                <stop offset="50%" stop-color="${config.color}"></stop>
                <stop offset="100%" stop-color="#4edea3"></stop>
            </linearGradient>
        </defs>
    `;

    // 1. Grilla horizontal y etiquetas del eje Y (5 niveles)
    svgContent += `<g opacity="0.4" stroke="#2d3449" stroke-dasharray="4 6" stroke-width="1">`;
    const yLabels = [];
    for (let i = 0; i < 5; i++) {
        const y = padTop + (plotHeight / 4) * i;
        const valAtY = maxVal - ((maxVal - minVal) / 4) * i;
        svgContent += `<line x1="${padLeft}" x2="${width - padRight}" y1="${y}" y2="${y}"></line>`;
        yLabels.push({ y: y + 4, text: `${valAtY.toFixed(config.decimals)} ${config.unit}` });
    }
    svgContent += `</g>`;

    // Etiquetas del eje Y
    svgContent += `<g fill="#869397" font-family="Geist" font-size="11" text-anchor="end">`;
    yLabels.forEach(lbl => {
        svgContent += `<text x="${padLeft - 10}" y="${lbl.y}">${lbl.text}</text>`;
    });
    svgContent += `</g>`;

    // 2. Grilla vertical
    svgContent += `<g opacity="0.25" stroke="#2d3449" stroke-dasharray="3 5" stroke-width="1">`;
    const vSteps = Math.min(validCount, 7);
    for (let i = 0; i < vSteps; i++) {
        const x = padLeft + (plotWidth / (vSteps - 1 || 1)) * i;
        svgContent += `<line x1="${x}" x2="${x}" y1="${padTop}" y2="${padBottom}"></line>`;
    }
    svgContent += `</g>`;

    // Puntos ordenados cronológicamente (más antiguo a la izquierda, más reciente a la derecha)
    const chronoData = [...validData].reverse();
    const totalChrono = chronoData.length;

    // 3. Dibujar Serie según Tipo
    if (config.type === 'bar') {
        // GRÁFICA DE BARRAS PARA PRECIPITACIÓN
        const barWidth = Math.max(6, Math.min(28, (plotWidth / totalChrono) * 0.7));
        svgContent += `<g class="chart-bars">`;
        chronoData.forEach((d, i) => {
            const x = getX(totalChrono - 1 - i, totalChrono);
            const y = getY(d.value);
            // Si el valor es 0, dibujamos una barra sutil de 2px en la base para confirmar que es un 0 válido
            const isZero = (d.value === 0);
            const barHeight = isZero ? 2 : Math.max(3, padBottom - y);
            const barY = isZero ? (padBottom - 2) : y;
            const barFill = isZero ? "#3d494c" : config.color;

            svgContent += `
                <rect x="${x - barWidth / 2}" y="${barY}" width="${barWidth}" height="${barHeight}" 
                      rx="2" fill="${barFill}" opacity="${isZero ? 0.6 : 0.85}"
                      class="chart-element cursor-pointer transition-all hover:opacity-100"
                      data-idx="${d.index}" data-val="${d.value}" data-ts="${d.timestamp}" data-unit="${config.unit}"
                      data-x="${x}" data-y="${barY}">
                </rect>
            `;
        });
        svgContent += `</g>`;
    } else {
        // GRÁFICA DE LÍNEA Y ÁREA RELLENA
        const points = chronoData.map((d, i) => ({
            x: getX(totalChrono - 1 - i, totalChrono),
            y: getY(d.value),
            val: d.value,
            ts: d.timestamp,
            idx: d.index
        }));

        // Construir Path suave o polilínea
        let linePath = `M ${points[0].x.toFixed(1)},${points[0].y.toFixed(1)}`;
        for (let i = 1; i < points.length; i++) {
            const prev = points[i - 1];
            const curr = points[i];
            const mx = ((prev.x + curr.x) / 2).toFixed(1);
            linePath += ` C ${mx},${prev.y.toFixed(1)} ${mx},${curr.y.toFixed(1)} ${curr.x.toFixed(1)},${curr.y.toFixed(1)}`;
        }

        const areaPath = `${linePath} L ${points[points.length - 1].x.toFixed(1)},${padBottom} L ${points[0].x.toFixed(1)},${padBottom} Z`;

        // Área
        svgContent += `<path d="${areaPath}" fill="url(#cyanGradient)"></path>`;

        // Línea
        svgContent += `<path d="${linePath}" fill="none" id="telemetry-chart-line" stroke="url(#lineStroke)" stroke-linecap="round" stroke-width="3"></path>`;

        // Puntos interactivos
        svgContent += `<g class="chart-points">`;
        const peakVal = Math.max(...points.map(p => p.val));
        let peakPointDrawn = false;

        points.forEach((p, i) => {
            const isPeak = (p.val === peakVal && !peakPointDrawn);
            if (isPeak) peakPointDrawn = true;
            const isLive = (i === points.length - 1);

            if (isPeak) {
                svgContent += `<circle cx="${p.x.toFixed(1)}" cy="${p.y.toFixed(1)}" r="5.5" fill="#06b6d4" stroke="#dae2fd" stroke-width="2" 
                    class="chart-element cursor-pointer" data-idx="${p.idx}" data-val="${p.val}" data-ts="${p.ts}" data-unit="${config.unit}" data-x="${p.x}" data-y="${p.y}"></circle>`;
            } else if (isLive) {
                svgContent += `<circle cx="${p.x.toFixed(1)}" cy="${p.y.toFixed(1)}" r="6" fill="#4edea3" stroke="#0b1326" stroke-width="2" 
                    class="chart-element cursor-pointer" data-idx="${p.idx}" data-val="${p.val}" data-ts="${p.ts}" data-unit="${config.unit}" data-x="${p.x}" data-y="${p.y}"></circle>`;
            } else {
                svgContent += `<circle cx="${p.x.toFixed(1)}" cy="${p.y.toFixed(1)}" r="4.5" fill="#0b1326" stroke="${config.color}" stroke-width="2.5" 
                    class="chart-element cursor-pointer hover:r-6 transition-all" data-idx="${p.idx}" data-val="${p.val}" data-ts="${p.ts}" data-unit="${config.unit}" data-x="${p.x}" data-y="${p.y}"></circle>`;
            }
        });
        svgContent += `</g>`;
    }

    svg.innerHTML = svgContent;

    // Actualizar etiquetas cronológicas inferiores del eje X
    renderTimelineLabels(chronoData);

    // Inicializar tooltips y eventos de hover sobre elementos
    attachChartHoverEvents();
}

// Renderiza las etiquetas del eje de tiempo X
function renderTimelineLabels(chronoData) {
    const container = document.getElementById('chart-x-axis-labels');
    if (!container) return;

    if (!chronoData || chronoData.length === 0) {
        container.innerHTML = `<span>--:--</span><span>--:--</span>`;
        return;
    }

    const count = chronoData.length;
    const step = Math.max(1, Math.floor(count / 5));
    const labels = [];

    for (let i = 0; i < count; i += step) {
        labels.push(chronoData[i]);
    }
    // Asegurar incluir el último punto (más reciente)
    if (labels[labels.length - 1] !== chronoData[count - 1]) {
        labels.push(chronoData[count - 1]);
    }

    let html = '';
    labels.forEach((item, idx) => {
        const isLast = (idx === labels.length - 1);
        const parts = item.timestamp.split(" ");
        const timePart = parts[1] ? parts[1].substring(0, 5) : parts[0];
        const datePart = parts[0] ? parts[0].substring(5) : "";

        if (isLast) {
            html += `<span class="text-tertiary font-bold flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-tertiary inline-block animate-pulse"></span>
                ${timePart} (${datePart})
            </span>`;
        } else {
            html += `<span>${timePart} <small class="text-outline text-[10px]">${datePart}</small></span>`;
        }
    });

    container.innerHTML = html;
}

// Actualiza los 3 bloques de métricas resumen bajo la gráfica
function updateSummaryMetrics(validData, config) {
    const avgEl = document.getElementById('metric-avg-val');
    const maxValEl = document.getElementById('metric-max-val');
    const maxTimeEl = document.getElementById('metric-max-time');
    const minValEl = document.getElementById('metric-min-val');
    const minTimeEl = document.getElementById('metric-min-time');

    if (!validData || validData.length === 0) {
        if (avgEl) avgEl.innerHTML = `-- <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
        if (maxValEl) maxValEl.innerHTML = `-- <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
        if (maxTimeEl) maxTimeEl.textContent = "Sin datos";
        if (minValEl) minValEl.innerHTML = `-- <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
        if (minTimeEl) minTimeEl.textContent = "Sin datos";
        return;
    }

    const values = validData.map(d => d.value);
    const sum = values.reduce((a, b) => a + b, 0);
    const avg = sum / values.length;

    let maxObj = validData[0];
    let minObj = validData[0];
    validData.forEach(d => {
        if (d.value > maxObj.value) maxObj = d;
        if (d.value < minObj.value) minObj = d;
    });

    if (avgEl) avgEl.innerHTML = `${avg.toFixed(config.decimals)} <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
    if (maxValEl) maxValEl.innerHTML = `${maxObj.value.toFixed(config.decimals)} <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
    if (maxTimeEl) maxTimeEl.textContent = `el ${maxObj.timestamp}`;
    if (minValEl) minValEl.innerHTML = `${minObj.value.toFixed(config.decimals)} <span class="text-body-sm font-normal text-outline">${config.unit}</span>`;
    if (minTimeEl) minTimeEl.textContent = `el ${minObj.timestamp}`;
}

// Control interactivo de tooltip flotante al pasar el mouse
function attachChartHoverEvents() {
    const elements = document.querySelectorAll('.chart-element');
    const tooltip = document.getElementById('chart-interactive-tooltip');
    if (!tooltip) return;

    elements.forEach(el => {
        el.addEventListener('mouseenter', (e) => {
            const ts = el.getAttribute('data-ts');
            const val = parseFloat(el.getAttribute('data-val'));
            const unit = el.getAttribute('data-unit');
            const x = parseFloat(el.getAttribute('data-x'));
            const y = parseFloat(el.getAttribute('data-y'));

            const timeLabel = tooltip.querySelector('.tooltip-time');
            const valLabel = tooltip.querySelector('.tooltip-val');

            if (timeLabel) timeLabel.textContent = ts;
            if (valLabel) valLabel.innerHTML = `${val} ${unit}`;

            // Posicionar tooltip sobre el punto SVG
            const percentX = (x / 1000) * 100;
            const percentY = (y / 320) * 100;
            tooltip.style.left = `${percentX}%`;
            tooltip.style.top = `${percentY}%`;
            tooltip.classList.remove('hidden');
        });

        el.addEventListener('mouseleave', () => {
            tooltip.classList.add('hidden');
        });
    });
}

// --- ACTUALIZACIÓN Y PAGINACIÓN DE LA TABLA ---
function renderTable(dataset) {
    const tbody = document.getElementById('telemetry-table-body');
    const counterLabel = document.getElementById('table-counter-label');
    const pageInfo = document.getElementById('table-page-info');
    const prevBtn = document.getElementById('btn-prev-page');
    const nextBtn = document.getElementById('btn-next-page');

    if (!tbody) return;

    // 1. Filtrado por búsqueda
    const q = DashboardState.searchQuery.trim().toLowerCase();
    let filtered = dataset;
    if (q) {
        filtered = dataset.filter(r => 
            r.timestamp.toLowerCase().includes(q) ||
            r.status.toLowerCase().includes(q) ||
            r.windDir.toLowerCase().includes(q)
        );
    }

    const totalFiltered = filtered.length;
    const totalPeriod = dataset.length;
    const rowsPerPage = DashboardState.rowsPerPage;
    const totalPages = Math.max(1, Math.ceil(totalFiltered / rowsPerPage));

    // Ajustar página si se sobrepasa
    if (DashboardState.currentPage > totalPages) {
        DashboardState.currentPage = totalPages;
    }

    const startIdx = (DashboardState.currentPage - 1) * rowsPerPage;
    const endIdx = Math.min(startIdx + rowsPerPage, totalFiltered);
    const pageRows = filtered.slice(startIdx, endIdx);

    // Actualizar etiqueta del contador
    if (counterLabel) {
        if (totalFiltered === 0) {
            counterLabel.innerHTML = `Mostrando <strong class="text-on-surface">0</strong> registros del período`;
        } else if (q) {
            counterLabel.innerHTML = `Mostrando <strong class="text-on-surface">${startIdx + 1}–${endIdx}</strong> de ${totalFiltered} filtrados (de ${totalPeriod} del período)`;
        } else {
            counterLabel.innerHTML = `Mostrando <strong class="text-on-surface">${startIdx + 1}–${endIdx}</strong> de ${totalPeriod} registros del período`;
        }
    }

    if (pageInfo) {
        pageInfo.textContent = `Página ${DashboardState.currentPage} de ${totalPages}`;
    }

    if (prevBtn) {
        prevBtn.disabled = (DashboardState.currentPage <= 1);
        prevBtn.classList.toggle('opacity-50', DashboardState.currentPage <= 1);
    }
    if (nextBtn) {
        nextBtn.disabled = (DashboardState.currentPage >= totalPages);
        nextBtn.classList.toggle('opacity-50', DashboardState.currentPage >= totalPages);
    }

    // Si no hay filas
    if (pageRows.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="py-space-md text-center text-on-surface-variant font-body-sm">
                    No se encontraron registros de telemetría para los filtros seleccionados.
                </td>
            </tr>
        `;
        return;
    }

    let rowsHtml = '';
    pageRows.forEach(r => {
        // Formato para variables presentes vs faltantes
        const tempDisplay = (r.temp !== null && r.temp !== undefined) 
            ? `<span class="font-mono text-primary font-semibold">${r.temp.toFixed(1)} °C</span>` 
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const humDisplay = (r.hum !== null && r.hum !== undefined)
            ? `<span class="font-mono text-on-surface">${r.hum} %</span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const pressDisplay = (r.press !== null && r.press !== undefined)
            ? `<span class="font-mono text-on-surface">${r.press.toFixed(1)}</span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const windDisplay = (r.windSpeed !== null && r.windSpeed !== undefined)
            ? `<span class="font-mono text-on-surface">${r.windSpeed.toFixed(1)} km/h <span class="text-on-surface-variant text-label-sm">${r.windDir}</span></span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const rainDisplay = (r.rain !== null && r.rain !== undefined)
            ? `<span class="font-mono text-on-surface ${r.rain > 0 ? 'text-primary font-semibold' : ''}">${r.rain.toFixed(1)}</span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const phDisplay = (r.ph !== null && r.ph !== undefined)
            ? `<span class="font-mono text-tertiary font-medium">${r.ph.toFixed(2)}</span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        const condDisplay = (r.cond !== null && r.cond !== undefined)
            ? `<span class="font-mono text-on-surface">${r.cond} µS/cm</span>`
            : `<span class="font-mono text-outline italic">N/D</span>`;

        let statusBadge = `
            <span class="inline-flex items-center gap-1 px-space-xs py-0.5 rounded-full bg-tertiary-container/30 text-tertiary font-label-sm font-semibold">
                <span class="w-1.5 h-1.5 rounded-full bg-tertiary"></span> ${r.status}
            </span>
        `;
        if (r.status === 'Flag Sensor' || r.status.includes('Flag')) {
            statusBadge = `
                <span class="inline-flex items-center gap-1 px-space-xs py-0.5 rounded-full bg-secondary-container/30 text-secondary-fixed font-label-sm font-semibold">
                    <span class="w-1.5 h-1.5 rounded-full bg-secondary"></span> ${r.status}
                </span>
            `;
        } else if (r.status === 'Lluvia') {
            statusBadge = `
                <span class="inline-flex items-center gap-1 px-space-xs py-0.5 rounded-full bg-primary-container/30 text-primary font-label-sm font-semibold">
                    <span class="w-1.5 h-1.5 rounded-full bg-primary"></span> ${r.status}
                </span>
            `;
        }

        rowsHtml += `
            <tr class="hover:bg-surface-container/50 transition-colors">
                <td class="py-space-sm px-space-md font-mono text-on-surface font-medium whitespace-nowrap">${r.timestamp}</td>
                <td class="py-space-sm px-space-sm text-right">${tempDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${humDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${pressDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${windDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${rainDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${phDisplay}</td>
                <td class="py-space-sm px-space-sm text-right">${condDisplay}</td>
                <td class="py-space-sm px-space-md text-center">${statusBadge}</td>
            </tr>
        `;
    });

    tbody.innerHTML = rowsHtml;
}

// Refresca todos los componentes de la vista
function updateView() {
    const dataset = getActiveDataset();
    document.querySelectorAll('[id^="card-"]').forEach(el => el.textContent = '—');
    updateCards(dataset);
    renderChart(dataset, DashboardState.currentSeries);
    renderTable(dataset);
}

// --- CONFIGURACIÓN DE EVENT LISTENERS ---
function initDashboard() {
    // 1. Selector de Variable en Gráfica (Tabs)
    const chartTabButtons = document.querySelectorAll('.chart-tab-btn');
    chartTabButtons.forEach(tab => {
        tab.addEventListener('click', () => {
            const series = tab.getAttribute('data-series');
            if (!series) return;

            chartTabButtons.forEach(t => {
                t.classList.remove('bg-primary-container', 'text-on-primary', 'font-semibold', 'shadow-sm');
                t.classList.add('text-on-surface-variant', 'font-medium');
            });
            tab.classList.remove('text-on-surface-variant', 'font-medium');
            tab.classList.add('bg-primary-container', 'text-on-primary', 'font-semibold', 'shadow-sm');

            DashboardState.currentSeries = series;
            const dataset = getActiveDataset();
            renderChart(dataset, series);
        });
    });

    // 2. Selector de Período (Timeframe Buttons)
    const timeframeButtons = document.querySelectorAll('.timeframe-btn');
    const customPanel = document.getElementById('custom-range-panel');

    timeframeButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tf = btn.getAttribute('data-timeframe');
            if (!tf) return;

            timeframeButtons.forEach(b => {
                b.classList.remove('bg-primary-container', 'text-on-primary', 'font-semibold', 'shadow-sm');
                b.classList.add('text-on-surface-variant', 'font-medium');
            });
            btn.classList.remove('text-on-surface-variant', 'font-medium');
            btn.classList.add('bg-primary-container', 'text-on-primary', 'font-semibold', 'shadow-sm');

            DashboardState.currentTimeframe = tf;
            DashboardState.currentPage = 1;
            DashboardState.isCustomEmpty = false;

            if (tf === 'custom') {
                if (customPanel) customPanel.classList.remove('hidden');
            } else {
                if (customPanel) customPanel.classList.add('hidden');
            }

            updateView();
        });
    });

    // 3. Panel de Rango Personalizado
    const btnApplyCustom = document.getElementById('btn-apply-custom');
    const btnEmptyCustom = document.getElementById('btn-empty-custom');
    const startInput = document.getElementById('custom-start-date');
    const endInput = document.getElementById('custom-end-date');

    if (btnApplyCustom && startInput && endInput) {
        btnApplyCustom.addEventListener('click', () => {
            DashboardState.customStart = startInput.value;
            DashboardState.customEnd = endInput.value;
            DashboardState.isCustomEmpty = false;
            DashboardState.currentPage = 1;
            updateView();
        });
    }

    if (btnEmptyCustom) {
        btnEmptyCustom.addEventListener('click', () => {
            DashboardState.isCustomEmpty = true;
            DashboardState.currentPage = 1;
            updateView();
        });
    }

    // 4. Búsqueda en tabla
    const searchInput = document.getElementById('table-search-input');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            DashboardState.searchQuery = e.target.value;
            DashboardState.currentPage = 1;
            const dataset = getActiveDataset();
            renderTable(dataset);
        });
    }

    // 5. Filas por página
    const rowsSelect = document.getElementById('table-rows-per-page');
    if (rowsSelect) {
        rowsSelect.addEventListener('change', (e) => {
            DashboardState.rowsPerPage = parseInt(e.target.value, 10);
            DashboardState.currentPage = 1;
            const dataset = getActiveDataset();
            renderTable(dataset);
        });
    }

    // 6. Botones de paginación
    const prevBtn = document.getElementById('btn-prev-page');
    const nextBtn = document.getElementById('btn-next-page');
    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            if (DashboardState.currentPage > 1) {
                DashboardState.currentPage--;
                const dataset = getActiveDataset();
                renderTable(dataset);
            }
        });
    }
    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            DashboardState.currentPage++;
            const dataset = getActiveDataset();
            renderTable(dataset);
        });
    }

    document.getElementById('btn-refresh').addEventListener('click', consultarMediciones);
    setInterval(consultarMediciones, 15000);

    // 8. Exportar CSV
    const exportBtn = document.getElementById('btn-export-csv');
    if (exportBtn) {
        exportBtn.addEventListener('click', () => {
            const dataset = getActiveDataset();
            if (!dataset || dataset.length === 0) {
                alert('No hay datos en el período seleccionado para exportar.');
                return;
            }

            let csv = "Fecha y Hora,Temperatura (C),Humedad (%),Presion (hPa),Viento (km/h),Direccion,Precipitacion (mm),pH Agua,Conductividad (uS/cm),Estado\n";
            dataset.forEach(r => {
                csv += `"${r.timestamp}",${r.temp ?? ""},${r.hum ?? ""},${r.press ?? ""},${r.windSpeed ?? ""},"${r.windDir}",${r.rain ?? ""},${r.ph ?? ""},${r.cond ?? ""},"${r.status}"\n`;
            });

            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `AeroSense_${DashboardState.currentTimeframe}_${Date.now()}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        });
    }

    // Carga inicial
    updateView();
    consultarMediciones();
}

// Iniciar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDashboard);
} else {
    initDashboard();
}
