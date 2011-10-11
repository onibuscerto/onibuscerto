package com.onibuscerto.core.entities;

import com.onibuscerto.core.utils.GlobalPosition;

/**
 * Interface que define um ponto de uma polilinha. A polilinha define um
 * caminho no mapa.
 */
public interface ShapePoint {

    /**
     * Retorna o id da polilinha da qual o ponto faz parte.
     * @return id da polilinha
     */
    public String getShapeId();

    /**
     * Atribui o id da polilinha da qual o ponto faz parte.
     * @param shapeId id da polilinha
     */
    public void setShapeId(String shapeId);

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
     * Retorna a latitude e a longitude do ponto em um objeto <code>GlobalPosition</code>.
     * @return a latitude e a longitude do ponto em um objeto <code>GlobalPosition</code>
     */
    public GlobalPosition getGlobalPosition();

    /**
     * Atribui a latitude e a longitude do ponto a partir de um objeto <code>GlobalPosition</code>.
     * @param globalPosition objeto que contém informações de latitude e longitude
     */
    public void setGlobalPosition(GlobalPosition globalPosition);

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

    /**
     * Retorna o próximo ponto da polilinha.
     * @return próximo ponto da polilinha
     */
    public ShapePoint getNext();

    /**
     * Atribui o próximo ponto da polilinha.
     * @param stopTime próximo ponto da polilinha
     */
    public void setNext(ShapePoint stopTime);

    /**
     * Verifica se existem mais pontos na polilinha depois deste.
     * @return true se este não for o último ponto da polilinha
     */
    public boolean hasNext();

    /**
     * Verifica se este é o primeiro ponto da polilinha.
     * @return true se este for o primeiro ponto da polilinha
     */
    public boolean isFirst();
}
