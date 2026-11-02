package com.station.project.application.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

class DataServiceTest {

    private DataRepositoryPort dataRepository;
    private SensorRepositoryPort sensorRepository;
    private DataService dataService;

    @BeforeEach
    void setUp() {
        dataRepository = mock(DataRepositoryPort.class);
        sensorRepository = mock(SensorRepositoryPort.class);
        dataService = new DataService(dataRepository, sensorRepository);
    }

    @Test
    void createsMissingSensorBeforeSavingData() {
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        Data data = Data.builder().sensor(sensor).build();
        when(sensorRepository.existsById(sensor.id())).thenReturn(false);
        when(dataRepository.save(data)).thenReturn(data);

        assertSame(data, dataService.save(data));

        InOrder inOrder = inOrder(sensorRepository, dataRepository);
        inOrder.verify(sensorRepository).existsById(sensor.id());
        inOrder.verify(sensorRepository).save(sensor);
        inOrder.verify(dataRepository).save(data);
    }

    @Test
    void doesNotCreateSensorThatAlreadyExists() {
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        Data data = Data.builder().sensor(sensor).build();
        when(sensorRepository.existsById(sensor.id())).thenReturn(true);
        when(dataRepository.save(data)).thenReturn(data);

        dataService.save(data);

        verify(sensorRepository, never()).save(sensor);
        verify(dataRepository).save(data);
    }

    @Test
    void createsMissingSensorBeforeUpdatingData() {
        UUID dataId = UUID.randomUUID();
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        Data existing = Data.builder().id(dataId).build();
        when(sensorRepository.existsById(sensor.id())).thenReturn(false);
        when(dataRepository.findById(dataId)).thenReturn(existing);

        dataService.update(sensor, 1, 2, 3, LocalDate.now(), dataId.toString());

        InOrder inOrder = inOrder(sensorRepository, dataRepository);
        inOrder.verify(sensorRepository).existsById(sensor.id());
        inOrder.verify(sensorRepository).save(sensor);
        inOrder.verify(dataRepository).findById(dataId);
        inOrder.verify(dataRepository).save(argThat(
                data -> data.id().equals(dataId) && data.sensor().equals(sensor)));
    }

    @Test
    void createsEachMissingSensorOnlyOnceWhenSavingBatch() {
        Sensor sensor = Sensor.builder().id("sensor-1").name("Sensor 1").build();
        Data first = Data.builder().sensor(sensor).build();
        Data second = Data.builder().sensor(sensor).build();
        when(sensorRepository.existsById(sensor.id())).thenReturn(false);

        dataService.saveAll(List.of(first, second));

        verify(sensorRepository).existsById(sensor.id());
        verify(sensorRepository).save(sensor);
        verify(dataRepository).saveAll(List.of(first, second));
    }
}
