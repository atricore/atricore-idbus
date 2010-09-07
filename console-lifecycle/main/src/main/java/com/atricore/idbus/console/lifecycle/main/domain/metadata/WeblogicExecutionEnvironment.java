package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Author: Dejan Maric
 */
public class WeblogicExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 475540870033858942L;

    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
