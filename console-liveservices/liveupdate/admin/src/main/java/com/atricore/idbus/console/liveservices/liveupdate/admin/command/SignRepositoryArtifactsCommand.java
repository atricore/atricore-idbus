package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Option;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * This command will sign all repository artifacts found in the given folder that match the
 * specified group/artifact and version options.
 *
 * If no option is not provided, all artifacts will be signed.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SignRepositoryArtifactsCommand extends ArtifactCommandSupport {

    @Option(name = "-e", aliases = "--expression", description = "Expression to match artifact descriptor name", required = false)
    String expression;

    @Argument
    String repositoryFolder;

    @Override
    protected Object doExecute(LiveUpdateAdminService svc) throws Exception {

        // For windows compatibility
        String folder = repositoryFolder.replace('\\', '/');
        File repoFolderFile = new File(folder);

        if (!repoFolderFile.exists() || !repoFolderFile.isDirectory())
            throw new IOException("File not found or not a directory " + repositoryFolder);

        File folderFile = repoFolderFile;

        signDescriptorsInFolder(svc, folderFile);
        return null;
    }

    protected void signDescriptorsInFolder(LiveUpdateAdminService svc, File folder) {
        if (!folder.exists() || !folder.isDirectory())
            return ;

        for (File child : folder.listFiles()) {

            if (child.isDirectory()) {
                signDescriptorsInFolder(svc, child);
                continue;
            }

            if (child.getName().endsWith(".xml")) {

                // TODO : Try to match expression with name

                InputStream in = null;
                OutputStream out = null;
                try {

                    byte[] artifactDescriptor;
                    LiveUpdateKeyResolver keyResolver;
                    in = new FileInputStream(child);

                    artifactDescriptor = IOUtils.toByteArray(in);
                    keyResolver = getLiveUpdateKeyResolver();

                    byte[] signedArtifactDescriptor = svc.signArtifactDescriptor(artifactDescriptor, keyResolver);

                    out = new FileOutputStream(child, false);
                    IOUtils.write(signedArtifactDescriptor, out);
    
                    System.out.println("Artifact descriptor file successfully signed " + child.getAbsolutePath());

                } catch (FileNotFoundException e) {
                    System.err.println("\u001B[31mFile not found: " + child.getAbsolutePath() + "\u001B[0m");
                } catch (Exception e) {
                    System.err.println("\u001B[31mError signing : " + child.getAbsolutePath() + "\u001B[0m.  " + e.getMessage());
                } finally {
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                }

            }

        }

    }


}
