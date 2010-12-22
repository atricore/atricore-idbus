package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
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

    private MetadataRepositoryManager mdManager;

    private ArtifactRepositoryManager artManager;

    private ProfileManager profileManager;

    private UpdateEngine engine;

    private UpdatesMonitor updatesMonitor;

    private ScheduledThreadPoolExecutor stpe;
    private Properties config;

    public void init() {
        // Start update check thread.
        logger.info("Initializing LiveUpdate service");

        for (Object k : config.keySet()) {
            String key = (String) k;
            if (key.startsWith("repo.md.")) {
                // We need to configure a repo, get repo base key.
                String repoName = key.substring("repo.md.".length());
                repoName = repoName.substring(0, repoName.indexOf('.'));
                String repoKeys = "repo.md." + repoName;

                // Get id,name, location, enabled

                String id = config.getProperty(repoKeys + ".id");
                String name = config.getProperty(repoKeys + ".name");
                boolean enabled = Boolean.parseBoolean(config.getProperty(repoKeys + ".enabled"));
                URI location = null;
                try {
                    location = new URI(config.getProperty(repoKeys + ".uri"));
                } catch (URISyntaxException e) {
                    logger.error("Invalid URI [] for repository " + id + " " + name);
                    continue;
                }

                if (logger.isDebugEnabled())
                    logger.debug("Configuring repository ["+id+"] " + name +
                            (enabled ? "enabled" : "disabled" ) + " at " + location);

                VFSMetadataRepositoryImpl repo = new VFSMetadataRepositoryImpl();
                repo.setId(id);
                repo.setName(name);
                repo.setLocation(location);
                repo.setEnabled(enabled);

                // TODO : Setup other poperties as public key, etc

                mdManager.addRepository(repo);


            }

        }
    }

    public Collection<Repository> getRepositories() {
        List<Repository> repos = new ArrayList<Repository>();

        repos.addAll(mdManager.getRepositories());
        repos.addAll(artManager.getRepositories());

        return repos;
    }

    // Analyze MD and see if updates apply. (use license information ....)
    public void checkForUpdates() {

        mdManager.refreshRepositories();

        for (UpdateDescriptorType ud : mdManager.getAvailableUpdates()) {
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
}
