package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.*;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
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

    private String certProviderName = "SUN";

    private CertStore certStore;

    public void init() throws LiveUpdateException {

        try {

            // Crate Collection CertStore using SUN Provider
            CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(new ArrayList()),
                    certProviderName);
            // TODO : Add CRLs, etc.

            // Start update check thread.
            logger.info("Initializing LiveUpdate service ...");
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

                    try {
                        MetadataRepository repo = (MetadataRepository) buildVFSRepository(VFSMetadataRepositoryImpl.class, repoKeys);
                        logger.info("Using LiveUpdate MD Repository at " + repo.getLocation());
                        mdManager.addRepository(repo);

                    } catch (LiveUpdateException e) {
                        logger.error("Ignoring MD repository definition : " + e.getMessage());

                        // When debugging, error log includs stack trace.
                        if (logger.isDebugEnabled())
                            logger.error("Ignoring MD repository definition : " + e.getMessage(), e);
                    }


                } else if (key.startsWith("repo.art.")) {
                    // We need to configure a repo, get repo base key.
                    String repoId = key.substring("repo.art.".length());
                    repoId = repoId.substring(0, repoId.indexOf('.'));

                    String repoKeys = "repo.art." + repoId;
                    if (used.contains(repoKeys))
                        continue;

                    used.add(repoKeys);

                    try {
                        ArtifactRepository repo = (ArtifactRepository) buildVFSRepository(VFSArtifactRepositoryImpl.class, repoKeys);
                        logger.info("Using LiveUpdate Artifact Repository at " + repo.getLocation());
                        artManager.addRepository(repo);
                    } catch (LiveUpdateException e) {
                        logger.error("Ignoring Artifact repository definition : " + e.getMessage());

                        // When debugging, error log includs stack trace.
                        if (logger.isDebugEnabled())
                            logger.error("Ignoring Artifact repository definition : " + e.getMessage(), e);
                    }
                }

            }
        } catch (NoSuchAlgorithmException e) {
            throw new LiveUpdateException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new LiveUpdateException(e);
        } catch (NoSuchProviderException e) {
            throw new LiveUpdateException(e);
        }
    }

    public ProfileType getCurrentProfile(boolean rebuild) throws LiveUpdateException {
        return this.profileManager.getCurrentProfile(rebuild);
    }

    public Collection<Repository> getRepositories() {

        List<Repository> repos = new ArrayList<Repository>();

        repos.addAll(mdManager.getRepositories());
        repos.addAll(artManager.getRepositories());

        return repos;
    }

    public UpdatesIndexType getRepositoryUpdates(String repoId) throws LiveUpdateException {
        return mdManager.getUpdatesIndex(repoId, true);
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() throws LiveUpdateException {
        Collection<UpdateDescriptorType> updates = mdManager.getUpdates();
        ProfileType profile = profileManager.getCurrentProfile(true);

        Map<String, UpdateDescriptorType> availableUpdates = new HashMap<String, UpdateDescriptorType>();

        for (InstallableUnitType installed : profile.getInstallableUnit()) {
            Collection<UpdateDescriptorType> uds = profileManager.getAvailableUpdates(installed, updates);
            for (UpdateDescriptorType ud : uds) {
                availableUpdates.put(ud.getID(), ud);
            }
        }

        return availableUpdates.values();

    }

    public Collection<UpdateDescriptorType> getAvailableUpdates(String iuFqn) throws LiveUpdateException {
        Collection<UpdateDescriptorType> updates = mdManager.getUpdates();
        ProfileType profile = profileManager.getCurrentProfile(true);

        for (InstallableUnitType installed : profile.getInstallableUnit()) {
            String installedFqn = installed.getGroup() + "/" + installed.getName() + "/" + installed.getVersion();
            if (installedFqn.equals(iuFqn)) {
                return profileManager.getAvailableUpdates(installed, updates);
            }

        }
        return new ArrayList<UpdateDescriptorType>();
    }

    public void cleanRepository(String repoId) throws LiveUpdateException {
        mdManager.clearRepository(repoId);
        artManager.clearRepository(repoId);
    }

    public void cleanAllRepositories() throws LiveUpdateException {
        mdManager.clearRepositories();
        artManager.clearRepositories();
    }

    // Analyze MD and see if updates apply. (use license information ....)
    public Collection<UpdateDescriptorType> checkForUpdates() throws LiveUpdateException {
        mdManager.refreshRepositories();
        return getAvailableUpdates();
    }

    public Collection<UpdateDescriptorType> checkForUpdates(String iuFqn) throws LiveUpdateException {
        mdManager.refreshRepositories();
        return getAvailableUpdates(iuFqn);
    }

    // Apply update
    public void applyUpdate(String group, String name, String version) throws LiveUpdateException {

        if (logger.isDebugEnabled())
            logger.debug("Trying to apply update for " + group + "/" + name + "/" + version);

        mdManager.refreshRepositories();

        Collection<UpdateDescriptorType> availableUpdates = getAvailableUpdates();
        InstallableUnitType installableUnit  = null;
        UpdateDescriptorType update = null;

        for (UpdateDescriptorType ud : availableUpdates) {
            InstallableUnitType iu = ud.getInstallableUnit();
            if (iu.getGroup().equals(group) && iu.getName().equals(name) && iu.getVersion().equals(version)) {
                installableUnit = iu;
                update = ud;
                if (logger.isDebugEnabled())
                    logger.debug("Found IU " + iu.getID() + " for " + group + "/" + name + "/" + version);
                break;
            }
        }

        if (installableUnit == null) {
            throw new LiveUpdateException("Update not available for current setup : " +
                    group + "/" + name + "/" + version);
        }

        logger.info("Applying Update " + group + "/" + name + "/" + version);

        Collection<UpdateDescriptorType> updates = mdManager.getUpdates();
        ProfileType updateProfile = profileManager.buildUpdateProfile(installableUnit, updates);

        engine.execute("updatePlan", updateProfile);
    }

    public ProfileType getUpdateProfile() throws LiveUpdateException {
        // TODO : Refresh repos every time ?
        Collection<UpdateDescriptorType> updates = mdManager.refreshRepositories();
        ProfileType profile = getCurrentProfile(true);

        for (InstallableUnitType iu : profile.getInstallableUnit()) {
            profileManager.buildUpdateProfile(iu, updates);
        }
        throw new UnsupportedOperationException("implement me");

    }


    public ProfileType getUpdateProfile(String group, String name, String version) throws LiveUpdateException {
        // TODO : Refresh repos every time ?
        mdManager.refreshRepositories();
        UpdateDescriptorType ud = mdManager.getUpdate(group, name, version);
        if (ud == null)
            throw new LiveUpdateException("No update found for " + group +"/"+name+"/"+version);
        
        return this.profileManager.buildUpdateProfile(ud.getInstallableUnit(), mdManager.getUpdates());
    }

    // -------------------------------------------< Utilities >

    protected AbstractVFSRepository buildVFSRepository(Class repoType, String repoKeys) throws LiveUpdateException {

        try {
            // Get id,name, location, enabled
            if (logger.isTraceEnabled())
                logger.trace("Adding new repository : " + repoKeys);

            // Repository ID
            String id = config.getProperty(repoKeys + ".id");
            if (id == null)
                throw new LiveUpdateException("Repository ID is required. Configuration keys " + repoKeys);

            // Repository Name
            String name = config.getProperty(repoKeys + ".name");
            if (name == null)
                throw new LiveUpdateException("Repository name is required for " + id);

            // Enabled
            boolean enabled = Boolean.parseBoolean(config.getProperty(repoKeys + ".enabled", "false"));

            // DS Validation
            boolean validateSignature = Boolean.parseBoolean(config.getProperty(repoKeys + ".validateSignature", "true"));


            // Certificate for DS validation
            // TODO : Use java.security.cert.CertStore to keep track of all certs :) !?
            String certFile =  config.getProperty(repoKeys + ".certificate");
            // TODO : Read/Decode/Validate the certificate file , use CertStore instance ..
            byte[] certificate = null;

            if (validateSignature && certificate == null) {
                throw new LiveUpdateException("Repository " + id + " has Digital Signature enabled, but no certificate was provided" );
            }

            // Repository Location
            URI location = null;
            try {
                // Since we're handling the configuration properties, we cannot rely on spring properties resolver.
                String l = config.getProperty(repoKeys + ".location");
                l = l.replaceAll("\\$\\{karaf\\.data\\}", dataFolder);
                location = new URI(l);

            } catch (Exception e) {
                logger.error("Invalid URI ["+config.getProperty(repoKeys + ".location")+"] for repository " + id + " " + name);
                return null;
            }

            if (logger.isDebugEnabled())
                logger.debug("Adding new VFS Repository ["+id+"] " + name +
                        (enabled ? "enabled" : "disabled" ) + " at " + location);


            AbstractVFSRepository repo = (AbstractVFSRepository) repoType.newInstance();
            repo.setId(id);
            repo.setName(name);
            repo.setLocation(location);
            repo.setEnabled(enabled);
            repo.setCertValue(certificate);
            repo.setSignatureValidationEnabled(validateSignature);
            // TODO : Take from license
            //repo.setUsername();
            //repo.setPassword();

            repo.setRepoFolder(new URI ("file://" + dataFolder + "/liveservices/liveupdate/repos/cache/" + id));

            return repo;
        } catch (Exception e) {
            throw new LiveUpdateException("Cannot configure repository. " + e.getMessage(), e);
        }

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

    public String getCertProviderName() {
        return certProviderName;
    }

    public void setCertProviderName(String certProviderName) {
        this.certProviderName = certProviderName;
    }

    //
}
