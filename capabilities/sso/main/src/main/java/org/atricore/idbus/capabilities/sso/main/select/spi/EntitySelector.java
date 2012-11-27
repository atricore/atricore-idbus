package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

/**
 *
 */
public interface EntitySelector {

    int getMode();

    boolean canHandle(EntitySelectionContext ctx);

    CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx) throws SSOException;

    String getSelectorAttributesEndpoint();
}
