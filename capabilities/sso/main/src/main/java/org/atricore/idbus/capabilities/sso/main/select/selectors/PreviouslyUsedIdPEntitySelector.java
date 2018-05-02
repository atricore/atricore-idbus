package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.Deque;

public class PreviouslyUsedIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(RequestedIdPEntitySelector.class);

    @Override
    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    @Override
    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx, SelectorChannel channel) throws SSOException {
        CircleOfTrustMemberDescriptor idp = null;


        // Try previous COT member
        // TODO : Consider that the previous selection may not be trusted/available for the current SP!
        String spName = ctx.getRequest().getIssuer();

        // The issuer must be a local SP ... we do not support remote access to this service yet!
        ServiceProvider sp = null;
        for (FederatedProvider provider : ctx.getCotManager().getCot().getProviders()) {
            if (provider.getName().equals(spName)) {
                sp = (ServiceProvider) provider;
                break;
            }
        }

        if (sp == null) {
            throw new SSOException("Entity Selector request issued by non-local SP " + spName);
        }

        Deque<String> idps = ctx.getSelectionState().getPreviousCotMembers();
        for (String idpAlias : idps) {
            // Check default channel
            FederationChannel defaultChannel = sp.getDefaultFederationService().getChannel();
            idp = lookupAliasInChannel(idpAlias, defaultChannel);
            if (idp != null) {
                logger.trace("Found previously selected IdP in default channel [" + defaultChannel.getName() + "] " + idp.getAlias());
                return idp;
            }

            // Check override channels
            for (FederationChannel overrideChannel : sp.getDefaultFederationService().getOverrideChannels()) {
                idp = lookupAliasInChannel(idpAlias, overrideChannel);
                if (idp != null) {
                    logger.trace("Found previously selected IdP in override channel [" + overrideChannel.getName() + "] " + idp.getAlias());
                    return idp;
                }
            }
        }

        logger.trace("No previously used IdP for " + sp);

        return null;
    }
}
