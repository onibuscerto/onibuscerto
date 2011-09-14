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
    public void setPaymentMathod(int paymentMethod);

    public int getTranfers();
    public void setTranfers(int transfers);

    public int getTransferDuration();
    public void setTranferDuration(int transferDuration);
}
