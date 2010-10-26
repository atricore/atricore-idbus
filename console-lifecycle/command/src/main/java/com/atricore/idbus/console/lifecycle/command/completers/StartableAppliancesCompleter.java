package com.atricore.idbus.console.lifecycle.command.completers;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import org.apache.karaf.shell.console.completer.StringsCompleter;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class StartableAppliancesCompleter extends OsgiCompleterSupport {

    @Override
    protected int complete(IdentityApplianceManagementService applianceMgrService, final String buffer, final int cursor, final List candidates) {

        StringsCompleter delegate = new StringsCompleter();

        /*

        TODO : Implement me
        try {

        } catch (IdentityServerException e) {
            // Ignore
        }
        */

        return delegate.complete(buffer, cursor, candidates);
    }

}

