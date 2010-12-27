package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

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

    private boolean init = false;

    /**
     * Builds a list with all available updates
     * @param uds
     * @return
     */
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

        init = true;

        return nodes.values();
    }


    public DependencyNode getDependency(InstallableUnitType iu) {
        return getDependency(iu.getGroup() + "/" + iu.getName() + "/" + iu.getVersion());
    }

    public DependencyNode getDependency(String fqKey) {
        if (!init)
            throw new IllegalStateException("Tree builder not initialized!");
        return nodes.get(fqKey);
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
                for (String fqKey : deps) {
                    DependencyNode dep = nodes.get(fqKey);

                    try {
                        if (versionsMatch(dep.getArtifactVersion(), req.getVersionRange().getExpression())) {
                            // Found it!!
                            // Set parent / child
                            node.addDependency(dep, req);
                            dep.addChild(node);
                        }
                    } catch (InvalidVersionSpecificationException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
        }
    }
    /**
     * True if the given version matches the version expression.
     * We use maven conventions (i.e. [2.0,2.1) refers to all 2.0.x versions)
     *
     * Some spec examples are
     * <ul>
     *   <li><code>1.0</code> Version 1.0</li>
     *   <li><code>[1.0,2.0)</code> Versions 1.0 (included) to 2.0 (not included)</li>
     *   <li><code>[1.0,2.0]</code> Versions 1.0 to 2.0 (both included)</li>
     *   <li><code>[1.5,)</code> Versions 1.5 and higher</li>
     *   <li><code>(,1.0],[1.2,)</code> Versions up to 1.0 (included) and 1.2 or higher</li>
     * </ul>

     */
    protected boolean versionsMatch(ArtifactVersion version, String expression)
            throws InvalidVersionSpecificationException {


        boolean match = false;
        if (expression.indexOf(',') > 0) {
            VersionRange vr = VersionRange.createFromVersionSpec(expression);
            match = vr.containsVersion(version);
        } else {
            ArtifactVersion v = new ArtifactVersion(expression);
            match = version.equals(v);
        }

        if (logger.isTraceEnabled())
            logger.trace("Matching " + version + " with " + expression + ":" + match);

        return match;

    }
}
