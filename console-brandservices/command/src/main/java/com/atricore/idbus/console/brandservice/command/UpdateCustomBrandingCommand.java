package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "update-external", description = "Create a new Branding definition based on an external OSGi module")
public class UpdateCustomBrandingCommand extends BrandingCommandSupport {
    @Argument(index = 0, name = "id", description = "The id of the new branding", required = true, multiValued = false)
    String id;

    @Option(name = "-n", aliases = "--name", description = "The name of the new branding", required = false, multiValued = false)
    String name;
    
    @Option(name = "-d", aliases = "--description", description = "The description of the new branding", required = false, multiValued = false)
    String description;

    @Option(name = "-u", aliases = "--bundle-uri", description = "The bundle containing branding resources", required = false, multiValued = false)
    String bundleUri;

    @Option(name = "--sso-app-class", description = "The Wicket application class to use as SSO UI application", required = false, multiValued = false)
    String customSSOAppClazz;
    
    @Option(name = "--openid-app-class", description = "The Wicket application class to use as OpenID UI application", required = false, multiValued = false)
    String customOpenIDAppClazz;

    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        
        CustomBrandingDefinition def = (CustomBrandingDefinition) svc.lookup(Long.parseLong(id));
        
        if (name != null)
            def.setName(name);
        
        if (description != null)
            def.setDescription(description);
        
        if (bundleUri != null)
            def.setBundleUri(bundleUri);
        
        if (customOpenIDAppClazz != null)
            def.setCustomOpenIdAppClazz(customOpenIDAppClazz);
        
        if (customSSOAppClazz != null)
            def.setCustomSsoAppClazz(customSSOAppClazz);
        
        def = (CustomBrandingDefinition) svc.update(def);

        if (getPrinter() != null)
            getPrinter().print(def);

        return def;
        
    }    
}
