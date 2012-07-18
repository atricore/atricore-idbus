package com.atricore.idbus.console.brandservice.main.internal.installer;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.domain.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundlePermission;

import java.util.Dictionary;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CustomBrandingInstaller extends OsgiBrandingInstaller {
    
    public boolean canHandle(BrandingDefinition def) {
        return def instanceof CustomBrandingDefinition;
    }


    public BrandingDefinition  install(BrandingDefinition def) throws BrandingServiceException {
        try { 
            CustomBrandingDefinition cd = (CustomBrandingDefinition) def;
            
            String bundleUri = cd.getBundleUri();
            Bundle bundle = getBundleContext().installBundle(bundleUri, null);
            
            // TODO : Validate the bundle ! It should be a fragment HOST for SSO UI!
            
            Dictionary d = bundle.getHeaders();
            String hostBundle = (String) d.get("Fragment-Host");
            if (hostBundle == null)
                throw new BrandingServiceException("The bundle provided for external custom branding must contain a 'Fragment-Host' header");
            
            if (!BrandManager.SSO_UI_BUNDLE.equals(hostBundle))
                throw new BrandingServiceException("The bundle provided for external custom branding must contain a 'Fragment-Host' header for " + BrandManager.SSO_UI_BUNDLE);

            cd.setBundleSymbolicName(bundle.getSymbolicName());

            return cd;

        } catch (Exception e) {
            throw new BrandingServiceException(e);
        }
        
    }

    public BrandingDefinition uninstall(BrandingDefinition def) throws BrandingServiceException {
        try {
            CustomBrandingDefinition cd = (CustomBrandingDefinition) def;

            String bundleName = cd.getBundleSymbolicName();
            // Look up for bundle and stop/remove it
            Bundle[] b = getBundleContext().getBundles();
            for (Bundle bundle : b) {
                if (bundle.getSymbolicName().equals(bundleName)) {
                    bundle.uninstall();
                }
            }

            return def;

        } catch (Exception e) {
            throw new BrandingServiceException(e);
        }
    }
}
