package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyTreeBuilder;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyWalker;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * This should be replaced by mave
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProfileManagerImpl implements ProfileManager, BundleContextAware {

    // TODO : List for bundle events, and look for descriptors !?

    private static final UUIDGenerator uuidGen = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog(ProfileManagerImpl.class);

    private static final String CONTEXT_DIR = "/META-INF/liveservices/";

    private static final String CONTEXT_FILES = "liveupdate-1.0.xml";

    private BundleContext bundleContext;

    private ProfileType profile;

    public ProfileType getCurrentProfile() throws LiveUpdateException {
        return getCurrentProfile(false);
    }

    public ProfileType getCurrentProfile(boolean rebuild) throws LiveUpdateException {

        if (!rebuild && profile != null)
            return profile;

        if (logger.isDebugEnabled())
            logger.debug("Building current profile");

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new LiveUpdateException("Features Service is unavailable. (no service reference)");
        }

        try {

            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new LiveUpdateException("Features Service is unavailable. (no service)");
            }

            List<InstallableUnitType> uis = new ArrayList<InstallableUnitType>();
            for (Bundle bundle : bundleContext.getBundles()) {

                if (logger.isTraceEnabled())
                    logger.trace("Looking for LiveUpdate 1.0 descriptors in " + bundle.getLocation());

                Enumeration lu = bundle.findEntries(CONTEXT_DIR, CONTEXT_FILES, false);
                if (lu == null)
                    continue;

                if (logger.isTraceEnabled())
                    logger.trace("LiveUpdate 1.0 configuration found in bundle " + bundle.getLocation());

                while (lu.hasMoreElements()) {

                    URL location = (URL) lu.nextElement();
                    if (logger.isDebugEnabled())
                        logger.debug("LiveUpdate 1.0 configuration location : " + location);

                    InputStream is = null;

                    try {
                        is = location.openStream();
                        if (is == null) {
                            logger.warn("LiveUpdate 1.0 configuration unreachable : " + location);
                            continue;
                        }

                        UpdatesIndexType udIdx = XmlUtils1.unmarshallUpdatesIndex(is, false);
                        if (logger.isTraceEnabled())
                            logger.trace("Found UpdatesIndex " + udIdx.getID());

                        for (UpdateDescriptorType  ud : udIdx.getUpdateDescriptor()) {

                            if (logger.isTraceEnabled())
                                logger.trace("Found UpdateDescriptor " + ud.getID() + " [" + ud.getDescription() + "] " +
                                        ud.getIssueInstant());

                            InstallableUnitType iu = ud.getInstallableUnit();

                            if (logger.isTraceEnabled())
                                logger.trace("Found InstalllableUnit " + iu.getID() + " " +
                                        iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion() +
                                        " [" + iu.getUpdateNature() + "]");


                            // With one feature installed, we consider the IU as installed.
                            Feature kf = svc.getFeature(iu.getName(), iu.getVersion());

                            if (kf != null) {

                                if (logger.isTraceEnabled())
                                    logger.trace("Karaf Feature found for LiveUpdate Feature " + kf.getId() +
                                            " [" + kf.getResolver() + "/" + kf.getName()+"/"+kf.getVersion()+"]");

                                if (svc.isInstalled(kf)) {

                                    if(logger.isTraceEnabled())
                                        logger.trace("Karaf Feature is installed " + kf.getId() +
                                            " [" + kf.getResolver() + "/" + kf.getName()+"/"+kf.getVersion()+"]");
                                    uis.add(iu);
                                }
                            } else {
                                logger.trace("Karaf Feature NOT found for LiveUpdate Feature " + iu.getGroup() +
                                        "/" + iu.getName() + "/" + iu.getVersion());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Cannot load LiveUpdate descriptor : " + e.getMessage(), e);
                    } finally {
                        if (is != null) try {is.close();} catch (IOException e) { /**/ }
                    }


                }
            }

            ProfileType p = new ProfileType();
            p.setID(uuidGen.generateId());
            p.setName("_CURRENT_");
            p.getInstallableUnit().addAll(uis);

            // Store it
            this.profile = p;

            return p;

        } catch (Exception e) {
            throw new LiveUpdateException(e);
        } finally {
          bundleContext.ungetService(ref);
        }
    }

    /**
     * Builds the profile containing all the necessary updates to install the provided update in the current setup
     */
    public ProfileType buildUpdateProfile(InstallableUnitType installable, Collection<UpdateDescriptorType> updates) throws LiveUpdateException {

        // Each profile is a different way to install the desire update in our setup

        DependencyNode installableNode = buildUpdateDependenciesPaths(installable, getCurrentProfile().getInstallableUnit(), updates);
        List<List<DependencyNode>> updatePaths = installableNode.getUpdatePaths();

        if (logger.isTraceEnabled())
            logger.trace("Found " + updatePaths.size() + " possible update paths (strategies)");

        if (updatePaths.size() < 1)
            return null;

        // Choose the shorter path, the one that requires the fewer installable units
        List<DependencyNode> updatePath = null;
        for (List<DependencyNode> p : updatePaths) {
            if (updatePath == null || p.size() < updatePath.size())
                updatePath = p;
        }

        // Check if any required dependency needs to bee installed/updated
        Collection<DependencyNode> requiredDependencies = installableNode.getRequiredDependencies();
        for (DependencyNode requiredDep : requiredDependencies) {

            boolean requiresInstall = true;
            boolean requiresUpdate = true;

            for (InstallableUnitType installedIu : profile.getInstallableUnit()) {

                if (installedIu.getGroup().equals(requiredDep.getGroup())
                        && installedIu.getName().equals(requiredDep.getName())) {

                    // Some dependency version is in place, do not require install.
                    requiresInstall = false;

                    if (logger.isDebugEnabled())
                        logger.debug("Installable Unit found for required dependency : " +
                                installedIu.getGroup() + "/" +
                                installedIu.getName() + "/" +
                                installedIu.getVersion());

                    if (installedIu.getVersion().equals(requiredDep.getVersion())) {
                        // Dependency is in place
                        requiresUpdate = false;
                        break;
                    }
                }
            }

            // TODO : Do this automatically .... 
            if (requiresInstall)
                throw new LiveUpdateException("Dependency install is required : " + requiredDep.getFqKey());

            if (requiresUpdate)
                throw new LiveUpdateException("Dependency upgrade is required : " + requiredDep.getFqKey());


        }

        ProfileType updateProfile = new ProfileType();
        updateProfile.setID(uuidGen.generateId());
        updateProfile.setName("Generated profile");

        for (DependencyNode dependencyNode : updatePath) {
            updateProfile.getInstallableUnit().add(0, dependencyNode.getInstallableUnit());
        }

        if (logger.isTraceEnabled())
            logger.trace("Selected update profile with " + updateProfile.getInstallableUnit().size() + " IUs");
        
        return updateProfile;
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates(InstallableUnitType updatable, Collection<UpdateDescriptorType> updates) throws LiveUpdateException {

        List<UpdateDescriptorType> availableUpdates = new ArrayList<UpdateDescriptorType>();
        for (DependencyNode au : buildAvailableUpdates(updatable, updates)) {
            availableUpdates.add(au.getUpdateDescriptor());
        }

        return availableUpdates;
    }

    // --------------------------------------------------< Utilities >

    /**
     * Builds the list of dependencies that can update the given iu
     * @param updatable the IU that needs to be updated.
     * @param updates the updates available for the updatable IU
     */
    protected  Collection<DependencyNode> buildAvailableUpdates(InstallableUnitType updatable, Collection<UpdateDescriptorType> updates) throws LiveUpdateException {
        // Create dependency tree for all possible updates
        DependencyTreeBuilder tb = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> dependencies = tb.buildDependencyList(updates);
        if (logger.isTraceEnabled())
            logger.trace("Processing " + dependencies.size() + " updates");

        DependencyNode updatableNode = tb.getDependency(updatable);

        if (updatableNode == null) {
            logger.warn("Installable Unit not found in updates : " +
                    updatable.getGroup() + "/" + updatable.getName() + "/" + updatable.getVersion());
            return new ArrayList<DependencyNode>();
        }

        AvailableUpdatesBuilder v = new AvailableUpdatesBuilder(updatableNode);
        DependencyWalker<Collection<DependencyNode>> w = new DeepFirstDependencyChildrenWalker<Collection<DependencyNode>>();

        w.walk(updatableNode, v);

        return v.getResult();
    }


    /**
     * Builds the list of update paths.  The list of updates must contain both IUs.
     */
    protected  DependencyNode buildUpdateDependenciesPaths(InstallableUnitType installable,
                                                           Collection<InstallableUnitType> installed,
                                                           Collection<UpdateDescriptorType> updates) throws LiveUpdateException {


        // Nothing to update
        InstallableUnitType updatable = null;
        for (InstallableUnitType i : installed) {
            if (i.getGroup().equals(installable.getGroup()) &&
                    i.getName().equals(installable.getName())) {
                updatable = i;
            }
        }

        // Create dependency tree for all possible updates
        DependencyTreeBuilder tb = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> dependencies = tb.buildDependencyList(updates);
        if (logger.isTraceEnabled())
            logger.trace("Processing " + dependencies.size() + " updates");

        // Look the Installable IU Dependency Node
        DependencyNode installableNode = tb.getDependency(installable);

        // Look the Updatable IU Dependency Node
        DependencyNode updatableNode = tb.getDependency(updatable);

        if (logger.isDebugEnabled())
            logger.debug("Building update paths for " +
                    (updatableNode != null ? updatableNode.getFqKey() : "<NONE>") +  " ==> " +
                    installableNode.getFqKey());

        if (updatableNode != null && !updatableNode.getFqName().equals(installableNode.getFqName())) {
            throw new LiveUpdateException("Installable Unit " + installableNode.getFqKey() +
                    " cannot update " + updatableNode.getFqKey());
        }

        if (installableNode.getUnsatisifed().size() > 0) {
            throw new LiveUpdateException("Cannot install " + installableNode +
                    ".  It has " + installableNode.getUnsatisifed().size() + " unsatisfied dependencies.");
        }

        // Now, build all possible update 'paths' and choose the shorter one.
        UpdateDependenciesPathsBuilder v = new UpdateDependenciesPathsBuilder(installableNode, updatableNode);
        DependencyWalker<DependencyNode> w = new DeepFirstDependencyParentsWalker<DependencyNode>();

        w.walk(installableNode, v);

        return v.getResult();

    }

    // --------------------------------------------------< Properties >
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    // --------------------------------------------------< Inner classes >

}
