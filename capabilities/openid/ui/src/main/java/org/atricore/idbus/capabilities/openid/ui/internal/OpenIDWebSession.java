package org.atricore.idbus.capabilities.openid.ui.internal;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

public class OpenIDWebSession extends WebSession {

    private ClaimsRequest claimsRequest;

    public OpenIDWebSession(Request request) {
        super(request);
    }

    /**
     * @return Current authenticated web session
     */
    public static OpenIDWebSession get()
    {
        return (OpenIDWebSession) Session.get();
    }

    public void setClaimsRequest(ClaimsRequest claimsRequest) {
        this.claimsRequest = claimsRequest;
    }

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }

}
