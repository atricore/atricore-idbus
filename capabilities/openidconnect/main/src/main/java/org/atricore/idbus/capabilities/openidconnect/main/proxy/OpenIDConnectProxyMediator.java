package org.atricore.idbus.capabilities.openidconnect.main.proxy;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.AbstractOpenIDConnectMediator;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class OpenIDConnectProxyMediator extends AbstractOpenIDConnectMediator  {

    private static final Log logger = LogFactory.getLog(OpenIDConnectProxyMediator.class);

    private JacksonFactory jacksonFactory;

    private HttpTransport httpTransport;

    private String authzTokenServiceLocation;

    private String mobileAuthzTokenServiceLocation;

    private String accessTokenServiceLocation;

    private String requestTokenServiceLocation;

    private String clientId;

    private String clientSecret;

    private String idpProxyAlias;

    private String scopes;

    private String mobileScopes;

    private String googleAppsDomain;

    private String userFields;

    /** Lock on the flow and credential. */
    private final Lock lock = new ReentrantLock();

    /** Persisted credential associated with the current request or {@code null} for none. */
    private Credential credential;

    @Override
    public void start() throws IdentityMediationException {
        super.start();
        jacksonFactory = new JacksonFactory();
        httpTransport = new ApacheHttpTransport();
    }


    @Override
    protected RouteBuilder createBindingRoutes(final BindingChannel bindingChannel) throws Exception {

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = bindingChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for bindingChannel : " + bindingChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    OpenIDConnectBinding binding = OpenIDConnectBinding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(bindingChannel, endpoint);

                    switch (binding) {
                        case SSO_REDIRECT:
                        case SSO_ARTIFACT:
                        case OPENID_HTTP_POST:
                        case OPENIDCONNECT_AUTHZ:

                            // ----------------------------------------------------------
                            // HTTP Incoming messages:
                            // ==> idbus-http ==> idbus-bind ==> openid-proxy
                            // ----------------------------------------------------------

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new AbstractCamelMediator.LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO samlr2-sp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + bindingChannel.getName()).
                                    process(new AbstractCamelMediator.LoggerProcessor(getLogger())).
                                    to("openidc-proxy:" + ed.getType() +
                                            "?channelRef=" + bindingChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new AbstractCamelMediator.LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO samlr2-sp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + bindingChannel.getName()).
                                        process(new AbstractCamelMediator.LoggerProcessor(getLogger())).
                                        to("openidc-proxy:" + ed.getType() +
                                                "?channelRef=" + bindingChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }

                            break;

                        default:
                            throw new OpenIDConnectException("Unsupported OpenID Connect Binding " + binding.getValue());
                    }


                }

            }
        };
    }

    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {
        String type = null;
        String location;
        String responseLocation;
        OpenIDConnectBinding binding = null;

        logger.debug("Creating OpenID Endpoint Descriptor : " + endpoint.getName());

        // ---------------------------------------------
        // Resolve Endpoint binding
        // ---------------------------------------------
        if (endpoint.getBinding() != null)
            binding = OpenIDConnectBinding .asEnum(endpoint.getBinding());
        else
            logger.warn("No OpenID Binding found in endpoint " + endpoint.getName());

        // ---------------------------------------------
        // Resolve Endpoint location
        // ---------------------------------------------
        location = endpoint.getLocation();
        if (location == null)
            throw new IdentityMediationException("Endpoint location cannot be null.  " + endpoint);

        if (location.startsWith("/"))
            location = channel.getLocation() + location;

        // ---------------------------------------------
        // Resolve Endpoint response location
        // ---------------------------------------------
        responseLocation = endpoint.getResponseLocation();
        if (responseLocation != null && responseLocation.startsWith("/"))
            responseLocation = channel.getLocation() + responseLocation;

        // ---------------------------------------------
        // Resolve Endpoint type
        // ---------------------------------------------

        // Remove qualifier, format can be :
        // 1 - {qualifier}type
        // 2 - qualifier:type
        int bracketPos = endpoint.getType().lastIndexOf("}");
        if (bracketPos > 0)
            type = endpoint.getType().substring(bracketPos + 1);
        else
            type = endpoint.getType().substring(endpoint.getType().lastIndexOf(":") + 1);


        return new EndpointDescriptorImpl(endpoint.getName(),
                type,
                binding.getValue(),
                location,
                responseLocation);
    }

    public JacksonFactory getJacksonFactory() {
        return jacksonFactory;
    }

    public void setJacksonFactory(JacksonFactory jacksonFactory) {
        this.jacksonFactory = jacksonFactory;
    }

    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    public void setHttpTransport(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Lock getLock() {
        return lock;
    }

    public String getIdpProxyAlias() {
        return idpProxyAlias;
    }

    public void setIdpProxyAlias(String idpProxyAlias) {
        this.idpProxyAlias = idpProxyAlias;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getAuthzTokenServiceLocation() {
        return authzTokenServiceLocation;
    }

    public void setAuthzTokenServiceLocation(String authzTokenServiceLocation) {
        this.authzTokenServiceLocation = authzTokenServiceLocation;
    }

    public String getMobileAuthzTokenServiceLocation() {
        return mobileAuthzTokenServiceLocation;
    }

    public void setMobileAuthzTokenServiceLocation(String mobileAuthzTokenServiceLocation) {
        this.mobileAuthzTokenServiceLocation = mobileAuthzTokenServiceLocation;
    }

    public String getAccessTokenServiceLocation() {
        return accessTokenServiceLocation;
    }

    public void setAccessTokenServiceLocation(String accessTokenServiceLocation) {
        this.accessTokenServiceLocation = accessTokenServiceLocation;
    }

    public String getRequestTokenServiceLocation() {
        return requestTokenServiceLocation;
    }

    public void setRequestTokenServiceLocation(String requestTokenServiceLocation) {
        this.requestTokenServiceLocation = requestTokenServiceLocation;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getMobileScopes() {
        return mobileScopes;
    }

    public void setMobileScopes(String mobileScopes) {
        this.mobileScopes = mobileScopes;
    }

    public String getGoogleAppsDomain() {
        return googleAppsDomain;
    }

    public void setGoogleAppsDomain(String googleAppsDomain) {
        this.googleAppsDomain = googleAppsDomain;
    }

    public String getUserFields() {
        return userFields;
    }

    public void setUserFields(String userFields) {
        this.userFields = userFields;
    }
}
