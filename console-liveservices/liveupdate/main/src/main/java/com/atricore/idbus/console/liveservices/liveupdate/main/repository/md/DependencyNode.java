package com.atricore.idbus.console.liveservices.liveupdate.main.repository.md;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DependencyNode   {

    private String fqName;

    private String fqKey;

    // IUs providing this feature
    private InstallableUnitType iu;

    // Nodes depending on us
    private List<DependencyNode> children = new ArrayList<DependencyNode>();

    // Nodes we depend on
    private List<DependencyNode> parents = new ArrayList<DependencyNode>();


    public DependencyNode(InstallableUnitType iu) {
        this.iu = iu;
        this.fqKey = iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion();
        this.fqName =  iu.getGroup() + "/" + iu.getName();
    }
}
