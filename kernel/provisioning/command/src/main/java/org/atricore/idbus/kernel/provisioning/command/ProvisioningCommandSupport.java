package org.atricore.idbus.kernel.provisioning.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.provisioning.command.printer.CmdPrinter;
import org.osgi.framework.ServiceReference;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

/**
 * Created by sgonzalez on 11/6/14.
 */
public abstract class ProvisioningCommandSupport extends OsgiCommandSupport {

    @Argument(index = 0, name = "targetUri", description = "Provisionig Service Target URI idm:idau.psp/pst", required = false)
    String targetUri;

    @Option(name = "-v", aliases = "--verbose", description = "Verbose command", required = false, multiValued = false)
    boolean verbose = false;

    protected CmdPrinter cmdPrinter;

    public CmdPrinter getCmdPrinter() {
        return cmdPrinter;
    }

    public void setCmdPrinter(CmdPrinter cmdPrinter) {
        this.cmdPrinter = cmdPrinter;
    }

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(IdentityMediationUnitRegistry.class.getName());
        if (ref == null) {
            cmdPrinter.printMsg("Identity Mediation Unit Registry Service is unavailable. (no service reference)");
            return null;
        }
        try {
            IdentityMediationUnitRegistry svc = (IdentityMediationUnitRegistry) getBundleContext().getService(ref);
            if (svc == null) {
                cmdPrinter.printMsg("Identity Mediation Unit Registry  Service service is unavailable. (no service)");
                return null;
            }

            ProvisioningTargetURI uri = new ProvisioningTargetURI(targetUri);

            IdentityMediationUnit idau = svc.lookupUnit(uri.getIdau());

            if (idau == null) {
                throw new Exception("IdAU not found " + uri.getIdau());
            }

            if (verbose)
                cmdPrinter.printMsg("IdAU " + idau.getName());

            PsPChannel pspChannel = null;
            ProvisioningServiceProvider psp = null;

            for (Channel c : idau.getChannels()) {

                if (c instanceof PsPChannel) {

                    PsPChannel pc = (PsPChannel) c;

                    if (pc.getProvider() != null && pc.getProvider().getName().equals(uri.getProvider())) {
                        pspChannel = pc;
                        psp = pc.getProvider();
                        break;
                    }
                }
            }

            if (pspChannel == null || psp == null) {
                throw new Exception("Provider not found " + uri.getProvider() + " in IDAU " + uri.getIdau());
            }

            if (verbose)
                cmdPrinter.printMsg("Provider " + psp.getName());


            for (ProvisioningTarget pst : psp.getProvisioningTargets()) {
                if (pst.getName().equals(uri.getTarget())) {
                    doExecute(psp, pst);
                    return null;
                }
            }

            cmdPrinter.printErrMsg("Provisioning Target not found for " + uri.getUri().toString());

        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }

    protected abstract void doExecute(ProvisioningServiceProvider psp, ProvisioningTarget pst);


    public class ProvisioningTargetURI {

        private URI uri;

        private String psp;

        private String pst;

        public ProvisioningTargetURI(String uriStr) throws URISyntaxException, MalformedURLException {
            this(new URI(uriStr));
        }

        public ProvisioningTargetURI(URI uri) throws MalformedURLException {
            this.uri = uri;


            if (!uri.getScheme().equals("idm"))
                throw new MalformedURLException("Invalid URI scheme: " + uri.getScheme());

            String path = uri.getRawPath();
            if (path == null)
                throw new MalformedURLException("Invalid URI path: NULL");

            StringTokenizer st = new StringTokenizer(path.trim(), "/");
            while (st.hasMoreElements()) {
                String t = st.nextToken();
                if (psp == null) {
                    psp = t;
                    continue;
                }

                if (pst == null) {
                    pst = t;
                    continue;
                }

                throw new MalformedURLException("Invalid URI path: " + path);
            }


        }

        public URI getUri() {
            return uri;
        }


        public String getIdau() {
            return uri.getRawAuthority();
        }

        public String getProvider() {
            return psp;
        }

        public String getTarget() {
            return pst;
        }
    }



}
