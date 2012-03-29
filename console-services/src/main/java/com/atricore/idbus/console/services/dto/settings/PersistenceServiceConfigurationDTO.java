package com.atricore.idbus.console.services.dto.settings;

public class PersistenceServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = 3304208642004252650L;

    private ServiceTypeDTO serviceType;

    private Integer port;

    private String username;

    private String password;

    public PersistenceServiceConfigurationDTO() {
        this.serviceType = ServiceTypeDTO.PERSISTENCE;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
