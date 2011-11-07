package org.atricore.idbus.capabilities.oauth2.main.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.MediationBindingFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2BindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(OAuth2BindingFactory.class);

    protected ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {
        OAuth2Binding b = null;
        try {
            b = OAuth2Binding.asEnum(binding);
        } catch (IllegalArgumentException e) {
                return null;
        }


        MediationBinding mb = null;
        switch (b) {
            case OAUTH2_SOAP:
                mb = new OAuth2SoapBinding(channel);
                break;

            case OAUTH2_RESTFUL:
                mb = new OAuth2RestfulBinding(channel);
                break;

            default:
                break;
        }

        return mb;

    }

}
