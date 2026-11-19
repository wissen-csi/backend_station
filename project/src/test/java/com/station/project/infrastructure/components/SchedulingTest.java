package com.station.project.infrastructure.components;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.station.project.application.services.CacheDataServices;
import com.station.project.application.services.DataBatchPersistenceService;
import com.station.project.domain.model.Data;

class SchedulingTest {

    private final CacheDataServices cacheDataServices = mock(CacheDataServices.class);
    private final DataBatchPersistenceService persistenceService =
            mock(DataBatchPersistenceService.class);
    private final Scheduling scheduling = new Scheduling(cacheDataServices, persistenceService);

    @Test
    void deletesOnlyBatchIdsAfterDatabasePersistenceSucceeds() {
        UUID readingId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Data reading = Data.builder().id(readingId).build();
        when(cacheDataServices.findAll()).thenReturn(List.of(reading));

        scheduling.saveRelationalDB();

        InOrder order = inOrder(persistenceService, cacheDataServices);
        order.verify(persistenceService).persistBatch(List.of(reading));
        order.verify(cacheDataServices).deleteById(readingId);
        verify(cacheDataServices, never()).deleteAll();
    }

    @Test
    void keepsRedisBatchWhenDatabasePersistenceFails() {
        Data reading = Data.builder().id(UUID.randomUUID()).build();
        when(cacheDataServices.findAll()).thenReturn(List.of(reading));
        doThrow(new IllegalStateException("database unavailable"))
                .when(persistenceService).persistBatch(List.of(reading));

        scheduling.saveRelationalDB();

        verify(cacheDataServices, never()).deleteById(reading.id());
        verify(cacheDataServices, never()).deleteAll();
    }
}
