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
@Command(scope = "liveupdate-admin", name = "validate-artifact-descriptor", description = "Validate artifact descriptor")
public class ValidateArtifactDescriptorSignatureCommand extends LiveUpdateAdminCommandSupport {

    @Option(name = "-k", aliases = "--keystore", description = "Keystore file", required = true, multiValued = false)
    private String keystoreFile;

    @Option(name = "-t", aliases = "--keystore-type", description = "Keystore type, default JKS", required = false, multiValued = false)
    private String keystoreType = "JKS";

    @Option(name = "-c", aliases = "--cert-alias", description = "Private Key alias", required = true, multiValued = false)
    private String certificateAlias;

    @Option(name = "-n", aliases = "--private-key-alias", description = "Private Key alias", required = true, multiValued = false)
    private String privateKeyAlias;

    @Option(name = "-p", aliases = "--keystore-pass", description = "Keystore password", required = true, multiValued = false)
    private String keystorePass;

    @Option(name = "-kp", aliases = "--private-key-pass", description = "Private Key password", required = false, multiValued = false)
    private String privateKeyPass;

    @Option(name = "-f", aliases = "--file", description = "Artifact descriptor file", required = true, multiValued = false)
    private String artifactDescriptorFile;

    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {
        byte[] keystore;
        byte[] signedArtifactDescriptor;
        try {
            keystore = readContent(keystoreFile);
            signedArtifactDescriptor = readContent(artifactDescriptorFile);
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
            svc.validateArtifactDescriptor(signedArtifactDescriptor, keyResolver);
            System.out.println("Signature is valid.");
        } catch (InvalidSignatureException e) {
            System.err.println("\u001B[31mSignature is not valid!\u001B[0m");
        }

        return null;
    }
}
