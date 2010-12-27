package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DefaultDependencyTreeBuilder;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DependencyTreeBuilder;
import com.atricore.liveservices.liveupdate._1_0.md.*;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.util.*;

/**
 * Manages a set of LiveUpdate MD repositories.
 *
 * It retrieves MD information for actual update services and stores it in the local repository representation.
 * Different transports are supported
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MetadataRepositoryManagerImpl extends AbstractRepositoryManager<MetadataRepository> implements MetadataRepositoryManager {

    private static final Log logger = LogFactory.getLog(MetadataRepositoryManagerImpl.class);

    /**
     * List of Installable Units, defined as dependent objects.
     */
    Map<String, DependencyNode> dependencies;

    public void init() {
        // RFU
    }

    public void addRepository(MetadataRepository repo) throws LiveUpdateException {
        repo.init();
        repos.add(repo);
    }

    public synchronized Collection<UpdateDescriptorType> refreshRepositories() {

        // TODO :Use indexes to keep track of updates/requirements/features
        // Loop over configured repos
        List<UpdateDescriptorType> newUpdates = new ArrayList<UpdateDescriptorType>();

        for (MetadataRepository repo : repos) {
            URI location = repo.getLocation();
            for (RepositoryTransport t : transports) {
                if (t.canHandle(location)) {

                    try {
                        // TODO : Provide license information
                        byte[] idxBin = t.loadContent(location);
                        UpdatesIndexType idx = XmlUtils1.unmarshallUpdatesIndex(new String(idxBin), false);
                        // Store updates to in repo
                        boolean add = false;
                        for (UpdateDescriptorType ud : idx.getUpdateDescriptor()) {
                            if (!repo.hasUpdate(ud.getID())) {
                                add = true;
                                newUpdates.add(ud);
                            }
                        }
                        repo.addUpdatesIndex(idx);

                    } catch (Exception e) {
                        logger.error("Cannot load updates list from repository " + repo.getName() +
                                " ["+repo.getId()+"] " + e.getMessage());

                        if (logger.isTraceEnabled())
                            logger.error("Cannot load updates list from repository " + repo.getName() +
                                    " ["+repo.getId()+"] " + e.getMessage(), e);
                    }

                }
            }
        }

        // Rebuild dependencies tree
        List<UpdateDescriptorType> updates = new ArrayList<UpdateDescriptorType>();
        for (MetadataRepository repo : repos) {
            updates.addAll(repo.getAvailableUpdates());
        }

        DependencyTreeBuilder b = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> deps = b.buildDependencyList(updates);

        this.dependencies.clear();
        for (DependencyNode dep : deps) {
            this.dependencies.put(dep.getFqKey(), dep);
        }

        return newUpdates;

        // Contact remote service,
        // Retrieve update list,
        // Store it locally
    }

    public synchronized Collection<UpdateDescriptorType> getUpdates(InstallableUnitType iu) {
        String fqKey = iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion();
        DependencyNode n = dependencies.get(fqKey);

        List<DependencyNode> uds = getDependents(n);
        Set<UpdateDescriptorType> updates = new HashSet<UpdateDescriptorType>();

        for (DependencyNode ud : uds) {
            updates.add(ud.getUpdateDescriptor());
        }

        return updates;

    }

    public synchronized UpdatesIndexType getUpdatesIndex(String repoName) throws LiveUpdateException {
        for (MetadataRepository metadataRepository : repos) {
            if (metadataRepository.getName().equals(repoName)) {
                return metadataRepository.getUpdates();
            }
        }
        return null;
    }

    public UpdateDescriptorType getUpdate(String id) throws LiveUpdateException {

        for ( MetadataRepository repo : repos) {
            for (UpdateDescriptorType ud : repo.getAvailableUpdates())
                if (ud.getID().equals(id))
                    return ud;
        }
        return null;
    }



    /**
     * Get list of dependent objects
     */
    protected List<DependencyNode> getDependents(DependencyNode dep) {
        List<DependencyNode> updates = new ArrayList<DependencyNode>();
        getDependents(dep, updates);
        return updates;
    }

    /**
     * Get list of dependent objects
     */
    protected void getDependents(DependencyNode dep, List<DependencyNode> updates) {
        if (dep.getChildren() != null) {
            updates.addAll(dep.getChildren());
            for (DependencyNode c : dep.getChildren()) {
                getDependents(c, updates);
            }
        }
    }


}
