package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyVisitor;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateProfileBuilderVisitor implements DependencyVisitor<List<ProfileType>> {

    private UUIDGenerator uuidGen = new UUIDGenerator();

    // Current setup
    private ProfileType profile;

    private InstallableUnitType updatableIu;

    private Stack<DependencyNode> currentUpdateProfile = new Stack();

    private List<ProfileType> updateProfiles = new ArrayList<ProfileType>();

    private boolean found;

    public UpdateProfileBuilderVisitor(ProfileType profile) {
        this.profile = profile;
        // TODO : Review this !!!
        this.updatableIu = profile.getInstallableUnit().get(0);
    }

    public void before(DependencyNode dep) {

        currentUpdateProfile.push(dep);

        found = dep.getInstallableUnit().getGroup().equals(updatableIu.getGroup()) &&
                dep.getInstallableUnit().getName().equals(updatableIu.getName()) &&
                dep.getInstallableUnit().getVersion().equals(updatableIu.getVersion());
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
            found = false;
        }

    }

    public boolean walNext(DependencyNode node) {
        // If we already found the updatable IU , we don't have to keep walking the tree.
        return !found;
    }

    public List<ProfileType> getResult() {
        return this.updateProfiles;
    }
}
