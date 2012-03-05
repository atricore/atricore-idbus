package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserDashboardBranding implements Serializable {

    private static final long serialVersionUID = 6144244229951310689L;

    private String id;

    private String name;

    public UserDashboardBranding() {

    }

    public UserDashboardBranding(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
