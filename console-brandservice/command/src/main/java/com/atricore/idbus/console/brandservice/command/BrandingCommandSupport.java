package com.atricore.idbus.console.brandservice.command;

import com.atricore.idbus.console.brandservice.command.printers.CmdPrinter;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class BrandingCommandSupport extends OsgiCommandSupport {

    @Option(name = "-v", aliases = "--verbose", description = "Print out additional information", required = false, multiValued = false)
    boolean verbose = false;

    protected CmdPrinter cmdPrinter;

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(BrandManager.class.getName());
        if (ref == null) {
            System.out.println("Identity Appliance Management Service is unavailable. (no service reference)");
            return null;
        }
        try {
            BrandManager svc = (BrandManager) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Identity Appliance Management Service service is unavailable. (no service)");
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

    protected abstract Object doExecute(BrandManager svc) throws Exception;

    public CmdPrinter getPrinter() {
        return cmdPrinter;
    }

    public void setPrinter(CmdPrinter cmdPrinter) {
        this.cmdPrinter = cmdPrinter;
    }

    protected byte[] loadFromUrl(String url) throws MalformedURLException {
        URL resource = new URL(url);
        InputStream is = null;
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        try {
            is = resource.openStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                bais.write(byteChunk, 0, n);
            }
            return bais.toByteArray();

        } catch  (IOException e) {
            getPrinter().printError(e);
            return null;
        } finally {
            if (is != null) try { is.close();} catch (IOException e) {/* ignore it*/}
        }

    }
}
