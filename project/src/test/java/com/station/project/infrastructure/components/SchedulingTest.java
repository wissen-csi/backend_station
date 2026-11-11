package com.station.project.infrastructure.components;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.station.project.application.services.CacheDataServices;
import com.station.project.application.services.DataService;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

class SchedulingTest {

    private CacheDataServices cacheDataServices;
    private DataService dataService;
    private Scheduling scheduling;

    @BeforeEach
    void setUp() {
        cacheDataServices = mock(CacheDataServices.class);
        dataService = mock(DataService.class);
        scheduling = new Scheduling(cacheDataServices, dataService);
    }

    @Test
    void savesCurrentAndClearsCacheAfterSuccessfulDatabaseSave() {
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        Data cachedData = Data.builder()
                .id(UUID.randomUUID())
                .sensor(sensor)
                .temperatura(22.4)
                .humedad(54.2)
                .presion(1014.2)
                .fechaHora(OffsetDateTime.now())
                .build();
        when(cacheDataServices.findAll()).thenReturn(List.of(cachedData));

        scheduling.saveRelationalDB();

        Data expected = Data.builder()
                .sensor(sensor)
                .temperatura(22.4)
                .humedad(54.2)
                .presion(1014.2)
                .fechaHora(cachedData.fechaHora())
                .build();
        verify(dataService).saveAll(List.of(expected));
        verify(cacheDataServices).deleteAll();
    }

    @Test
    void keepsCacheWhenDatabaseSaveFails() {
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        when(cacheDataServices.findAll()).thenReturn(List.of(Data.builder()
                .sensor(sensor)
                .fechaHora(OffsetDateTime.now())
                .build()));
        doThrow(new RuntimeException("database failure")).when(dataService)
                .saveAll(org.mockito.ArgumentMatchers.anyList());

        scheduling.saveRelationalDB();

        verify(cacheDataServices, never()).deleteAll();
    }
}
