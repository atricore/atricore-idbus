package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserDashboardBranding implements Serializable {

    private static final long serialVersionUID = 6144244229951310689L;

    private String id;

    private String name;

    private String description;

    private String skin;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
