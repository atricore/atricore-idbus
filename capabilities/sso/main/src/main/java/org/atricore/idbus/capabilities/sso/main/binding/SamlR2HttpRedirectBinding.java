/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.sso.main.binding;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.w3._1999.xhtml.Html;

import java.io.*;
import java.net.URLEncoder;
import java.util.zip.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2HttpRedirectBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpRedirectBinding.class);

    public SamlR2HttpRedirectBinding(Channel channel) {
        super(SSOBinding.SAMLR2_REDIRECT.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null ||
                !httpMsg.getHeader("http.requestMethod").equals("GET")) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        try {

            // HTTP Request Parameters from HTTP Request body
            MediationState state = createMediationState(exchange);

            // HTTP Redirect SSOBinding supports the following parameters
            String base64SAMLRequest = state.getTransientVariable("SAMLRequest");
            String base64SAMLResponse = state.getTransientVariable("SAMLResponse");
            String relayState = state.getTransientVariable("RelayState");

            // Follow SAML 2.0 Binding, section 3.4.4.1 DEFLATE Encoding , regarding Digital Siganture usage.
            String sigAlg = state.getTransientVariable("SigAlg");       // Use HTTP Redirect binding Signature Algorithm
            String signature = state.getTransientVariable("Signature"); // Validate HTTP Redirect binding Signature

            if (base64SAMLRequest != null && base64SAMLResponse != null) {
                throw new IllegalStateException("Received both SAML Request and SAML Response");
            }

            if (base64SAMLRequest == null && base64SAMLResponse == null) {
                throw new IllegalStateException("Recevied no SAML Request nor SAML Response");
            }


            if (base64SAMLRequest != null) {

                // By default, we use inflate/deflate
                base64SAMLRequest = inflateFromRedirect(base64SAMLRequest, true);

                if (logger.isDebugEnabled())
                    logger.debug("Received SAML 2.0 Request [" + base64SAMLRequest + "]");

                // SAML Request
                RequestAbstractType samlRequest = XmlUtils.unmarshalSamlR2Request(base64SAMLRequest, false);

                if (logger.isDebugEnabled())
                    logger.debug("Received SAML 2.0 Request " + samlRequest.getID());

                // Store relay state to send it back later
                if (relayState != null) {
                    state.setLocalVariable("urn:org:atricore:idbus:samr2:protocol:relayState:" + samlRequest.getID(), relayState);
                }

                return new MediationMessageImpl<RequestAbstractType>(httpMsg.getMessageId(),
                        samlRequest,
                        XmlUtils.decode(base64SAMLRequest),
                        null,
                        relayState,
                        null,
                        state);
            } else {

                // SAML Response
                base64SAMLResponse = inflateFromRedirect(base64SAMLResponse, true);

                if (logger.isDebugEnabled())
                    logger.debug("Received SAML 2.0 Response [" + base64SAMLResponse + "]");

                StatusResponseType samlResponse = XmlUtils.unmarshalSamlR2Response(base64SAMLResponse, false);

                if (logger.isDebugEnabled())
                    logger.debug("Received SAML 2.0 Response " + samlResponse.getID());

                return new MediationMessageImpl<StatusResponseType>(httpMsg.getMessageId(),
                        samlResponse,
                        XmlUtils.decode(base64SAMLResponse),
                        null,
                        relayState,
                        null,
                        state);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
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
            String location = ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation();

            // ------------------------------------------------------------
            // Create HTML Form for response body
            // ------------------------------------------------------------

            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Redirect to " + location);

            String msgName = null;
            java.lang.Object msgValue = null;
            String element = out.getContentType();
            boolean isResponse = false;
            String relayState = out.getRelayState();

            boolean signMsg = false;

            if (out.getContent() instanceof RequestAbstractType) {

                msgName = "SAMLRequest";

                RequestAbstractType req = (RequestAbstractType) out.getContent();

                if (req.getSignature() != null) {
                    // Strip DS information from request/response!
                    req.setSignature(null);
                    signMsg = true;
                }

                // Marshall
                String s = XmlUtils.marshalSamlR2Request((RequestAbstractType) out.getContent(), element, false);

                // Use default DEFLATE (rfc 1951)
                msgValue = deflateForRedirect(s, true);
                msgValue = URLEncoder.encode((String) msgValue, "UTF-8");

            } else if (out.getContent() instanceof StatusResponseType) {
                msgName = "SAMLResponse";
                isResponse = true;

                // Strip DS information from request/response!
                StatusResponseType res = (StatusResponseType) out.getContent();
                if (res.getSignature() != null) {
                    res.setSignature(null);
                    signMsg = true;
                }

                // Marshall
                String s = XmlUtils.marshalSamlR2Response((StatusResponseType) out.getContent(), element, false);

                // Use default DEFLATE (rfc 1951)
                msgValue = deflateForRedirect(s, true);
                msgValue = URLEncoder.encode((String) msgValue, "UTF-8");

                StatusResponseType samlResponse = (StatusResponseType) out.getContent();
                if (samlResponse.getInResponseTo() != null) {
                    String rs = (String) out.getState().getLocalVariable("urn:org:atricore:idbus:samr2:protocol:relayState:" +
                            samlResponse.getInResponseTo());
                    if (relayState != null && rs != null && !relayState.equals(rs)) {
                        relayState = rs;
                        logger.warn("Provided relay state does not match stored state : " + relayState + " : " + rs +
                                ", forcing " + relayState);
                    }
                }

            }

            if (out.getContent() == null) {
                throw new NullPointerException("Cannot REDIRECT null content to " + location);
            }
            if (!(msgValue instanceof String)) {
                throw new IllegalArgumentException("Cannot REDIRECT content of type " + msgValue.getClass().getName());
            }

            String qryString = msgName + "=" + (String) msgValue;
            if (out.getRelayState() != null) {
                qryString += "&RelayState=" + relayState;
            }

            // Follow SAML 2.0 Binding, section 3.4.4.1 DEFLATE Encoding , regarding Digital Siganture usage.
            // Generate HTTP Redirect binding SigAlg and Signature parameters (this requires access to Provider signer!)
            MediationState state = samlOut.getMessage().getState();
            SamlR2Signer signer = (SamlR2Signer) state.getAttribute("SAMLR2Signer");
            if (signer != null) {
                qryString = "?" + signer.signQueryString(qryString);
            } else {
                qryString = "?" + qryString;
            }

            Message httpOut = exchange.getOut();
            Message httpIn = exchange.getIn();
            String redirLocation = this.buildHttpTargetLocation(httpIn, ed, isResponse) + qryString;

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            if (!isEnableAjax()) {
                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Content-Type", "text/html");
                httpOut.getHeaders().put("Location", redirLocation);
            } else {

                Html redir = this.createHtmlRedirectMessage(redirLocation);
                String marshalledHttpResponseBody = XmlUtils.marshal(redir, "http://www.w3.org/1999/xhtml", "html",
                        new String[]{"org.w3._1999.xhtml"});

                httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
                httpOut.getHeaders().put("Pragma", "no-cache");
                httpOut.getHeaders().put("http.responseCode", 200);
                httpOut.getHeaders().put("Content-Type", "text/html");

                ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
                httpOut.setBody(baos);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String deflateForRedirect(String redirStr, boolean encode) {

        int n = redirStr.length();
        byte[] redirIs = null;
        try {
            redirIs = redirStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        byte[] deflated = new byte[n];

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(redirIs);
        deflater.finish();
        int len = deflater.deflate(deflated);
        deflater.end();

        byte[] exact = new byte[len];

        System.arraycopy(deflated, 0, exact, 0, len);

        if (encode) {
            byte[] base64Str = new Base64().encode(exact);
            return new String(base64Str);
        }

        return new String(exact);
    }

    public static String inflateFromRedirect(String redirStr, boolean decode) throws Exception {

        if (redirStr == null || redirStr.length() == 0) {
            throw new RuntimeException("Redirect string cannot be null or empty");
        }

        byte[] redirBin = null;
        if (decode)
            redirBin = new Base64().decode(removeNewLineChars(redirStr).getBytes());
        else
            redirBin = redirStr.getBytes();

        // Decompress the bytes
        Inflater inflater = new Inflater(true);
        inflater.setInput(redirBin);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        try {
            int resultLength = 0;
            int buffSize= 1024;
            byte[] buff = new byte[buffSize];
            while (!inflater.finished()) {
                resultLength = inflater.inflate(buff);
                baos.write(buff, 0, resultLength);
            }

        } catch (DataFormatException e) {
            throw new RuntimeException("Cannot inflate SAML message : " + e.getMessage(), e);
        }

        inflater.end();

        // Decode the bytes into a String
        String outputString = null;
        try {
            outputString = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot convert byte array to string " + e.getMessage(), e);
        }
        return outputString;
    }

    public static String removeNewLineChars(String s) {
        String retString = null;
        if ((s != null) && (s.length() > 0) && (s.indexOf('\n') != -1)) {
            char[] chars = s.toCharArray();
            int len = chars.length;
            StringBuffer sb = new StringBuffer(len);
            for (int i = 0; i < len; i++) {
                char c = chars[i];
                if (c != '\n') {
                    sb.append(c);
                }
            }
            retString = sb.toString();
        } else {
            retString = s;
        }
        return retString;
    }

}