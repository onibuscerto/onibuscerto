package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.FareRule;
import java.util.Collection;

public interface FareRuleFactory {

    public Collection<FareRule> getAllFareRules();

    public FareRule createFareRule(String id);

    public FareRule getFareRuleById(String id);
}
