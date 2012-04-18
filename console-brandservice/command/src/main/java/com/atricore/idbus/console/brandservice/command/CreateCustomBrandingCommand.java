package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.domain.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    @Option(name = "-r", aliases = "--resource-url", description = "The URL pointing to the bundle containing branding resources", required = false, multiValued = false)
    String resourceUrl;

    @Option(name = "--sso-app-class", description = "The Wicket application class to use as SSO UI application", required = false, multiValued = false)
    String customSSOAppClazz;

    @Option(name = "--openid-app-class", description = "The Wicket application class to use as OpenID UI application", required = false, multiValued = false)
    String customOpenIDAppClazz;

    @Option(name = "-i", aliases = "--id", description = "The ID of the definition, used by the runtime", required = false, multiValued = false)
    String webBrandingId;

    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        CustomBrandingDefinition def = new CustomBrandingDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setBundleUri(bundleUri);
        def.setCustomOpenIdAppClazz(customOpenIDAppClazz);
        def.setCustomSsoAppClazz(customSSOAppClazz);
        def.setWebBrandingId(webBrandingId != null ? webBrandingId : name);

        if (resourceUrl != null) {
            // Load resource!
            URL resource = new URL(resourceUrl);
            InputStream is = null;
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            try {
                is = resource.openStream();
                byte[] byteChunk = new byte[4096];
                int n;
                while ((n = is.read(byteChunk)) > 0) {
                    bais.write(byteChunk, 0, n);
                }
                def.setResource(bais.toByteArray());

            } catch  (IOException e) {
                getPrinter().printError(e);
                return null;
            } finally {
                if (is != null) try { is.close();} catch (IOException e) {/* ignore it*/}
            }
        }

        def = (CustomBrandingDefinition) svc.create(def);

        if (getPrinter() != null)
            getPrinter().print(def);

        return def;
    }
}
