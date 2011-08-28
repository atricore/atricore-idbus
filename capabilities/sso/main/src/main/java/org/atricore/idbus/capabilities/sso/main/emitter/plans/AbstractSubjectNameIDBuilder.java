package org.atricore.idbus.capabilities.sso.main.emitter.plans;

import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractSubjectNameIDBuilder implements SubjectNameIDBuilder {

    protected SSOUser getSsoUser(Subject s) {
        Set<SSOUser> ssoUsers = s.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() != 1)
            throw new RuntimeException("Subject must contain a SSOUser principal");

        return ssoUsers.iterator().next();
    }

    protected SSONameValuePair getProperty(SSOUser ssoUser, String name) {

        for (int i = 0; i < ssoUser.getProperties().length; i++) {
            SSONameValuePair nv = ssoUser.getProperties()[i];
            if (nv.getName().equals(name))
                return nv;

        }
        return null;
    }

    protected String getPropertyValue(SSOUser ssoUser, String name) {
        SSONameValuePair nv = getProperty(ssoUser, name);
        if (nv != null)
            return nv.getValue();

        return null;
    }

}
