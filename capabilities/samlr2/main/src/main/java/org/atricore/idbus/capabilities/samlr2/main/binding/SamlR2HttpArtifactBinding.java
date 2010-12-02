package org.atricore.idbus.capabilities.samlr2.main.binding;

import oasis.names.tc.saml._2_0.metadata.*;
import oasis.names.tc.saml._2_0.protocol.ArtifactResolveType;
import oasis.names.tc.saml._2_0.protocol.ArtifactResponseType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.binding.plans.SamlR2ArtifactToSamlR2ArtifactResolvePlan;
import org.atricore.idbus.capabilities.samlr2.main.common.AbstractSamlR2Mediator;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.planning.*;
import static org.atricore.idbus.capabilities.samlr2.main.common.plans.SamlR2PlanningConstants.*;

import javax.xml.namespace.QName;
import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2HttpArtifactBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpArtifactBinding.class);

    private String artifactParameterName = "SAMLArt";

    private SamlArtifactEncoder artifactEncoder;

    public SamlR2HttpArtifactBinding(Channel channel) {
        super(SamlR2Binding.SAMLR2_ARTIFACT.getValue(), channel);
        artifactEncoder = new SamlR2ArtifactEncoderImpl();
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();
        if (httpMsg.getHeader("http.requestMethod") == null) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        try {
            // HTTP Request Parameters from HTTP Request body
            MediationState state = createMediationState(exchange);

            // HTTP-Artifact SamlR2Binding supports the following parameters
            String samlArtStr = state.getTransientVariable(artifactParameterName);
            String relayState = state.getTransientVariable("RelayState");

            if (samlArtStr == null || "".equals(samlArtStr))
                throw new IllegalArgumentException("'"+artifactParameterName+"' parameter not found!");

            // TODO : Use planning instead of builder :)
            SamlArtifact samlArtifact = getEncoder().decode(samlArtStr);

            // Access issuer Reolver endpoint to get value!
            String sourceId = samlArtifact.getSourceID();
            CircleOfTrustMemberDescriptor resolverMemberDescr = this.getProvider().getCotManager().loolkupMemberById(sourceId);
            if (resolverMemberDescr == null) {
                /* Unknown SOURCE ID! */
                logger.warn("Unkonw SAML Artifact SourceID ["+sourceId+"]");
                throw new RuntimeException("Unkonw SAML Artifact SourceID ["+sourceId+"]");
            }

            FederationChannel fChannel= (FederationChannel) channel;

            CircleOfTrustMemberDescriptor memberDescr = fChannel.getMember();

            // Find SAML 2.0 Metadata
            MetadataEntry md = resolverMemberDescr.getMetadata();
            EntityDescriptorType samlMd = (EntityDescriptorType) md.getEntry();

            // Find ArtifactResolutionService endpoint
            EndpointDescriptor samlResolverEd = resolveEntityResolverEndpoint(samlMd,
                    samlArtifact.getEndpointIndex());

            ArtifactResolveType req = buildArtifactResolveType(
                    memberDescr,
                    resolverMemberDescr,
                    samlArtifact,
                    samlResolverEd,
                    fChannel);

            // Resolve Artifact using binding to send SOAP message
            ArtifactResponseType res = (ArtifactResponseType) this.channel.getIdentityMediator().sendMessage(req, samlResolverEd, channel);
            java.lang.Object msgValue = res.getAny();
            if (logger.isTraceEnabled())
                logger.trace("Received SAML Message : " + msgValue);

            // See if we're dealing with a request or a response
            if (msgValue instanceof RequestAbstractType) {

                RequestAbstractType samlRequest = (RequestAbstractType) msgValue;
                logger.debug("Received SAML Request " + samlRequest.getID());
                return new MediationMessageImpl<RequestAbstractType>(httpMsg.getMessageId(),
                        samlRequest,
                        XmlUtils.marshallSamlR2Request(samlRequest, false), // TODO : Comment if too slow!
                        null,
                        relayState,
                        null,
                        state);

            } else if (msgValue instanceof StatusResponseType) {
                StatusResponseType samlResponse = (StatusResponseType) msgValue;
                logger.debug("Received SAML Response " + samlResponse.getID());
                return new MediationMessageImpl<StatusResponseType>(httpMsg.getMessageId(),
                        samlResponse,
                        XmlUtils.marshallSamlR2Response(samlResponse, false), // TODO : Comment if too slow!
                        null,
                        relayState,
                        null,
                        state);

            } else {
                throw new RuntimeException("Unknown SAML 2.0 Type : " + msgValue);
            }


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {
        try {
            MediationMessage out = samlOut.getMessage();
            EndpointDescriptor ed = out.getDestination();

            // ------------------------------------------------------------
            // Validate received message
            // ------------------------------------------------------------
            assert ed != null : "Mediation Response MUST Provide a destination";
            if (out.getContent() == null) {
                throw new NullPointerException("Cannot Create form with null content for action " + ed.getLocation());
            }

            // ------------------------------------------------------------
            // Create HTML Form for response body
            // ------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Artifact to " + ed.getLocation());



            String msgName = null;
            java.lang.Object msgValue = out.getContent();
            String element = out.getContentType();
            boolean isResponse = false;

            if (out.getContent() instanceof RequestAbstractType) {
                msgName = "SAMLRequest";
            } else if (out.getContent() instanceof StatusResponseType) {
                msgName = "SAMLResponse";
                isResponse = true;
            }

            if (msgValue == null) {
                throw new NullPointerException("Cannot send null content to " + ed.getLocation());
            }

            MessageQueueManager aqm = getArtifactQueueManager();
            Artifact artifact = aqm.pushMessage(msgValue);

            FederationChannel fChannel = (FederationChannel) channel;
            CircleOfTrustMemberDescriptor cotMember = fChannel.getMember();

            SamlArtifact samlArtifact = new SamlArtifact(4,
                    0,
                    cotMember.getId(),
                    artifact.getContent());

            if (logger.isTraceEnabled())
                logger.trace("Created SAML Artifact " + samlArtifact);

            String samlArtifactEnc = getEncoder().encode(samlArtifact);

            String qryString = "?" + artifactParameterName + "=" + samlArtifactEnc;
            if (out.getRelayState() != null) {
                qryString += "&relayState=" + out.getRelayState();
            }

            Message httpOut = exchange.getOut();
            Message httpIn = exchange.getIn();
            String redirLocation = this.buildHttpTargetLocation(httpIn, ed, isResponse) + qryString;

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 302);
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", redirLocation);



        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected MessageQueueManager getArtifactQueueManager() {
        AbstractCamelMediator mediator = (AbstractCamelMediator) getChannel().getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }

    public String getArtifactParameterName() {
        return artifactParameterName;
    }

    public void setArtifactParameterName(String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }

    public SamlArtifactEncoder getEncoder() {
        return getArtifactEncoder();
    }

    public SamlArtifactEncoder getArtifactEncoder() {
        return artifactEncoder;
    }

    public void setArtifactEncoder(SamlArtifactEncoder artifactEncoder) {
        this.artifactEncoder = artifactEncoder;
    }

    protected EndpointDescriptor resolveEntityResolverEndpoint(EntityDescriptorType samlMd,
                                                               int edIdx) throws CircleOfTrustManagerException {

        // We need to find out if the entity is external or not !
        boolean preferLocalBindings = this.getProvider().getCotManager().isLocalMember(samlMd.getEntityID());

        EndpointType samlEndpoint = null;
        for (RoleDescriptorType roleDescriptor : samlMd.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

            if (roleDescriptor instanceof SSODescriptorType) {
                SSODescriptorType ssoDescriptor = (SSODescriptorType) roleDescriptor;
                for (IndexedEndpointType samlIdxEndpoint : ssoDescriptor.getArtifactResolutionService()) {

                    if (edIdx > 0) {

                        if (edIdx == samlIdxEndpoint.getIndex()) {
                            samlEndpoint = samlIdxEndpoint;
                            break;
                        }

                    } else {

                        if (preferLocalBindings &&
                            samlIdxEndpoint.getBinding().equals(SamlR2Binding.SAMLR2_LOCAL.getValue())) {
                            samlEndpoint = samlIdxEndpoint;
                            break;
                        }

                        if (samlIdxEndpoint.getBinding().equals(SamlR2Binding.SAMLR2_SOAP.getValue())) {
                            if (samlEndpoint == null)
                                samlEndpoint = samlIdxEndpoint;
                        }
                    }
                }
            }
            if (samlEndpoint != null)
                break;
        }

        if (samlEndpoint == null)
            throw new RuntimeException("Cannot resovle SAML 2.0 ArtifactResolutionService for entity " +
                    samlMd.getEntityID());

        return new EndpointDescriptorImpl("ArtifactResolutionService",
                "ArtifactResolveType",
                samlEndpoint.getBinding(),
                samlEndpoint.getLocation(),
                samlEndpoint.getResponseLocation());

    }


    protected ArtifactResolveType buildArtifactResolveType(CircleOfTrustMemberDescriptor member,
                                                 CircleOfTrustMemberDescriptor destMember,
                                                 SamlArtifact samlArt,
                                                 EndpointDescriptor ed,
                                                 FederationChannel channel
    ) throws IdentityPlanningException, SamlR2Exception {

        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2ArtifactToSamlR2ArtifactResolvePlan.class);
        IdentityPlanExecutionExchange idPlanExchange = new IdentityPlanExecutionExchangeImpl();

        // TODO !
        IdentityMediationEndpoint idEndpoint = null;

        // Publish some important attributes:
        // Circle of trust will allow actions to access identity configuration

        idPlanExchange.setProperty(VAR_COT, this.getProvider().getCotManager().getCot());
        idPlanExchange.setProperty(VAR_COT_MEMBER, member);
        idPlanExchange.setProperty(VAR_CHANNEL, this.channel);
        idPlanExchange.setProperty(VAR_ENDPOINT, idEndpoint);

        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, destMember);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, ed);
        idPlanExchange.setProperty(VAR_COT_MEMBER, channel.getMember());
        idPlanExchange.setProperty(VAR_RESPONSE_CHANNEL, channel);

        // Create in/out artifacts

        // saml artifact
        IdentityArtifact in =
            new IdentityArtifactImpl(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "SAMLArt"),
                    samlArt );
        idPlanExchange.setIn(in);

        // ArtifactResolve
        IdentityArtifact<ArtifactResolveType> out =
                new IdentityArtifactImpl<ArtifactResolveType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "ArtifactResolve"),
                        new ArtifactResolveType());
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

        return (ArtifactResolveType) idPlanExchange.getOut().getContent();

    }

    protected IdentityPlan findIdentityPlanOfType(Class planClass) throws SamlR2Exception {

        for (IdentityMediationEndpoint e : channel.getEndpoints()) {
            for (IdentityPlan p : e.getIdentityPlans()) {
                if (planClass.isInstance(p))
                    return p;
            }
        }

        logger.warn("No identity plan of class " + planClass.getName() + " was found for binding " +
                SamlR2HttpArtifactBinding.class.getSimpleName());

        return null;

    }


}
