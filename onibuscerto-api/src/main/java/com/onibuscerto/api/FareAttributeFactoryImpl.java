package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import com.onibuscerto.api.factories.FareAttributeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class FareAttributeFactoryImpl implements FareAttributeFactory {

    @Override
    public FareAttribute createFareAttribute(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FareAttribute getFareAttributeById(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
