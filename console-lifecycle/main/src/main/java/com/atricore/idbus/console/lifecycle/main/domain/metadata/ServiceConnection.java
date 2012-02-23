package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class ServiceConnection extends Connection {

    private static final long serialVersionUID = 3331662637388944594L;

    private ServiceProvider sp;

    private ServiceResource resource;

    public ServiceProvider getSp() {
        return sp;
    }

    public void setSp(ServiceProvider sp) {
        this.sp = sp;
    }

    public ServiceResource getResource() {
        return resource;
    }

    public void setResource(ServiceResource resource) {
        this.resource = resource;
    }
}
