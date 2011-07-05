package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnitType;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Provider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceProject;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.ProjectModuleLayout;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.atricore.idbus.bundles.maven.MavenEmbeddedRuntime;
import org.atricore.idbus.bundles.maven.MavenRuntimeExecutionOutcome;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ApplianceBuilderTransformer extends AbstractTransformer implements BundleContextAware {


    private static final Log logger = LogFactory.getLog(ApplianceBuilderTransformer .class);

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
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

            IdApplianceProject prj = event.getContext().getProject();
            IdentityAppliance appliance = prj.getIdAppliance();

            ProjectModuleLayout layout = prj.getRootModule().getLayout();

            FileObject baseDir = layout.getWorkDir();

            if (logger.isDebugEnabled())
                logger.debug("Building appliance at " + baseDir.getURL());

            String[] goals = new String [] { "clean", "deploy" };

            MavenEmbeddedRuntime rt = new MavenEmbeddedRuntime(bundleContext,
                    baseDir.getName().getPath(),
                    Arrays.asList(goals));
            rt.setLocalRepositoryDirectory(System.getProperty("karaf.home") + "/data/work/maven/repository");

            if (logger.isTraceEnabled())
                logger.trace("Created Maven runtime");

            MavenRuntimeExecutionOutcome outcome = rt.doExecute();
            if (logger.isTraceEnabled())
                logger.trace("Executed Maven");

            rt.destroy();

            if (logger.isTraceEnabled())
                logger.trace("Destroyed Maven runtime");

            if (outcome.hasExceptions()) {
                logger.debug("Error building appliance at " + baseDir.getURL());
                throw new TransformException("Error building appliance " +
                                             ((IdentityApplianceDefinition) event.getData()).getName() +
                                             " at " + baseDir.getURL());
            }

            IdentityApplianceDefinition applianceDef = (IdentityApplianceDefinition) event.getData();
            IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();

            IdProjectModule rootModule = event.getContext().getProject().getRootModule();
            // Identity Appliance Module
            IdProjectModule idaModule = getApplianceModule(rootModule);
            if (idaModule == null)
                throw new TransformException("No 'Appliance' module found in project");

            String featureName = applianceDef.getName();
            URI featuresUri = new URI("mvn:" + idaModule.getGroup() +
                    "/" + idaModule.getName() +
                    "/" + idaModule.getVersion() +
                    "/xml/features");

            applianceDep.setFeatureName(featureName);
            applianceDep.setFeatureUri(featuresUri.toString());

            Collection<IdProjectModule> idauModules = getApplianceUnitModules(rootModule);

            applianceDep.getIdaus().clear();

            for (IdProjectModule idauModule : idauModules) {

                IdentityApplianceUnit idau = new IdentityApplianceUnit ();

                // TODO : Need a way to set providers in different IDAUs !!
                // TODO : Clone providers or use a DeployedProvider definition !?!
                List<Provider> providers = new ArrayList<Provider>();
                for (Provider p : applianceDef.getProviders()) {
                    providers.add(p);
                }
                idau.setProviders(providers);

                idau.setDescription(idauModule.getDescription());
                idau.setType(IdentityApplianceUnitType.FEDERATION_UNIT);

                idau.setBundleName(idauModule.getName());
                idau.setGroup(idauModule.getGroup());
                idau.setName(idauModule.getName());
                idau.setVersion(idauModule.getVersion());

                applianceDep.getIdaus().add(idau);
            }

        } catch (Exception e) {
            throw new TransformException(e.getMessage(), e);
        }

        return null;
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
