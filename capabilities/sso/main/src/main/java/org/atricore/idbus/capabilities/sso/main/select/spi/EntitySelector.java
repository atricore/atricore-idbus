package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.internal.EntitySelectionState;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.List;

/**
 *
 */
public interface EntitySelector {

    int getMode();

    boolean canHandle(EntitySelectionContext ctx);

    CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx, SelectorChannel channel) throws SSOException;

    List<EndpointDescriptor> getUserClaimsEndpoints(EntitySelectionState selectionState, SelectorChannel channel);
}
