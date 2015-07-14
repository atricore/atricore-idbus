package org.atricore.idbus.capabilities.sso.main.common.producers;

import oasis.names.tc.saml._2_0.idbus.MetadataRequestType;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MetadataProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog(MetadataProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public MetadataProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MetadataRequestType mdRequest = (MetadataRequestType) in.getMessage().getContent();

        // Locate current channel's metadata file:

        FederationChannel fChannel = (FederationChannel) channel;

        // This MUST be a SAML 2 federation channel, therefore the member contains the MD information
        CircleOfTrustMemberDescriptor member = fChannel.getMember();

        CircleOfTrustManager mgr = fChannel.getFederatedProvider().getCotManager();

        MetadataEntry mdEntry = mgr.findEntityMetadata(member.getAlias());

        // Remove non-normative bindings, just in case:

        EntityDescriptorType samlMd = (EntityDescriptorType) mdEntry.getEntry();

        for (RoleDescriptorType samlRole : samlMd.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
            if (samlRole instanceof IDPSSODescriptorType) {
                prepareIdPDescriptor(mdEntry, (IDPSSODescriptorType) samlRole);
            } else if (samlRole instanceof SPSSODescriptorType) {
                prepareSPDescriptor(mdEntry, (SPSSODescriptorType) samlRole);
            }
        }

        // TODO : Sign MD ?!

        String responseId = uuidGenerator.generateId();

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(responseId,
                mdEntry, "MetadataResposne", null, null, in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected void prepareIdPDescriptor(MetadataEntry mdEntry, IDPSSODescriptorType mdIdP) {

        // Remove services using non-normative protocols (i.e. local:)
        // We assume that we're altering the actual list!
        removeNonNormativeEndpoints(mdIdP.getAssertionIDRequestService());
        removeNonNormativeEndpoints(mdIdP.getSingleSignOnService());
        removeNonNormativeEndpoints(mdIdP.getNameIDMappingService());
        removeNonNormativeEndpoints(mdIdP.getManageNameIDService());
        removeNonNormativeEndpoints(mdIdP.getSingleLogoutService());
        removeNonNormativeIndexedEndpoints(mdIdP.getArtifactResolutionService());

    }

    protected void prepareSPDescriptor(MetadataEntry mdEntry, SPSSODescriptorType mdSP) {
        // Remove services using non-normative protocols (i.e. local:)
        // We assume that we're altering the actual list!
        removeNonNormativeEndpoints(mdSP.getManageNameIDService());
        removeNonNormativeEndpoints(mdSP.getSingleLogoutService());
        removeNonNormativeIndexedEndpoints(mdSP.getAssertionConsumerService());
        removeNonNormativeIndexedEndpoints(mdSP.getArtifactResolutionService());
    }

    protected void removeNonNormativeIndexedEndpoints(List<IndexedEndpointType> endpoints) {
        List<IndexedEndpointType> validEdpoints = new ArrayList<IndexedEndpointType>();

        for (IndexedEndpointType endpoint : endpoints) {
            if (endpoint.getBinding() == null) {
                continue;
            }

            SSOBinding b = SSOBinding.asEnum(endpoint.getBinding());
            if (!b.isNormative())
                continue;

            validEdpoints.add(endpoint);
        }

        endpoints.clear();
        endpoints.addAll(validEdpoints);
    }

    protected void removeNonNormativeEndpoints(List<EndpointType> endpoints) {
        List<EndpointType> validEdpoints = new ArrayList<EndpointType>();

        for (EndpointType endpoint : endpoints) {
            if (endpoint.getBinding() == null) {
                continue;
            }

            SSOBinding b = SSOBinding.asEnum(endpoint.getBinding());
            if (!b.isNormative())
                continue;

            validEdpoints.add(endpoint);
        }

        endpoints.clear();
        endpoints.addAll(validEdpoints);
    }
}
