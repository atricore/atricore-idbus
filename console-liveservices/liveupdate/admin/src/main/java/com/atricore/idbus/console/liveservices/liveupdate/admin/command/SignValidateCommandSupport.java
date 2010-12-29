package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeystoreKeyResolver;
import org.apache.felix.gogo.commands.Option;

import java.io.FileNotFoundException;

public abstract class SignValidateCommandSupport extends LiveUpdateAdminCommandSupport {

    @Option(name = "-k", aliases = "--keystore", description = "Keystore file", required = true, multiValued = false)
    protected String keystoreFile;

    @Option(name = "-t", aliases = "--keystore-type", description = "Keystore type, default JKS", required = false, multiValued = false)
    protected String keystoreType = "JKS";

    @Option(name = "-c", aliases = "--cert-alias", description = "Private Key alias", required = true, multiValued = false)
    protected String certificateAlias;

    @Option(name = "-n", aliases = "--private-key-alias", description = "Private Key alias", required = true, multiValued = false)
    protected String privateKeyAlias;

    @Option(name = "-p", aliases = "--keystore-pass", description = "Keystore password", required = true, multiValued = false)
    protected String keystorePass;

    @Option(name = "-kp", aliases = "--private-key-pass", description = "Private Key password", required = false, multiValued = false)
    protected String privateKeyPass;

    protected LiveUpdateKeyResolver getLiveUpdateKeyResolver() throws Exception {
        byte[] keystore;
        try {
            keystore = readContent(keystoreFile);
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

        return keyResolver;
    }
}
