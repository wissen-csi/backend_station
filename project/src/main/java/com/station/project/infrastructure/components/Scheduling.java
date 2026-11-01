package com.station.project.infrastructure.components;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.station.project.application.services.CacheDataServices;
import com.station.project.application.services.DataService;
import com.station.project.domain.model.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduling {

    private final CacheDataServices cacheDataServices;
    private final DataService dataService;

    @Scheduled(cron = " 10 * * * * *")
    public void saveRelationalDB() {
        List<Data> list = cacheDataServices.findAll().stream().map(x-> Data.builder().id(null).sensor(x.sensor()).data(x.data()).data1(x.data1()).data2(x.data2()).build()).toList();
        
        if (list != null && !list.isEmpty()) {
            log.info("-----> Intentando guardar {} registros en PostgreSQL...", list.size());
            
            try {
                dataService.saveAll(list);
                log.info("-----> ¡Registros guardados en PostgreSQL exitosamente!");
            } catch (Exception e) {
                log.error("-----> Error guardando en PostgreSQL. Detalles: {}", e.getMessage());
            } finally {
                log.info("-----> Limpiando Redis para preparar el siguiente lote.");
                cacheDataServices.deleteAll();
            }
        } else {
            log.info("No hay datos nuevos en Redis para guardar.");
        }
    }
}