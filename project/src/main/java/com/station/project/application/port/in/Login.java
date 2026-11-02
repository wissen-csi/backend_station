package com.station.project.application.port.in;

import org.yaml.snakeyaml.util.Tuple;

public interface Login {
public Tuple<String,String> login(String userName, String password);
}
