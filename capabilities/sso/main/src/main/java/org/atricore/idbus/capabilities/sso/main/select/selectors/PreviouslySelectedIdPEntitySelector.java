package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

/**
 * Created by sgonzalez on 12/23/14.
 */
public class PreviouslySelectedIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(RequestedIdPEntitySelector.class);

    @Override
    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    @Override
    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx, SelectorChannel channel) throws SSOException {

        CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor)
                ctx.getMediationState().getLocalVariable("urn:org:atricore:idbus:capabilities:sso:select:usr:cotMember");

        // TODO : Consider that the previous selection may not be trusted/available for the current SP!

        if (idp != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using previously selected COT member : " + idp);
                return idp;
            }
        }

        // Try previous COT member
        {
            String idpAliasValue = ctx.getSelectionState().getPreviousCotMember();
            if (idpAliasValue != null) {

                if (logger.isDebugEnabled())
                    logger.debug("Using IdP alias " + idpAliasValue);

                // Support both encoded and decoded IDP alias values
                idp = ctx.getCotManager().lookupMemberByAlias(idpAliasValue);
                if (idp == null) {
                    String decodedIdpAlias = new String(Base64.decodeBase64(idpAliasValue.getBytes()));
                    idp = ctx.getCotManager().lookupMemberByAlias(decodedIdpAlias);
                }

            }

        }

        return idp;
    }
}
