package com.atricore.idbus.console.brandservice.main.internal.installer;

import com.atricore.idbus.console.brandservice.main.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.CustomBrandingDefinition;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

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
            cd.setBundleSymbolicName(bundle.getSymbolicName());

            return cd;

        } catch (Exception e) {
            throw new BrandingServiceException(e);
        }
        
    }


}
