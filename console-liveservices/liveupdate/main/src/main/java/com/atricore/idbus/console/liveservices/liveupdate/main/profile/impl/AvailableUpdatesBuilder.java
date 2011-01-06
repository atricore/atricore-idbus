package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AvailableUpdatesBuilder implements DependencyVisitor<Collection<DependencyNode>> {

    private DependencyNode updatableNode;

    private Map<String, DependencyNode> availableUpdates = new HashMap<String, DependencyNode>();

    private boolean walkNext;

    public AvailableUpdatesBuilder(DependencyNode updatableNode) {
        this.updatableNode = updatableNode;
    }

    public void before(DependencyNode dep) {
        if (updatableNode.getFqName().equals(dep.getFqName())) {

            // Do not store the updatable node.
            if (!updatableNode.getFqKey().equals(dep.getFqKey()))
                availableUpdates.put(dep.getFqKey(), dep);

            walkNext = true;
        } else {
            walkNext = false;
        }
    }

    public void after(DependencyNode node) {

    }

    public boolean walNext(DependencyNode node) {
        return walkNext;
    }

    public Collection<DependencyNode> getResult() {
        return availableUpdates.values();
    }
}
