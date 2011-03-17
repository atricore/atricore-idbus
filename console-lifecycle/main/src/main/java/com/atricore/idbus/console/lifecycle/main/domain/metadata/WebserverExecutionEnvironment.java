package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Author: Dejan Maric
 */
public class WebserverExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475740871223858432L;

    private String containerType;

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }
}
