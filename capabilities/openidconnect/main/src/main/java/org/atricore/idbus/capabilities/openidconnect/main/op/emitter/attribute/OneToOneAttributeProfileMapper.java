package org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.emitter.IDTokenEmitter;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectRoleType;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Profile mapper that will map ALL claims , one to one.
 */
public class OneToOneAttributeProfileMapper implements  OIDCAttributeProfileMapper {

    public static final Log logger = LogFactory.getLog(OneToOneAttributeProfileMapper.class);

    @Override
    public IDTokenClaimsSet toAttributes(Object rstCtx, Subject subject, List<AbstractPrincipalType> proxyPrincipals, IDTokenClaimsSet claimsSet) {

        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() < 1) {
            logger.error("Can't build ID Token for SimplePrincipal.  Try attaching an ID vault to your IDP/VP");
            return null;
        }
        SSOUser user = ssoUsers.iterator().next();

        // Additional claims
        Set<String> usedProps = new HashSet<String>();
        if (user.getProperties() != null) {
            for (SSONameValuePair property : user.getProperties()) {
                usedProps.add(property.getName());
                claimsSet.setClaim(property.getName(), property.getValue());
            }
        }

        // Groups
        Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);
        Set<String> usedRoles = new HashSet<String>();

        for (SSORole ssoRole : ssoRoles) {
            usedRoles.add(ssoRole.getName());
        }

        // Add proxy principals (principals received from a proxied provider), but only if we don't have such a principal yet.
        if (proxyPrincipals != null) {
            for (AbstractPrincipalType principal : proxyPrincipals) {
                if (principal instanceof SubjectAttributeType) {
                    SubjectAttributeType attr = (SubjectAttributeType) principal;
                    String name = attr.getName();
                    if (name != null) {
                        int idx = name.lastIndexOf(':');
                        if (idx >= 0) name = name.substring(idx + 1);
                    }

                    String value = attr.getValue();
                    if (!usedProps.contains(name)) {
                        claimsSet.setClaim(name, value);
                        usedProps.add(name);
                    }
                } else if (principal instanceof SubjectRoleType) {
                    SubjectRoleType role = (SubjectRoleType) principal;
                    if (!usedRoles.contains(role.getName())) {
                        usedRoles.add(role.getName());
                    }
                }
            }
        }

        JWTClaimsSet previousClaims = IDTokenEmitter.getPreviousIdTokenClaims(rstCtx);
        if (usedRoles.size() < 1) {
            if (previousClaims != null &&  previousClaims.getClaim("groups") != null)
                claimsSet.setClaim("groups", previousClaims.getClaim("groups"));
        } else {
            // Create role claim with used roles
            claimsSet.setClaim("groups", usedRoles);
        }

        return claimsSet;
    }
}
