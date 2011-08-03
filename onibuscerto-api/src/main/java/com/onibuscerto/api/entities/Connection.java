package com.onibuscerto.api.entities;

/**
 * Interface que representa uma conexão entre duas paradas.
 */
public interface Connection {

    /**
     * Retorna a parada de origem da conexão.
     * @return origem da conexão
     */
    public Stop getSource();

    /**
     * Retorna a parada de destino da conexão.
     * @return destino da conexão
     */
    public Stop getTarget();

    /**
     * Retorna o horário de partida desta conexão, a partir da origem.
     * @return horário de partida
     */
    public int getDepartureTime();

    /**
     * Atribui o horário de partida desta conexão, a partir da origem.
     * @param departureTime horário de partida
     */
    public void setDepartureTime(int departureTime);

    /**
     * Retorna o tempo em minutos entre a origem e o destino da conexão.
     * @return custo em minutos
     */
    public int getTimeCost();

    /**
     * Atribui o tempo em minutos entre a origem e o destino da conexão.
     * @param timeCost custo em minutos
     */
    public void setTimeCost(int timeCost);
}
