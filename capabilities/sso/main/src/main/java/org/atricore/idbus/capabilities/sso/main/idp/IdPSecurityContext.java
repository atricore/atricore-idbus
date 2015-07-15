package org.atricore.idbus.capabilities.sso.main.idp;

import oasis.names.tc.saml._2_0.assertion.AuthnStatementType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * // TODO : Move to kernel mediation
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdPSecurityContext implements java.io.Serializable {

    private Subject subject;

    private String sessionIndex;

    private boolean sloInProgress;

    private AuthnStatementType authnStatement;

    private Set<ProviderSecurityContext> registry = new HashSet<ProviderSecurityContext>();

    private List<AbstractPrincipalType> proxyPrincipals;

    public IdPSecurityContext(Subject subject, String sessionIndex, AuthnStatementType authnStatement) {
        this.subject = subject;
        this.sessionIndex = sessionIndex;
        this.authnStatement = authnStatement;
        this.sloInProgress = false;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getSessionIndex() {
        return sessionIndex;
    }

    public AuthnStatementType getAuthnStatement() {
        return authnStatement;
    }

    public void register(ProviderSecurityContext pSecCtx) {
        registry.add(pSecCtx);
    }

    public void register(NameIDType id, String relayState) {

        register(new ProviderSecurityContext(id, relayState));
    }

    public Collection<ProviderSecurityContext> lookupProviders() {
        return registry;
    }

    public ProviderSecurityContext lookupProvider(NameIDType providerId) {

        for (ProviderSecurityContext p : registry) {
            if (providerId.equals(p.getProviderId()))
                return p;
        }

        return null;
    }

    public void clear() {
        this.sessionIndex = null;
        this.subject = null;
        this.authnStatement = null;
        this.registry.clear();
        this.sloInProgress = false;
    }


    public void setProxyPrincipals(List<AbstractPrincipalType> proxyPrinciapsl) {
        this.proxyPrincipals = proxyPrinciapsl;
    }

    public List<AbstractPrincipalType> getProxyPrincipals() {
        return proxyPrincipals;
    }

    public boolean isSloInProgress() {
        return sloInProgress;
    }

    public void setSloInProgress(boolean sloInProgress) {
        this.sloInProgress = sloInProgress;
    }
}
