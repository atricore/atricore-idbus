package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
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

    private Map<String, Map<String, UpdateDescriptorType>> updatesByFeature =
            new HashMap<String, Map<String, UpdateDescriptorType>>();

    public void init() {

    }

    public Collection<UpdateDescriptorType> refreshRepositories() {

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
                            storeUpdateByFeature(ud);
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

        return newUpdates;

        // Contact remote service,
        // Retrieve update list,
        // Store it locally
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() {
        return null;
    }

    public void addRepository(MetadataRepository repo) throws LiveUpdateException {
        repo.init();
        repos.add(repo);

    }

    protected void storeUpdateByFeature(UpdateDescriptorType ud) {

        for (InstallableUnitType iu : ud.getInstallableUnit()) {

            for (FeatureType f : iu.getFeature()) {

                String k = f.getGroup() + "/" + f.getName() + "/" + f.getVersion().getVersion();
                Map<String, UpdateDescriptorType> uds = updatesByFeature.get(k);
                if (uds == null) {
                    uds = new HashMap<String, UpdateDescriptorType>();
                    updatesByFeature.put(k, uds);
                }

                if (!uds.containsKey(ud.getID())) {
                    uds.put(ud.getID(), ud);
                }
            }

        }
    }


    protected void lookForUpdates(InstallableUnitType iu) {
        for (RequiredFeatureType f  : iu.getRequirement()) {
            // TODO : Support version ranges!
            String k = f.getGroup() + "/" + f.getName() + "/" + f.getVersionRange();


        }
    }

}
