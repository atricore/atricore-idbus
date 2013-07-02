package org.atricore.idbus.capabilities.oauth2.rserver.http;

import java.security.Principal;
import java.util.Properties;

public class OAuth2Principal implements Principal {

    private String name;

    private Properties props;

    public OAuth2Principal(String name, Properties props) {
        this.name = name;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }
}
