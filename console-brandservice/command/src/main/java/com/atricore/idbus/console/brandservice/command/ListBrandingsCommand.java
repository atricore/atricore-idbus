package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.main.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "brand", name = "list", description = "List all configured brandings")
public class ListBrandingsCommand extends  BrandingCommandSupport {


    @Override
    protected Object doExecute(BrandManager svc) throws Exception {
        Collection<BrandingDefinition> brandings = svc.list();
        getPrinter().printAll(brandings);
        return null;
    }
}
