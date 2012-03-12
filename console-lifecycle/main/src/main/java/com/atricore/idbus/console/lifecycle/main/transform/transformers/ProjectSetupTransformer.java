package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceProject;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProjectSetupTransformer extends AbstractTransformer {
    
    private String mvnProxyHost;
    
    private String mvnProxyPort;

    private boolean mvnProxyActive;

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // We create an entire IDAU for the project, future versions may create several IDAUs

        IdApplianceProject prj = event.getContext().getProject();
        IdentityApplianceDefinition appliance = (IdentityApplianceDefinition) event.getData();

        String namespace = prj.getIdAppliance().getNamespace() != null ?
                prj.getIdAppliance().getNamespace() : "org.atricore.idbus.appliance";

        event.getContext().put("idaNS", namespace);
        event.getContext().put("idaBasePath", toFolderName(namespace));
        event.getContext().put("idaBasePackage", toPackageName(namespace));

        // ---------------------------------------------------------------------
        // Identity Appliance Unit module for federation
        // ---------------------------------------------------------------------
        // TODO : Support several type of IDAUs
        IdProjectModule federationIdau = new IdProjectModule(namespace, prj.getId(),
                "idau", prj.getDefinition().getDescription(), "1.0." + appliance.getRevision() , "ApplianceUnit");
        IdProjectResource<String> federationIdauPom = new IdProjectResource<String>(idGen.generateId(),
                "pom", "mvn-pom", "federation-idau");
        federationIdauPom.setClassifier("velocity");
        federationIdauPom.setScope(IdProjectResource.Scope.PROJECT);
        federationIdau.addResource(federationIdauPom);

        // ---------------------------------------------------------------------
        // Identity Appliance module for features
        // ---------------------------------------------------------------------
        IdProjectModule idAppliance = new IdProjectModule(namespace, prj.getId(),
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
        // Main project (root)
        // ---------------------------------------------------------------------
        IdProjectModule projectModule = new IdProjectModule(namespace, prj.getId(),
                "project", prj.getDefinition().getDescription(), "1.0." + appliance.getRevision() , "Project");

        IdProjectResource<String> projectPom = new IdProjectResource<String>(idGen.generateId(),
                "pom", "mvn-pom", "project");
        projectPom.setClassifier("velocity");
        projectPom.setScope(IdProjectResource.Scope.PROJECT);

        IdProjectResource<String> projectSettings = new IdProjectResource<String>(idGen.generateId(),
                "settings", "mvn-settings", "project");
        projectSettings.setClassifier("velocity");
        projectSettings.setScope(IdProjectResource.Scope.PROJECT);
        
        Map<String, Object> mvnSettingsParams = new HashMap<String, Object>();
        mvnSettingsParams.put("proxyHost", mvnProxyHost);
        mvnSettingsParams.put("proxyPort", mvnProxyPort);
        mvnSettingsParams.put("proxyActive", mvnProxyActive);
        projectSettings.setParams(mvnSettingsParams);

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

    public String getMvnProxyHost() {
        return mvnProxyHost;
    }

    public void setMvnProxyHost(String mvnProxyHost) {
        this.mvnProxyHost = mvnProxyHost;
    }

    public String getMvnProxyPort() {
        return mvnProxyPort;
    }

    public void setMvnProxyPort(String mvnProxyPort) {
        this.mvnProxyPort = mvnProxyPort;
    }

    public boolean isMvnProxyActive() {
        return mvnProxyActive;
    }

    public void setMvnProxyActive(boolean mvnProxyActive) {
        this.mvnProxyActive = mvnProxyActive;
    }
}

