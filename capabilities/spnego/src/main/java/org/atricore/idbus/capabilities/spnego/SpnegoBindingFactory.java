package org.atricore.idbus.capabilities.spnego;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class SpnegoBindingFactory extends SamlR2BindingFactory {

    private static final Log logger = LogFactory.getLog(SpnegoBindingFactory.class);

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setApplicationContext(applicationContext);
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
            case SPNEGO_HTTP_INITIATION:
                mb = new SpnegoHttpInitiatorBinding(channel);
                break;
            case SPNEGO_HTTP_NEGOTIATION:
                mb = new SpnegoHttpNegotiatorBinding(channel);
                break;
            default:
        }
        
        if (mb != null && mb instanceof AbstractMediationBinding) {
            Map<String, ConfigurationContext> cfgs  = applicationContext.getBeansOfType(ConfigurationContext.class);
            if (cfgs.size() == 1) {
                ConfigurationContext cfg = cfgs.values().iterator().next();
                ((AbstractMediationBinding)mb).setConfigurationContext(cfg);
            }

            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        } else {
            mb = super.createBinding(binding, channel);
        }

        return mb;
    }
}

