package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.Skinnable;

import java.util.List;
import java.util.Set;

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
     * Returns all the COT members associated with this provider's default servcie.
     * @return
     */
    List<CircleOfTrustMemberDescriptor> getMembers();

    /**
     * Returns all the COT members associated with a provider's given service
     * @return
     */
    List<CircleOfTrustMemberDescriptor> getMembers(String svcType);


    /**
     * Returns all the COT members associated with this provider (for any service)
     * @return
     */
    List<CircleOfTrustMemberDescriptor> getAllMembers();


    /**
     * Set of Federation services available in this provider.
     * @return
     */
    Set<FederationService> getFederationServices();

    /**
     * Default federation service
     */
    FederationService getDefaultFederationService();





}
