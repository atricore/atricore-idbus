package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.GoogleAuthzTokenConsumerProducer;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;

import java.text.ParseException;
import java.util.Collection;

public abstract class OpenIdSubjectMapper {

    private static final Log logger = LogFactory.getLog(OpenIdSubjectMapper.class);

    protected FederatedProvider provider;
    protected OIDCTokens tokens;
    protected JWT idToken;
    protected JWTClaimsSet claims;

    public OpenIdSubjectMapper(FederatedProvider provider, OIDCTokens tokens) {
        try {
            this.provider = provider;
            this.tokens = tokens;
            this.idToken = tokens.getIDToken();
            this.claims = idToken.getJWTClaimsSet();
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public abstract SubjectType toSubject();

    public abstract Collection<? extends SubjectAttributeType> getAttributes();
}
