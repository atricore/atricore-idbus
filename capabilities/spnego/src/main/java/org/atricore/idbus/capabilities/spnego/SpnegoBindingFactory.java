package org.atricore.idbus.capabilities.spnego;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.MediationBindingFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class SpnegoBindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpnegoBindingFactory.class);

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {
        
        SpnegoBinding b = null;
        try {
            b = SpnegoBinding.asEnum(binding);
        } catch (IllegalArgumentException e) {
                return null;
        }
        
        
        MediationBinding mb = null;
        switch (b) {
            case SPNEGO_HTTP:
                mb = new SpnegoHttpBinding(channel);
                break;
            default:
                logger.warn("Invalid Spnego Binding " + binding);
        }
        
        if (mb != null && mb instanceof AbstractMediationBinding) {
            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }
        
        return mb;
        
    }
}

