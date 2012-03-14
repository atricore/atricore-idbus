package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PersistenceServiceConfiguration implements  ServiceConfiguration {

    private ServiceType serviceType;
    
    private Integer port;
    
    private String username;
    
    private String password;

    public PersistenceServiceConfiguration() {
        this.serviceType = ServiceType.PERSISTENCE;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }
}
