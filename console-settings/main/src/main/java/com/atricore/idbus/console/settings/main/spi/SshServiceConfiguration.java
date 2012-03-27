package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SshServiceConfiguration implements  ServiceConfiguration {

    private ServiceType serviceType;

    private int port;

    private String[] bindAddresses;

    public SshServiceConfiguration() {
        this.serviceType = ServiceType.SSH;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getBindAddresses() {
        return bindAddresses;
    }

    public void setBindAddresses(String[] bindAddresses) {
        this.bindAddresses = bindAddresses;
    }
}
