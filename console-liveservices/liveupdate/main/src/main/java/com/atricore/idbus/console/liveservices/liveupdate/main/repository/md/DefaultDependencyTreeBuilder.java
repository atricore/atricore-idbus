package com.atricore.idbus.console.liveservices.liveupdate.main.repository.md;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DefaultDependencyTreeBuilder implements DependencyTreeBuilder {
    public DependencyNode buildDependencyTree(Collection<InstallableUnitType> ius) {
        for (InstallableUnitType iu : ius) {
            DependencyNode n = new DependencyNode(iu);
        }
        return null;
    }
}
