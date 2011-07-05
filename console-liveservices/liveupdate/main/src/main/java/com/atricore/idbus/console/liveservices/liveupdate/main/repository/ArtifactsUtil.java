package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactsUtil {

    public static String getArtifactFolderPath(ArtifactKeyType artifact) {
        String artFolderName = artifact.getGroup().replace('.', '/');
        artFolderName += "/" + artifact.getName();
        artFolderName += "/" + artifact.getVersion();

        return artFolderName;
    }

    public static String getArtifactFolderPath(String baseFolder, ArtifactKeyType artifact) {
        return baseFolder + "/" + getArtifactFolderPath(artifact);
    }

    public static String getArtifactFileName(ArtifactKeyType artifact) {
        // Name and version
        String artFileName = artifact.getName() + "-" + artifact.getVersion();

        // Classifier (optional)
        if (artifact.getClassifier() != null && !"".equals(artifact.getClassifier())) {
            artFileName += "-" + artifact.getClassifier();
        }

        // Type (default jar)
        artFileName += "." +
                (artifact.getType() == null || artifact.getType().equals("bundle") ? "jar" : artifact.getType());

        return artFileName;
    }

    public static String getArtifactFilePath(ArtifactKeyType artifact) {
        return getArtifactFolderPath(artifact) + "/" + getArtifactFileName(artifact);
    }

    public static String getArtifactFilePath(String baseFolder, ArtifactKeyType artifact) {
        return baseFolder + "/" + getArtifactFilePath(artifact);
    }

    public static String getArtifactDescriptorPath(ArtifactKeyType artifact) {
        return getArtifactFolderPath(artifact) + "/" + getArtifactFileName(artifact) + ".xml";
    }

    public static String getArtifactDescriptorPath(String baseFolder, ArtifactKeyType artifact) {
        return baseFolder + "/" + getArtifactDescriptorPath(artifact);
    }
}
