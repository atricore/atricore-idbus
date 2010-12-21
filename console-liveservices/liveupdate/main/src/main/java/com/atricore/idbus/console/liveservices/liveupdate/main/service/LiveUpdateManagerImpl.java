package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.liveservices.liveupdate._1_0.md.FeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

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

    private MetadataRepositoryManager mdManager;

    private ProfileManager profileManager;

    private UpdateEngine engine;

    private UpdatesMonitor updatesMonitor;

    private ScheduledThreadPoolExecutor stpe;

    public void init() {
        // Start update check thread.
    }

    // Analyze MD and see if updates apply. (use license information ....)
    public void checkForUpdates() {

        mdManager.refreshRepositories();

        for (UpdateDescriptorType ud : mdManager.getAvailableUpdates()) {
            for (InstallableUnitType iu : ud.getInstallableUnit()) {
                for (FeatureType f : iu.getFeature()) {

                }
            }
        }
    }

    // Apply update
    public void applyUpdate(String ID) {

    }


}
