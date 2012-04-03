package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SystemConfiguration implements ServiceConfiguration {

    private String nodeId;

    private Boolean haEnabled;

    public ServiceType getServiceType() {
        return ServiceType.SYSTEM;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Boolean isHaEnabled() {
        return haEnabled;
    }

    public void setHaEnabled(Boolean haEnabled) {
        this.haEnabled = haEnabled;
    }
}
