package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class WebserverExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {
    private static final long serialVersionUID = 475740871323858432L;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
