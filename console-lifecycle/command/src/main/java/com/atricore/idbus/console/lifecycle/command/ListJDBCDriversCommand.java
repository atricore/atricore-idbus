package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ListAvailableJDBCDriversRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ListAvailableJDBCDriversResponse;
import org.apache.felix.gogo.commands.Command;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "jdbc", name = "list", description = "List defined JDBC Drivers")
public class ListJDBCDriversCommand extends ManagementCommandSupport {

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {
        ListAvailableJDBCDriversRequest request = new ListAvailableJDBCDriversRequest();
        ListAvailableJDBCDriversResponse response = svc.listAvailableJDBCDrivers(request);

        getPrinter().printAll(response.getDrivers());

        return null;
    }
}
