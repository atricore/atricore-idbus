package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SsoLocalBinding extends AbstractMediationBinding {

    private static final Log logger = LogFactory.getLog(SsoLocalBinding.class);

    public SsoLocalBinding(Channel channel) {
        super(SSOBinding.SSO_LOCAL.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        CamelMediationExchange samlR2exchange = message.getExchange();
        Exchange exchange = samlR2exchange.getExchange();

        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        // Converting from Local Message to SAMLR2 Message
        // Is this a Loca message?
        Message in = exchange.getIn();

        if (in.getBody() instanceof SSORequestAbstractType) {

            MediationState state = null;
            LocalState lState = null;
            MediationMessage body;

            SSORequestAbstractType ssoRequestAbstracType = (SSORequestAbstractType) in.getBody();

            try {

                Method getSsoSessionId = ssoRequestAbstracType.getClass().getMethod("getSsoSessionId");
                String ssoSessionId = (String) getSsoSessionId.invoke(ssoRequestAbstracType);

                ProviderStateContext ctx = createProviderStateContext();
                lState = ctx.retrieve("ssoSessionId", ssoSessionId);

                if (logger.isDebugEnabled())
                    logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for ssoSessionId " + ssoSessionId);

            } catch (NoSuchMethodException e) {
                // Ignore this ...
                if (logger.isTraceEnabled())
                    logger.trace("SSO Request does not have session index : " + e.getMessage());

            } catch (InvocationTargetException e) {
                logger.error("Cannot recover local state : " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error("Cannot recover local state : " + e.getMessage(), e);
            }

            if (lState == null) {
                try {

                    Method getRelayStateReference = ssoRequestAbstracType.getClass().getMethod("getRelayStateReference");
                    String relayStateReference = (String) getRelayStateReference.invoke(ssoRequestAbstracType);

                    ProviderStateContext ctx = createProviderStateContext();
                    lState = ctx.retrieve(relayStateReference);

                    if (logger.isDebugEnabled())
                        logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for relayStateReference " + relayStateReference);

                } catch (NoSuchMethodException e) {
                    // Ignore this ...
                    if (logger.isTraceEnabled())
                        logger.trace("SSO Request does not have session index : " + e.getMessage());

                } catch (InvocationTargetException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                }

            }

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
            } else {
                state = new MediationStateImpl(lState);

            }

            // Process Saml Response in SOAP Channel
            body = new MediationMessageImpl(
                    in.getMessageId(),
                    in.getBody(),
                    null,
                    null,
                    null,
                    state);

            return body;
        } else if (in.getBody() instanceof SSOCredentialClaimsRequest) {
            MediationState state = null;
            LocalState lState = null;
            MediationMessage body;

            SSOCredentialClaimsRequest samlr2ClaimRequest = (SSOCredentialClaimsRequest) in.getBody();

            ProviderStateContext ctx = createProviderStateContext();
            lState = ctx.retrieve(samlr2ClaimRequest.getTargetRelayState());

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
            } else {
                state = new MediationStateImpl(lState);

            }

            // Process Saml Response in SOAP Channel
            body = new MediationMessageImpl(
                    in.getMessageId(),
                    in.getBody(),
                    null,
                    null,
                    null,
                    state);

            return body;

        }else {
            throw new IllegalArgumentException("Unknown message type " + in.getBody());
        }


    }

    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {

        if (logger.isDebugEnabled())
            logger.debug("Copying LOCAL Message");

        MediationMessage outMsg = message.getMessage();
        copyBackState(outMsg.getState(), exchange);
        exchange.getOut().setBody(outMsg.getContent());

    }

    public void copyFaultMessageToExchange(CamelMediationMessage faultMessage, Exchange exchange) {
        if (logger.isTraceEnabled())
            logger.trace("Copy Fault to Exchange for Local binding!");
        logger.warn("'copyFaultMessageToExchange' Not implemented , dumping error to log file");

        MediationMessage m = faultMessage.getMessage();
        if (m.getFault() != null) {
            logger.error(m.getFault().getMessage(), m.getFault());
        }

    }

    @Override
    public Object sendMessage(MediationMessage message) {

        if (logger.isTraceEnabled())
            logger.trace("Sending new SSO message using Local Binding");

        IdentityMediationUnitContainer uc = channel.getUnitContainer();

        if (uc instanceof CamelIdentityMediationUnitContainer) {

            EndpointDescriptor ed = message.getDestination();

            ProducerTemplate t = ((CamelIdentityMediationUnitContainer)uc).getTemplate();
            String camelEndpoint = "direct:" + ed.getLocation();

            if (logger.isTraceEnabled())
                logger.trace("Sending message content to [" + camelEndpoint+"]");

            Object o = t.sendBody(camelEndpoint, message.getContent());

            if (logger.isTraceEnabled())
                logger.trace("Received from ["+camelEndpoint+"] " + o);

            return o;

        } else {
            throw new UnsupportedOperationException("Unint container type unknown " + uc);
        }
    }
}
