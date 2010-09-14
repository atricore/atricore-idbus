package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class WeblogicExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {

    private static final long serialVersionUID = 475540870033858942L;

    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
