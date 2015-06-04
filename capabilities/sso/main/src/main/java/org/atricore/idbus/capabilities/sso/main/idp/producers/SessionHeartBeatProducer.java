package org.atricore.idbus.capabilities.sso.main.idp.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.IDPSessionHeartBeatRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.IDPSessionHeartBeatResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.session.SSOSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SessionHeartBeatProducer extends SSOProducer {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog( SessionHeartBeatProducer.class );

    public SessionHeartBeatProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess( CamelMediationExchange exchange ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        if (content instanceof IDPSessionHeartBeatRequestType) {
            doProcessSessionHeartBeat(exchange, (IDPSessionHeartBeatRequestType) content);
        } else {

            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.UNKNOWN_REQUEST.getValue(),
                    content.getClass().getName(),
                    null);
        }

    }

    protected void doProcessSessionHeartBeat(CamelMediationExchange exchange, IDPSessionHeartBeatRequestType request) throws SSOSessionException {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        // Verify issuer
        if (request.getIssuer() == null)
            throw new SSOSessionException("Issuer must be provided in IDPSessionHeartBeatRequest : " + request.getID());

        CircleOfTrustMemberDescriptor sp = getCotManager().lookupMemberByAlias(request.getIssuer());
        if (sp == null) {
            throw new SSOSessionException("Unknown SP in IDPSessionHeartBeatRequest : " + request.getID()+ ", " + request.getIssuer());
        }

        // Recover local session information
        IdPSecurityContext secCtx =
                (IdPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

        IDPSessionHeartBeatResponseType response = new IDPSessionHeartBeatResponseType();
        response.setID(uuidGenerator.generateId());
        response.setInReplayTo(request.getID());
        response.setSsoSessionId(request.getSsoSessionId());
        response.setIssuer(resolveSpChannel(sp).getMember().getAlias());

        if (secCtx == null || secCtx.getSessionIndex() == null) {

            if (logger.isDebugEnabled())
                logger.debug("No Security Context found for " + getProvider().getName().toUpperCase() + "_SECURITY_CTX: " + secCtx);
            // No SSO Session available, send response.
            response.setValid(false);

        } else {

            if (logger.isDebugEnabled())
                logger.debug("Security Context found " + secCtx);

            try {
                updateIDPSecurityContext(secCtx);
                if (logger.isTraceEnabled())
                    logger.trace("SSO Session is valid: " + secCtx.getSessionIndex());

                response.setValid(true);

            } catch (NoSuchSessionException e) {

                if (logger.isDebugEnabled())
                    logger.debug("SSO Session not found or invalid: " + secCtx.getSessionIndex());

                response.setValid(false);
            }


        }

        // Send response back
        EndpointDescriptor destination = new EndpointDescriptorImpl("IDPSessionHeartBeatService",
                "IDPSessionHeartBeatService",
                endpoint.getBinding(),
                null, null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                response, "IDPSessionHeartBeatResponse", null, destination, in.getMessage().getState()));


    }

    protected void updateIDPSecurityContext(IdPSecurityContext secCtx) throws SSOSessionException {

        if (logger.isDebugEnabled())
            logger.debug("Updating IDP Security Context for " + secCtx.getSessionIndex());

        SPChannel spChannel = (SPChannel) channel;
        SSOSessionManager ssoSessionManager = spChannel.getSessionManager();
        ssoSessionManager.accessSession(secCtx.getSessionIndex());

    }
}
