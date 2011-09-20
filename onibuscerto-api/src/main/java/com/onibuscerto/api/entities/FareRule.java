/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.onibuscerto.api.entities;

/**
 *
 * @author Bruno
 */
public interface FareRule {
//fare_id,route_id,origin_id,destination_id,contains_id
    public FareAttribute getFareAttribute();
    public void setFareAttribute(FareAttribute fare);
}
