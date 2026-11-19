package com.station.project.infrastructure.components;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.station.project.application.services.CacheDataServices;
import com.station.project.application.services.DataBatchPersistenceService;
import com.station.project.domain.model.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduling {

    private final CacheDataServices cacheDataServices;
    private final DataBatchPersistenceService dataBatchPersistenceService;

    @Scheduled(fixedDelay = 5000)
    public void saveRelationalDB() {
        List<Data> list = cacheDataServices.findAll();

        if (!list.isEmpty()) {
            log.info("-----> Intentando guardar {} registros en PostgreSQL...", list.size());
            try {
                dataBatchPersistenceService.persistBatch(list);
                log.info("-----> ¡Registros guardados en PostgreSQL exitosamente!");
            } catch (RuntimeException e) {
                log.error("-----> Error guardando en PostgreSQL. Los datos permanecen en Redis.", e);
                return;
            }

            for (Data data : list) {
                try {
                    cacheDataServices.deleteById(data.id());
                } catch (RuntimeException e) {
                    log.error("La lectura {} se guardó en PostgreSQL, pero no se pudo retirar de Redis. "
                            + "Se reintentará en el siguiente ciclo.", data.id(), e);
                }
            }
        } else {
            log.info("No hay datos nuevos en Redis para guardar.");
        }
    }
}
