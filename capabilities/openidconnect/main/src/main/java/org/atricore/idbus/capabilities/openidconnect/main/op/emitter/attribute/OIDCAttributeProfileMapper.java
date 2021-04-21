package org.atricore.idbus.capabilities.openidconnect.main.op.emitter.attribute;

import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;

import javax.security.auth.Subject;
import java.util.List;

public interface OIDCAttributeProfileMapper {

    IDTokenClaimsSet toAttributes(Object rstCtx,
                                  Subject ssoSubject,
                                  List<AbstractPrincipalType> proxyPrincipals,
                                  IDTokenClaimsSet claimsSet);

}
