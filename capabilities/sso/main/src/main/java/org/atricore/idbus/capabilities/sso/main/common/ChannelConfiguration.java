package org.atricore.idbus.capabilities.sso.main.common;

import java.io.Serializable;

/**
 * Created by sgonzalez.
 */
public abstract class ChannelConfiguration implements Serializable {

    private String name;

    public ChannelConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
