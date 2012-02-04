package org.atricore.idbus.capabilities.oauth2.rserver.util;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessTokenEnvelope;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.rserver.OAuth2RServerException;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverImpl;

import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2PrintATEnvelope {

    private String secret;

    private String tokenStr;

    private SecureAccessTokenResolverImpl r;

    public static void main(String[] args) throws Exception {
        String secret = args[0];
        String tokenStr = args[1];

        OAuth2PrintATEnvelope c = new OAuth2PrintATEnvelope(secret, tokenStr);
        c.print();
    }

    public OAuth2PrintATEnvelope(String secret, String tokenStr) throws OAuth2RServerException {
        this.secret = secret;
        this.tokenStr = tokenStr;

        Properties config = new Properties();
        config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_PROPERTY, "@WSX3edc");

        r = (SecureAccessTokenResolverImpl) SecureAccessTokenResolverFactory.newInstance(config).newResolver();
    }

    public void print() throws IOException, OAuth2RServerException {
        OAuth2AccessTokenEnvelope env  = org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils.unmarshalAccessTokenEnvelope(tokenStr, true);
        OAuth2AccessToken at = r.resolve(env.getToken());

        System.out.println("AT.TimeStamp:" + at.getTimeStamp());

        for (OAuth2Claim claim : at.getClaims()) {
            System.out.println("AT.Claim:" + claim.getType() + "=" + claim.getValue());
        }
    }
}
