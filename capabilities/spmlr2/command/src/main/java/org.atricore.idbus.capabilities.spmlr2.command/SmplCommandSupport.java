package org.atricore.idbus.capabilities.spmlr2.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.osgi.framework.ServiceReference;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class SmplCommandSupport extends OsgiCommandSupport {

    @Argument(index = 0, name = "idauId", description = "The id if the identity appliance", required = true, multiValued = false)
    String idauId;

    @Argument(index = 1, name = "pspId", description = "The id if the Provisioning Service Provider", required = true, multiValued = false)
    String pspId;

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(IdentityMediationUnitRegistry.class.getName());
        if (ref == null) {
            System.out.println("Identity Mediation Unit Registry Service is unavailable. (no service reference)");
            return null;
        }
        try {
            IdentityMediationUnitRegistry svc = (IdentityMediationUnitRegistry) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Identity Mediation Unit Registry  Service service is unavailable. (no service)");
                return null;
            }

            IdentityMediationUnit idau = svc.lookupUnit(idauId);

            if (idau == null) {
                throw new Exception ("Identity Appliance Unit not found " + idauId);
            }

            PsPChannel pspChannel = null;
            ProvisioningServiceProvider psp = null;

            for (Channel c: idau.getChannels()) {
                if (c instanceof PsPChannel) {
                    PsPChannel pc = (PsPChannel) c;

                    if (pc.getProvider() != null && pc.getProvider().getName().equals(pspId)) {
                        pspChannel = pc;
                        psp = pc.getProvider();
                    }
                }
            }

            if (pspChannel == null || psp == null) {
                throw new Exception("Provisioning Service Provider not found " + pspId);
            }

            doExecute(psp, pspChannel);

        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }

    public String getIdauId() {
        return idauId;
    }

    public void setIdauId(String idauId) {
        this.idauId = idauId;
    }

    public String getPspId() {
        return pspId;
    }

    public void setPspId(String pspId) {
        this.pspId = pspId;
    }

    protected abstract Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception;
}
