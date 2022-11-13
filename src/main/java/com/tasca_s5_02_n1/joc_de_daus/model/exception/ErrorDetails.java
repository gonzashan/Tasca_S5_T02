package com.tasca_s5_02_n1.joc_de_daus.model.exception;


public class ErrorDetails extends Message {

    private Integer status;

    //Constructors
    public ErrorDetails(Integer status, String message) {
        super(message);
        this.status = status;
    }

    public ErrorDetails(){}

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
