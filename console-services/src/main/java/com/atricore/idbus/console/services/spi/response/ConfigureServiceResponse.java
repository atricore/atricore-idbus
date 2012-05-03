package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.settings.ServiceTypeDTO;

public class ConfigureServiceResponse {

    private ServiceTypeDTO serviceType;

    private boolean restart;

    public ConfigureServiceResponse() {
    }

    public ConfigureServiceResponse(ServiceTypeDTO serviceType, boolean restart) {
        this.serviceType = serviceType;
        this.restart = restart;
    }

    public ServiceTypeDTO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeDTO serviceType) {
        this.serviceType = serviceType;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
