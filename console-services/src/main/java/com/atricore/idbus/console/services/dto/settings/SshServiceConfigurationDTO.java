package com.atricore.idbus.console.services.dto.settings;

public class SshServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = 2011312717717262946L;

    private ServiceTypeDTO serviceType;

    private Integer port;

    private String bindAddress;

    public SshServiceConfigurationDTO() {
        this.serviceType = ServiceTypeDTO.SSH;
    }

    public ServiceTypeDTO getServiceType() {
        return serviceType;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
}
