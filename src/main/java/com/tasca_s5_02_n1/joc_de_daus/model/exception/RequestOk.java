package com.tasca_s5_02_n1.joc_de_daus.model.exception;

public class RequestOk extends Message{

    public RequestOk(String message) {
        super(message);
    }

    public RequestOk(){}

    public String getMessage(){
        return super.getMessage();
    }
    public void setMessage(String message){
       super.setMessage(message);
    }
}
