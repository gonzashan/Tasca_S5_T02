package com.tasca_s5_02_n1.joc_de_daus.model.domain;

//import org.springframework.data.mongodb.core.mapping.Document;


import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDate;



@Document(collection = "player")
public class PlayerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    @javax.persistence.Id

    //@Column(name = "namePlayer", unique = false)
    private String namePlayer;

    private LocalDate signUpDate;

    private boolean logged;


    /** CONSTRUCTOR
     *
     */
    public PlayerModel() {
        this.signUpDate = java.time.LocalDate.now();
    }

    public PlayerModel(String id, String namePlayer) {
        this.id = id;
        this.namePlayer = namePlayer;
    }

    public String getId() {
        return id;
    }

    public void setId(String idPlayer) {
        this.id = idPlayer;
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

    public boolean getLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
}

