package org.atricore.idbus.kernel.common.support.jdbc;

import org.atricore.idbus.kernel.common.support.osgi.ExternalResourcesClassLoader;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class DriverDescriptor {

    private String name;

    private String description;

    private String driverclassName;

    private String url;

    private String webSiteUrl;

    private String location;

    private boolean usesClassPath;

    private List<String> jarFileNames;

    private ExternalResourcesClassLoader driverLoader;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDriverclassName() {
        return driverclassName;
    }

    public void setDriverclassName(String driverclassName) {
        this.driverclassName = driverclassName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ExternalResourcesClassLoader getDriverLoader() {
        return driverLoader;
    }

    public void setDriverLoader(ExternalResourcesClassLoader driverLoader) {
        this.driverLoader = driverLoader;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public boolean isUsesClassPath() {
        return usesClassPath;
    }

    public void setUsesClassPath(boolean usesClassPath) {
        this.usesClassPath = usesClassPath;
    }

    public List<String> getJarFileNames() {
        return jarFileNames;
    }

    public void setJarFileNames(List<String> jarFileNames) {
        this.jarFileNames = jarFileNames;
    }
}
