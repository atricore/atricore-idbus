package com.atricore.idbus.console.settings.main.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LogServiceConfiguration implements  ServiceConfiguration {

    private static final long serialVersionUID = 5162885934632001564L;


    public static final int MODE_DEVELOP = 0;

    public static final int MODE_DEBUG = 5;

    public static final int MODE_PROD = 10;

    public static final int MODE_CUSTOM = 20;

    private ServiceType serviceType;

    private Integer serviceMode;

    private List<LogConfigProperty> configProperties;

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

    public List<LogConfigProperty> getConfigProperties() {
        if (configProperties == null) {
            configProperties = new ArrayList<LogConfigProperty>();
        }
        return configProperties;
    }

    public void setConfigProperties(List<LogConfigProperty> configProperties) {
        this.configProperties = configProperties;
    }
}
