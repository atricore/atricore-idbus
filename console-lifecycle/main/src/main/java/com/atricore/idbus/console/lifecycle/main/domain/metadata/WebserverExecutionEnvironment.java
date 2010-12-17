package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Author: Dejan Maric
 */
public class WebserverExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475740871223858432L;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
