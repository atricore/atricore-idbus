package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class JEEExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {

    private static final long serialVersionUID = 475540870033834381L;

    private String instance;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
