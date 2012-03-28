package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "delete", description = "Delte a Branding definition")
public class DeleteBrandingCommand extends BrandingCommandSupport {

    @Argument(index = 0, name = "id", description = "The id of the branding", required = true, multiValued = false)
    String id;


    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        svc.delete(Long.parseLong(id));
        return null;
    }
}
