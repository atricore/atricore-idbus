package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.*;

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
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProfileManagerImpl implements ProfileManager, BundleContextAware {

    // TODO : Liste for bundle events, and look for descriptors !?

    private static final UUIDGenerator uuidGen = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog(ProfileManagerImpl.class);

    private static final String CONTEXT_DIR = "/META-INF/liveservices/";

    private static final String CONTEXT_FILES = "liveupdate-1.0.xml";

    private BundleContext bundleContext;

    private ProfileType profile;

    public ProfileType getCurrentProfile() throws LiveUpdateException {
        if (profile == null)
            profile = buildCurrentProfile();

        return profile;
    }

    public ProfileType buildCurrentProfile() throws LiveUpdateException {

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
            p.setName("_SELF_");
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

        DependencyNode installableNode = buildUpdatePaths(installable, updates);
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
        List<DependencyNode> requiredDependencies = installableNode.getRequiredDependencies();
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
            updateProfile.getInstallableUnit().add(dependencyNode.getInstallableUnit());
        }

        if (logger.isTraceEnabled())
            logger.trace("Selected update profile with " + updateProfile.getInstallableUnit() + " IUs");
        
        return updateProfile;
    }

    public Collection<UpdateDescriptorType> getUpdates(InstallableUnitType installed, Collection<UpdateDescriptorType> updates) {
        buildUpdatePaths(installed, updates);
    }

    // --------------------------------------------------< Utilities >

    /**
     * Builds the list of dependencies that can update the given iu
     * @param iu
     * @param updates
     * @return
     */
    protected  Collection<DependencyNode> buildUpdateDependencies(InstallableUnitType iu, Collection<UpdateDescriptorType> updates) {
        // Create dependency tree for all possible updates
        DependencyTreeBuilder tb = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> dependencies = tb.buildDependencyList(updates);
        if (logger.isTraceEnabled())
            logger.trace("Processing " + dependencies.size() + " updates");

        // Look the IU Dependency Node
        DependencyNode install = tb.getDependency(iu);

        // TODO : define other wwalker , to go from parent to children
        // TODO : define UpdateDependenciesBuilderVisitor
        throw new UnsupportedOperationException("implement me");
    }


    /**
     * Builds the list of update paths
     * @param iu
     * @param updates
     * @return
     */
    protected  DependencyNode buildUpdatePaths(InstallableUnitType iu, Collection<UpdateDescriptorType> updates) {
        // Create dependency tree for all possible updates
        DependencyTreeBuilder tb = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> dependencies = tb.buildDependencyList(updates);
        if (logger.isTraceEnabled())
            logger.trace("Processing " + dependencies.size() + " updates");

        // Look the IU Dependency Node
        DependencyNode install = tb.getDependency(iu);

        // Now, build all possible update 'paths' and choose the shorter one.
        UpdatePathsBuilderVisitor v = new UpdatePathsBuilderVisitor(iu);
        DependencyWalker<List<ProfileType>> w = new DeepFirstDependencyWalker<List<ProfileType>>();

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
