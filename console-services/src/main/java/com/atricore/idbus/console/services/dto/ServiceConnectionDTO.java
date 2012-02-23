package com.atricore.idbus.console.services.dto;

public class ServiceConnectionDTO extends ConnectionDTO {

    private static final long serialVersionUID = 3086610222762604163L;

    private ServiceProviderDTO sp;

    private ServiceResourceDTO resource;

    public ServiceProviderDTO getSp() {
        return sp;
    }

    public void setSp(ServiceProviderDTO sp) {
        this.sp = sp;
    }

    public ServiceResourceDTO getResource() {
        return resource;
    }

    public void setResource(ServiceResourceDTO resource) {
        this.resource = resource;
    }
}
