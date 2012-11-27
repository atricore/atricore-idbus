package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

import java.util.List;

/**
 * @author sgonzalez@atricore.org
 */
public interface EntitySelectorManager {


    List<String> resolveAttributeEndpoints(String realmName, EntitySelectionContext ctx) throws SSOException;

    CircleOfTrustMemberDescriptor selectEntity(String realmName, EntitySelectionContext ctx) throws SSOException;

}
