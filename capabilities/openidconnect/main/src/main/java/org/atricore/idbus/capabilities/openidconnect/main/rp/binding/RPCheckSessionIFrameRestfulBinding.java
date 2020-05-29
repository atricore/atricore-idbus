package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDHttpBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.UserInfoRequestRestfulBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

public class RPCheckSessionIFrameRestfulBinding extends AbstractOpenIDHttpBinding {

    private static final Log logger = LogFactory.getLog(UserInfoRequestRestfulBinding.class);

    public RPCheckSessionIFrameRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_CHKSESSION_IFRAME_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        return null;
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage openIdConnectOut, Exchange exchange) {
        super.copyMessageToExchange(openIdConnectOut, exchange);
    }
}
