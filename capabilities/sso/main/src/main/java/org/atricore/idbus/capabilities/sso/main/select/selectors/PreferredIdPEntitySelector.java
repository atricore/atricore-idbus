package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

/**
 *
 */
public class PreferredIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(PreferredIdPEntitySelector .class);

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx) {
        String idpAlias = ctx.getRequestAttribute(PREFERRED_IDP_ATTR);

        CircleOfTrustMemberDescriptor idp = null;

        if (idpAlias != null) {
            // Preferred IdP alias should not be encoded ...
            if (logger.isDebugEnabled())
                logger.debug("Using IdP alias from request attribute " + idpAlias);

            idp = ctx.getCotManager().lookupMemberByAlias(idpAlias);
            if (idp == null) {
                String decodedIdpAlias = new String(Base64.decodeBase64(idpAlias.getBytes()));
                idp = ctx.getCotManager().lookupMemberByAlias(decodedIdpAlias);
            }

        }

        return idp;
    }
}
