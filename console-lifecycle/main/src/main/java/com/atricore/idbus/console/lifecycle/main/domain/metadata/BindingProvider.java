package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Deprecated
public class BindingProvider extends LocalProvider {

    private Channel bindingChannel;
    private static final long serialVersionUID = 3973974337910746241L;

    public Channel getBindingChannel() {
        return bindingChannel;
    }

    public void setBindingChannel(Channel bindingChannel) {
        this.bindingChannel = bindingChannel;
    }

    @Override
    public ProviderRole getRole() {
        return ProviderRole.Binding;
    }

    @Override
    public void setRole(ProviderRole role) {
        throw new UnsupportedOperationException("Cannot change provider role");
    }
}
