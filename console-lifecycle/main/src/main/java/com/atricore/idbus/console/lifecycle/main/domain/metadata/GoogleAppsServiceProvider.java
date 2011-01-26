package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class GoogleAppsServiceProvider extends ExternalServiceProvider {

    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
