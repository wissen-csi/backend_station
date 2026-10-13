package com.station.project.application.port.in;

import java.util.List;

import com.station.project.domain.model.Data;

public interface SaveDataCache {
    public Data save(Data data);
    public List<Data> saveAll(List<Data> list);
}
