package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.Skinnable;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface FederatedProvider extends Provider, Skinnable {

    /**
     * Returns the COT definition
     * @return
     */
    CircleOfTrust getCircleOfTrust();

    /**
     * Returns all the COT members associated with this provider.
     * @return
     */
    List<CircleOfTrustMemberDescriptor> getMembers();

}
