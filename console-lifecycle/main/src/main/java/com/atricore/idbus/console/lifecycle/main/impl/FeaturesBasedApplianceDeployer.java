package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceState;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceDeployer;
import org.apache.felix.karaf.features.Feature;
import org.apache.felix.karaf.features.FeaturesService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.net.URI;
import java.util.Date;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class FeaturesBasedApplianceDeployer implements ApplianceDeployer, BundleContextAware {

    // TODO : Rely on Spring DM references to obtain a featureservice instance
    private BundleContext bundleContext;

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public boolean isDeployed(IdentityAppliance appliance) throws IdentityServerException {
        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {
            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }

            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();

            for (Feature f : svc.listFeatures()) {
                if (f.getName().equals(applianceDep.getFeatureName()) &&
                        f.getVersion().equals("1.0." + applianceDep.getDeployedRevision())) {
                    return true;
                }
            }

        } catch (Exception e) {
            throw new IdentityServerException("Cannot deploy appliance " + appliance.getId(), e);

        } finally {
            getBundleContext().ungetService(ref);
        }

        return false;
    }

    public boolean isStarted(IdentityAppliance appliance) throws IdentityServerException {
        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {
            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }

            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();

            for (Feature f : svc.listFeatures()) {
                if (f.getName().equals(applianceDep.getFeatureName()) &&
                        f.getVersion().equals("1.0." + applianceDep.getDeployedRevision())) {
                    return  svc.isInstalled(f);
                }
            }

        } catch (Exception e) {
            throw new IdentityServerException("Cannot deploy appliance " + appliance.getId(), e);

        } finally {
            getBundleContext().ungetService(ref);
        }

        return false;
    }

    public IdentityAppliance deploy(IdentityAppliance appliance) throws IdentityServerException {

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {

            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

            if (!appliance.getState().equals(IdentityApplianceState.BUILT.toString()))
                throw new IllegalStateException("Appliance in state " + appliance.getState() + " cannot be undeployed");

            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();
            if (applianceDep == null)
                throw new IdentityServerException("No Appliance Deployment information found for appliance " +
                        appliance.getId());


            svc.addRepository(new URI(applianceDep.getFeatureUri()));
            //svc.installFeature(applianceDep.getFeatureName());

            applianceDep.setDeployedRevision(applianceDef.getRevision());
            applianceDep.setDeploymentTime(new Date());

            appliance.setState(IdentityApplianceState.DEPLOYED.toString());

            return appliance;
        } catch (Exception e) {
            throw new IdentityServerException("Cannot deploy appliance " + appliance.getId(), e);

        } finally {
            getBundleContext().ungetService(ref);
        }
    }

    public IdentityAppliance undeploy(IdentityAppliance appliance) throws IdentityServerException {

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {

            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }

            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();
            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

            if (!appliance.getState().equals(IdentityApplianceState.DEPLOYED.toString()))
                throw new IllegalStateException("Appliance in state " + appliance.getState() + " cannot be undeployed");

            svc.removeRepository(new URI(applianceDep.getFeatureUri()));

            appliance.setIdApplianceDeployment(null); // Clear deployment information!
            appliance.setState(IdentityApplianceState.BUILT.toString());

            return appliance;

        } catch (Exception e) {
            throw new IdentityServerException("Cannot undeploy appliance " + appliance.getId(), e);
        } finally {
            getBundleContext().ungetService(ref);
        }
    }

    public IdentityAppliance start(IdentityAppliance appliance) throws IdentityServerException {

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {


            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }

            IdentityApplianceState state = IdentityApplianceState.valueOf(appliance.getState());
            switch (state) {
                case DEPLOYED:
                    IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();
                    String featureName = applianceDep.getFeatureName();
                    String featureVersion = "1.0." + appliance.getIdApplianceDeployment().getDeployedRevision();
                    try {
                        svc.installFeature(featureName, featureVersion);
                    } catch (Exception e) {
                        throw new IdentityServerException("Cannot start appliance " + appliance.getId() +
                                " using feature " + featureName, e);
                    }
                    appliance.setState(IdentityApplianceState.STARTED.toString());
                    break;
                default:
                    throw new IllegalStateException("Appliance in state " + state + " cannot be started!");
            }

            return appliance;
        } finally {
            getBundleContext().ungetService(ref);
        }
    }

    public IdentityAppliance stop(IdentityAppliance appliance) throws IdentityServerException {

        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            throw new IdentityServerException("Features Service is unavailable. (no service reference)");
        }

        try {

            FeaturesService svc = (FeaturesService) getBundleContext().getService(ref);
            if (svc == null) {
                throw new IdentityServerException("Features Service is unavailable. (no service)");
            }


            IdentityApplianceState state = IdentityApplianceState.valueOf(appliance.getState());
            switch (state) {
                case STARTED:
                    IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();
                    String featureName = applianceDep.getFeatureName();
                    try {
                        svc.uninstallFeature(featureName);
                    } catch (Exception e) {
                        throw new IdentityServerException("Cannot stop appliance " + appliance.getId() +
                                " using feature " + featureName, e);
                    }
                    appliance.setState(IdentityApplianceState.DEPLOYED.toString());

                    break;
                default:
                    throw new IllegalStateException("Appliance in state " + state + " cannot be stopped!");
            }

            return appliance;
        } finally {
            getBundleContext().ungetService(ref);
        }

    }
}
