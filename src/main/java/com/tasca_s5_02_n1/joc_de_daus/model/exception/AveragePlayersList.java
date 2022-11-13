package com.tasca_s5_02_n1.joc_de_daus.model.exception;

import java.util.Map;

public class AveragePlayersList extends Message{

    private Map<String,Double> listPlayersAverage;

    public AveragePlayersList(Map<String,Double> result, String message) {
        super(message + " Resultados. 200 Ok!");
        this.listPlayersAverage = result;

    }

    public Map<String,Double> getListPlayersAverage() {
        return listPlayersAverage;
    }
}
