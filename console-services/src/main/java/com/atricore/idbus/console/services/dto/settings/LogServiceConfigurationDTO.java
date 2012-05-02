package com.atricore.idbus.console.services.dto.settings;

import java.util.ArrayList;
import java.util.List;

public class LogServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = -6745347326343814857L;

    public static final int MODE_DEV = 0;

    public static final int MODE_PROD = 10;

    public static final int MODE_CUSTOM = 20;

    private ServiceTypeDTO serviceType;

    private Integer serviceMode;

    private List<LogConfigPropertyDTO> configProperties;

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

    public List<LogConfigPropertyDTO> getConfigProperties() {
        if (configProperties == null) {
            configProperties = new ArrayList<LogConfigPropertyDTO>();
        }
        return configProperties;
    }

    public void setConfigProperties(List<LogConfigPropertyDTO> configProperties) {
        this.configProperties = configProperties;
    }
}
