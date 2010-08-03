package org.atricore.idbus.capabilities.samlr2.management.internal;

import org.atricore.idbus.capabilities.samlr2.management.BindingProviderMBean;
import org.atricore.idbus.kernel.main.mediation.provider.BindingProvider;
import org.atricore.idbus.kernel.main.mediation.provider.LocalProvider;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BindingProviderMBeanImpl extends AbstractProviderMBean implements BindingProviderMBean {

    private BindingProvider bindingProvider;

    @Override
    protected LocalProvider getProvider() {
        return bindingProvider;
    }

    public BindingProvider getBindingProvider() {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider) {
        this.bindingProvider = bindingProvider;
    }

}
