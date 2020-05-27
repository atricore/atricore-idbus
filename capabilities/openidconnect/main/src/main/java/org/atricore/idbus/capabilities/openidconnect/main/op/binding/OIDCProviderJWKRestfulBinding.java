package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.SerializeException;
import net.minidev.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.ByteArrayInputStream;

public class OIDCProviderJWKRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(OIDCProviderJWKRestfulBinding.class);

    public OIDCProviderJWKRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_JWK_RESTFUL.getValue(), channel);
    }

}
