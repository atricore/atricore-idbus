package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

/**
 *
 */
public class RequestedIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(RequestedIdPEntitySelector.class);

    @Override
    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx) throws SSOException {

        CircleOfTrustMemberDescriptor idp = null;

        // Try with requested IDP alias first
        {
            String idpAlias = ctx.getAttribute(REQUESTED_IDP_ALIAS_ATTR);
            if (idpAlias != null) {

                if (logger.isDebugEnabled())
                    logger.debug("Using IdP alias " + idpAlias);

                // Support both encoded and decoded IDP alias values
                idp = ctx.getCotManager().lookupMemberByAlias(idpAlias);
                if (idp == null) {
                    String decodedIdpAlias = new String(Base64.decodeBase64(idpAlias.getBytes()));
                    idp = ctx.getCotManager().lookupMemberByAlias(decodedIdpAlias);
                }

            }
        }

        // Now try with requested IDP ID
        {
            String idpId = ctx.getAttribute(REQUESTED_IDP_ID_ATTR);
            if (idpId != null && idp == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Using IdP ID " + idpId);

                idp = ctx.getCotManager().lookupMemberById(idpId);
                if (idp == null) {
                    String decodedIdpId = new String(Base64.decodeBase64(idpId.getBytes()));
                    idp = ctx.getCotManager().lookupMemberById(decodedIdpId);
                }
            }
        }

        return idp;


    }


}
