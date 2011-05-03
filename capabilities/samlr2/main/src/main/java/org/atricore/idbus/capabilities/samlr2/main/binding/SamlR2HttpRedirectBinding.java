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

package org.atricore.idbus.capabilities.samlr2.main.binding;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.*;
import java.net.URLEncoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2HttpRedirectBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpRedirectBinding.class);

    public SamlR2HttpRedirectBinding(Channel channel) {
        super(SamlR2Binding.SAMLR2_REDIRECT.getValue(), channel);
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

            // HTTP Redirect SamlR2Binding supports the following parameters
            String base64SAMLRequest = state.getTransientVariable("SAMLRequest");
            String base64SAMLResponse = state.getTransientVariable("SAMLResponse");
            String relayState = state.getTransientVariable("RelayState");

            // TODO : Follow SAML 2.0 Binding, section 3.4.4.1 DEFLATE Encoding , regarding Digital Siganture usage.
            String sigAlg = state.getTransientVariable("SigAlg");       // TODO : Use HTTP Redirect binding Signature Algorithm
            String signature = state.getTransientVariable("Signature"); // TODO : Validate HTTP Redirect binding Signature

            if (base64SAMLRequest != null && base64SAMLResponse != null) {
                throw new IllegalStateException("Received both SAML Request and SAML Response");
            }

            if (base64SAMLRequest == null && base64SAMLResponse == null) {
                throw new IllegalStateException("Recevied no SAML Request nor SAML Response");
            }


            if (base64SAMLRequest != null) {

                // By default, we use inflate/deflate
                base64SAMLRequest = inflate(base64SAMLRequest, true);

                // SAML Request
                RequestAbstractType samlRequest = XmlUtils.unmarshalSamlR2Request(base64SAMLRequest, false);
                logger.debug("Received SAML Request " + samlRequest.getID());

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
                base64SAMLResponse = inflate(base64SAMLResponse, true);

                StatusResponseType samlResponse = XmlUtils.unmarshalSamlR2Response(base64SAMLResponse, false);
                logger.debug("Received SAML Response " + samlResponse.getID());
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

            // ------------------------------------------------------------
            // Create HTML Form for response body
            // ------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Redirect to " + ed.getLocation());

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
                msgValue = deflate(s, true);
                msgValue = URLEncoder.encode((String) msgValue, "UTF-8");

            } else if (out.getContent() instanceof StatusResponseType) {
                msgName = "SAMLResponse";

                // Strip DS information from request/response!
                StatusResponseType res = (StatusResponseType) out.getContent();
                if (res.getSignature() != null) {
                    res.setSignature(null);
                    signMsg = true;
                }

                // Marshall
                String s = XmlUtils.marshalSamlR2Response((StatusResponseType) out.getContent(), element, false);

                // Use default DEFLATE (rfc 1951)
                msgValue = deflate(s, true);
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
                throw new NullPointerException("Cannot REDIRECT null content to " + ed.getLocation());
            }
            if (!(msgValue instanceof String)) {
                throw new IllegalArgumentException("Cannot REDIRECT content of type " + msgValue.getClass().getName());
            }

            String qryString = "?" + msgName + "=" + (String) msgValue;
            if (out.getRelayState() != null) {
                qryString += "&RelayState=" + relayState;
            }

            // TODO : Follow SAML 2.0 Binding, section 3.4.4.1 DEFLATE Encoding , regarding Digital Siganture usage.
            // TODO : Generate HTTP Redirect binding SigAlg and Signature parameters (this requires access to Provider signer!)

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

    protected String deflate(String in, boolean encode) throws Exception {

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DeflaterOutputStream deflated = new DeflaterOutputStream(bytesOut, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        ByteArrayInputStream inflated = new ByteArrayInputStream(in.getBytes());


        byte[] buf = new byte[1024];
        int read = inflated.read(buf);
        while (read > 0) {
            deflated.write(buf, 0, read);
            read = inflated.read(buf);
        }


        deflated.flush();
        deflated.finish();


        byte[] encodedbytes = bytesOut.toByteArray();
        if (encode) {
            encodedbytes = new Base64().encode(encodedbytes);
        }

        deflated.close();

        return new String(encodedbytes);

    }

    protected String inflate(String in, boolean decode) throws Exception {

        byte[] decodedBytes = in.getBytes();
        if (decode) {
            decodedBytes = new Base64().decode(in.getBytes());
        }
        
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes);
        InputStream inflater = new InflaterInputStream(bytesIn, new Inflater(true));

        // This gets rid of platform specific EOL chars ...
        BufferedReader r = new BufferedReader(new InputStreamReader(inflater));
        StringBuffer sb = new StringBuffer();
        
        String l = r.readLine();
        while (l != null) {
            sb.append(l);
            l = r.readLine();
        }

        return sb.toString();

    }

}
