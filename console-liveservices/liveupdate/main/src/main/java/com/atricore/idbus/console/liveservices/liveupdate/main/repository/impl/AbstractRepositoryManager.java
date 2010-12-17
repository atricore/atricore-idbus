package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.ProvisioningAgent;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryManager;

import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractRepositoryManager<T> implements RepositoryManager {

    public URI[] getKnownRepositories() {
        return new URI[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URI[] getKnownMetaDataRepositories() {
        return new URI[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URI[] getKnownArtifactRepositories() {
        return new URI[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isEnabled(URI repository) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEnabled(URI repository) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ProvisioningAgent getAgent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
