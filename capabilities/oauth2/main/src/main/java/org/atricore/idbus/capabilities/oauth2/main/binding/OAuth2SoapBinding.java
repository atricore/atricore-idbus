package org.atricore.idbus.capabilities.oauth2.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.MessageContentsList;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2MessagingConstants;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthRequestAbstractType;
import org.atricore.idbus.common.oauth._2_0.wsdl.OAuthPortType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationSoapBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;

import javax.xml.ws.Service;
import java.lang.reflect.Method;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2SoapBinding extends AbstractMediationSoapBinding {

    private static final Log logger = LogFactory.getLog(OAuth2SoapBinding.class);

    public OAuth2SoapBinding(Channel channel) {
        super(OAuth2Binding.OAUTH2_SOAP.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // Get HTTP Exchange from SAML Exchange
        CamelMediationExchange samlR2exchange = message.getExchange();
        Exchange exchange = samlR2exchange.getExchange();

        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        // Converting from CXF Message to SAMLR2 Message
        // Is this a CXF message?
        Message in = exchange.getIn();

        if (in.getBody() instanceof MessageContentsList) {

            MessageContentsList mclIn = (MessageContentsList) in.getBody() ;
            logger.debug("Using CXF Message Content : " + mclIn.get(0));

            MediationState state = null;
            LocalState lState = null;

            MediationMessage body;
            if (mclIn.get(0) instanceof OAuthRequestAbstractType) {

                // Process Saml Request in SOAP Channel

                // Try to restore provider state based on sessionIndex

                OAuthRequestAbstractType oauthRequestAbstracType = (OAuthRequestAbstractType) mclIn.get(0);

                /* TODO : Add state support for OAUTH SOAP Based on an OAUTH request property ... ?!
                try {

                    Method getSsoSessionId = oauthRequestAbstracType.getClass().getMethod("getSsoSessionId");
                    String ssoSessionId = (String) getSsoSessionId.invoke(oauthRequestAbstracType);

                    ProviderStateContext ctx = createProviderStateContext();

                    // Add retries just in case we're in a cluster (they are disabled in non HA setups)
                    int retryCount = getRetryCount();
                    if (retryCount > 0) {
                        lState = ctx.retrieve("ssoSessionId", ssoSessionId, retryCount, getRetryDelay());
                    } else {
                        lState = ctx.retrieve("ssoSessionId", ssoSessionId);
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("Local state was" + (lState == null ? " NOT" : "") + " retrieved for ssoSessionId " + ssoSessionId);

                } catch (NoSuchMethodException e) {
                    // Ignore this ...
                    if (logger.isTraceEnabled())
                        logger.trace("SSO Request does not have session index : " + e.getMessage(), e);

                } catch (InvocationTargetException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error("Cannot recover local state : " + e.getMessage(), e);
                }
                */


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
                    mclIn.get(0),
                    null,
                    null,
                    null,
                    state);

            return body;

        } else {
            throw new IllegalArgumentException("Unknown message type " + in.getBody());
        }

    }


    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Sending new SSO message using SOAP Binding");

        EndpointDescriptor endpoint = message.getDestination();

        String soapEndpoint = endpoint.getLocation();

        Service service = Service.create(OAuth2MessagingConstants.SERVICE_NAME);
        service.addPort(OAuth2MessagingConstants.PORT_NAME,
                javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING,
                soapEndpoint);

        Object content = message.getContent();

        if (!(content instanceof OAuthRequestAbstractType )) {
            throw new IdentityMediationException("Unsupported content " + content);
        }

        String soapMethodName = content.getClass().getSimpleName();
        soapMethodName = soapMethodName.substring(0, soapMethodName.length() - 4); // Remove Type

        if (soapMethodName.startsWith("IDP")) {
            soapMethodName = "idp" + soapMethodName.substring(3);
        } else if (soapMethodName.startsWith("SP")) {
            soapMethodName = "sp" + soapMethodName.substring(2);
        } else {
            soapMethodName = soapMethodName.substring(0).toLowerCase() + soapMethodName.substring(1);
        }

        if (logger.isTraceEnabled())
            logger.trace("Using soap method ["+soapMethodName+"]");

        OAuthPortType port = service.getPort(OAuth2MessagingConstants.PORT_NAME, OAuthPortType.class);

        if (logger.isTraceEnabled())
            logger.trace("Sending SSO SOAP Request: " + content);


        try {
            Method soapMethod = port.getClass().getMethod(soapMethodName, content.getClass());

            Object o = soapMethod.invoke(port, content);

            if (logger.isTraceEnabled())
                logger.trace("Received SSO SOAP Response: " + o);

            return o;

        } catch (NoSuchMethodException e) {
            throw new IdentityMediationException("SOAP Method not impelmented " + soapMethodName + ": " +
                    e.getMessage(), e);

        } catch (Exception e) {
            throw new IdentityMediationException("SOAP Method not impelmented " + soapMethodName + ": " +
                    e.getMessage(), e);
        }

    }
}