package com.tasca_s5_02_n1.joc_de_daus.model.domain;

import com.tasca_s5_02_n1.joc_de_daus.utils.Utilities;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "score")
public class ScoreModel {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "idPlayer", nullable = false)
    private String idPlayer;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "bet", nullable = false)
    private int bet;

    @Column(name = "leftDice", nullable = false)
    private int leftDice;

    @Column(name = "rightDice", nullable = false)
    private int rightDice;

    @Column(name = "wins", nullable = false)
    private int wins;

    //Constructor with an arguments
    public ScoreModel(String idPlayer, int score, int bet, int leftDice, int rightDice) {
        this.idPlayer = idPlayer;
        this.score = score;
        this.bet = bet;
        this.leftDice = leftDice;
        this.rightDice = rightDice;
        this.wins = Utilities.getPointsFromSideDiceThrow(bet,score,leftDice,rightDice);
        //this.wins = Utilities.getPointsFromDiceThrow(bet,score,leftDice,rightDice);
    }

    public ScoreModel(String id, String idPlayer, int score, int bet, int leftDice, int rightDice) {
        this.id = id;
        this.idPlayer = idPlayer;
        this.score = score;
        this.bet = bet;
        this.leftDice = leftDice;
        this.rightDice = rightDice;
        this.wins = Utilities.getPointsFromSideDiceThrow(bet,score,leftDice,rightDice);
        //this.wins = Utilities.getPointsFromDiceThrow(bet,score,leftDice,rightDice);
    }
    public int getLeftDice() {
        return leftDice;
    }

    public void setLeftDice(int leftDice) {
        this.leftDice = leftDice;
    }

    public int getRightDice() {
        return rightDice;
    }

    public void setRightDice(int rightDice) {
        this.rightDice = rightDice;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public ScoreModel() {
    }

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
