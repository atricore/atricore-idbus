package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.io.File;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactsUtil {

    public static String getArtifactFolderName(String baseFolder, ArtifactKeyType artifact) {
        String artFoldername = baseFolder;

        artFoldername += "/" + artifact.getGroup().replace('.', '/');
        artFoldername += "/" + artifact.getName();
        artFoldername += "/" + artifact.getVersion();
        artFoldername += "/" + artifact.getName();

        return artFoldername;

    }

    public static String getArtifactFileName(String baseFolder, ArtifactKeyType artifact) {

        String artFileName = getArtifactFolderName(baseFolder, artifact);

        artFileName += "-" + artifact.getVersion();
        if (artifact.getClassifier() != null && !"".equals(artifact.getClassifier()))
            artFileName += "-" + artifact.getClassifier();

        artFileName += "." + (artifact.getType() == null || artifact.getType().equals("bundle") ?
                "jar" : artifact.getType());

        return artFileName;

    }

}
