package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class Step {

    private String name;

    private Set<InstallOperation> operations = new HashSet<InstallOperation>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<InstallOperation> getOperations() {
        return operations;
    }
}
