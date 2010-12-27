package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.ArtifactRepositoryManagerImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.MetadataRepositoryManagerImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Periodically analyze MD and see if updates apply.
 * Keep track of current version/update
 * Queue update processes, to be triggered on reboot.
 * Manage update lifecycle.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateManagerImpl implements LiveUpdateManager {

    private static final Log logger = LogFactory.getLog(LiveUpdateManagerImpl.class);

    private List<RepositoryTransport> transports;

    private ProfileManager profileManager;

    private UpdateEngine engine;

    private UpdatesMonitor updatesMonitor;

    private String karafData;

    private ScheduledThreadPoolExecutor stpe;
    private Properties config;
    private MetadataRepositoryManagerImpl mdManager;
    private ArtifactRepositoryManagerImpl artManager;

    public void init() throws LiveUpdateException {


        // Start update check thread.
        logger.info("Initializing LiveUpdate service");

        Set<String> used = new HashSet<String>();

        for (Object k : config.keySet()) {
            String key = (String) k;

            if (key.startsWith("repo.md.")) {

                // We need to configure a repo, get repo base key.
                String repoId = key.substring("repo.md.".length());
                repoId = repoId.substring(0, repoId.indexOf('.'));

                String repoKeys = "repo.md." + repoId;
                if (used.contains(repoKeys))
                    continue;

                used.add(repoKeys);

                // Get id,name, location, enabled

                if (logger.isTraceEnabled())
                    logger.trace("Adding new MD repository : " + repoKeys);

                String id = config.getProperty(repoKeys + ".id");
                String name = config.getProperty(repoKeys + ".name");
                boolean enabled = Boolean.parseBoolean(config.getProperty(repoKeys + ".enabled"));
                URI location = null;
                try {
                    // Since we're handling the configuration properties, we cannot rely on spring properties resolver.
                    String l = config.getProperty(repoKeys + ".location");
                    l = l.replaceAll("\\$\\{karaf\\.data\\}", karafData);
                    location = new URI(l);

                } catch (Exception e) {
                    logger.error("Invalid URI ["+config.getProperty(repoKeys + ".location")+"] for repository " + id + " " + name);
                    continue;
                }

                if (logger.isDebugEnabled())
                    logger.debug("Adding new VFS MD Repository ["+id+"] " + name +
                            (enabled ? "enabled" : "disabled" ) + " at " + location);

                VFSMetadataRepositoryImpl repo = new VFSMetadataRepositoryImpl();
                repo.setId(id);
                repo.setName(name);
                repo.setLocation(location);
                repo.setEnabled(enabled);
                try {
                    repo.setRepoFolder(new URI ("file://" + karafData + "/liveservices/liveupdate/repos/md/cache/" + id));
                } catch (URISyntaxException e) {
                    logger.error("Invalid repository ID : " + id + ". " +e.getMessage());
                    continue;
                }


                // TODO : Setup other poperties like public key, etc

                mdManager.addRepository(repo);


            } else if (key.startsWith("repo.art.")) {
                // TODO : Configure Artifact Repository

            }

        }
    }

    public ProfileType getCurrentProfile() throws LiveUpdateException {
        return this.profileManager.getCurrentProfile();
    }

    public Collection<Repository> getRepositories() {
        List<Repository> repos = new ArrayList<Repository>();

        repos.addAll(mdManager.getRepositories());
        repos.addAll(artManager.getRepositories());

        return repos;
    }

    public UpdatesIndexType getRepositoryUpdates(String repoName) throws LiveUpdateException {
        return mdManager.getUpdates(repoName);
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() throws LiveUpdateException {

        ProfileType profile = profileManager.getCurrentProfile();
        List<InstallableUnitType> ius = profile.getInstallableUnit();

        Map<String, UpdateDescriptorType> updates = new HashMap<String, UpdateDescriptorType>();

        for (InstallableUnitType iu : ius) {

            Collection<UpdateDescriptorType> uds = mdManager.getAvailableUpdates(iu);

            for (UpdateDescriptorType ud : uds) {
                updates.put(ud.getID(), ud);
            }
        }

        return updates.values();
    }


    // Analyze MD and see if updates apply. (use license information ....)
    public void checkForUpdates() {



        // TODO :
        /*
            1. Get Available updates for a given IU
            2. Get Available updates for new IUs from that update.
            3. Build a list and store it in memory/disk!?
         */
        Collection<UpdateDescriptorType> uds = mdManager.refreshRepositories();
        for (UpdateDescriptorType ud : uds) {
            for (InstallableUnitType iu : ud.getInstallableUnit()) {

            }
        }
    }

    // Apply update
    public void applyUpdate(String ID) {

    }


    public void setConfig(Properties config) {
        this.config = config;
    }

    public Properties getConfig() {
        return config;
    }

    public void setMetadataRepositoryManager(MetadataRepositoryManagerImpl metadataRepositoryManager) {
        this.mdManager = metadataRepositoryManager;
    }

    public MetadataRepositoryManagerImpl getMetadataRepositoryManager() {
        return mdManager;
    }

    public void setArtifactRepositoryManager(ArtifactRepositoryManagerImpl artifactRepositoryManager) {
        this.artManager = artifactRepositoryManager;
    }

    public ArtifactRepositoryManagerImpl getArtifactRepositoryManager() {
        return artManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public String getKarafData() {
        return karafData;
    }

    public void setKarafData(String karafData) {
        this.karafData = karafData;
    }
}
