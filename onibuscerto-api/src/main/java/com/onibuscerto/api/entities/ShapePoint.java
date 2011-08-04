package com.onibuscerto.api.entities;

/**
 * Interface que define um ponto de uma polilinha. A polilinha define um
 * caminho no mapa.
 */
public interface ShapePoint {

    /**
     * Retorna o id da polilinha da qual o ponto faz parte.
     * @return id da polilinha
     */
    public int getShapeId();

    /**
     * Atribui o id da polilinha da qual o ponto faz parte.
     * @param shapeId id da polilinha
     */
    public void setShapeId(int shapeId);

    /**
     * Retorna a latitude do ponto.
     * @return latitude em graus
     */
    public double getLatitude();

    /**
     * Atribui a latitude do ponto.
     * @param latitude latitude em graus
     */
    public void setLatitude(double latitude);

    /**
     * Retorna a longitude do ponto.
     * @return longitude em graus
     */
    public double getLongitude();

    /**
     * Atribui a longitude do ponto.
     * @param longitude longitude em graus
     */
    public void setLongitude(double longitude);

    /**
     * Retorna o número de sequência do ponto na polilinha.
     * @return número de sequência
     */
    public int getSequence();

    /**
     * Atribui o némero de sequência do ponto na polilinha.
     * @param sequence numero de sequência
     */
    public void setSequence(int sequence);

    /**
     * Retorna a distância percorrida, na mesma unidade usada no GTFS, do início
     * da polilinha até o ponto atual.
     * @return distância em unidades
     */
    public double getShapeDistTraveled();

    /**
     * Atribui a distância percorrida, na mesma unidade usada no GTFS, do inicio
     * da polilinha até o ponto atual.
     * @param shapeDistTraveled distância percorrida
     */
    public void setShapeDistTraveled(double shapeDistTraveled);
}
