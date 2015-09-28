package org.atricore.idbus.capabilities.sso.main.idp;

import oasis.names.tc.saml._2_0.assertion.AuthnStatementType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.authn.SSOUser;

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

    private static final Log logger = LogFactory.getLog(IdPSecurityContext.class);

    private Subject subject;

    private String sessionIndex;

    private String idpProxySessionIndex;

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
        if (logger.isDebugEnabled())
            logger.debug("Register Provider Security Context for " + pSecCtx.getProviderId());
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

        logger.debug("Clear Security Context w/session " + sessionIndex);

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

    public String getIdpProxySessionIndex() {
        return idpProxySessionIndex;
    }

    public void setIdpProxySessionIndex(String idpProxySessionIndex) {
        this.idpProxySessionIndex = idpProxySessionIndex;
    }

    @Override
    public String toString() {
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        SSOUser ssoUser = ssoUsers.size() > 0 ? ssoUsers.iterator().next() : null;
        String ssoUserName = ssoUser != null ? ssoUser.getName() : "N/A";

        return "sessionIndex=["+sessionIndex+"] ssouser["+ssoUserName+"] authnStatement[" + (authnStatement != null ? authnStatement : "N/A") + "]" ;
    }
}
