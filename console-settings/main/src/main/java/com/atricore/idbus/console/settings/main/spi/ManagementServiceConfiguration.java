package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ManagementServiceConfiguration implements  ServiceConfiguration {

    private ServiceType serviceType;

    private Integer rmiRegistryPort;

    private Integer rmiServerPort;

    private String serviceUrl;

    public ManagementServiceConfiguration() {
        this.serviceType = ServiceType.MANAGEMENT;
    }

    public ServiceType getServiceType() {
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
