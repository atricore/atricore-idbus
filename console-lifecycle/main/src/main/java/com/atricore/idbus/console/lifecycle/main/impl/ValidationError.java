package com.atricore.idbus.console.lifecycle.main.impl;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ValidationError {
    private String msg;

    private Throwable error;

    public ValidationError(String msg) {
        this.msg = msg;
    }

    public ValidationError(String msg, Throwable error) {
        this.msg = msg;
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getError() {
        return error;
    }
    
}
