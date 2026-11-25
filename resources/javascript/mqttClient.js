(function () {
    const brokerUrl = 'wss://p6e0f47e.ala.us-east-1.emqxsl.com:8084/mqtt';
    const topic = 'sensores/data';
    const form = document.getElementById('mqtt-connect-form');

    if (!form) return;

    const usernameInput = document.getElementById('mqtt-username');
    const passwordInput = document.getElementById('mqtt-password');
    const statusDetail = document.getElementById('mqtt-status-detail');
    const statusLabel = document.getElementById('mqtt-status-label');
    const statusDot = document.getElementById('mqtt-status-dot');
    const statusPing = document.getElementById('mqtt-status-ping');
    const lastMessage = document.getElementById('mqtt-last-message');
    const syncLabel = document.getElementById('sync-time-label');
    let client;

    function setStatus(message, connected) {
        statusDetail.textContent = message;
        statusLabel.textContent = connected ? 'MQTT conectado' : message;
        statusLabel.classList.toggle('text-tertiary', connected);
        statusLabel.classList.toggle('text-on-surface-variant', !connected);
        statusDot.classList.toggle('bg-tertiary', connected);
        statusDot.classList.toggle('bg-outline', !connected);
        statusPing.classList.toggle('bg-tertiary', connected);
        statusPing.classList.toggle('bg-outline', !connected);
        statusPing.classList.toggle('animate-ping', connected);
    }

    function updateMetrics(payload) {
        if (!payload || typeof payload !== 'object' || Array.isArray(payload)) return;

        const values = payload.metrics && typeof payload.metrics === 'object'
            ? Object.assign({}, payload, payload.metrics)
            : payload;
        const metricAliases = {
            temperature: ['temperature_c', 'temperature', 'temp'],
            humidity: ['humidity_rh', 'humidity', 'humidity_percent'],
            pressure: ['barometer_hpa', 'pressure_hpa', 'pressure'],
            wind: ['wind_speed_kmh', 'wind_speed', 'wind'],
            rain: ['rainfall_mm', 'rain_mm', 'rain'],
            ph: ['ph_probe', 'ph'],
            conductivity: ['conductivity_us_cm', 'conductivity']
        };

        Object.keys(metricAliases).forEach(function (metric) {
            const key = metricAliases[metric].find(function (alias) {
                return values[alias] !== undefined && values[alias] !== null;
            });
            if (!key) return;

            const numericValue = Number(values[key]);
            if (!Number.isFinite(numericValue)) return;

            document.querySelectorAll('[data-mqtt-metric="' + metric + '"]').forEach(function (element) {
                element.textContent = numericValue.toLocaleString('es-ES', { maximumFractionDigits: 2 });
            });
        });
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        if (!window.mqtt || typeof window.mqtt.connect !== 'function') {
            setStatus('No se pudo cargar MQTT.js. Revisa la conexión a Internet y recarga.', false);
            return;
        }

        const username = usernameInput.value.trim();
        const password = passwordInput.value;
        if (!username || !password) {
            setStatus('Ingresa el usuario y la contraseña del broker.', false);
            return;
        }

        if (client) client.end(true);
        setStatus('Conectando al broker MQTT…', false);

        const connection = window.mqtt.connect(brokerUrl, {
            username: username,
            password: password,
            protocolVersion: 4,
            clean: true,
            connectTimeout: 10000,
            reconnectPeriod: 3000,
            clientId: 'frontend-' + Math.random().toString(16).slice(2, 10)
        });
        client = connection;
        let connectionError = '';

        connection.on('connect', function () {
            if (client !== connection) return;
            connectionError = '';
            passwordInput.value = '';
            setStatus('Conectado; suscribiendo a ' + topic + '.', true);
            connection.subscribe(topic, { qos: 0 }, function (error) {
                if (client !== connection) return;
                if (error) setStatus('Conectado, pero no se pudo suscribir a ' + topic + ': ' + error.message, true);
                else setStatus('Conectado y escuchando ' + topic + '.', true);
            });
        });

        connection.on('reconnect', function () {
            if (client !== connection) return;
            connectionError = '';
            setStatus('Reconectando al broker MQTT…', false);
        });

        connection.on('error', function (error) {
            if (client !== connection) return;
            connectionError = error.message;
            setStatus('Error MQTT: ' + error.message, false);
        });

        connection.on('close', function () {
            if (client !== connection) return;
            setStatus(connectionError ? 'Conexión cerrada: ' + connectionError : 'Conexión MQTT cerrada.', false);
        });

        connection.on('message', function (receivedTopic, message) {
            if (client !== connection) return;
            const text = message.toString();
            lastMessage.textContent = receivedTopic + '\n' + text;

            try {
                updateMetrics(JSON.parse(text));
            } catch (error) {
   
            }

            if (syncLabel) syncLabel.textContent = 'Hace unos segundos';
        });
    });
})();
