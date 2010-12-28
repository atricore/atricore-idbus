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

    // TODO : Keep track of container bundle
    // TODO : Serialize and store ?!
    private ProfileType profile;

    public ProfileType getCurrentProfile() throws LiveUpdateException {

        if (profile != null)
            return profile;

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

                            for (InstallableUnitType iu : ud.getInstallableUnit()) {

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
                                        break;
                                    }
                                } else {
                                    logger.trace("Karaf Feature NOT found for LiveUpdate Feature " + iu.getGroup() +
                                            "/" + iu.getName() + "/" + iu.getVersion());
                                }
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

            // Only store profile if any IU was found ...
            if (p.getInstallableUnit().size() > 0) {
                this.profile = p;
            }

            return p;

        } catch (Exception e) {
            throw new LiveUpdateException(e);
        } finally {
          bundleContext.ungetService(ref);
        }
    }

    /**
     * Builds the profile containing all the necessary updates to install the provided IU in our the current setup
     */
    public ProfileType buildUpdateProfile(UpdateDescriptorType update, Collection<UpdateDescriptorType> updates) throws LiveUpdateException {

        List<UpdateDescriptorType> uds = new ArrayList<UpdateDescriptorType>(updates);

        boolean found = false;
        for (UpdateDescriptorType u : updates) {
            if (u.getID().equals(update.getID())) {
                found = true;
                break;
            }
        }

        if (!found) {
            if (logger.isDebugEnabled())
                logger.debug("Requested update is not part of updates collection, adding it !");
            uds.add(update);
        }

        // Create dependency tree
        DependencyTreeBuilder tb = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> dependencies = tb.buildDependencyList(uds);

        if (logger.isTraceEnabled())
            logger.trace("Processing " + dependencies.size() + " updates");

        // Look the IU Dependency Node
        // TODO : What if there's more than one IU ?
        InstallableUnitType iu = update.getInstallableUnit().get(0);
        DependencyNode install = tb.getDependency(iu);

        // Now, build all possible update 'paths' and choose the shorter one.
        DependencyVisitor<List<ProfileType>> v = new UpdateProfileBuilderVisitor(getCurrentProfile());
        DependencyWalker<List<ProfileType>> w = new DeepFirstDependencyWalker<List<ProfileType>>();

        // Each profile is a different way to install the desire update in our setup
        List<ProfileType> profiles = w.walk(install, v);
        if (logger.isTraceEnabled())
            logger.trace("Found " + profiles.size() + " possible update profiles (strategies)");        

        // Choose the shorter path, the one that requires the fewer installable units
        ProfileType updateProfile = null;
        for (ProfileType p : profiles) {
            if (updateProfile == null || p.getInstallableUnit().size() < updateProfile.getInstallableUnit().size()) {
                updateProfile = p;
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("Selected update profile with " + updateProfile.getInstallableUnit() + " IUs");
        return updateProfile;
    }

    // --------------------------------------------------< Utilities >

    // --------------------------------------------------< Properties >
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
