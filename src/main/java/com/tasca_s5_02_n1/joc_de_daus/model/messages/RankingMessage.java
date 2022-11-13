package com.tasca_s5_02_n1.joc_de_daus.model.messages;


import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;

public class RankingMessage extends Message {

    private double rankingResult;

    public RankingMessage(String message, double rankingResult) {
        super(message);
        this.rankingResult = rankingResult;
    }

    public RankingMessage() {
    }

    public double getRankingResult() {
        return rankingResult;
    }

    public void setRankingResult(double rankingResult) {
        this.rankingResult = rankingResult;
    }
}
