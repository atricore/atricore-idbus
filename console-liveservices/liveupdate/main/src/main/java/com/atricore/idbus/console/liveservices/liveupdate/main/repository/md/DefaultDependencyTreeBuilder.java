package com.atricore.idbus.console.liveservices.liveupdate.main.repository.md;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DefaultDependencyTreeBuilder implements DependencyTreeBuilder {

    private static final Log logger = LogFactory.getLog(DefaultDependencyTreeBuilder.class);

    protected Map<String, DependencyNode> nodes = new HashMap<String, DependencyNode>();

    protected Map<String, Set<String>> dependenciesByName = new HashMap<String, Set<String>>();

    public Collection<DependencyNode> buildDependencyList(Collection<UpdateDescriptorType> uds) {

        if (logger.isDebugEnabled())
            logger.debug("Generating dependency tree for " + uds.size() + " Updates.");

        nodes.clear();

        for (UpdateDescriptorType ud : uds) {

            if (logger.isTraceEnabled())
                logger.trace("Processing update " + ud.getID() + ":" + ud.getDescription());

            for (InstallableUnitType iu : ud.getInstallableUnit()) {

                if (logger.isTraceEnabled())
                    logger.trace("Processing dependecy for IU " + iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion());

                // New dependency node
                DependencyNode node = new DependencyNode(ud, iu);

                // Old dependency node
                DependencyNode oldNode = nodes.put(node.getFqKey(), node);
                if (oldNode != null) {
                    // Duplicate Installable Unit!
                    // TODO : Use latest upadte ?!
                    if (!node.getUpdateDescriptor().getID().equals(oldNode.getUpdateDescriptor().getID()))
                        logger.warn("Duplicate installable unit found in Update Descriptors : " +
                            ud.getID() + "/" + node.getUpdateDescriptor().getID());
                }

                // Dependencies by name index
                Set<String> deps = dependenciesByName.get(node.getFqName());
                if (deps == null) {
                    deps = new HashSet<String>();
                    dependenciesByName.put(node.getFqName(), deps);
                }
                deps.add(node.getFqKey());

            }
        }

        resolveDependencies();

        if (logger.isDebugEnabled())
            logger.debug("Generated dependency tree for " + uds.size() + " Updates.");

        for (DependencyNode n : nodes.values()) {

            for (RequiredFeatureType unsatisifed : n.getUnsatisifed()) {
                logger.warn("IU " + n.getFqKey() + " can't resolve requirement " +
                        unsatisifed.getGroup() + "/" +
                        unsatisifed.getName() + "/" +
                        unsatisifed.getVersionRange().getExpression());

            }
        }

        return nodes.values();
    }

    protected void resolveDependencies() {

        for (DependencyNode node : nodes.values()) {
            resolveDependencies(node);
        }
    }

    protected void resolveDependencies(DependencyNode node) {

        for (RequiredFeatureType req : node.getUnsatisifed()) {
            String fqName = req.getGroup() + "/" + req.getName();

            Set<String> deps = dependenciesByName.get(fqName);
            if (deps != null) {

                // Make sure that versions match, for now: ignore expression and use equals

                for (String fqKey : deps) {
                    DependencyNode dep = nodes.get(fqKey);
                    // TODO : Support version range
                    if (dep.getVersion().equals(req.getVersionRange().getExpression())) {
                        // Found it!!
                        // Set parent / child
                        node.addDependency(dep, req);
                        dep.addChild(node);
                    }
                }

            }
        }
    }
}
