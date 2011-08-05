package com.onibuscerto.api.entities;

/**
 * Interface que define um tipo de entidade com uma posição no mapa. A posição
 * no mapa é dada pelo par latitude e longitude, em graus.
 */
public interface Location {

    /**
     * Retorna a latitude da entidade.
     * @return latitude em graus
     */
    public double getLatitude();

    /**
     * Atribui a latitude da entidade.
     * @param latitude latitude em graus
     */
    public void setLatitude(double latitude);

    /**
     * Retorna a longitude da entidade.
     * @return longitude em graus
     */
    public double getLongitude();

    /**
     * Atribui a longitude da entidade.
     * @param longitude longitude em graus
     */
    public void setLongitude(double longitude);
}
