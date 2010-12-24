package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DefaultDependencyTreeBuilder;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.md.DependencyTreeBuilder;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DependencyTreeTest {

    private static final Log logger = LogFactory.getLog(DependencyTreeTest.class);

    private ApplicationContext applicationContext;

    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/dependency-tree-test-beans.xml"}
        );
    }

    @Test
    public void testDependencyTree01() throws Exception{

        // Get update Index descriptor
        InputStream is = getClass().getResourceAsStream("/com/atricore/idbus/console/liveservices/liveupdate/main/test/dependency-tree-test-update-01.xml");
        UpdatesIndexType udIdx = XmlUtils1.unmarshallUpdatesIndex(is, false);

        // Build dependency graph
        DependencyTreeBuilder b = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> nodes = b.buildDependencyList(udIdx.getUpdateDescriptor());

        // Check the outcome, we should have versions from 2.0.0 to 2.0.3
        assert nodes.size() == 4 : "Invalid number of nodes " + nodes.size() + ", expected 4";

        // Check the tree structure for each version
        checkTree01(nodes);

    }

    @Test
    public void testDependencyTree02() throws Exception{

        // Get update Index descriptor
        InputStream is = getClass().getResourceAsStream("/com/atricore/idbus/console/liveservices/liveupdate/main/test/dependency-tree-test-update-02.xml");
        UpdatesIndexType udIdx = XmlUtils1.unmarshallUpdatesIndex(is, false);

        // Build dependency graph
        DependencyTreeBuilder b = new DefaultDependencyTreeBuilder();
        Collection<DependencyNode> nodes = b.buildDependencyList(udIdx.getUpdateDescriptor());

        // Check the outcome, we should have versions from 2.0.0 to 2.0.3
        assert nodes.size() == 5 : "Invalid number of nodes " + nodes.size() + ", expected 5";

        // Check the tree structure for each version
        //checkTree01(nodes);

    }


    /**
     * This will check versions 2.0.0, 2.0.1, 2.0.2 and 2.0.3 only, in that order!
     * @param nodes
     */
    protected void checkTree01(Collection<DependencyNode> nodes) {

        int major = 2;
        int minor = 0;

        DependencyNode n = getDependency(nodes, "2.0.0");

        for (int patch = 0 ; patch < 4 ; patch++) {

            String v = major + "." + minor + "." + patch;

            assert n.getVersion().equals(v) : "Invalid dependency version " + n.getVersion() +
                    " for " + n.getFqKey() + ", expected " + v;

            Collection<DependencyNode> children = n.getChildren();
            assert (patch < 3 && children.size() == 1) || (patch == 3 && children.size() == 0)
                    : "Invalid number of children for " + n.getVersion() + " " + children.size();

            Collection<DependencyNode> parents = n.getParents();
            assert (patch > 0 && parents.size() ==1) || (patch == 0 && parents.size() == 0)
                    : "Invalid number of parents for " + n.getVersion() + " " + children.size();

            // Move forward on the tree.
            if (patch < 3)
                n = children.iterator().next();



        }

    }

    protected DependencyNode getDependency(Collection<DependencyNode> nodes, String version) {
        for (DependencyNode node : nodes) {
            if (node.getVersion().equals(version))
                return node;
        }
        return null;
    }

}
