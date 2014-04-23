package org.atricore.idbus.kernel.main.mediation.provider;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BindingProviderImpl extends AbstractFederatedLocalProvider implements BindingProvider {

    // Optional, some binding providers act as proxies for remote providers
    private FederatedRemoteProvider proxy;

    public FederatedRemoteProvider getProxy() {
        return proxy;
    }

    public void setProxy(FederatedRemoteProvider proxy) {
        this.proxy = proxy;
    }
}
