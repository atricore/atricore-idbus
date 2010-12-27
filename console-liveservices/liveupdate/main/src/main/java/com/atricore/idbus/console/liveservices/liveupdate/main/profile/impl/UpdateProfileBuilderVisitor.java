package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateProfileBuilderVisitor implements DependencyVisitor {

    // Current setup
    private ProfileType profile;

    private InstallableUnitType currentIu;

    private Stack<DependencyNode> currentUpdateProfile = new Stack();

    private List<ProfileType> updateProfiles = new ArrayList<ProfileType>();

    private boolean found;

    public void before(DependencyNode dep) {

        currentUpdateProfile.push(dep);

        // TODO : Consider group and name
        found = dep.getInstallableUnit().getVersion().equals(currentIu.getVersion());
    }

    public void after(DependencyNode dep) {
        currentUpdateProfile.pop();

        if (found) {
            ProfileType profile = new ProfileType();
            // TODO :
            profile.setID("TBD");
            profile.setName("TBD");

            for (DependencyNode node : currentUpdateProfile) {
                profile.getInstallableUnit().add(node.getInstallableUnit());
            }

            this.updateProfiles.add(profile);
            found = false;
        }

    }

    public boolean walNext(DependencyNode node) {
        return !found;
    }

    protected InstallableUnitType getUpdateableIU() {
        // TODO : Review this !!!!
        return profile.getInstallableUnit().get(0);
    }
}
