package com.onibuscerto.api.entities;

/**
 * Interface que representa uma conexão a pé entre dois nós do grafo.
 */
public interface WalkingConnection extends Connection {

    /**
     * Atribui a distância a ser percorrida a pé entre os nós, em metros.
     * @param distance a distância entre os nós, em metros
     */
    public void setWalkingDistance(double distance);

    /**
     * Retorna a distância a ser percorrida a pé entre os nós, em metros.
     * @return a distância entre os nós, em metros
     */
    public double getWalkingDistance();
}
