package org.atricore.idbus.capabilities.management.main.transform.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.karaf.features.FeaturesService;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceDeployment;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceUnit;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceUnitType;
import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityApplianceDefinition;
import org.atricore.idbus.capabilities.management.main.exception.TransformException;
import org.atricore.idbus.capabilities.management.main.transform.IdProjectModule;
import org.atricore.idbus.capabilities.management.main.transform.TransformEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Deprecated
public class ApplianceDeployerTransformer extends AbstractTransformer implements BundleContextAware {


    private static final Log logger = LogFactory.getLog(ApplianceBuilderTransformer .class);

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        try {
            IdentityApplianceDefinition applianceDef = (IdentityApplianceDefinition) event.getData();
            IdProjectModule rootModule = event.getContext().getProject().getRootModule();

            // Identity Appliance Module
            IdProjectModule idaModule = getApplianceModule(rootModule);
            if (idaModule == null)
                throw new TransformException("No 'Appliance' module found in project");

            // Identity Appliance Units
            Collection<IdProjectModule> idauModules = getApplianceUnitModules(rootModule);

            IdentityApplianceDeployment applianceDep = event.getContext().getProject().getIdAppliance().getIdApplianceDeployment();
            if (applianceDep == null)
                throw new TransformException("No 'Deployment Definition' found in project");

            String featureName = applianceDef.getName() + "-" + idaModule.getId() + "-idau";
            URI featuresUri = new URI("mvn:" + idaModule.getGroup() +
                    "/" + idaModule.getName() +
                    "/" + idaModule.getVersion() +
                    "/xml/features");

            applianceDep.setFeatureName(featureName);
            applianceDep.setFeatureUri(featuresUri.toString());

            for (IdProjectModule idauModule : idauModules) {
                IdentityApplianceUnit idau = new IdentityApplianceUnit ();
                idau.setDescription(idauModule.getDescription());
                idau.setType(IdentityApplianceUnitType.FEDERATION_UNIT);

                idau.setBundleName(idauModule.getName());
                idau.setGroup(idauModule.getGroup());
                idau.setName(idauModule.getName());
                idau.setVersion(idauModule.getVersion());

                applianceDep.getIdaus().add(idau);
            }

            if (logger.isTraceEnabled())
                    logger.trace("Installing features repository: " + featuresUri);

            System.out.println("Identity Appliance feature name '" + featureName + "'");

            return null;
        } catch (URISyntaxException e) {
            throw new TransformException("Invalid features repository: " + e.getMessage(), e);
        }
    }

    protected IdProjectModule getApplianceModule(IdProjectModule root) {


        for (int i = 0; i < root.getModules().size(); i++) {
            IdProjectModule idProjectModule = root.getModules().get(i);
            if (idProjectModule.getType().equals("Appliance")) {
                return idProjectModule;
            }
        }

        return null;
    }

    protected Collection<IdProjectModule> getApplianceUnitModules(IdProjectModule root) {

        List<IdProjectModule> modules = new ArrayList<IdProjectModule>();


        for (int i = 0; i < root.getModules().size(); i++) {
            IdProjectModule idProjectModule = root.getModules().get(i);
            if (!idProjectModule.getType().equals("Appliance")) {
                modules.add(idProjectModule);
            }
        }

        return modules;
    }

}

