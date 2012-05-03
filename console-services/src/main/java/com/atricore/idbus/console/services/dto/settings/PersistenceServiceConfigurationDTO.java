package com.atricore.idbus.console.services.dto.settings;

public class PersistenceServiceConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = 3304208642004252650L;

    private ServiceTypeDTO serviceType;

    private Integer port;

    private String username;

    private String password;

    private boolean useExternalDB;

    private String connectionUrl;

    private String connectionUsername;

    private String connectionPassword;

    private String connectionDriver;

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

    public boolean isUseExternalDB() {
        return useExternalDB;
    }

    public void setUseExternalDB(boolean useExternalDB) {
        this.useExternalDB = useExternalDB;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getConnectionUsername() {
        return connectionUsername;
    }

    public void setConnectionUsername(String connectionUsername) {
        this.connectionUsername = connectionUsername;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionDriver() {
        return connectionDriver;
    }

    public void setConnectionDriver(String connectionDriver) {
        this.connectionDriver = connectionDriver;
    }
}
