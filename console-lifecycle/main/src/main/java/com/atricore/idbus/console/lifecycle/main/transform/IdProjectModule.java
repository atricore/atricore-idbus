package com.atricore.idbus.console.lifecycle.main.transform;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdProjectModule {

    private String id;

    private String name;

    private String group;

    private String description;

    private String version;

    private String type;

    private IdentityAppliance idAppliance;

    private IdentityApplianceUnit idau;

    private IdProjectModule parent;

    private Map<String, IdProjectResource> resources;

    private Map<String, IdProjectSource> sources;

    private List<IdProjectModule> modules;

    private ProjectModuleLayout layout;

    private List<String> embeddedDependencies;

    // Module path will be used when creating resources/sources, based on the project layout.
    private String path;

    private String pkg;


    public IdProjectModule(String id) {
        this.id = id;
        modules = new ArrayList<IdProjectModule>();
        resources = new HashMap<String, IdProjectResource>();
        sources = new HashMap<String, IdProjectSource>();
        embeddedDependencies = new ArrayList<String>();
    }

    public IdProjectModule(String ns, String groupId, String id, String description, String version, String type) {
        this(id);
        this.description = description;
        this.version = version;
        this.type = type;
        this.group = ns + "." + groupId;
        this.name = group + "." + id;
        modules = new ArrayList<IdProjectModule>();
        resources = new HashMap<String, IdProjectResource>();
        embeddedDependencies = new ArrayList<String>();
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IdentityApplianceUnit getIdau() {
        return idau;
    }

    public void setIdau(IdentityApplianceUnit idau) {
        this.idau = idau;
    }

    public IdentityAppliance getIdAppliance() {
        return idAppliance;
    }

    public void setIdAppliance(IdentityAppliance idAppliance) {
        this.idAppliance = idAppliance;
    }

    public IdProjectModule getParent() {
        return parent;
    }

    public void setParent(IdProjectModule parent) {
        this.parent = parent;
    }

    public ProjectModuleLayout getLayout() {
        return layout;
    }

    public void setLayout(ProjectModuleLayout layout) {
        this.layout = layout;
    }

    public void addResource(IdProjectResource r) {
        this.resources.put(r.getId(), r);
    }

    public Collection<IdProjectResource> getResources() {
        return resources.values();
    }

    public IdProjectResource getResourceById(String id) {
        return resources.get(id);
    }

    public IdProjectResource getResourceByName(String name) {
        for (IdProjectResource idProjectResource : resources.values()) {
            if (idProjectResource.getName().equals(name))
                return idProjectResource;
        }

        return null;
    }

    public void addSource(IdProjectSource r) {
        this.sources.put(r.getId(), r);
    }

    public Collection<IdProjectSource> getSources() {
        return sources.values();
    }

    public IdProjectSource getSourceById(String id) {
        return sources.get(id);
    }

    public IdProjectSource getSourceByName(String name) {
        for (IdProjectSource idProjectSource : sources.values()) {
            if (idProjectSource.getName().equals(name))
                return idProjectSource;
        }

        return null;
    }


    public List<IdProjectModule> getModules() {
        return modules;
    }

    public void addEmbeddedDependency(String dependency) {
        this.embeddedDependencies.add(dependency);
    }

    public List<String> getEmbeddedDependencies() {
        return embeddedDependencies;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPackage() {
        return pkg;
    }

    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    @Override
    public String toString() {
        return id + "/" + version  + "/" +  type;
    }

}
