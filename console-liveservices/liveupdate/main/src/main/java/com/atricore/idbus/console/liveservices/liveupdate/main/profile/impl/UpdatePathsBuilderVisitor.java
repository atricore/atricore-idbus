package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyVisitor;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdatePathsBuilderVisitor implements DependencyVisitor<DependencyNode> {

    private UUIDGenerator uuidGen = new UUIDGenerator();

    // Current setup
    private ProfileType profile;

    private InstallableUnitType updateableIu;

    private DependencyNode updatableNode;

    private Stack<DependencyNode> currentUpdatePath = new Stack();

    private List<DependencyNode> requireDependecies = new ArrayList<DependencyNode>();

    private boolean walkNext;

    private boolean found;

    public UpdatePathsBuilderVisitor(InstallableUnitType  iu) {
        this.updateableIu = iu;

    }

    public void before(DependencyNode dep) {

        currentUpdatePath.push(dep);

        if (!dep.getInstallableUnit().getGroup().equals(updateableIu.getGroup()) ||
            !dep.getInstallableUnit().getName().equals(updateableIu.getName())) {

            // Dependency on another IU type !! (like a 3rd party lib ...)
            requireDependecies.add(dep);
            walkNext = false;

        } else {
            walkNext = !dep.getInstallableUnit().getVersion().equals(updateableIu.getVersion());
            updatableNode = dep;

            found = !walkNext;
        }
    }

    public void after(DependencyNode dep) {
        currentUpdatePath.pop();

        if (found) {

            // We found the IU to be updated in the path, this is a possible updateProfile.
            // We have to store it as a candidate and reset the flag.
            List<DependencyNode> updatePath = new ArrayList<DependencyNode>(currentUpdatePath.size());
            updatePath.addAll(currentUpdatePath);
            dep.addUpdatePath(updatePath);

        }

        requireDependecies.clear();

        // Reset walk next
        walkNext = true;

    }

    public boolean walNext(DependencyNode node) {
        // If we already found the updatable IU , we don't have to keep walking the tree.
        return walkNext;
    }

    public DependencyNode getResult() {
        return this.updatableNode;
    }

}
