package com.atricore.idbus.console.liveservices.liveupdate.command;

import com.atricore.idbus.console.liveservices.liveupdate.command.printers.CmdPrinter;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class LiveUpdateCommandSupport extends OsgiCommandSupport {

    @Option(name = "-v", aliases = "--verbose", description = "Print out additional information", required = false, multiValued = false)
    boolean verbose = false;

    private CmdPrinter printer;

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(LiveUpdateManager.class.getName());
        if (ref == null) {
            System.out.println("LiveUpdate Service is unavailable. (no service reference)");
            return null;
        }
        try {
            LiveUpdateManager svc = (LiveUpdateManager) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("LiveUpdate Service service is unavailable. (no service)");
                return null;
            }

            doExecute(svc);

        } catch (LiveUpdateException e) { // Force reference to exception class , do not change
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public CmdPrinter getPrinter() {
        return printer;
    }

    public void setPrinter(CmdPrinter printer) {
        this.printer = printer;
    }

    protected abstract Object doExecute(LiveUpdateManager svc) throws Exception;

}