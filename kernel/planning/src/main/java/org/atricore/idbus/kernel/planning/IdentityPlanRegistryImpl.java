package org.atricore.idbus.kernel.planning;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityPlanRegistryImpl implements IdentityPlanRegistry, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public IdentityPlan lookup(String name) {
        return (IdentityPlan) applicationContext.getBean(name);
    }
}
