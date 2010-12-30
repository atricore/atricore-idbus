package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.util.InvalidSignatureException;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.FileNotFoundException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate-admin", name = "validate-updates-index", description = "Validate updates index")
public class ValidateUpdatesIndexSignatureCommand extends SignValidateCommandSupport {

    @Option(name = "-i", aliases = "--in", description = "Updates index file", required = true, multiValued = false)
    private String updatesIndexFile;

    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {
        byte[] signedUpdatesIndex;
        LiveUpdateKeyResolver keyResolver;
        try {
            signedUpdatesIndex = readContent(updatesIndexFile);
            keyResolver = getLiveUpdateKeyResolver();
        } catch (FileNotFoundException e) {
            System.err.println("\u001B[31mFile not found: " + e.getMessage() + "\u001B[0m");
            return null;
        }

        try {
            svc.validateUpdatesIndex(signedUpdatesIndex, keyResolver);
            System.out.println("Signature is valid.");
        } catch (InvalidSignatureException e) {
            System.err.println("\u001B[31mSignature is not valid!\u001B[0m");
        }
        
        return null;
    }
}
