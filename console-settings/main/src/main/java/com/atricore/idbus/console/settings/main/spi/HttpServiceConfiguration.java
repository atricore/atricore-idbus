package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HttpServiceConfiguration implements  ServiceConfiguration {

    private static final long serialVersionUID = 763557007790413922L;

    private ServiceType serviceType;
    
    private String serverId;
    
    private Integer port;
    
    private String[] bindAddresses;
    
    private Integer sessionTimeout;
    
    private Integer maxHeaderBufferSize;
    
    private Boolean disableSessionUrl;
    
    private Boolean enableSsl;
    
    private Integer sslPort;
    
    private String sslKeystorePath;
    
    private String sslKeystorePassword;
    
    private String sslKeyPassword;

    private boolean followRedirects;

    private String includeFollowUrls;

    private String excludeFollowUrls;

    public HttpServiceConfiguration() {
        this.serviceType = ServiceType.HTTP;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String[] getBindAddresses() {
        return bindAddresses;
    }

    public void setBindAddresses(String[] bindAddresses) {
        this.bindAddresses = bindAddresses;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getMaxHeaderBufferSize() {
        return maxHeaderBufferSize;
    }

    public void setMaxHeaderBufferSize(Integer maxHeaderBufferSize) {
        this.maxHeaderBufferSize = maxHeaderBufferSize;
    }

    public Boolean isDisableSessionUrl() {
        return disableSessionUrl;
    }

    public void setDisableSessionUrl(Boolean disableSessionUrl) {
        this.disableSessionUrl = disableSessionUrl;
    }

    public Boolean isEnableSsl() {
        return enableSsl;
    }

    public Boolean getEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(Boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public Integer getSslPort() {
        return sslPort;
    }

    public void setSslPort(Integer sslPort) {
        this.sslPort = sslPort;
    }

    public String getSslKeystorePath() {
        return sslKeystorePath;
    }

    public void setSslKeystorePath(String sslKeystorePath) {
        this.sslKeystorePath = sslKeystorePath;
    }

    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public String getSslKeyPassword() {
        return sslKeyPassword;
    }

    public void setSslKeyPassword(String sslKeyPassword) {
        this.sslKeyPassword = sslKeyPassword;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String getIncludeFollowUrls() {
        return includeFollowUrls;
    }

    public void setIncludeFollowUrls(String includeFollowUrls) {
        this.includeFollowUrls = includeFollowUrls;
    }

    public String getExcludeFollowUrls() {
        return excludeFollowUrls;
    }

    public void setExcludeFollowUrls(String excludeFollowUrls) {
        this.excludeFollowUrls = excludeFollowUrls;
    }
}
