package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.util.io.IClusterable;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebAppConfig implements IClusterable {

    private String appName;

    private String brandingId;

    private String mountPoint;

    // Optional, used for IdP based UI Applications
    private String idpName;

    private String selfServicesSpName;

    private Properties properties = new Properties();

    // Identity Appliance Unit name
    private String unitName;

    private String selfServicesSharedSecret;

    // HTTP Headers to be added to responses

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBrandingId() {
        return brandingId;
    }

    public void setBrandingId(String brandingId) {
        this.brandingId = brandingId;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getIdpName() {
        return idpName;
    }

    public void setIdpName(String idpName) {
        this.idpName = idpName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSelfServicesSpName() {
        return selfServicesSpName;
    }

    public void setSelfServicesSpName(String selfServicesSpName) {
        this.selfServicesSpName = selfServicesSpName;
    }

    public String getSelfServicesSharedSecret() {
        return selfServicesSharedSecret;
    }

    public void setSelfServicesSharedSecret(String selfServicesSharedSecret) {
        this.selfServicesSharedSecret = selfServicesSharedSecret;
    }
}
