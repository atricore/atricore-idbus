package org.atricore.idbus.capabilities.sso.main.binding.producers;

import oasis.names.tc.saml._1_0.protocol.RequestType;
import oasis.names.tc.saml._1_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.ArtifactResolveType;
import oasis.names.tc.saml._2_0.protocol.ArtifactResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.binding.*;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.support.SAMLR11Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
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
 * This producer can resolve SAML 1.1 and SAML 2.0 artifacts, the artifact must resolve to a SAML message
 * using the same request version.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactResolutionProducer extends SSOProducer {

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
        if (content instanceof RequestType) {
            doProcessSaml11ArtifactResolve(exchange, (RequestType) content);
            return;

        } else if (content instanceof ArtifactResolveType) {
            doProcessSaml2ArtifactResolve(exchange, (ArtifactResolveType) content);
            return;
        }

        throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
            null,
            StatusDetails.UNKNOWN_REQUEST.getValue(),
            content.getClass().getName(),
            null);

    }

    protected void doProcessSaml2ArtifactResolve(CamelMediationExchange exchange,
                                                  ArtifactResolveType request ) throws Exception {
        if (logger.isTraceEnabled())
            logger.trace("Received ArtifactResolve request " + request.getID());

        String samlArtEnc = request.getArtifact();

        // Get encoder from configured HTTP Artifact binding
        SamlArtifactEncoder encoder = getSaml2ArtifactEncoder();
        SamlArtifact samlArt = encoder.decode(samlArtEnc);

        // Recover original SAML Msg
        MessageQueueManager aqm = getArtifactQueueManager();
        SamlMessageWrapper wrapper = (SamlMessageWrapper) aqm.pullMessage(new ArtifactImpl(samlArt.getMessageHandle()));
        if (wrapper ==null) {
            synchronized (this) {
                wait(100);
                wrapper = (SamlMessageWrapper) aqm.pullMessage(new ArtifactImpl(samlArt.getMessageHandle()));
            }
        }
        Object samlMsg = wrapper.getMsg();
        String samlType = wrapper.getType();

        if (logger.isTraceEnabled())
            logger.trace("Resolved SAML Artifact " + samlArt + " to " + samlMsg);

        // We're on a back-channel service, there's no actual destination endpoint
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(),
                endpoint.getBinding(),
                endpoint.getLocation(),
                endpoint.getResponseLocation());

        ArtifactResponseType response = buildSaml2ArtifactResponse(exchange, ed, samlMsg, samlType);

        // --------------------------------------------------------------------
        // Send Response to requester (this is SOAP or LOCAL)
        // --------------------------------------------------------------------

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                response, "Response", null, ed, null));

        exchange.setOut(out);

        return;


    }


    protected void doProcessSaml11ArtifactResolve(CamelMediationExchange exchange,
                                                  RequestType request ) throws Exception {


        if (logger.isTraceEnabled())
            logger.trace("Received ArtifactResolve request " + request.getRequestID());

        String samlArtEnc = null;
        for (String s : request.getAssertionArtifact()) {
            samlArtEnc = s;
        }

        // Get encoder from configured HTTP Artifact binding
        SamlArtifactEncoder encoder = getSaml11ArtifactEncoder();
        SamlArtifact samlArt = encoder.decode(samlArtEnc);

        // Recover original SAML Msg
        MessageQueueManager aqm = getArtifactQueueManager();
        Object samlMsg = aqm.pullMessage(new ArtifactImpl(samlArt.getMessageHandle()));

        if (logger.isTraceEnabled())
            logger.trace("Resolved SAML Artifact " + samlArt + " to " + samlMsg);

        // We're on a back-channel service, there's no actual destination endpoint
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(),
                endpoint.getBinding(),
                endpoint.getLocation(),
                endpoint.getResponseLocation());

        ResponseType response = buildSaml11ArtifactResponse(exchange, ed, samlMsg);

        // --------------------------------------------------------------------
        // Send Response to requester (this is SOAP or LOCAL)
        // --------------------------------------------------------------------

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.toString(),
                response, "Response", null, ed, null));

        exchange.setOut(out);

        return;

    }

    protected ResponseType buildSaml11ArtifactResponse(
            CamelMediationExchange exchange,
            EndpointDescriptor ed,
            java.lang.Object samlMsg) throws IdentityPlanningException, SSOException {

        // Let's do it simple for now ...
        /*
        RequestType request =
                (RequestType) ((CamelMediationMessage)exchange.getIn()).getMessage().getContent();

        ResponseType response = new ResponseType();

        // ID's
        response.setInResponseTo(request.getRequestID());
        response.setResponseID(uuidGenerator.generateId());
        response.setMajorVersion(BigInteger.valueOf(1));
        response.setMinorVersion(BigInteger.valueOf(0));

        // Issue instant


        // Content (not signed!)


        // Status
        StatusCodeType statusCode = new StatusCodeType();
        statusCode.setValue(StatusCode11.TOP_SUCCESS.getQName());

        StatusType status = new StatusType();
        status.setStatusCode(statusCode);

        response.setStatus(status);


        return response;
        */

        RequestType request =
                (RequestType) ((CamelMediationMessage)exchange.getIn()).getMessage().getContent();
        
        ResponseType response = (ResponseType) samlMsg;
        response.setInResponseTo(request.getRequestID());
        
        return response;


    }


    protected ArtifactResponseType buildSaml2ArtifactResponse(
            CamelMediationExchange exchange,
            EndpointDescriptor ed,
            java.lang.Object samlMsg,
            String samlType) throws IdentityPlanningException, SSOException {

        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        FederationChannel fChannel = (FederationChannel) channel;

        // Publish IdP Metadata

        //idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, idp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, fChannel.getMember());
        idPlanExchange.setProperty(VAR_SAMLR2_ARTIFACT, samlMsg);
        idPlanExchange.setProperty(VAR_SAMLR2_ARTIFACT_TYPE, samlType);

        // Get SPInitiated authn request, if any!
        ArtifactResolveType request =
                (ArtifactResolveType) ((CamelMediationMessage)exchange.getIn()).getMessage().getContent();

        // Create in/out artifacts
        IdentityArtifact in =
            new IdentityArtifactImpl(new QName(SAMLR11Constants.SAML_PROTOCOL_NS, "ArtifactResolve"), request );
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
            throw new SSOException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SSOException("Plan Exchange OUT must not be null!");

        return (ArtifactResponseType) idPlanExchange.getOut().getContent();
    }

    protected MessageQueueManager getArtifactQueueManager() {
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }


    protected SamlArtifactEncoder getSaml2ArtifactEncoder() {
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2HttpArtifactBinding b =
                (SamlR2HttpArtifactBinding) mediator.getBindingFactory().createBinding(SSOBinding.SAMLR2_ARTIFACT.getValue(), channel);
        return b.getArtifactEncoder();
    }

    protected SamlArtifactEncoder getSaml11ArtifactEncoder() {
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR11HttpArtifactBinding b =
                (SamlR11HttpArtifactBinding) mediator.getBindingFactory().createBinding(SSOBinding.SAMLR11_ARTIFACT.getValue(), channel);
        return b.getArtifactEncoder();
    }


}
