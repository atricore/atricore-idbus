package com.atricore.idbus.console.liveservices.liveupdate.main.profile;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface DependencyTreeBuilder {

    Collection<DependencyNode> buildDependencyList(Collection<UpdateDescriptorType> uds);

    DependencyNode getDependency(String fqKey);

    DependencyNode getDependency(InstallableUnitType iu);
}
