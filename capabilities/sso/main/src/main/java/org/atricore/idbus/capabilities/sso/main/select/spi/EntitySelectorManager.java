package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.internal.EntitySelectionState;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.Collection;
import java.util.List;

/**
 * @author sgonzalez@atricore.org
 */
public interface EntitySelectorManager {


    List<EndpointDescriptor> resolveUserClaimsEndpoints(EntitySelectionState selectionState, SelectorChannel channel, String realmName) throws SSOException;

    CircleOfTrustMemberDescriptor selectEntity(String realmName, EntitySelectionContext ctx, SelectorChannel channel) throws SSOException;

}
