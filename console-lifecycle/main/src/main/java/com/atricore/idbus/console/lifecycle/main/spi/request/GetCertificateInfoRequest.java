package com.atricore.idbus.console.lifecycle.main.spi.request;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.SamlR2ProviderConfig;

public class GetCertificateInfoRequest extends AbstractManagementRequest {

    private SamlR2ProviderConfig config;

    public SamlR2ProviderConfig getConfig() {
        return config;
    }

    public void setConfig(SamlR2ProviderConfig config) {
        this.config = config;
    }
}
