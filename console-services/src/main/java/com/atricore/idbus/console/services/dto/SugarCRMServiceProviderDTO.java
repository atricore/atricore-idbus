package com.atricore.idbus.console.services.dto;

public class SugarCRMServiceProviderDTO extends ExternalSaml2ServiceProviderDTO {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
