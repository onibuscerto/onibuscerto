/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onibuscerto.api.entities;

/**
 *
 * @author Bruno
 */
public interface FareAttribute {
//fare_id,price,currency_type,payment_method,transfers,transfer_duration

    public String getFareId();

    public double getPrice();

    public void setPrice(double price);

    public String getCurrencyType();

    public void setCurrencyType(String currencyType);

    public int getPaymentMethod();

    public void setPaymentMethod(int paymentMethod);

    public int getTransfers();

    public void setTransfers(int transfers);

    public int getTransferDuration();

    public void setTransferDuration(int transferDuration);
}
