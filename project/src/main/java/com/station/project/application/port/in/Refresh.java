package com.station.project.application.port.in;

import org.yaml.snakeyaml.util.Tuple;

public interface Refresh {
public Tuple<String,String> refresh(String authHeader);
}
