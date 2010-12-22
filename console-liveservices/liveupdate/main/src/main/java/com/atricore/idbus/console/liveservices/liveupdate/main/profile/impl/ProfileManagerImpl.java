package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.ProfileManager;
import com.atricore.liveservices.liveupdate._1_0.md.FeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
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
                                            iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion().getVersion() +
                                            " [" + iu.getUpdateNature() + "]");

                                for (FeatureType f : iu.getFeature()) {

                                    if (logger.isTraceEnabled())
                                        logger.trace("Found Feature " + f.getGroup()  + "/" + f.getName() + "/" + f.getVersion());

                                    // With one feature installed, we consider the IU as installed.
                                    Feature kf = svc.getFeature(f.getName(), f.getVersion().getVersion());

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
                                        logger.trace("Karaf Feature NOT found for LiveUpdate Feature " + f.getGroup() +
                                                "/" + f.getName() + "/" + f.getVersion().getVersion());
                                    }
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
            p.setID("id001");
            p.setName("_SELF_");
            p.getInstallableUnit().addAll(uis);

            this.profile = p;

            return profile;

        } catch (Exception e) {
            throw new LiveUpdateException(e);
        } finally {
          bundleContext.ungetService(ref);
        }
    }

    public ProfileType createProfile(ProfileType original, Collection<UpdateDescriptorType> updates) {
        return null;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
