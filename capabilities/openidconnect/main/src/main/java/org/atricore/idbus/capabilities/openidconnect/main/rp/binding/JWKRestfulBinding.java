package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDRestfulBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;

public class JWKRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(JWKRestfulBinding.class);

    public JWKRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_JWK_RESTFUL.getValue(), channel);
    }

}
