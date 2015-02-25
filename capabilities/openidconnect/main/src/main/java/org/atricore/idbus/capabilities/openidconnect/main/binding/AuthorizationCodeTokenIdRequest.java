package org.atricore.idbus.capabilities.openidconnect.main.binding;

/**
 * Created by sgonzalez on 2/24/15.
 */

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.Key;

import java.util.Collection;

/**
 * Google client extension to support OpenIDConnect Authorization code request (not provided yet)
 */
public class AuthorizationCodeTokenIdRequest extends AuthorizationCodeTokenRequest {

    @Key("client_id")
    private String clientId;

    @Key("client_secret")
    private String clientSecret;

    @Key("hd")
    private String hd;

    public AuthorizationCodeTokenIdRequest(HttpTransport transport,
                                           JsonFactory jsonFactory,
                                           GenericUrl tokenServerUrl,
                                           String code,
                                           String clientId,
                                           String clientSecret) {
        super(transport, jsonFactory, tokenServerUrl, code);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public AuthorizationCodeTokenIdRequest setClientId(String clientId) {
        this.clientId = Preconditions.checkNotNull(clientId);
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public AuthorizationCodeTokenIdRequest setClientSecret(String clientSecret) {
        this.clientSecret = Preconditions.checkNotNull(clientSecret);
        return this;
    }

    public String getHd() {
        return hd;
    }

    public AuthorizationCodeTokenIdRequest setHd(String hd) {
        this.hd = Preconditions.checkNotNull(hd);
        return this;
    }

    @Override
    public AuthorizationCodeTokenIdRequest setRequestInitializer(
            HttpRequestInitializer requestInitializer) {
        return (AuthorizationCodeTokenIdRequest) super.setRequestInitializer(requestInitializer);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setTokenServerUrl(GenericUrl tokenServerUrl) {
        return (AuthorizationCodeTokenIdRequest) super.setTokenServerUrl(tokenServerUrl);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setScopes(Collection<String> scopes) {
        return (AuthorizationCodeTokenIdRequest) super.setScopes(scopes);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setGrantType(String grantType) {
        return (AuthorizationCodeTokenIdRequest) super.setGrantType(grantType);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setClientAuthentication(
            HttpExecuteInterceptor clientAuthentication) {
        return (AuthorizationCodeTokenIdRequest) super.setClientAuthentication(clientAuthentication);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setCode(String code) {
        return (AuthorizationCodeTokenIdRequest) super.setCode(code);
    }

    @Override
    public AuthorizationCodeTokenIdRequest setRedirectUri(String redirectUri) {
        return (AuthorizationCodeTokenIdRequest) super.setRedirectUri(redirectUri);
    }

    @Override
    public AuthorizationCodeTokenIdRequest set(String fieldName, Object value) {
        return (AuthorizationCodeTokenIdRequest) super.set(fieldName, value);
    }
}