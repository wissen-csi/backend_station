package com.station.project.application.services;

import java.nio.charset.StandardCharsets;
import java.util.List;


import org.springframework.stereotype.Service;

import com.station.project.application.port.in.ExportData;
import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.domain.model.Data;

import lombok.RequiredArgsConstructor;

/**
 * Convierte las lecturas meteorológicas persistidas en formatos CSV y Excel.
 */
@Service
@RequiredArgsConstructor
public class DataExportService implements ExportData {

    private final DataRepositoryPort dataRepository;

    /**
     * Exporta las lecturas de PostgreSQL como CSV UTF-8.
     * Los valores nulos se representan como celdas vacías.
     */
    @Override
    public byte[] exportCsv() {
        List<Data> readings = dataRepository.findAll();
        StringBuilder csv = new StringBuilder();

        // BOM UTF-8 para facilitar la apertura del archivo en Excel.
        csv.append('\uFEFF');
        csv.append("id,sensorId,sensorName,windSpeed,windDirection,precipitation,")
                .append("temperature,altitude,humidity,pressure,measuredAt\r\n");

        for (Data data : readings) {
            csv.append(csvValue(data.id())).append(',')
                    .append(csvValue(data.sensor() == null ? null : data.sensor().id())).append(',')
                    .append(csvValue(data.sensor() == null ? null : data.sensor().name())).append(',')
                    .append(csvValue(data.windSpeed())).append(',')
                    .append(csvValue(data.windDirection())).append(',')
                    .append(csvValue(data.precipitation())).append(',')
                    .append(csvValue(data.temperature())).append(',')
                    .append(csvValue(data.altitude())).append(',')
                    .append(csvValue(data.humidity())).append(',')
                    .append(csvValue(data.pressure())).append(',')
                    .append(csvValue(data.measuredAt()))
                    .append("\r\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Exporta las lecturas de PostgreSQL como un libro de Excel (.xlsx).
     */
    @Override
    public byte[] exportExcel() {
        List<Data> readings = dataRepository.findAll();

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Lecturas meteorológicas");
            String[] headers = {
                    "ID", "ID sensor", "Nombre sensor", "Velocidad viento",
                    "Dirección viento", "Precipitación", "Temperatura", "Altitud",
                    "Humedad", "Presión", "Fecha de medición"
            };

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);

            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            for (int column = 0; column < headers.length; column++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(column);
                cell.setCellValue(headers[column]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Data data : readings) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                setText(row, 0, data.id());
                setText(row, 1, data.sensor() == null ? null : data.sensor().id());
                setText(row, 2, data.sensor() == null ? null : data.sensor().name());
                setNumber(row, 3, data.windSpeed());
                setNumber(row, 4, data.windDirection());
                setNumber(row, 5, data.precipitation());
                setNumber(row, 6, data.temperature());
                setNumber(row, 7, data.altitude());
                setNumber(row, 8, data.humidity());
                setNumber(row, 9, data.pressure());
                setText(row, 10, data.measuredAt());
            }

            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                    0, Math.max(0, readings.size()), 0, headers.length - 1));
            for (int column = 0; column < headers.length; column++) {
                sheet.autoSizeColumn(column);
            }

            workbook.write(output);
            return output.toByteArray();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("No fue posible generar el archivo Excel", exception);
        }
    }

    private static void setText(org.apache.poi.ss.usermodel.Row row, int column, Object value) {
        if (value != null) {
            row.createCell(column).setCellValue(value.toString());
        }
    }

    private static void setNumber(org.apache.poi.ss.usermodel.Row row, int column, Number value) {
        if (value != null) {
            row.createCell(column).setCellValue(value.doubleValue());
        }
    }

    private static String csvValue(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString();
        boolean mustQuote = text.contains(",") || text.contains("\"")
                || text.contains("\r") || text.contains("\n");

        if (text.contains("\"")) {
            text = text.replace("\"", "\"\"");
        }

        return mustQuote ? "\"" + text + "\"" : text;
    }
}
