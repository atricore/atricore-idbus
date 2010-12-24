package com.atricore.idbus.console.liveservices.liveupdate.main.repository.md;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DependencyNode   {

    private String fqName;

    private String fqKey;

    private Map<String, RequiredFeatureType> unsatisfied = new HashMap<String, RequiredFeatureType>();

    // Update Descriptor
    private UpdateDescriptorType ud;

    // IUs providing this feature
    private InstallableUnitType iu;

    // Nodes depending on us
    private List<DependencyNode> children = new ArrayList<DependencyNode>();

    // Nodes we depend on
    private List<DependencyNode> parents = new ArrayList<DependencyNode>();


    public DependencyNode(UpdateDescriptorType ud, InstallableUnitType iu) {
        this.ud = ud;
        this.iu = iu;
        this.fqKey = iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion();
        this.fqName =  iu.getGroup() + "/" + iu.getName();

        if (iu.getRequirement() != null) {

            for (RequiredFeatureType req : iu.getRequirement()) {
                String reqFqKey = req.getGroup() + "/" + req.getName() + "/" + req.getVersionRange().getExpression();
                unsatisfied.put(reqFqKey, req);
            }
        }
    }

    public String getFqName() {
        return fqName;
    }

    public String getFqKey() {
        return fqKey;
    }

    public Collection<RequiredFeatureType> getUnsatisifed() {
        ArrayList<RequiredFeatureType> reqs = new ArrayList<RequiredFeatureType>();
        reqs.addAll(unsatisfied.values());
        return reqs;
    }

    public String getGroup() {
        return iu.getGroup();
    }

    public String getName() {
        return iu.getName();
    }

    public String getVersion() {
        return iu.getVersion();
    }

    public void addDependency(DependencyNode dep, RequiredFeatureType req) {
        this.parents.add(dep);
        this.unsatisfied.remove(req.getGroup() + "/" + req.getName() + "/" + req.getVersionRange().getExpression());
    }

    public void addChild(DependencyNode node) {
        this.children.add(node);
    }

    public UpdateDescriptorType getUpdateDescriptor() {
        return ud;
    }

    public InstallableUnitType getInstallableUnit() {
        return iu;
    }

    public Collection<DependencyNode> getParents() {
        return parents;
    }

    public Collection<DependencyNode> getChildren() {
        return children;
    }

}
