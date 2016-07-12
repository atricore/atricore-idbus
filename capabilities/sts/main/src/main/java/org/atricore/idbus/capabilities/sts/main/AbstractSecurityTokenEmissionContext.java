package org.atricore.idbus.capabilities.sts.main;

import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * Created by sgonzalez.
 */
public class AbstractSecurityTokenEmissionContext implements Serializable, SecurityTokenEmissionContext {

    private Subject subject;

    private String sessionIndex;

    private SSOSession ssoSession;

    private int sessionCount;

    @Override
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String getSessionIndex() {
        return sessionIndex;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    @Override
    public SSOSession getSsoSession() {
        return ssoSession;
    }

    public void setSsoSession(SSOSession ssoSession) {
        this.ssoSession = ssoSession;
    }

    @Override
    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }
}
