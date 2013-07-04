package org.atricore.idbus.capabilities.oauth2.rserver.http;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2ClaimType;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolver;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.OAuth2RServerException;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class OAuth2Filter implements Filter {

    private AccessTokenResolver tokenResolver;

    public void init(FilterConfig filterConfig) throws ServletException {

        Properties props = null;
        String oauth2Cfg = filterConfig.getInitParameter("org.atricore.idbus.capabilities.oauth2.cfg");

        if (oauth2Cfg != null) {
            props = new Properties();
            InputStream is = null;
            try {
                is = new URL(oauth2Cfg).openStream();
                props.load(is);
            } catch (MalformedURLException e) {
                throw new ServletException(e);
            } catch (IOException e) {
                throw new ServletException(e);
            } finally {
                if (is != null) try { is.close(); } catch (IOException ioe) {/**/} 
            }


        } else {
            props = new Properties();

            String secretEnc = filterConfig.getInitParameter( SecureAccessTokenResolverFactory.SHARED_SECRECT_ENC_PROPERTY);
            if (secretEnc  != null)
                props.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_ENC_PROPERTY, secretEnc);

            String secret = filterConfig.getInitParameter( SecureAccessTokenResolverFactory.SHARED_SECRECT_PROPERTY);
            if (secret != null)
                props.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_PROPERTY, secret);

            String sign = filterConfig.getInitParameter( SecureAccessTokenResolverFactory.SHARED_SECRECT_SIGN_PROPERTY);
            if (sign != null)
                props.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_SIGN_PROPERTY, sign);

            String tokenValidityInterval = filterConfig.getInitParameter( SecureAccessTokenResolverFactory.TOKEN_VALIDITY_INTERVAL_PROPERTY);
            if (tokenValidityInterval != null)
                props.setProperty(SecureAccessTokenResolverFactory.TOKEN_VALIDITY_INTERVAL_PROPERTY, tokenValidityInterval);
        }

        try {
            tokenResolver = AccessTokenResolverFactory.newInstance(props).newResolver();
        } catch (OAuth2RServerException e) {
            throw new ServletException(e);
        }
    }

    public void doFilter(ServletRequest r, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) r;
        String accessTokenStr = request.getParameter("access_token");
        if (accessTokenStr == null) {

            Enumeration h = request.getHeaderNames();
            while (h.hasMoreElements()) {
                String n = (String) h.nextElement();
                if (n.equalsIgnoreCase("authorization")) {
                    accessTokenStr = request.getHeader(n);
                    // Get the value after the first space:
                    accessTokenStr = accessTokenStr.substring(7);
                    break;
                }
            }

        }

        try {
            if (accessTokenStr != null) {
                OAuth2AccessToken accessToken = tokenResolver.resolve(accessTokenStr);
                // Wrap the request with identity information
                filterChain.doFilter(new OAuth2ServletRequest(accessToken, request), response);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (OAuth2RServerException e) {
            throw new ServletException(e);
        }

    }

    public void destroy() {

    }

    public class OAuth2ServletRequest extends HttpServletRequestWrapper {

        private OAuth2AccessToken accessToken;

        private Set<String> roles = null;

        private Principal principal = null;

        public OAuth2ServletRequest(OAuth2AccessToken at, HttpServletRequest request) {
            super(request);
            this.accessToken = at;
        }

        @Override
        public String getRemoteUser() {
            return getUserPrincipal().getName();
        }

        @Override
        public boolean isUserInRole(String role) {
            if (roles == null) {
                roles = new HashSet<String>();
                for(OAuth2Claim c : accessToken.getClaims()) {
                    if (c.getType().equals(OAuth2ClaimType.ROLE.toString()))
                        roles.add(c.getValue());
                }
            }

            return roles.contains(role);
        }
        @Override
        public Principal getUserPrincipal() {
            if (principal == null) {

                String name = null;
                Properties props = new Properties();

                for (OAuth2Claim c : accessToken.getClaims()) {
                    if (c.getType().equals(OAuth2ClaimType.USERID.toString()))
                        name = c.getValue();
                    else if (c.getType().equals(OAuth2ClaimType.ATTRIBUTE.toString())) {
                        props.setProperty(c.getValue(), c.getAttribute());
                    } else if (c.getType().equals(OAuth2ClaimType.ROLE.toString())) {

                    }

                }

                principal = new OAuth2Principal(name, props);
            }

            return principal;
        }

        @Override
        public String getAuthType() {
            return "JOSSO-OAUTH2";
        }
    }

}
