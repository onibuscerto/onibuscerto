/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.onibuscerto.api.entities;

import java.util.Collection;

/**
 *
 * @author Bruno
 */
public interface FareRule {
//fare_id,route_id,origin_id,destination_id,contains_id
    public String getId();
    public void setId(String id);

    public FareAttribute getFareAttribute();
    public void setFareAttribute(FareAttribute fare);

    public Stop getSource();
    public void setSource(Stop source);

    public Stop getTarget();
    public void setTarget(Stop target);

    public Collection<Stop> getStopsFromFare();
    public void setStopsFromFare(Collection<Stop> stops);
}
