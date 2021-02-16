package org.atricore.idbus.capabilities.sso.main.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.capabilities.sts.main.policies.AccountLockedAuthnPolicy;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import javax.security.auth.Subject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SPbyGroupAccessAuthzPolicy extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(AccountLockedAuthnPolicy.class);

    public SPbyGroupAccessAuthzPolicy() {
        super("idbus-sp-by-group-authz", "SP by group");
    }

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {

        Set<PolicyEnforcementStatement> s = new HashSet<PolicyEnforcementStatement>();

        if (context instanceof SamlR2SecurityTokenEmissionContext) {
            SamlR2SecurityTokenEmissionContext ctx = (SamlR2SecurityTokenEmissionContext) context;
            String alias = ctx.getMember().getAlias();
            SPChannelConfiguration cfg = ctx.getSpChannelConfig();

            // Get user groups

            // Check groups using required/restricted groups list, considering matching mode

        }

        return s;
    }

}
