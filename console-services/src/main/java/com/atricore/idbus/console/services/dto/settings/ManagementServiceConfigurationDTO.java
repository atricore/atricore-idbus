package com.atricore.idbus.console.services.dto.settings;

public class ManagementServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = -2117166017313730779L;

    private ServiceTypeDTO serviceType;

    private Integer rmiRegistryPort;

    private Integer rmiServerPort;

    private String serviceUrl;

    public ManagementServiceConfigurationDTO() {
        this.serviceType = ServiceTypeDTO.MANAGEMENT;
    }

    public ServiceTypeDTO getServiceType() {
        return serviceType;
    }

    public Integer getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    public void setRmiRegistryPort(Integer rmiRegistryPort) {
        this.rmiRegistryPort = rmiRegistryPort;
    }

    public Integer getRmiServerPort() {
        return rmiServerPort;
    }

    public void setRmiServerPort(Integer rmiServerPort) {
        this.rmiServerPort = rmiServerPort;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
