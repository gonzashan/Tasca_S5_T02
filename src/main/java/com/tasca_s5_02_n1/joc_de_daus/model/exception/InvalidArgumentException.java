package com.tasca_s5_02_n1.joc_de_daus.model.exception;

import org.openqa.selenium.WebDriverException;

public class InvalidArgumentException extends WebDriverException {

    public int status;
    public InvalidArgumentException(String message) {
        super(message);
    }
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentException(){}
}