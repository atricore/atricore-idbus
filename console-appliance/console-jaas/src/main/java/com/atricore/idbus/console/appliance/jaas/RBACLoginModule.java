package com.atricore.idbus.console.appliance.jaas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.jaas.modules.RolePrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RBACLoginModule implements LoginModule {

    private static Log logger = LogFactory.getLog(RBACLoginModule.class);

    //
    private List<String> allowedRoles;

    private Subject subject;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

        try {
            this.subject = subject;

            allowedRoles = new ArrayList<String>();

            String rolesCsv = (String) options.get("allowed.roles");
            if (rolesCsv != null) {
                StringTokenizer st = new StringTokenizer(rolesCsv, ",");
                while(st.hasMoreTokens()) {
                    allowedRoles.add(st.nextToken());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean login() throws LoginException {
        return true;
    }

    public boolean commit() throws LoginException {

        Set<RolePrincipal> roles = subject.getPrincipals(RolePrincipal.class);
        for (RolePrincipal role : roles)  {
            String roleName = role.getName();
            for (int i = 0 ; i < allowedRoles.size(); i++) {
                String allowedRoleName = allowedRoles.get(i);
                if (allowedRoleName.equals(roleName)) {
                    logger.debug("User has role " + allowedRoleName);
                    return true;
                }
            }
        }

        logger.debug("User does not have any of the allowed roles.");

        throw new LoginException("Authorization failed");
    }

    public boolean abort() throws LoginException {
        this.subject = null;
        return true;
    }

    public boolean logout() throws LoginException {
        this.subject = null;
        return true;
    }
}
