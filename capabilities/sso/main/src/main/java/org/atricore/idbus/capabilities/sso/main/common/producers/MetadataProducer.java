package org.atricore.idbus.capabilities.sso.main.common.producers;

import oasis.names.tc.saml._2_0.idbus.MetadataRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

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

        String responseId = uuidGenerator.generateId();

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(responseId,
                mdEntry, "MetadataResposne", null, null, in.getMessage().getState()));

        exchange.setOut(out);

    }
}
