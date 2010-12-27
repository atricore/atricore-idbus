package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.UpdateEngineImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.UpdateEngineImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
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

    private String dataFolder;

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
                    l = l.replaceAll("\\$\\{karaf\\.data\\}", dataFolder);
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
                    repo.setRepoFolder(new URI ("file://" + dataFolder + "/liveservices/liveupdate/repos/md/cache/" + id));
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
        return mdManager.getUpdatesIndex(repoName);
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() throws LiveUpdateException {

        ProfileType profile = profileManager.getCurrentProfile();
        List<InstallableUnitType> ius = profile.getInstallableUnit();

        Map<String, UpdateDescriptorType> updates = new HashMap<String, UpdateDescriptorType>();

        for (InstallableUnitType iu : ius) {

            Collection<UpdateDescriptorType> uds = mdManager.getUpdates();

            for (UpdateDescriptorType ud : uds) {
                updates.put(ud.getID(), ud);
            }
        }

        return updates.values();
    }


    // Analyze MD and see if updates apply. (use license information ....)
    public Collection<UpdateDescriptorType> checkForUpdates() throws LiveUpdateException {
        Collection<UpdateDescriptorType> uds = mdManager.refreshRepositories();
        return getAvailableUpdates();
    }

    // Apply update
    public void applyUpdate(String group, String name, String version) throws LiveUpdateException {

        if (logger.isDebugEnabled())
            logger.debug("Trying to apply update for " + group + "/" + name + "/" + version);

        Collection<UpdateDescriptorType> updates = getAvailableUpdates();
        InstallableUnitType installableUnit  = null;
        for (UpdateDescriptorType ud : updates) {
            for (InstallableUnitType iu : ud.getInstallableUnit()) {
                if (iu.getGroup().equals(group) && iu.getName().equals(name) && iu.getVersion().equals(version)) {
                    installableUnit = iu;
                    if (logger.isDebugEnabled())
                        logger.debug("Found IU " + iu.getID() + " for " + group + "/" + name + "/" + version);
                    break;
                }
            }
        }

        if (installableUnit == null) {
            throw new LiveUpdateException("Update not available for current setup : " +
                    group + "/" + name + "/" + version);
        }

        logger.info("Applying Update " + group + "/" + name + "/" + version);

        // TODO : Calculate all the required IUs from our profile to the new one
        // UpdateContext ctx = buildUpdateContext(profile, iu);

        // TODO : Setup an update plan , could include rebooting, custom actions, etc.
        //engine.execute("updatePlan", ctx);
    }

    // -------------------------------------------< Utilities >

    protected UpdateContext buildUpdateContext(ProfileType profile, InstallableUnitType iu) {
        return null;//this.mdManager.
    }

    // -------------------------------------------< Properties >
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

    public UpdateEngine getEngine() {
        return engine;
    }

    public void setEngine(UpdateEngine engine) {
        this.engine = engine;
    }

    public String getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    //
}
