package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.ProvisioningAgent;

import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface RepositoryManager {

    URI[] getKnownRepositories();

    URI[] getKnownMetaDataRepositories();

    URI[] getKnownArtifactRepositories();

    boolean isEnabled(URI repository);

    void setEnabled(URI repository);

    ProvisioningAgent getAgent();

}
