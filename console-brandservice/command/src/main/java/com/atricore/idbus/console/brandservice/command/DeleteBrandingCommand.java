package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "delete", description = "Delte a Branding definition")
public class DeleteBrandingCommand extends BrandingCommandSupport {
    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
