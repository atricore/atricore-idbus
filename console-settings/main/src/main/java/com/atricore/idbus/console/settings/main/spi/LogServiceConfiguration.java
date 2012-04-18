package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LogServiceConfiguration implements  ServiceConfiguration {

    private static final long serialVersionUID = 5162885934632001564L;

    public static final int MODE_DEV = 0;

    public static final int MODE_PROD = 10;

    public static final int MODE_CUSTOM = 20;

    private ServiceType serviceType;

    private Integer serviceMode;

    public LogServiceConfiguration() {
        this.serviceType = ServiceType.LOG;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Integer getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(Integer serviceMode) {
        this.serviceMode = serviceMode;
    }
}
