package com.atricore.idbus.console.services.dto.settings;

public class LogServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = -6745347326343814857L;

    public static final int MODE_DEV = 0;

    public static final int MODE_PROD = 10;

    private ServiceTypeDTO serviceType;

    private Integer serviceMode;

    public LogServiceConfigurationDTO() {
        this.serviceType = ServiceTypeDTO.LOG;
    }

    public ServiceTypeDTO getServiceType() {
        return serviceType;
    }

    public Integer getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(Integer serviceMode) {
        this.serviceMode = serviceMode;
    }
}
