package org.atricore.idbus.capabilities.sso.ui.security;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;


public class WebSSOSession extends AuthenticatedWebSession {

    private Roles roles = new Roles();

    public WebSSOSession(Request request) {
        super(request);
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = false;

        return authenticated;
    }

    public Roles getRoles() {
        return roles;
    }


}
