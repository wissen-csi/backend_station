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
        List<Data> list = cacheDataServices.findAll().stream()
                .map(x -> Data.builder()
                        .id(null)
                        .sensor(x.sensor())
                        .fechaHora(x.fechaHora())
                        .temperatura(x.temperatura())
                        .humedad(x.humedad())
                        .presion(x.presion())
                        .vientoVelocidad(x.vientoVelocidad())
                        .vientoDireccion(x.vientoDireccion())
                        .lluvia(x.lluvia())
                        .calidad(x.calidad())
                        .build())
                .toList();

        if (list != null && !list.isEmpty()) {
            log.info("-----> Intentando guardar {} registros en PostgreSQL...", list.size());

            try {
                dataService.saveAll(list);
                log.info("-----> ¡Registros guardados en PostgreSQL exitosamente!");
            } catch (Exception e) {
                log.error("-----> Error guardando en PostgreSQL. Los datos permanecen en Redis.", e);
                return;
            }

            try {
                log.info("-----> Limpiando Redis para preparar el siguiente lote.");
                cacheDataServices.deleteAll();
            } catch (Exception e) {
                log.error("-----> Los datos se guardaron en PostgreSQL, pero no se pudo limpiar Redis.", e);
            }
        } else {
            log.info("No hay datos nuevos en Redis para guardar.");
        }
    }
}