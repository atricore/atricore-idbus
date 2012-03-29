package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.dto.settings.ServiceTypeDTO;

public class ServiceConfigurationException extends Exception {

    private ServiceTypeDTO serviceType;

    public ServiceConfigurationException() {
        super();
    }

    public ServiceConfigurationException(String s) {
        super(s);
    }

    public ServiceConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceConfigurationException(Throwable throwable) {
        super(throwable);
    }

    public ServiceConfigurationException(ServiceTypeDTO serviceType, Throwable throwable) {
        super(throwable);
        this.serviceType = serviceType;
    }

    public ServiceTypeDTO getServiceType() {
        return serviceType;
    }
}
