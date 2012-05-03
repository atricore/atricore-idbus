package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactQueueManagerConfiguration  implements ServiceConfiguration{

    private String brokerName;

    private String brokerHost;

    private String brokerBindAddress;

    private Integer brokerPort;

    public ServiceType getServiceType() {
        return ServiceType.AQM;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public String getBrokerBindAddress() {
        return brokerBindAddress;
    }

    public void setBrokerBindAddress(String brokerBindAddress) {
        this.brokerBindAddress = brokerBindAddress;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }
}
