package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.ProvisioningAgent;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractRepositoryManager<T> implements RepositoryManager {

    protected List<T> repos = new ArrayList<T>();

    protected List<RepositoryTransport> transports;

    public Collection getRepositories() {
        return repos;
    }

    public URI[] getKnownRepositories() {
        return new URI[0];
    }

    public URI[] getKnownMetaDataRepositories() {
        return new URI[0];
    }

    public URI[] getKnownArtifactRepositories() {
        return new URI[0];
    }

    public boolean isEnabled(URI repository) {
        return false;
    }

    public void setEnabled(URI repository) {

    }

    public ProvisioningAgent getAgent() {
        return null;
    }

    public void setTransports(List<RepositoryTransport> transports) {
        this.transports = transports;
    }

    public List<RepositoryTransport> getTransports() {
        return transports;
    }

}