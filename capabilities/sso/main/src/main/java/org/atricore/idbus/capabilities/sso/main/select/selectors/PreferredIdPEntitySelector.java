package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

/**
 *
 */
public class PreferredIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(PreferredIdPEntitySelector .class);

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx) {
        UserClaim idpAlias = (UserClaim) ctx.getAttribute(PREFERRED_IDP_ATTR);

        CircleOfTrustMemberDescriptor idp = null;

        if (idpAlias != null) {

            String idpAliasName = (String) idpAlias.getValue();

            // Preferred IdP alias should not be encoded ...
            if (logger.isDebugEnabled())
                logger.debug("Using IdP alias from request attribute " + idpAlias);

            idp = ctx.getCotManager().lookupMemberByAlias(idpAliasName);
            if (idp == null) {
                String decodedIdpAlias = new String(Base64.decodeBase64(idpAliasName.getBytes()));
                idp = ctx.getCotManager().lookupMemberByAlias(decodedIdpAlias);
            }

        }

        return idp;
    }
}
