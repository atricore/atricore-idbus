package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SshServiceConfiguration implements  ServiceConfiguration {

    private static final long serialVersionUID = -3802594907694348402L;

    private ServiceType serviceType;

    private Integer port;

    private String bindAddress;

    public SshServiceConfiguration() {
        this.serviceType = ServiceType.SSH;
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

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
}
