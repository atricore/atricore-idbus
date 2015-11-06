package org.atricore.idbus.capabilities.openidconnect.main.binding;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;

/**
 * Created by sgonzalez on 2/24/15.
 */
public class OpenIDConnectFBAuthzBinding extends OpenIDConnectHttpAuthzBinding {

    public OpenIDConnectFBAuthzBinding(Channel channel) {
        super(channel);
    }

    @Override
    public Object sendMessage(MediationMessage message) throws IdentityMediationException {
        return null;
    }
}
