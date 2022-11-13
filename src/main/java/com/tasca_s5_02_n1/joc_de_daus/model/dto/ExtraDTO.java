package com.tasca_s5_02_n1.joc_de_daus.model.dto;

import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;

public class ExtraDTO extends Message {

    private String idPlayer;
    private String namePlayer;
    private double wins;


    public ExtraDTO(String message) {
        super(message);
    }

    public ExtraDTO() {
    }

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }



    public double getWins() {
        return wins;
    }

    public void setWins(double wins) {
        this.wins = wins;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }
}
