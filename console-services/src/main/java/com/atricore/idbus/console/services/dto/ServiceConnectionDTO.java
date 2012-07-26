package com.atricore.idbus.console.services.dto;

public class ServiceConnectionDTO extends ConnectionDTO {

    private static final long serialVersionUID = 3086610222762604163L;

    private InternalSaml2ServiceProviderDTO sp;

    private ServiceResourceDTO resource;

    public InternalSaml2ServiceProviderDTO getSp() {
        return sp;
    }

    public void setSp(InternalSaml2ServiceProviderDTO sp) {
        this.sp = sp;
    }

    public ServiceResourceDTO getResource() {
        return resource;
    }

    public void setResource(ServiceResourceDTO resource) {
        this.resource = resource;
    }
}
