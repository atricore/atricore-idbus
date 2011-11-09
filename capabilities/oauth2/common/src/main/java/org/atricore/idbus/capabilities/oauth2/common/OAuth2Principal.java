package org.atricore.idbus.capabilities.oauth2.common;

import java.security.Principal;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Principal implements Principal {

    private String name;

    public OAuth2Principal() {

    }

    public OAuth2Principal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
