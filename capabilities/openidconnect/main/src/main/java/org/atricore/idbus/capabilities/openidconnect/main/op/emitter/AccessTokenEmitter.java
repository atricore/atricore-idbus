package org.atricore.idbus.capabilities.openidconnect.main.op.emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmitter;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenProcessingContext;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.planning.IdentityArtifact;

/**
 * Emit an access Token
 */
public class AccessTokenEmitter extends AbstractSecurityTokenEmitter {

    private static final Log logger = LogFactory.getLog(AccessTokenEmitter.class);

    @Override
    public boolean isTargetedEmitter(SecurityTokenProcessingContext context, Object requestToken, String tokenType) {
        return context.getProperty(WSTConstants.SUBJECT_PROP) != null &&
                WSTConstants.WST_OIDC_ACCESS_TOKEN_TYPE.equals(tokenType);
    }

    @Override
    protected IdentityArtifact createOutArtifact(Object requestToken, String tokenType) {
        return null;
    }
}
