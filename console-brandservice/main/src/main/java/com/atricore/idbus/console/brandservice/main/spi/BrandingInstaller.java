package com.atricore.idbus.console.brandservice.main.spi;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BrandingInstaller extends BundleContextAware {
    
    boolean canHandle(BrandingDefinition def);

    BrandingDefinition install(BrandingDefinition def) throws BrandingServiceException;

    BrandingDefinition uninstall(BrandingDefinition def) throws BrandingServiceException;


}
