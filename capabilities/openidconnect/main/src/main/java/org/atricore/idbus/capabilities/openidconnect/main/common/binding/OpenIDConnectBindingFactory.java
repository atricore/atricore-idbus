package org.atricore.idbus.capabilities.openidconnect.main.common.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.binding.AuthnHttpBinding;
import org.atricore.idbus.capabilities.openidconnect.main.op.binding.LogoutHttpBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.OPStateRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.ProviderConfigurationRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.JWKRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.rp.binding.CheckSessionIFrameRestfulBinding;
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

            case SSO_REDIRECT:
                mb = new SsoHttpRedirectBinding(channel);
                break;
            case SSO_ARTIFACT:
                mb = new SsoHttpArtifactBinding(channel);
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
            case OPENID_PROVIDER_CHKSESSION_IFRAME_RESTFUL:
                mb = new CheckSessionIFrameRestfulBinding(channel);
                break;
            case OPENID_PROVIDER_STATE_RESTFUL:
                mb = new OPStateRestfulBinding(channel);
                break;
            case OPENID_PROVIDER_JWK_RESTFUL:
                mb = new JWKRestfulBinding(channel);
                break;
            case OPENID_PROVIDER_INFO_RESTFUL:
                mb = new ProviderConfigurationRestfulBinding(channel);
                break;
            case OPENID_PROVIDER_LOGOUT_HTTP:
                mb = new LogoutHttpBinding(channel);
                break;

            default:
        }

        if (mb != null && mb instanceof AbstractMediationBinding) {
            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }
        return mb;
    }
}