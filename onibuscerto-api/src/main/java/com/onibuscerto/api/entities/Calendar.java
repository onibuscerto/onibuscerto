package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Calendar {

    /**
     * Retorna o id que identifica as datas em que o serviço estará disponível
     * para um ou mais trajetos.
     * @return id do service
     */
    public String getServiceId();

    /**
     * Retorna o id que identifica as datas em que o serviço estará disponível
     * @param serviceId id do service
     */
    public void setServiceId(String serviceId);

    /**
     * Retorna um vetor contendo a disponibilidade de serviço por dia da semana
     * @return disponibilidade do serviço por dia da semana:
     * 0: indisponível
     * 1: disponível
     */
    public String getDaysOfWork(String day);

    /**
     * Atribui um vetor contendo a disponibilidade de serviço por dia da semana
     * @param days vetor com a disponibilidade do serviço por dia da semana:
     * 0: indisponível
     * 1: disponível
     */
    public void setDaysOfWork(String day, String value);

    /**
     * Retorna a data de início do serviço.
     * @return data de início do serviço.
     */
    public String getStartDate();

    /**
     * Atribui a data de início do serviço. O valor do campo startDate deve
     * estar no formato AAAAMMDD.
     * @param startDate data de início do serviço.
     */
    public void setStartDate(String startDate);

    /**
     * Retorna a data de término do serviço.
     * @return data de término do serviço.
     */
    public String getEndDate();

    /**
     * Atribui a data de término do serviço. O valor do campo endDate deve
     * estar no formato AAAAMMDD.
     * @param endDate data de início do serviço.
     */
    public void setEndDate(String endDate);
}
