package org.atricore.idbus.capabilities.spmlr2.main.binding;

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
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2BindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpmlR2BindingFactory.class);

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {
        
        SpmlR2Binding b = null;
        try {
            b = SpmlR2Binding.asEnum(binding);
        } catch (IllegalArgumentException e) {
                return null;
        }
        
        
        MediationBinding mb = null;
        switch (b) {
            case SPMLR2_SOAP:
                mb = new SpmlR2SoapBinding(channel);
                break;
            case SPMLR2_LOCAL:
                mb = new SpmlR2LocalBinding(channel);
                break;
            default:
                logger.warn("Unknown SPMLR2 Binding! " + binding);
        }

        if (mb != null && mb instanceof AbstractMediationBinding) {
            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }

        return mb;
        
    }
}

