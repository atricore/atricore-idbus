package com.atricore.idbus.console.licensing.command;

import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.licensing.command.printers.CmdPrinter;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class LicenseCommandSupport extends OsgiCommandSupport {

    CmdPrinter cmdPrinter;
    LicenseManager svc;

    @Override
    protected Object doExecute() throws Exception {
        // Get service reference and all execute again
//        LicenseManager svc = null;

        return doExecute(svc);
    }

    protected abstract Object doExecute(LicenseManager svc) throws Exception ;

    public void setCmdPrinter(CmdPrinter cmdPrinter) {
        this.cmdPrinter = cmdPrinter;
    }

    public void setSvc(LicenseManager svc) {
        this.svc = svc;
    }
}
