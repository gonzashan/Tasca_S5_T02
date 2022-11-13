package com.tasca_s5_02_n1.joc_de_daus.model.exception;

public abstract class Message {

    private String message;
    // Constructor
    public Message(String message) {
        this.message = message;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
}
