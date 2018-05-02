package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.select.internal.EntitySelectionState;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.List;

/**
 *
 */
public abstract class AbstractEntitySelector implements EntitySelector, EntitySelectorConstants {

    private List<EndpointDescriptor> userClaimsEndpoints;

    public int getMode() {
        return 0;
    }

    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    public List<EndpointDescriptor> getUserClaimsEndpoints(EntitySelectionContext ctx, SelectorChannel channel) {
        return userClaimsEndpoints;
    }

    public void setUserClaimsEndpoints(List<EndpointDescriptor> userClaimsEndpoints) {
        this.userClaimsEndpoints = userClaimsEndpoints;
    }

    protected CircleOfTrustMemberDescriptor lookupAliasInChannel(String idpAlias, FederationChannel channel) {
        for (FederatedProvider trusted : channel.getTrustedProviders()) {
            for (CircleOfTrustMemberDescriptor member : trusted.getMembers()) {
                if (member.getAlias().equals(idpAlias)) {
                    return member;
                }
            }
        }

        return null;
    }



}
