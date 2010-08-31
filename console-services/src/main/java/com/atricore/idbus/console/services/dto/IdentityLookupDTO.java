package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class IdentityLookupDTO extends ConnectionDTO {
    private static final long serialVersionUID = 3879493987564134875L;

    private ProviderDTO provider;

    private IdentitySourceDTO identitySource;

    public ProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }

    public IdentitySourceDTO getIdentitySource() {
        return identitySource;
    }

    public void setIdentitySource(IdentitySourceDTO identitySource) {
        this.identitySource = identitySource;
    }
}
