package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityLookup extends Connection {

    private static final long serialVersionUID = 3879493987564134875L;

    private long id;

    private LocalProvider provider;

    private IdentitySource identitySource;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalProvider getProvider() {
        return provider;
    }

    public void setProvider(LocalProvider provider) {
        this.provider = provider;
    }

    public IdentitySource getIdentitySource() {
        return identitySource;
    }

    public void setIdentitySource(IdentitySource identitySource) {
        this.identitySource = identitySource;
    }
}
