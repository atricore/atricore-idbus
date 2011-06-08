package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIntegratedAuthenticationDTO extends AuthenticationServiceDTO {

    private static final long serialVersionUID = -7512383445913433166L;

private String protocol;

    private String domain;

    private String serviceClass;

    private String host;

    private int port;

    private String serviceName;

    private ResourceDTO keyTab;

    private boolean overwriteKerberosSetup;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ResourceDTO getKeyTab() {
        return keyTab;
    }

    public void setKeyTab(ResourceDTO keyTab) {
        this.keyTab = keyTab;
    }

    public boolean isOverwriteKerberosSetup() {
        return overwriteKerberosSetup;
    }

    public void setOverwriteKerberosSetup(boolean overwriteKerberosSetup) {
        this.overwriteKerberosSetup = overwriteKerberosSetup;
    }
}
