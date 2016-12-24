package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sgonzalez.
 */
public class WeChatAuthzTokenConsumerProducer extends AuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(WeChatAuthzTokenConsumerProducer.class);

    private static final int MAX_NUM_OF_USER_INFO_RETRIES = 1;

    public WeChatAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthorizationCodeResponseUrl authnResp) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // OpenID Connect authorization code response
        String code = authnResp.getCode();

        if (authnResp.getError() != null) {
            // onError(req, resp, responseUrl);
            logger.error("Error received [" + authnResp.getError() + "] " + authnResp.getErrorDescription() + ", uri:" + authnResp.getErrorDescription());
            throw new OpenIDConnectException("OpenId Connect error: " + authnResp.getError() + " " +  authnResp.getErrorDescription());
        } else if (code == null) {
            logger.error("Missing authorization code ");
            throw new OpenIDConnectException("Illegal response, no authorization code received ");
        }

        // Validate relay state

        String expectedRelayState = (String) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:relayState");
        String relayState = authnResp.getState();
        if (!expectedRelayState.equals(relayState)) {
            // Invalid response
            if (logger.isDebugEnabled())
                logger.debug("Invalid state [" + relayState + "], expected [" + expectedRelayState + "]");

            throw new OpenIDConnectException("Illegal response, received OpenID Connect state is not valid");
        }

        // ---------------------------------------------------------------
        // Request access token
        // ---------------------------------------------------------------

        String accessTokenSvcLocation = mediator.getAccessTokenServiceLocation();


        /*

Parameter	Required	Description
appid	Yes	The unique ID of the official account
secret	Yes	The appsecret of the official account
code	Yes	The code parameter obtained in the first step
grant_type	Yes	authorization_code
         */

        accessTokenSvcLocation += ("?appid=" + mediator.getClientId());
        accessTokenSvcLocation += ("&secret=" + mediator.getClientSecret());
        accessTokenSvcLocation += ("&code=" + code);
        accessTokenSvcLocation += ("&grant_type=authorization_code");

// SSL
        // general setup
        SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" and "https" protocol schemes, they are
        // required by the default operator to look up socket factories.
        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory.setHostnameVerifier(new AllowAllHostnameVerifier());
        supportedSchemes.register(new Scheme("https", sslSocketFactory, 443));

        // prepare parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);

        DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);

        if (System.getProperty("http.proxyHost") != null) {
            int proxyPort = System.getProperty("http.proxyPort") != null ? Integer.parseInt(System.getProperty("http.proxyPort")) : 8080;
            HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), proxyPort, "http");
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        HttpGet httpget = new HttpGet(accessTokenSvcLocation);

        if (logger.isTraceEnabled()) logger.trace("executing request " + httpget.getURI());

        HttpResponse response = httpclient.execute(httpget);
        // TODO : Error handling

        if (logger.isTraceEnabled())
            logger.trace(response.getStatusLine());

        // Get hold of the response entity
        HttpEntity entity = response.getEntity();


        // If the response does not enclose an entity, there is no need
        // to bother about connection release
        String json = "";
        if (entity != null) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(entity.getContent()));
            try {

                // do something useful with the response
                json +=reader.readLine();

            } catch (IOException ex) {

                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;

            } catch (RuntimeException ex) {

                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                httpget.abort();
                throw ex;

            } finally {

                // Closing the input stream will trigger connection release
                reader.close();

            }
        }

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();

        // TODO : Parse JSON

        /*

            {
            "access_token":"f1MiiWy9CsTNEwrFGzPHMGpkw74RrL7Fl_f9Ab7C2RrXWZlnohvsOKy4SFdm7Xfsgv8Wf_wSF3NhT8zuXSrRySaF_mH5BZ8G6zgnhE5ZHDY",
            "expires_in":7200,
            "refresh_token":"QVklhJRaFXp1A-nJjudmI403--4dbpqaQnMpIKTSawzEdLWJuYoLZV30swkLdhMEvePQmOILPQuqLDTVTHif1JHs6AjszhYGXR2A-g0oJ08",
            "openid":"ojVeMxJqmVessDDO5T4PBjcwm2qI",
            "scope":"snsapi_login",
            "unionid":"oXzObv-Mk7u8FxoTgrRw77VMFi2Q"
            }

         */

        Map<String, Object> map = (Map<String, Object>) fromJsonString(json, Map.class);

        String openid = (String) map.get("openid");
        String accessToken = (String) map.get("access_token");
        String unionid = (String) map.get("unionid");
        Integer accessTokenExpiresIn = (Integer) map.get("expires_in");

        // Look up user by openid:

        SubjectType subject;

        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();

        subject = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(openid);
        a.setFormat(NameIDFormat.UNSPECIFIED.getValue());
        a.setLocalName(openid);
        a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
        a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

        subject.getAbstractPrincipal().add(a);

        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(accessToken);
        attrs.add(accessTokenAttr);

        SubjectAttributeType accessTokenExpiresInAttr = new SubjectAttributeType();
        accessTokenExpiresInAttr.setName("accessTokenExpiresIn");
        accessTokenExpiresInAttr.setValue(accessTokenExpiresIn + "");
        attrs.add(accessTokenExpiresInAttr);

        SubjectAttributeType openIdSubjectAttr = new SubjectAttributeType();
        openIdSubjectAttr.setName("openid");
        openIdSubjectAttr.setValue(openid);
        attrs.add(openIdSubjectAttr);

        SubjectAttributeType unionOpenIdSubjectAttr = new SubjectAttributeType();
        unionOpenIdSubjectAttr .setName("unionid");
        unionOpenIdSubjectAttr .setValue(unionid);
        attrs.add(unionOpenIdSubjectAttr );

        SubjectAttributeType authnCtxClassAttr = new SubjectAttributeType();
        authnCtxClassAttr.setName("authnCtxClass");
        authnCtxClassAttr.setValue(AuthnCtxClass.PPT_AUTHN_CTX.getValue());
        attrs.add(authnCtxClassAttr);

        //addUserAttributes(user, attrs);
        subject.getAbstractPrincipal().addAll(attrs);

        SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
        ssoResponse.setID(uuidGenerator.generateId());
        ssoResponse.setIssuer(getFederatedProvider().getName());
        SPInitiatedAuthnRequestType ssoRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        if (ssoRequest != null) {
            ssoResponse.setInReplayTo(ssoRequest.getID());
        }

        ssoResponse.setSessionIndex(sessionUuidGenerator.generateId());
        ssoResponse.setSubject(subject);
        ssoResponse.getSubjectAttributes().addAll(attrs);

        // ------------------------------------------------------------------------------
        // Send SP Authentication response
        // ------------------------------------------------------------------------------
        SPInitiatedAuthnRequestType authnRequest = (SPInitiatedAuthnRequestType) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:authnRequest");

        // Send response back
        String destinationLocation = resolveSpProxyACS(authnRequest);

        if (logger.isTraceEnabled())
            logger.trace("Sending response to " + destinationLocation);

        EndpointDescriptor destination =
                new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "AssertionConsumerService",
                        OpenIDConnectBinding.SSO_ARTIFACT.getValue(),
                        destinationLocation, null);

        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResponse", "", destination, in.getMessage().getState()));

        exchange.setOut(out);


    }


}
