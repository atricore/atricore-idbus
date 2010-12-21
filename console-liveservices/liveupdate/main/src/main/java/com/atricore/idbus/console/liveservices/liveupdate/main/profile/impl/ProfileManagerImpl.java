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
import org.apache.karaf.shell.console.BundleContextAware;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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

    private static final Log logger = LogFactory.getLog(ProfileManagerImpl.class);

    private BundleContext bundleContext;

    public ProfileType getCurrentProfile() throws LiveUpdateException {

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new LiveUpdateException("Features Service is unavailable. (no service reference)");
        }

        try {

            List<InstallableUnitType> profile = new ArrayList<InstallableUnitType>();

            OsgiBundlespaceClassLoader cl = new OsgiBundlespaceClassLoader(bundleContext,
                    getClass().getClassLoader(),
                    bundleContext.getBundle());

            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new LiveUpdateException("Features Service is unavailable. (no service)");
            }

            Enumeration<URL> ius = cl.getResources("META-INF/liveservices/liveupdate-1.0.xml");
            while (ius.hasMoreElements()) {
                URL url = ius.nextElement();

                InputStream is = null;
                try {
                    is = url.openStream();
                    if (is != null) {
                        UpdatesIndexType udIdx = XmlUtils1.unmarshallUpdatesIndex(is, false);
                        for (UpdateDescriptorType  ud : udIdx.getUpdateDescriptor()) {
                            for (InstallableUnitType iu : ud.getInstallableUnit()) {
                                for (FeatureType f : iu.getFeature()) {

                                    // With one feature installed, we consider the IU as installed.
                                    Feature kf = svc.getFeature(f.getName(), f.getVersion().getVersion());
                                    if (svc.isInstalled(kf)) {
                                        profile.add(iu);
                                        break;
                                    }
                                }
                            }
                        }
                    }


                } catch (Exception e) {
                    logger.error("Cannot load LiveUpdate descriptor from " + url + " : " + e.getMessage(), e);
                } finally {
                    if (is != null) try { is.close(); } catch (IOException e) { /**/ }
                }
            }

            ProfileType p = new ProfileType();
            p.setName("_SELF_");
            p.getInstallableUnit().addAll(profile);
            return null;

        } catch (IOException e) {
            throw new LiveUpdateException(e);
        } finally {
                getBundleContext().ungetService(ref);
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
