package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import org.neo4j.graphdb.Node;

public class FareAttributeImpl implements FareAttribute {

    private final Node underlyingNode;
    static final String KEY_ID = "fare_id";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CURRENCY_TYPE = "currency_type";
    private static final String KEY_PAYMENT_METHOD = "payment_method";
    private static final String KEY_TRANSFERS = "transfers";
    private static final String KEY_TRANSFER_DURATION = "transfer_duration";

    FareAttributeImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setFareId(id);
    }

    FareAttributeImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getFareId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private void setFareId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public double getPrice() {
        return (Double) underlyingNode.getProperty(KEY_PRICE);
    }

    @Override
    public void setPrice(double price) {
        underlyingNode.setProperty(KEY_PRICE, price);
    }

    @Override
    public String getCurrencyType() {
        return (String) underlyingNode.getProperty(KEY_CURRENCY_TYPE);
    }

    @Override
    public void setCurrencyType(String currencyType) {
        underlyingNode.setProperty(KEY_CURRENCY_TYPE, currencyType);
    }

    @Override
    public int getPaymentMethod() {
        return (Integer) underlyingNode.getProperty(KEY_PAYMENT_METHOD);
    }

    @Override
    public void setPaymentMethod(int paymentMethod) {
        underlyingNode.setProperty(KEY_PAYMENT_METHOD, paymentMethod);
    }

    @Override
    public int getTransfers() {
        return (Integer) underlyingNode.getProperty(KEY_TRANSFERS);
    }

    @Override
    public void setTransfers(int transfers) {
        underlyingNode.setProperty(KEY_TRANSFERS, transfers);
    }

    @Override
    public int getTransferDuration() {
        return (Integer) underlyingNode.getProperty(KEY_TRANSFER_DURATION);
    }

    @Override
    public void setTransferDuration(int transferDuration) {
        underlyingNode.setProperty(KEY_TRANSFER_DURATION, transferDuration);
    }
}
