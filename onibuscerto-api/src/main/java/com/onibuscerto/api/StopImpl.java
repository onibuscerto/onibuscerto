package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import org.neo4j.graphdb.Node;

class StopImpl extends LocationImpl implements Stop {

    static final String KEY_ID = "stop_id";
    private static final String KEY_NAME = "stop_name";

    StopImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setId(id);
    }

    StopImpl(Node underlyingNode) {
        super(underlyingNode);
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getName() {
        return (String) underlyingNode.getProperty(KEY_NAME);
    }

    @Override
    public void setName(String name) {
        underlyingNode.setProperty(KEY_NAME, name);
    }
}
