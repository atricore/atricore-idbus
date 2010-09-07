package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Author: Dejan Maric
 */
public class JBossExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475540870033867381L;

    private String instance;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
