package com.atricore.idbus.console.brandservice.main.internal.installer;

import com.atricore.idbus.console.brandservice.main.spi.BrandingInstaller;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class OsgiBrandingInstaller implements BrandingInstaller, BundleContextAware {
    
    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
    
    protected Bundle resolveBundle(String symbolicName) {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle.getSymbolicName().equals(symbolicName))
                return bundle;
        }
        return null;
    }
}
