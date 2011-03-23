package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class WebserverExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {
    private static final long serialVersionUID = 475740871323858432L;

    private String containerType;

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }
}
