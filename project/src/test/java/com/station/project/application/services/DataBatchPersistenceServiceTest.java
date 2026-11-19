package com.station.project.application.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

class DataBatchPersistenceServiceTest {

    private final DataRepositoryPort dataRepository = mock(DataRepositoryPort.class);
    private final SensorRepositoryPort sensorRepository = mock(SensorRepositoryPort.class);
    private final DataBatchPersistenceService service =
            new DataBatchPersistenceService(dataRepository, sensorRepository);

    @Test
    void persistsOriginalReadingIdAndCreatesMissingSensorBeforeBatch() {
        UUID readingId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Sensor sensor = Sensor.builder().id("sensor-1").name("Estacion Norte").build();
        Data reading = Data.builder()
                .id(readingId)
                .sensor(sensor)
                .windSpeed(5.5)
                .precipitation(0)
                .temperature(22)
                .altitude(12)
                .humidity(60)
                .pressure(1013)
                .measuredAt(LocalDateTime.of(2026, 11, 19, 15, 27))
                .build();
        when(sensorRepository.existsById(sensor.id())).thenReturn(false);

        service.persistBatch(List.of(reading));

        verify(sensorRepository).save(sensor);
        verify(dataRepository).saveAll(List.of(reading));
    }

    @Test
    void rejectsReadingWithoutIdWithoutWritingToDatabase() {
        Data reading = Data.builder()
                .sensor(Sensor.builder().id("sensor-1").name("Estacion Norte").build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> service.persistBatch(List.of(reading)));

        verify(sensorRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(dataRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }
}
