package org.atricore.idbus.capabilities.oauth2.client.jaxrs;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/4/13
 */
public class OAuth2ClientAuthenticationFilter implements ClientRequestFilter {

    private String authzHeader;

    public OAuth2ClientAuthenticationFilter(String accessToken) {
        this.authzHeader = "OAuth2 " + accessToken;
    }

    public void filter(ClientRequestContext rc) throws IOException {

        if (!rc.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            rc.getHeaders().add(HttpHeaders.AUTHORIZATION, authzHeader);
        }

    }
}
