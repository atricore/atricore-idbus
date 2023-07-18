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
import java.util.*;

/**
 * Profile mapper that will map ALL claims , one to one.
 */
public class OneToOneAttributeProfileMapper implements  OIDCAttributeProfileMapper {

    public static final Log logger = LogFactory.getLog(OneToOneAttributeProfileMapper.class);

    @Override
    public IDTokenClaimsSet toAttributes(Object rstCtx, Subject subject, List<AbstractPrincipalType> proxyPrincipals, IDTokenClaimsSet claimsSet) {

        // List of properties and its values, merged from SSOuser and proxy principals
        Map<String, Set<String>> ps = new HashMap<>();

        // List of property names already used.
        Set<String> usedProps = new HashSet<String>();
        Set<String> usedRoles = new HashSet<String>();

        // Get SSOUser claims
        Set<SSOUser> ssoUsers = subject.getPrincipals(SSOUser.class);
        if (ssoUsers != null && ssoUsers.size() > 1) {
            SSOUser user = ssoUsers.iterator().next();
            if (user.getProperties() != null) {
                for (SSONameValuePair property : user.getProperties()) {
                    Set<String> values = ps.get(property.getName());
                    if (values == null) {
                        values = new HashSet<>();
                        ps.put(property.getName(), values);
                    }
                    values.add(property.getValue());
                }
            }

            // Process properties with multiple values
            for (String name : ps.keySet()) {
                usedProps.add(name);
                Set<String> values = ps.get(name);
                if (values.size() == 1) {
                    claimsSet.setClaim(name, values.iterator().next());
                } else {
                    claimsSet.setClaim(name, values);
                }
            }

            // Groups
            Set<SSORole> ssoRoles = subject.getPrincipals(SSORole.class);

            for (SSORole ssoRole : ssoRoles) {
                usedRoles.add(ssoRole.getName());
            }
        }

        // Add proxy principals (principals received from a proxied provider), but only if we don't have such a principal yet.
        if (proxyPrincipals != null) {
            Map<String, Set<String>> pps = new HashMap<>();
            for (AbstractPrincipalType principal : proxyPrincipals) {
                if (principal instanceof SubjectAttributeType) {
                    SubjectAttributeType attr = (SubjectAttributeType) principal;
                    String name = attr.getName();
                    if (name != null) {
                        int idx = name.lastIndexOf(':');
                        if (idx >= 0) name = name.substring(idx + 1);
                    }

                    String value = attr.getValue();
                    Set<String> values = pps.get(name);
                    if (values == null) {
                        values = new HashSet<>();
                        pps.put(name, values);
                    }
                    values.add(value);
                } else if (principal instanceof SubjectRoleType) {
                    SubjectRoleType role = (SubjectRoleType) principal;
                    if (!usedRoles.contains(role.getName())) {
                        usedRoles.add(role.getName());
                    }
                }
            }

            for (String name : pps.keySet()) {
                if (!usedProps.contains(name)) {
                    Set<String> values = pps.get(name);
                    if (values.size() == 1) {
                        claimsSet.setClaim(name, values.iterator().next());
                    } else {
                        claimsSet.setClaim(name, values);
                    }
                    usedProps.add(name);
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
