package org.atricore.idbus.capabilities.sts.main;

import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;

/**
 * Created by sgonzalez.
 */
public interface SecurityTokenEmissionContext {

    Subject getSubject();

    SSOSession getSsoSession();

    String getSessionIndex();

    int getSessionCount();


}
