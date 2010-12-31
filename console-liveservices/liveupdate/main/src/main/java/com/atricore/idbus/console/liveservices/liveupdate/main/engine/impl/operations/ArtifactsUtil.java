package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

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

    public static String getArtifactBaseFileName(ArtifactKeyType artifact) {
        String artFileName = artifact.getName() + "-" + artifact.getVersion();
        if (artifact.getClassifier() != null && !"".equals(artifact.getClassifier()))
            artFileName += "-" + artifact.getClassifier();

        return artFileName;
    }

    public static String getArtifactFilePath(ArtifactKeyType artifact) {
        return getArtifactFolderPath(artifact) + "/" + getArtifactBaseFileName(artifact) + "." +
                    (artifact.getType() == null || artifact.getType().equals("bundle") ? "jar" : artifact.getType());
    }

    public static String getArtifactFilePath(String baseFolder, ArtifactKeyType artifact) {
        return baseFolder + "/" + getArtifactFilePath(artifact);
    }

    public static String getArtifactDescriptorPath(ArtifactKeyType artifact) {
        return getArtifactFolderPath(artifact) + "/" + getArtifactBaseFileName(artifact) + ".xml";
    }

    public static String getArtifactDescriptorPath(String baseFolder, ArtifactKeyType artifact) {
        return baseFolder + "/" + getArtifactDescriptorPath(artifact);
    }
}
