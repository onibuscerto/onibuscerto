package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.FareAttribute;

public interface FareAttributeFactory {

    public FareAttribute createFareAttribute(String id);

    public FareAttribute getFareAttributeById(String id);
}
