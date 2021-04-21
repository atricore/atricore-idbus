package org.atricore.idbus.capabilities.openidconnect.main.op;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * OpenID Connect / OAuth2 authorization grant
 */
public class AuthorizationGrant implements Serializable {

    private String id;
    private long creationTime;
    private long expiresOn;
    private String ssoSessionId;
    private Subject subject;
    private String nonce;
    private String codeChallenge;
    private String codeChallengeMethod;

    public AuthorizationGrant(String id, String ssoSessionId, Subject subject, String nonce,
                              String codeChallenge,
                              String codeChallengeMethod,
                              long expiresOn) {

        this.id = id;
        this.ssoSessionId = ssoSessionId;
        this.subject = subject;
        this.creationTime = System.currentTimeMillis();
        this.expiresOn = expiresOn;
        this.nonce = nonce;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;

    }

    public String getId() {
        return id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getSsoSessionId() {
        return ssoSessionId;
    }

    public Subject getSubject() {
        return subject;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public String getNonce() {
        return nonce;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }
}
