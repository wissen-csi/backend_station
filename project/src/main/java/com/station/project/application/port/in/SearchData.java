package com.station.project.application.port.in;

import java.util.UUID;

import com.station.project.domain.model.Data;

public interface SearchData {
    Data findById(UUID id);
}

