package com.tasca_s5_02_n1.joc_de_daus.model.dto;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;

import java.time.LocalDate;
import java.util.*;

public class PlayerDTO extends Message {

    private String idPlayer;

    private String namePlayer;

    private LocalDate signUpDate;

    private boolean logged;

    private int games;

    private List<ScoreModel> scoreList = new ArrayList<>();

    private int points, loser;

    private double average;

    private List<String> usersRegistred = new ArrayList<>();

    private int ranking;

    private String token;


    /**
     * Constructors
     */
    public PlayerDTO() {
        super("Updated!");
    }


    public PlayerDTO(String name) {

        this.namePlayer = name;
    }

    public PlayerDTO(String idPlayer,String namePlayer){
        this.idPlayer = idPlayer;
        this.namePlayer = namePlayer;

    }




    /**
     * Getters & Setters
     */
    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }


    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }


    public LocalDate getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(LocalDate signUpDate) {
        this.signUpDate = signUpDate;
    }


    public List<ScoreModel> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<ScoreModel> scoreList) {
        this.scoreList = scoreList;
    }


    public List<String> getUsersRegistred() {
        return usersRegistred;
    }

    public void setUsersRegistred(List<String> usersRegistred) {
        this.usersRegistred = usersRegistred;
    }


    public int getPoints() {
        getScoreList().stream().filter(r -> r.getWins() >= 1).forEach(r -> this.points+= r.getWins());
        return points;
    }

    public void setPoints(int points) {

        this.points = points;
    }

    public int getLoser() {
        return loser;
    }

    public void setLoser(int loser) {
        this.loser = loser;
    }


    public double getAverage() {

        //System.out.println(this.namePlayer);
        if (Double.isNaN((this.games * 100.0f) / this.scoreList.size()))
            return 0;
        else
            return  Math.round((this.games * 100.0f) / this.scoreList.size());
    }

    public void setAverage(double average) {

        this.average = average;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }


    public int getGames() {
        this.games = 0;
        getScoreList().stream().filter(r -> r.getWins() >= 1).forEach(r -> this.games++);
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }


    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
