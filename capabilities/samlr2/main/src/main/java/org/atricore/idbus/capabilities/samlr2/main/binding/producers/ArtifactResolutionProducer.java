package org.atricore.idbus.capabilities.samlr2.main.binding.producers;

import oasis.names.tc.saml._2_0.protocol.ArtifactResolveType;
import oasis.names.tc.saml._2_0.protocol.ArtifactResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.binding.SamlArtifact;
import org.atricore.idbus.capabilities.samlr2.main.binding.SamlArtifactEncoder;
import org.atricore.idbus.capabilities.samlr2.main.binding.SamlR2HttpArtifactBinding;
import org.atricore.idbus.capabilities.samlr2.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.samlr2.main.common.AbstractSamlR2Mediator;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusCode;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusDetails;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactResolutionProducer extends SamlR2Producer {

    private static final Log logger = LogFactory.getLog( ArtifactResolutionProducer.class );

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public ArtifactResolutionProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        // Get artifact from AQM and send it in response.
        if (content instanceof ArtifactResolveType) {

            ArtifactResolveType request = (ArtifactResolveType) content;

            String samlArtEnc = request.getArtifact();

            // Get encoder from configured HTTP Artifact binding
            SamlArtifactEncoder encoder = getArtifactEncoder();
            SamlArtifact samlArt = encoder.decode(samlArtEnc);

            // Recover original SAML Msg
            MessageQueueManager aqm = getArtifactQueueManager();
            Object samlMsg = aqm.pullMessage(new ArtifactImpl(samlArt.getMessageHandle()));

            // Create resposne
            SamlR2Binding binding = SamlR2Binding.asEnum(endpoint.getBinding());
            EndpointDescriptor ed = resolveSpSloEndpoint(request.getIssuer(), new SamlR2Binding [] { binding } , true);

            ArtifactResponseType response = buildArtifactResponse(exchange, ed, samlMsg);

            // --------------------------------------------------------------------
            // Send Response to requester (this is SOAP or LOCAL)
            // --------------------------------------------------------------------

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(response.getID(),
                    response, "ArtifactResponse", null, ed, null));

            exchange.setOut(out);

            return;

        }

        throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
            null,
            StatusDetails.UNKNOWN_REQUEST.getValue(),
            content.getClass().getName(),
            null);

    }

    protected ArtifactResponseType buildArtifactResponse(
            CamelMediationExchange exchange,
            EndpointDescriptor ed,
            java.lang.Object samlMsg) throws IdentityPlanningException, SamlR2Exception {

        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        FederationChannel fChannel = (FederationChannel) channel;

        // Publish IdP Metadata

        //idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, fChannel.getMember());
        //idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, spChannel);


        // Get SPInitiated authn request, if any!
        ArtifactResolveType request =
                (ArtifactResolveType) ((CamelMediationMessage)exchange.getIn()).getMessage().getContent();

        // Create in/out artifacts
        IdentityArtifact in =
            new IdentityArtifactImpl(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "ArtifactResolve"), request );
        idPlanExchange.setIn(in);

        IdentityArtifact<ArtifactResponseType> out =
                new IdentityArtifactImpl<ArtifactResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "ArtifactResponse"),
                        new ArtifactResponseType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SamlR2Exception("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SamlR2Exception("Plan Exchange OUT must not be null!");

        return (ArtifactResponseType) idPlanExchange.getOut().getContent();
    }

    protected MessageQueueManager getArtifactQueueManager() {
        AbstractSamlR2Mediator mediator = (AbstractSamlR2Mediator) channel.getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }


    protected SamlArtifactEncoder getArtifactEncoder() {
        AbstractSamlR2Mediator mediator = (AbstractSamlR2Mediator) channel.getIdentityMediator();
        SamlR2HttpArtifactBinding b =
                (SamlR2HttpArtifactBinding) mediator.getBindingFactory().createBinding(SamlR2Binding.SAMLR2_ARTIFACT.getValue(), channel);
        return b.getArtifactEncoder();
    }

}
