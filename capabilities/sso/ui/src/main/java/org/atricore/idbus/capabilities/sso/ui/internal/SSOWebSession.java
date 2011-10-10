package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;

public class SSOWebSession extends WebSession {

    private ClaimsRequest claimsRequest;

    public SSOWebSession(Request request) {
        super(request);
    }

    /**
     * @return Current authenticated web session
     */
    public static SSOWebSession get()
    {
        return (SSOWebSession) Session.get();
    }

    public void setClaimsRequest(ClaimsRequest claimsRequest) {
        this.claimsRequest = claimsRequest;
    }

    public ClaimsRequest getClaimsRequest() {
        return claimsRequest;
    }

}
