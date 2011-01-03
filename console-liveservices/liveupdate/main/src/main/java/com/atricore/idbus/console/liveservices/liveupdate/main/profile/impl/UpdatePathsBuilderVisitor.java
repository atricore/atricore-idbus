package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyVisitor;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdatePathsBuilderVisitor implements DependencyVisitor<DependencyNode> {

    private Log logger = LogFactory.getLog(UpdatePathsBuilderVisitor.class);

    private UUIDGenerator uuidGen = new UUIDGenerator();

    // Installable Unit, the unit that will be used as update.
    private DependencyNode installableNode;

    // Updatable Unit, the unit that needs to be updated.
    private DependencyNode updatableNode;

    private Stack<DependencyNode> currentUpdatePath = new Stack();

    private boolean walkNext;

    private boolean found;

    public UpdatePathsBuilderVisitor(DependencyNode installableNode, DependencyNode updatableNode) {
        this.installableNode = installableNode;
        this.updatableNode = updatableNode;
    }

    public void before(DependencyNode dep) {

        try {

            // Previous node in the stack is our parent.
            DependencyNode parent = !currentUpdatePath.empty() ? currentUpdatePath.peek() : null;

            // Is this a different IU or a different version of the IU that will be installed ?

            // 1. Different IU being installed
            if (!dep.getGroup().equals(installableNode.getGroup()) ||
                !dep.getName().equals(installableNode.getName())) {

                // Store this dependency as required by the previous node, this is a different IU
                if (parent  != null)
                    parent.addRequiredDependency(dep);

                walkNext = false;
                found = false;
                return ;
            }

            // 2. Same IU being installed, check if is the udpatable IU,
            if (updatableNode != null) {

                if (dep.getVersion().equals(updatableNode.getVersion())) {
                    // We found the IU we want to update
                    walkNext = false;
                    found = true;

                } else {
                    // We still looking for the IU to update
                    walkNext = true;
                    found = false;
                }

            } else {
                // TODO : Check if this is the last node for this IU.  It could be a valid install path
                walkNext = true;
                found = false;
            }
        } finally {
            // Push the node
            currentUpdatePath.push(dep);
        }
    }

    public void after(DependencyNode dep) {

        // This is the dep node.
        currentUpdatePath.pop();

        // We found the updatable
        if (found) {

            // We found the IU to be updated in the path, this is a possible updateProfile.
            // We have to store it as a candidate and reset the flag.
            List<DependencyNode> updatePath = new ArrayList<DependencyNode>(currentUpdatePath.size());
            updatePath.addAll(currentUpdatePath);
            dep.addUpdatePath(updatePath);

        }

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
