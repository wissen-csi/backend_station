package com.station.project.infrastructure.components;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.station.project.application.services.CacheDataServices;
import com.station.project.application.services.DataService;
import com.station.project.domain.model.Data;

import lombok.AllArgsConstructor;

@Component 
@AllArgsConstructor 
public class Scheduling {
    private CacheDataServices cacheDataServices;
    private DataService dataService;
    @Scheduled (cron = " 10 * * * * *")
    public void saveRelationalDB(){
        List<Data> list = cacheDataServices.findAll();
        if(list!=null){
            dataService.saveAll(list);
            cacheDataServices.deleteAll();
        }
    }
    public void sendmails(){
        //to do
    }
    
}
