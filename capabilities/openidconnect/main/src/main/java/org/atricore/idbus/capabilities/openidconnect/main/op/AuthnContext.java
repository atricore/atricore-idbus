package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AuthnContext implements Serializable {

    private static final Log logger = LogFactory.getLog(AuthnContext.class);

    public AuthnContext() {
        uuid = UUIDGenerator.generateJDKId();
    }

    private String uuid;

    private String idToken;
    private RefreshToken refreshToken;
    private AccessToken accessToken;

    public String getIdTokenStr() {
        return idToken;
    }

    public JWT getIdToken() {
        try {
            return idToken != null ? JWTParser.parse(idToken) : null;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIdTokenStr(String idTokenStr) {
        this.idToken = idTokenStr;
    }

    public void setIdToken(JWT idToken) {
        this.idToken = idToken != null ? idToken.getParsedString() : null;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }


}
