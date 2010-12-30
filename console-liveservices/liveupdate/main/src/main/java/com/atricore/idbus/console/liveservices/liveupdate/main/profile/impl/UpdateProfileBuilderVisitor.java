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
public class UpdateProfileBuilderVisitor implements DependencyVisitor<List<ProfileType>> {

    private UUIDGenerator uuidGen = new UUIDGenerator();

    // Current setup
    private ProfileType profile;

    private InstallableUnitType updateableIu;

    private Stack<DependencyNode> currentUpdateProfile = new Stack();

    private List<ProfileType> updateProfiles = new ArrayList<ProfileType>();

    private List<DependencyNode> requireDependecies = new ArrayList<DependencyNode>();

    private boolean walkNext;

    private boolean found;

    public UpdateProfileBuilderVisitor(InstallableUnitType  iu) {
        this.updateableIu = iu;

    }

    public void before(DependencyNode dep) {

        currentUpdateProfile.push(dep);

        if (!dep.getInstallableUnit().getGroup().equals(updateableIu.getGroup()) ||
            !dep.getInstallableUnit().getName().equals(updateableIu.getName())) {

            // Dependency on another IU type !! (like a 3rd party lib ...)
            requireDependecies.add(dep);
            walkNext = false;

        } else {
            walkNext = !dep.getInstallableUnit().getVersion().equals(updateableIu.getVersion());
            found = !walkNext;
        }
    }

    public void after(DependencyNode dep) {
        currentUpdateProfile.pop();

        if (found) {

            // We found the IU to be updated in the path, this is a possible updateProfile.
            // We have to store it as a candidate and reset the flag.
            ProfileType profile = new ProfileType();
            profile.setID(uuidGen.generateId());
            profile.setName("gen-profile-" + (this.updateProfiles.size() + 1));

            for (DependencyNode node : currentUpdateProfile) {
                profile.getInstallableUnit().add(node.getInstallableUnit());
            }

            this.updateProfiles.add(profile);
        }

        // Reset walk next
        walkNext = true;

    }

    public boolean walNext(DependencyNode node) {
        // If we already found the updatable IU , we don't have to keep walking the tree.
        return walkNext;
    }

    public List<ProfileType> getResult() {
        return this.updateProfiles;
    }

    public List<DependencyNode> getRequireDeps() {
        return requireDependecies;
    }
}
