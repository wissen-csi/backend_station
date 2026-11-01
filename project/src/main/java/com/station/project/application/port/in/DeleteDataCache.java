package com.station.project.application.port.in;

import java.util.UUID;

public interface DeleteDataCache {
    public void deleteById(UUID id);
    public void deleteAll();
}
