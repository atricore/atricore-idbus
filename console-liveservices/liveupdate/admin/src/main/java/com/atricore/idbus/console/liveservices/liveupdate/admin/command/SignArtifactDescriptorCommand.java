package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.io.FileNotFoundException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@Command(scope = "liveupdate-admin", name = "sign-artifact-descriptor", description = "Sign artifact descriptor")
public class SignArtifactDescriptorCommand extends SignValidateCommandSupport {

    @Option(name = "-i", aliases = "--in", description = "Artifact descriptor input file", required = true, multiValued = false)
    private String artifactDescriptorFile;

    @Option(name = "-o", aliases = "--out", description = "Signed artifact descriptor output file", required = true, multiValued = false)
    private String signedArtifactDescriptorFile;

    @Option(name = "-r", aliases = "--replace", description = "Replace destination file", required = false, multiValued = false)
    private boolean replace;
    
    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {
        byte[] artifactDescriptor;
        LiveUpdateKeyResolver keyResolver;
        try {
            artifactDescriptor = readContent(artifactDescriptorFile);
            keyResolver = getLiveUpdateKeyResolver();
        } catch (FileNotFoundException e) {
            System.err.println("\u001B[31mFile not found: " + e.getMessage() + "\u001B[0m");
            return null;
        }

        byte[] signedArtifactDescriptor = svc.signArtifactDescriptor(artifactDescriptor, keyResolver);
        writeContent(signedArtifactDescriptorFile, signedArtifactDescriptor, replace);

        System.out.println("Artifact descriptor file successfully signed.");

        return null;
    }
}
