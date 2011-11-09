package org.atricore.idbus.capabilities.oauth2.common;

import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2AccessToken implements java.io.Serializable {


    private String user;

    private String[] roles;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

}

