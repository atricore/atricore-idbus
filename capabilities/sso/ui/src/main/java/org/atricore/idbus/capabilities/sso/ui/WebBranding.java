package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.util.io.IClusterable;
import org.atricore.idbus.capabilities.sso.ui.spi.IPageHeaderContributor;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBranding implements IClusterable, BundleContextAware {


    private String id;

    private String skin;

    private String bundleId;

    private String bundleContextId;
    
    private Properties properties = new Properties();
    
    private String defaultLocale;
    
    private String fallbackUrl;

    private Set<String> allowedResourcePatterns;

    private transient BundleContext bundleContext;
    
    // List of bundle symbolic names that may contain web resources:
    private List<ResourcesBundle> resourceBundles;

    // Be careful, the order DOES matter here!
    private List<BrandingResource> resources = new ArrayList<BrandingResource>();

    private List<IPageHeaderContributor> pageHeaderContributors = new ArrayList<IPageHeaderContributor>();

    private String ssoAppClazz;

    private String ssoIdPAppClazz;

    // TODO : Support other application customization properties

    public List<IPageHeaderContributor> getPageHeaderContributors() {
        return pageHeaderContributors;
    }

    public void setPageHeaderContributors(List<IPageHeaderContributor> pageHeaderContributors) {
        this.pageHeaderContributors = pageHeaderContributors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public List<BrandingResource> getResources() {
        return resources;
    }

    public void setResources(List<BrandingResource> resources) {
        this.resources = resources;
    }

    public String getBundleContextId() {
        return bundleContextId;
    }

    public void setBundleContextId(String bundleContextId) {
        this.bundleContextId = bundleContextId;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContextId = bundleContext.getBundle().getSymbolicName();
        this.bundleContext = bundleContext;
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

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getFallbackUrl() {
        return fallbackUrl;
    }

    public void setFallbackUrl(String fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
    }

    public List<ResourcesBundle> getResourceBundles() {
        return resourceBundles;
    }

    public void setResourceBundles(List<ResourcesBundle> resourceBundles) {
        this.resourceBundles = resourceBundles;
    }

    public Set<String> getAllowedResourcePatterns() {
        return allowedResourcePatterns;
    }

    public void setAllowedResourcePatterns(Set<String> allowedResourcesPatterns) {
        this.allowedResourcePatterns = allowedResourcesPatterns;
    }

    public String getSsoAppClazz() {
        return ssoAppClazz;
    }

    public void setSsoAppClazz(String ssoAppClazz) {
        this.ssoAppClazz = ssoAppClazz;
    }

    public String getSsoIdPAppClazz() {
        return ssoIdPAppClazz;
    }

    public void setSsoIdPAppClazz(String ssoIdPAppClazz) {
        this.ssoIdPAppClazz = ssoIdPAppClazz;
    }

    public class ResourcesBundle {
        
        private String bundleSymbolicName;
        
        private String[] packages;

        public String getBundleSymbolicName() {
            return bundleSymbolicName;
        }

        public void setBundleSymbolicName(String bundleSymbolicName) {
            this.bundleSymbolicName = bundleSymbolicName;
        }

        public String[] getPackages() {
            return packages;
        }

        public void setPackages(String[] packages) {
            this.packages = packages;
        }
    }
}
