package com.atricore.idbus.console.services.dto.settings;

public class ArtifactQueueManagerConfigurationDTO implements ServiceConfigurationDTO {

    private static final long serialVersionUID = -801322609129487599L;

    private String brokerName;

    private String brokerHost;

    private String brokerBindAddress;

    private Integer brokerPort;

    public ServiceTypeDTO getServiceType() {
        return ServiceTypeDTO.AQM;
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
