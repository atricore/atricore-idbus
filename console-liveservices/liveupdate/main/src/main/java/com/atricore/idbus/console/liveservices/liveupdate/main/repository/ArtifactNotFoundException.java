package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactNotFoundException extends LiveUpdateException {

    public ArtifactNotFoundException(ArtifactKeyType artifact) {
        // TODO : Add type and classifier, if any
        super("Artifact Not found " +
                artifact.getGroup() + "/" + artifact.getName() + "/" + artifact.getVersion());
    }
}
