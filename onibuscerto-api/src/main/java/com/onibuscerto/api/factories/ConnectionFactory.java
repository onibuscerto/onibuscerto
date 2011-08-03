package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Connection;
import com.onibuscerto.api.entities.Stop;

public interface ConnectionFactory {

    public Connection createConnection(Stop source, Stop target);
}
