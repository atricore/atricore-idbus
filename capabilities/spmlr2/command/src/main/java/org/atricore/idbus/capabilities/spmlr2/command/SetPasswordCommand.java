package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.password.SetPasswordRequestType;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "spml", name = "setpassword", description = "SPML Set PASSWORD operation")
public class SetPasswordCommand extends SpmlCommandSupport {

    @Option(name = "-i", aliases = "--id", description = "User ID", required = true, multiValued = false)
    Long userId;

    @Option(name = "-o", aliases = "--old-password", description = "Old Password ", required = true, multiValued = false)
    String oldPassword;

    @Option(name = "-p", aliases = "--password", description = "New Password ", required = true, multiValued = false)
    String password;

    @Option(name = "-c", aliases = "--password-confirmation", description = "Password Confirmation ", required = true, multiValued = false)
    String passwordConfirmation;

    @Override
    protected RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        if (!password.equals(passwordConfirmation)) {
            throw new RuntimeException("Password and confirmation do not match");
        }

        SetPasswordRequestType spmlRequest = new SetPasswordRequestType ();
        spmlRequest.setCurrentPassword(oldPassword);
        spmlRequest.setPassword(password);

        PSOIdentifierType psoId = new PSOIdentifierType();
        psoId.setID(userId + "");
        psoId.setTargetID(targetId);

        spmlRequest.setPsoID(psoId);

        return spmlRequest;
        
    }
}
