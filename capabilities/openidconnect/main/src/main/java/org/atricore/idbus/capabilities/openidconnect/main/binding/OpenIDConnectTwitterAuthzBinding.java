package org.atricore.idbus.capabilities.openidconnect.main.binding;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;

public class OpenIDConnectTwitterAuthzBinding extends OpenIDConnectHttpAuthzBinding {

    public OpenIDConnectTwitterAuthzBinding(Channel channel) {
        super(channel);
    }

    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {
        return null;
    }
}
