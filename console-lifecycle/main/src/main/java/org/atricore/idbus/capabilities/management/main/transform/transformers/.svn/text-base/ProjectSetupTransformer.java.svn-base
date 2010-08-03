package org.atricore.idbus.capabilities.management.main.transform.transformers;

import org.atricore.idbus.capabilities.management.main.exception.TransformException;
import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityApplianceDefinition;
import org.atricore.idbus.capabilities.management.main.transform.IdApplianceProject;
import org.atricore.idbus.capabilities.management.main.transform.IdProjectModule;
import org.atricore.idbus.capabilities.management.main.transform.IdProjectResource;
import org.atricore.idbus.capabilities.management.main.transform.TransformEvent;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProjectSetupTransformer extends AbstractTransformer {

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // We create an entire IDAU for the project, future versions may create several IDAUs

        IdApplianceProject prj = event.getContext().getProject();
        IdentityApplianceDefinition appliance = (IdentityApplianceDefinition) event.getData();


        // TODO : Define several IDAU types

        // ---------------------------------------------------------------------
        // Identity Appliance Unit module for federation
        // ---------------------------------------------------------------------
        IdProjectModule federationIdau = new IdProjectModule(prj.getId(),
                "federation", prj.getDefinition().getDescription(), "1.0." + appliance.getRevision() , "Federation");
        IdProjectResource<String> federationIdauPom = new IdProjectResource<String>(idGen.generateId(),
                "pom", "mvn-pom", "federation-idau");
        federationIdauPom.setClassifier("velocity");
        federationIdauPom.setScope(IdProjectResource.Scope.PROJECT);
        federationIdau.addResource(federationIdauPom);

        // ---------------------------------------------------------------------
        // Identity Appliance module for features
        // ---------------------------------------------------------------------
        IdProjectModule idAppliance = new IdProjectModule(prj.getId(),
                "features", prj.getDefinition().getDescription(), "1.0." + appliance.getRevision() , "Appliance");
        IdProjectResource<String> idAppliancePom = new IdProjectResource<String>(idGen.generateId(),
                "pom", "mvn-pom", "appliance");
        idAppliancePom.setClassifier("velocity");
        idAppliancePom.setScope(IdProjectResource.Scope.PROJECT);
        idAppliance.addResource(idAppliancePom);

        IdProjectResource<String> idApplianceFeatures = new IdProjectResource<String>(idGen.generateId(),
                prj.getDefinition().getName() + ".identity-appliance-features", "idbus-features", "appliance");
        idApplianceFeatures.setClassifier("velocity");
        idAppliance.addResource(idApplianceFeatures);

        // ---------------------------------------------------------------------
        // Main project
        // ---------------------------------------------------------------------
        IdProjectModule projectModule = new IdProjectModule(prj.getId(),
                "project", prj.getDefinition().getDescription(), "1.0." + appliance.getRevision() , "Project");

        IdProjectResource<String> projectPom = new IdProjectResource<String>(idGen.generateId(),
                "pom", "mvn-pom", "project");
        projectPom.setClassifier("velocity");
        projectPom.setScope(IdProjectResource.Scope.PROJECT);

        IdProjectResource<String> projectSettings = new IdProjectResource<String>(idGen.generateId(),
                "settings", "mvn-settings", "project");
        projectSettings.setClassifier("velocity");
        projectSettings.setScope(IdProjectResource.Scope.PROJECT);

        projectModule.addResource(projectPom);
        projectModule.addResource(projectSettings);       


        // Wire submodules
        projectModule.getModules().add(idAppliance);
        idAppliance.setParent(projectModule);

        projectModule.getModules().add(federationIdau);
        federationIdau.setParent(projectModule);

        // Add main module to project
        prj.setRootModule(projectModule);

        // Set current module as federation Idau ...
        event.getContext().setCurrentModule(federationIdau);
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        //event.getContext().setCurrentModule(null);

        return event.getContext().getProject();
    }
}

