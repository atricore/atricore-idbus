package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "new-external", description = "Create a new Branding definition based on an external OSGi module")
public class CreateCustomBrandingCommand extends BrandingCommandSupport {
    
    @Argument(index = 0, name = "name", description = "The name of the new branding", required = true, multiValued = false)
    String name;
    
    @Option(name = "-d", aliases = "--description", description = "The description of the new branding", required = true, multiValued = false)
    String description;

    @Option(name = "-u", aliases = "--bundle-uri", description = "The bundle containing branding resources", required = true, multiValued = false)
    String bundleUri;

    @Option(name = "--sso-app-class", description = "The Wicket application class to use as SSO UI application", required = false, multiValued = false)
    String customSSOAppClazz;
    
    @Option(name = "--openid-app-class", description = "The Wicket application class to use as OpenID UI application", required = false, multiValued = false)
    String customOpenIDAppClazz;

    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        CustomBrandingDefinition def = new CustomBrandingDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setBundleUri(bundleUri);
        def.setCustomOpenIdAppClazz(customOpenIDAppClazz);
        def.setCustomSsoAppClazz(customSSOAppClazz);
        
        def = (CustomBrandingDefinition) svc.create(def);

        if (getPrinter() != null)
            getPrinter().print(def);

        return def;
    }
}
