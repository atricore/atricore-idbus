package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.util.InvalidSignatureException;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeystoreKeyResolver;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.FileNotFoundException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate-admin", name = "validate-updates-index", description = "Validate updates index")
public class ValidateUpdatesIndexSignatureCommand extends SignValidateCommandSupport {

    @Option(name = "-f", aliases = "--file", description = "Updates index file", required = true, multiValued = false)
    private String updatesIndexFile;

    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {
        byte[] keystore;
        byte[] signedUpdatesIndex;
        try {
            keystore = readContent(keystoreFile);
            signedUpdatesIndex = readContent(updatesIndexFile);
        } catch (FileNotFoundException e) {
            System.err.println("\u001B[31mFile not found: " + e.getMessage() + "\u001B[0m");
            return null;
        }
        
        LiveUpdateKeystoreKeyResolver keyResolver = new LiveUpdateKeystoreKeyResolver();
        keyResolver.setKeystoreFile(keystore);
        keyResolver.setKeystoreType(keystoreType);
        keyResolver.setKeystorePass(keystorePass);
        keyResolver.setCertificateAlias(certificateAlias);
        keyResolver.setPrivateKeyAlias(privateKeyAlias);
        keyResolver.setPrivateKeyPass(privateKeyPass);

        try {
            svc.validateUpdatesIndex(signedUpdatesIndex, keyResolver);
            System.out.println("Signature is valid.");
        } catch (InvalidSignatureException e) {
            System.err.println("\u001B[31mSignature is not valid!\u001B[0m");
        }
        
        return null;
    }
}
