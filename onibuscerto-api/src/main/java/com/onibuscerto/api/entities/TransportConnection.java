package com.onibuscerto.api.entities;

/**
 * Interface que representa uma conexão com o uso de transporte público.
 */
public interface TransportConnection extends Connection {

    /**
     * Retorna o identificador da Trip na qual esta conexão está contida.
     * @return identificador da Trip
     */
    public String getTripId();

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
}
