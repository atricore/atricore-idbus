package com.atricore.idbus.console.liveservices.liveupdate.tests.command;

import com.atricore.idbus.console.liveservices.liveupdate.tests.services.spi.DistributableTestService;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class LiveUpdateTestsCommandSupport  extends OsgiCommandSupport {

    @Option(name = "-v", aliases = "--verbose", description = "Print out additional information", required = false, multiValued = false)
    boolean verbose = false;

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(DistributableTestService.class.getName());
        if (ref == null) {
            System.out.println("LiveUpdate Service is unavailable. (no service reference)");
            return null;
        }
        try {
            DistributableTestService svc = (DistributableTestService) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("LiveUpdate Service service is unavailable. (no service)");
                return null;
            }

            doExecute(svc);

        } catch (Exception e) { // Force reference to exception class , do not change
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

    protected abstract Object doExecute(DistributableTestService svc) throws Exception;

}