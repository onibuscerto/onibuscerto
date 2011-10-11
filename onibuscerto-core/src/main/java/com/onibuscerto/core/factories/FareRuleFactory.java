package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.FareRule;
import java.util.Collection;

public interface FareRuleFactory {

    public Collection<FareRule> getAllFareRules();

    public FareRule createFareRule(String id);

    public FareRule getFareRuleById(String id);
}
