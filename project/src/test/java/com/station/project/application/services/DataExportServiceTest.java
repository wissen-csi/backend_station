package com.station.project.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.station.project.application.port.in.ExportData;
import com.station.project.application.port.in.SearchData;
import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;
import com.station.project.infrastructure.controller.DataExportController;

class DataExportServiceTest {

    private final DataRepositoryPort dataRepository = mock(DataRepositoryPort.class);
    private final DataExportService service = new DataExportService(dataRepository);

    @Test
    void exportPortAndControllerWireWithoutSearchDataBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(DataRepositoryPort.class, () -> mock(DataRepositoryPort.class));
            context.register(DataExportService.class, DataExportController.class);
            context.refresh();

            assertNotNull(context.getBean(ExportData.class));
            assertNotNull(context.getBean(DataExportController.class));
            assertTrue(context.getBeansOfType(SearchData.class).isEmpty());
        }
    }

    @Test
    void exportCsvReadsPersistedDataAndEscapesCsvValues() {
        Data reading = Data.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .sensor(Sensor.builder().id("sensor-1").name("Estación, \"Norte\"").build())
                .windSpeed(10.5)
                .windDirection(null)
                .precipitation(1.2)
                .temperature(22.3)
                .altitude(150.0)
                .humidity(60.0)
                .pressure(1013.0)
                .measuredAt(LocalDateTime.of(2026, 11, 19, 14, 48))
                .build();
        when(dataRepository.findAll()).thenReturn(List.of(reading));

        String csv = new String(service.exportCsv(), StandardCharsets.UTF_8);

        assertTrue(csv.startsWith("\uFEFFid,sensorId,sensorName,"));
        assertTrue(csv.contains("\"Estación, \"\"Norte\"\"\""));
        assertTrue(csv.contains("10.5,,1.2,22.3,150.0,60.0,1013.0,2026-11-19T14:48"));
        verify(dataRepository).findAll();
    }

    @Test
    void exportExcelProducesReadableWorkbookFromPersistedData() throws Exception {
        Data reading = Data.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .sensor(Sensor.builder().id("sensor-1").name("Estación Norte").build())
                .windSpeed(10.5)
                .windDirection(180.0)
                .precipitation(1.2)
                .temperature(22.3)
                .altitude(150.0)
                .humidity(60.0)
                .pressure(1013.0)
                .measuredAt(LocalDateTime.of(2026, 11, 19, 14, 48))
                .build();
        when(dataRepository.findAll()).thenReturn(List.of(reading));

        byte[] excel = service.exportExcel();

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            var sheet = workbook.getSheet("Lecturas meteorológicas");
            assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Estación Norte", sheet.getRow(1).getCell(2).getStringCellValue());
            assertEquals(10.5, sheet.getRow(1).getCell(3).getNumericCellValue());
        }
        verify(dataRepository).findAll();
    }
}
