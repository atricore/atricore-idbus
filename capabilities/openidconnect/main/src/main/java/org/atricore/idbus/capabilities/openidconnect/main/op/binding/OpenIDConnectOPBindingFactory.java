package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
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
public class OpenIDConnectOPBindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(OpenIDConnectOPBindingFactory.class);

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
            default:
        }

        if (mb != null && mb instanceof AbstractMediationBinding) {
            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }
        return mb;
    }
}
