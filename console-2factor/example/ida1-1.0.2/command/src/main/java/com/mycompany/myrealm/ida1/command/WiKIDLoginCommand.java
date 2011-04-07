package com.mycompany.myrealm.ida1.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;
import org.osgi.framework.ServiceReference;

import javax.security.auth.Subject;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WiKIDLoginCommand extends OsgiCommandSupport {

    @Argument(index = 0, name = "username", description = "Username", required = true, multiValued = false)
    String username;

    @Argument(index = 1, name = "passcode", description = "Passcode", required = true, multiValued = false)
    String passcode;


    @Override
    protected Object doExecute() throws Exception {
        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(Authenticator.class.getName());
        if (ref == null) {
            System.out.println("Authenticator Service is unavailable. (no service reference)");
            return null;
        }
        try {
            Authenticator svc = (Authenticator) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Authenticator Service service is unavailable. (no service)");
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

    public void doExecute(Authenticator svc) throws Exception {

        Credential[] credentials = new Credential[2];

        credentials[0] = svc.newCredential("2factor-authentication", "username", username);
        credentials[1] = svc.newCredential("2factor-authentication", "passcode", passcode);

        Subject s = svc.check(credentials, "2factor-authentication");

        System.out.println(s);
    }


}
