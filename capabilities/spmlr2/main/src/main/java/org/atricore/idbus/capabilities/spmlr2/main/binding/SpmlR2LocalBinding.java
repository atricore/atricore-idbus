package org.atricore.idbus.capabilities.spmlr2.main.binding;

import oasis.names.tc.spml._2._0.RequestType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2LocalBinding extends AbstractMediationBinding {

    private static final Log logger = LogFactory.getLog(SpmlR2LocalBinding.class);

    public SpmlR2LocalBinding(Channel channel) {
        super(SpmlR2Binding.SPMLR2_LOCAL.getValue(), channel);
    }


    public MediationMessage createMessage(CamelMediationMessage message) {
        CamelMediationExchange spmlR2exchange = message.getExchange();
        Exchange exchange = spmlR2exchange.getExchange();

        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        // Converting from Local Message to SpmlR2 Message
        // Is this a Loca message?
        Message in = exchange.getIn();

        if (in.getBody() instanceof RequestType) {

            MediationState state = null;

            // TODO : Support state based on protocol features.
            LocalState lState = null;
            MediationMessage body;

/*
            RequestType spmlReq = (RequestType) in.getBody();

            try {

                Method getSessionIndex = spmlReq.getClass().getMethod("getSessionIndex");
                List<String> sessionIndexes = (List<String>) getSessionIndex.invoke(spmlReq);

                if (sessionIndexes != null) {
                    if (sessionIndexes.size() > 0) {
                        String sessionIndex = sessionIndexes.get(0);

                        ProviderStateContext ctx = createProviderStateContext();
                        lState = ctx.retrieve("idpSsoSessionId", sessionIndex);

                        if (logger.isDebugEnabled())
                            logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for ssoSessionId " + sessionIndex);
                    }
                }

            } catch (NoSuchMethodException e) {
                // Ignore this ...
                if (logger.isTraceEnabled())
                    logger.trace("SSO Request does not have session index : " + e.getMessage(), e);

            } catch (InvocationTargetException e) {
                logger.error("Cannot recover local state : " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error("Cannot recover local state : " + e.getMessage(), e);
            }

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
            } else {
                state = new MediationStateImpl(lState);

            }

            */

            // Process Saml Response in SOAP Channel
            body = new MediationMessageImpl(
                    in.getMessageId(),
                    in.getBody(),
                    null,
                    null,
                    null,
                    state);

            return body;

        } else {
            throw new IllegalArgumentException("Unknown message type " + in.getBody());
        }    }

    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        if (logger.isDebugEnabled())
            logger.debug("Copying SAML 2.0 LOCAL Message");

        MediationMessage outMsg = message.getMessage();
        copyBackState(outMsg.getState(), exchange);
        exchange.getOut().setBody(outMsg.getContent());
    }

    public void copyFaultMessageToExchange(CamelMediationMessage faultMessage, Exchange exchange) {
        if (logger.isTraceEnabled())
            logger.trace("Copy Fault to Exchange for Local binding!");

        // TODO : Implement me!
    }

    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {
        if (logger.isTraceEnabled())
            logger.trace("Sending new SAML 2.0 message using SSO Local Binding");

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

