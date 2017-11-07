package org.atricore.idbus.capabilities.openidconnect.main.op.binding.logging;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientUpdateRequest;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

import java.net.URI;

/**
 * 
 */
public class OpenIDConnectLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(OpenIDConnectLogMessageBuilder.class);

    public boolean canHandle(Message message) {
        if (!(message instanceof CamelMediationMessage))
            return false;

        CamelMediationMessage oauthMsg = (CamelMediationMessage) message;
        if (oauthMsg.getMessage() == null) {
            logger.trace("No message found in mediation message : " + oauthMsg.getMessageId());
            return false;
        }

        Object content = oauthMsg.getMessage().getContent();
        if (content == null) {
            logger.trace("No message content found in mediation message : " + oauthMsg.getMessageId());
            return false;
        }

        if (content instanceof Request) {
            return true;
        } else if (content instanceof Response) {
            return true;
        }

        return false;
    }

    public String getType() {
        return "openid-connect";
    }

    public String buildLogMessage(Message message) {
        try {
            StringBuffer logMsg = new StringBuffer();
            CamelMediationMessage oauthMsg = (CamelMediationMessage) message;

            if (oauthMsg.getMessage() == null) {
                logger.warn("No message found in mediation message : " + oauthMsg.getMessageId());
                return null;
            }

            Object content = oauthMsg.getMessage().getContent();

            if (content instanceof Request) {
                Request r = (Request) content;
                // TODO : Format for log

                URI endpointUri = r.getEndpointURI();



                if (r instanceof OIDCClientUpdateRequest) {
                    OIDCClientUpdateRequest oidc = (OIDCClientUpdateRequest) r;
                    OIDCClientMetadata md = oidc.getOIDCClientMetadata();

                    logMsg.append("oidc-msg=\"OIDCClientUpdateRequest\" application-type=\"" + md.getApplicationType().toString() + "\"");

                } else if (r instanceof OIDCClientRegistrationRequest) {
                    OIDCClientRegistrationRequest oidc = (OIDCClientRegistrationRequest) r;
                    OIDCClientMetadata md = oidc.getOIDCClientMetadata();

                    logMsg.append("oidc-msg=\"OIDCClientRegistrationRequest\" application-type=\"" + md.getApplicationType().toString() + "\"");


                } else if (r instanceof TokenRequest) {
                    TokenRequest oidc = (TokenRequest) r;

                    AuthorizationGrant ag = oidc.getAuthorizationGrant();
                    String grant = ag != null ? ag.getType().toString() : "null";
                    String scope = oidc.getScope() != null ? oidc.getScope().toString() : "null";
                    String clientId = oidc.getClientID() != null ? oidc.getClientID().getValue() : "null";

                    logMsg.append("oidc-msg=\"TokenRequest\" client-id=\"" + clientId + "\" authz-grant=\"" + grant + "\" scope=\"" + scope + "\"");


                } else if (r instanceof AuthenticationRequest) {
                    AuthenticationRequest oidc = (AuthenticationRequest) r;

                    String scope = oidc.getScope() != null ? oidc.getScope().toString() : "null";
                    String clientId = oidc.getClientID() != null ? oidc.getClientID().getValue() : "null";
                    String requestUri = oidc.getRequestURI() != null  ? oidc.getRequestURI().toString() : "null";

                    logMsg.append("oidc-msg=\"AuthenticationRequest\" client-id=\"" + clientId +
                            "\"request-uri=\"" + requestUri +
                            "\" scope=\"" + scope +
                            "\" query-string=\"" + oidc.toQueryString());

                } else if (r instanceof AuthorizationRequest) {
                    AuthorizationRequest oidc = (AuthorizationRequest) r;

                    String scope = oidc.getScope() != null ? oidc.getScope().toString() : "null";
                    String clientId = oidc.getClientID() != null ? oidc.getClientID().getValue() : "null";
                    String responseMode = oidc.getResponseMode() != null ? oidc.getResponseMode().getValue() : "null";
                    String responseType = oidc.getResponseType() != null ? oidc.getResponseType().toString() : "null";
                    String state = oidc.getState() != null ? oidc.getState().getValue() : "null";
                    String redirUri = oidc.getRedirectionURI() != null ? oidc.getRedirectionURI().toString() : "null";



                    logMsg.append("oidc-msg=\"AuthenticationRequest\" client-id=\"" + clientId +
                            "\" request-uri=\"" + redirUri +
                            "\" scope=\"" + scope +
                            "\" response-mode=\"" + responseMode +
                            "\" response-type=\"" + responseType +
                            "\" state=\"" + state +
                            "\" query-string=\"" + oidc.toQueryString());

                }

                logMsg.append(r.toString());

            } else if (content instanceof Response) {
                Response r = (Response) content;

                if (r instanceof OIDCAccessTokenResponse) {
                    OIDCAccessTokenResponse oidc = (OIDCAccessTokenResponse) r;

                    JWT jwt = oidc.getIDToken();
                    String idTokenStr = oidc.getIDTokenString();
                    AccessToken at = oidc.getAccessToken();

                    logMsg.append("oidc-msg=\"OIDCAccessTokenResponse\" " +
                            "id-token-header=\"" + (jwt != null ? jwt.getHeader().toString() : "null") + "\" " +
                            "id-token-string=\"" + idTokenStr + "\" " +
                            "access-token=\"" + (at != null ? at.toAuthorizationHeader() : "null") + "\" " +
                            "access-token-json=\"" + (at != null ? at.toJSONString() : "null") + "\"");

                } else if (r instanceof AuthenticationResponse) {
                    AuthenticationResponse oidc = (AuthenticationResponse) r;

                    if (oidc instanceof AuthenticationSuccessResponse) {
                        AuthenticationSuccessResponse s = (AuthenticationSuccessResponse) oidc;
                        JWT jwt = s.getIDToken();
                        String state = s.getSessionState() != null ? s.getSessionState().getValue() : "null";

                        logMsg.append("oidc-msg=\"AuthenticationSuccessResponse\" " +
                                " id-token-header=\"" + (jwt != null ? jwt.getHeader().toString() : "null") + "\"" +
                                " state=\"" + state + "\"");


                    } else if (oidc instanceof AuthenticationErrorResponse) {
                        AuthenticationErrorResponse e = (AuthenticationErrorResponse) oidc;

                        ErrorObject error = e.getErrorObject();
                        String state = e.getState() != null ? e.getState().getValue() : "null";

                        logMsg.append("oidc-msg=\"AuthenticationSuccessResponse\" " +
                                " error-code=\"" + (error != null ? error.getCode() : "null") + "\"" +
                                " error-description=\"" + (error != null ? error.getDescription() : "null") + "\"" +
                                " error-json=\"" + (error != null ? error.toJSONObject().toJSONString() : "null") + "\"" +
                                " state=\"" + state + "\"");

                    }
                } else if (r instanceof TokenErrorResponse) {

                    TokenErrorResponse e = (TokenErrorResponse) r;

                    ErrorObject error = e.getErrorObject();
                    //String state = e.getState() != null ? e.getState().getValue() : "null";

                    logMsg.append("oidc-msg=\"TokenErrorResponse\" " +
                            " error-code=\"" + (error != null ? error.getCode() : "null") + "\"" +
                            " error-description=\"" + (error != null ? error.getDescription() : "null") + "\"" +
                            " error-json=\"" + (error != null ? error.toJSONObject().toJSONString() : "null") + "\"");

                }


                // TODO : Format for log
                logMsg.append(r.toString());

            } else if (content == null) {
                logger.debug("No Message content");
            } else {
                logger.warn("Unknown Message content " + content);
            }

            return logMsg.toString();

        } catch (Exception e) {
            logger.error("Cannot generate mediation log message: " + e.getMessage(), e);
            return null;
        }

    }
}


