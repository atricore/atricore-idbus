package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

import java.util.Map;

/**
 * Emit a refresh token that can later be used to renew an access token
 */
public class RefreshTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(RefreshTokenEmitter.class);

    private Map<String, OIDCClientInformation> clients;


    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_REFRESH_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }

    public Map<String, OIDCClientInformation> getClients() {
        return clients;
    }

    public void setClients(Map<String, OIDCClientInformation> clients) {
        this.clients = clients;
    }
}