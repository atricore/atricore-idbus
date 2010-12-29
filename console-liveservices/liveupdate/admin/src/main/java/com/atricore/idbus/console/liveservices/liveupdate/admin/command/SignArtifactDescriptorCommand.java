package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeystoreKeyResolver;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.FileNotFoundException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate-admin", name = "sign-artifact-descriptor", description = "Sign artifact descriptor")
public class SignArtifactDescriptorCommand extends SignValidateCommandSupport {

    @Option(name = "-f", aliases = "--file", description = "Artifact descriptor file", required = true, multiValued = false)
    private String artifactDescriptorFile;

    @Option(name = "-s", aliases = "--signed-file", description = "Signed artifact descriptor file", required = true, multiValued = false)
    private String signedArtifactDescriptorFile;

    @Option(name = "-r", aliases = "--replace", description = "Replace destination file", required = false, multiValued = false)
    private boolean replace;
    
    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {
        byte[] keystore;
        byte[] artifactDescriptor;
        try {
            keystore = readContent(keystoreFile);
            artifactDescriptor = readContent(artifactDescriptorFile);
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

        byte[] signedArtifactDescriptor = svc.signArtifactDescriptor(artifactDescriptor, keyResolver);
        writeContent(signedArtifactDescriptorFile, signedArtifactDescriptor, replace);

        System.out.println("Artifact descriptor file successfully signed.");

        return null;
    }
}
