package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "publish", description = "Publish all installed branding defintions in the runtime")
public class PublishBrandingsCommand extends BrandingCommandSupport {

    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        svc.publish();
        return null;
    }
}
