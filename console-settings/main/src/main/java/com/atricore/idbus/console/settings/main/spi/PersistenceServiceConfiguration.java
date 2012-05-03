package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PersistenceServiceConfiguration implements  ServiceConfiguration {

    private static final long serialVersionUID = 1316383500271946278L;

    private ServiceType serviceType;
    
    private Integer port;
    
    private String username;
    
    private String password;

    private boolean useExternalDB;

    private String connectionUrl;

    private String connectionUsername;

    private String connectionPassword;

    private String connectionDriver;



    public PersistenceServiceConfiguration() {
        this.serviceType = ServiceType.PERSISTENCE;
    }

    public ServiceType getServiceType() {
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
