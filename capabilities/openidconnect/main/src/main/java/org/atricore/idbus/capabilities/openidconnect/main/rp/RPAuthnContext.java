package org.atricore.idbus.capabilities.openidconnect.main.rp;

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
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RPAuthnContext implements Serializable {

    private static final Log logger = LogFactory.getLog(RPAuthnContext.class);

    public RPAuthnContext() {
        uuid = UUIDGenerator.generateJDKId();
    }

    private String uuid;

    // Request sent to SSO endpoint
    private SPInitiatedAuthnRequestType ssoAuthnRequest;

    // Request sent to SLO endponit
    private SPInitiatedLogoutRequestType sloRequest;

    // Non-serializable version
    private transient AuthenticationRequest authnRequest;
    private Map<String, List<String>> authnRequestAsParams;

    // Non-serializable version
    private transient LogoutRequest logoutRequest;
    private Map<String, List<String>> logoutRequestAsParams;

    // Current emitted Authorization code
    private AuthorizationCode authorizationCode;

    private long accessTokenNotOnOrAfter;

    // Selected IDP Alias
    private String idpAlias;

    private String idToken;

    private RefreshToken refreshToken;
    private AccessToken accessToken;
    private AuthorizationCode authzCode;

    private String rpSession;
    private State rpSessionState;
    private String idPSession;

    public String getIdpAlias() {
        return idpAlias;
    }

    public void setIdpAlias(String idpAlias) {
        this.idpAlias = idpAlias;
    }

    public SPInitiatedAuthnRequestType getSsoAuthnRequest() {
        return ssoAuthnRequest;
    }

    public void setSsoAuthnRequest(SPInitiatedAuthnRequestType ssoAuthnRequest) {
        this.ssoAuthnRequest = ssoAuthnRequest;
    }

    public void setSloRequest(SPInitiatedLogoutRequestType request) {
        this.sloRequest = request;
    }

    public SPInitiatedLogoutRequestType getSloRequest() {
        return sloRequest;
    }

    public AuthenticationRequest getAuthnRequest() {

        if (authnRequest == null && authnRequestAsParams != null) {
            try {
                authnRequest = AuthenticationRequest.parse(authnRequestAsParams);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return authnRequest;
    }

    public void setAuthnRequest(AuthenticationRequest authnRequest) {
        this.authnRequest = authnRequest;

        if (authnRequest != null) {
            try {
                this.authnRequestAsParams = authnRequest.toParameters();
            } catch (SerializeException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.authnRequestAsParams = null;
        }
    }

    public void setLogoutRequest(LogoutRequest logoutRequest) {
        this.logoutRequest = logoutRequest;

        if (logoutRequest != null) {
            try {
                this.logoutRequestAsParams = logoutRequest.toParameters();
            } catch (SerializeException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.logoutRequestAsParams = null;
        }
    }

    public LogoutRequest getLogoutRequest() {
        if (logoutRequest == null && logoutRequestAsParams != null) {
            try {
                logoutRequest = LogoutRequest.parse(logoutRequestAsParams);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return logoutRequest;
    }

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

    public String getRPSession() {
        return rpSession;
    }

    public void setRPSession(String rpSession) {
        this.rpSession = rpSession;
    }

    public void setIdPSession(String idPSession) {
        this.idPSession = idPSession;
    }

    public String getIdPSession() {
        return idPSession;
    }

    public State getRPSessionState() {
        return rpSessionState;
    }

    public void setRpSessionState(State rpSessionState) {
        this.rpSessionState = rpSessionState;
    }

    public void setAuthorizationCode(AuthorizationCode authorizationCode) {
        this.authzCode = authorizationCode;
    }

    public AuthorizationCode getAuthorizationCode() {
        return authzCode;
    }
}
