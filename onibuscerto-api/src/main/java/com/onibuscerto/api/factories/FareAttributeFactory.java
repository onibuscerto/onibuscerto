package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.FareAttribute;

public interface FareAttributeFactory {

    public FareAttribute createFareAttribute(String id);

    public FareAttribute getFareAttributeById(String id);
}
