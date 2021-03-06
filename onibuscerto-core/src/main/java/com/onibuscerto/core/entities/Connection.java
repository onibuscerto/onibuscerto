package com.onibuscerto.core.entities;

/**
 * Interface que representa uma conexão entre dois locais.
 */
public interface Connection {

    /**
     * Retorna a parada de origem da conexão.
     * @return origem da conexão
     */
    public Location getSource();

    /**
     * Retorna a parada de destino da conexão.
     * @return destino da conexão
     */
    public Location getTarget();

    /**
     * Retorna o tempo em segundos entre a origem e o destino da conexão.
     * @return custo em segundos
     */
    public int getTimeCost();

    /**
     * Atribui o tempo em segundos entre a origem e o destino da conexão.
     * @param timeCost custo em segundos
     */
    public void setTimeCost(int timeCost);
}
