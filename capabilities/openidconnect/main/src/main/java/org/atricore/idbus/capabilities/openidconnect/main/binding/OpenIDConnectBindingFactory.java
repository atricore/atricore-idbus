package org.atricore.idbus.capabilities.openidconnect.main.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.TokenRequestRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.UserInfoRequestRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.op.binding.*;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.binding.OpenIDConnectHttpAuthzBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.OIDCProviderConfigurationRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.OIDCProviderJWKRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.RPCheckSessionIFrameRestfulBinding;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpRedirectBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.MediationBindingFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 */
public class OpenIDConnectBindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(OpenIDConnectBindingFactory.class);

    protected ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {

        OpenIDConnectBinding b = null;
        try {
            b = OpenIDConnectBinding.asEnum(binding);
        } catch (IllegalArgumentException e) {
            return null;
        }


        MediationBinding mb = null;
        switch (b) {
            // TODO: Factor out SSO binding to SSO capability
            case SSO_REDIRECT:
                mb = new SsoHttpRedirectBinding(channel);
                break;
            case SSO_ARTIFACT:
                mb = new SsoHttpArtifactBinding(channel);
                break;
            case OPENID_HTTP_POST:

                // mb = new OpenIDConnectHttpPostBinding(channel);
                break;
            case OPENIDCONNECT_AUTHZ:
                mb = new OpenIDConnectHttpAuthzBinding(channel);
                break;
            case OPENID_PROVIDER_LOGOUT_HTTP:
                mb = new LogoutHttpBinding(channel);
                break;

            case OPENID_PROVIDER_AUTHZ_HTTP:
                mb = new AuthnHttpBinding(channel);
                break;

            case OPENID_PROVIDER_TOKEN_RESTFUL:
                mb = new TokenRequestRestfulBinding(channel);
                break;

            case OPENID_PROVIDER_USERINFO_RESTFUL:
                mb = new UserInfoRequestRestfulBinding(channel);
                break;

            case OPENID_PROVIDER_INFO_RESTFUL:
                mb = new OIDCProviderConfigurationRestfulBinding(channel);
                break;

            case OPENID_PROVIDER_JWK_RESTFUL:
                mb = new OIDCProviderJWKRestfulBinding(channel);
                break;

            case OPENID_PROVIDER_CHKSESSION_IFRAME_RESTFUL:
                mb = new RPCheckSessionIFrameRestfulBinding(channel);
                break;


        }

        if (mb != null && mb instanceof AbstractMediationBinding) {
            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }
        return mb;
    }
}

