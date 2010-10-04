package org.atricore.idbus.kernel.common.support.osgi.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.ExternalResourcesClassLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ClassLoaderTest {

    private static final Log logger = LogFactory.getLog(ClassLoaderTest.class);

    private static String baseDir ;

    @BeforeClass
    public static void setupTestSuite() {
        baseDir = System.getProperty("baseDir");
        logger.info("Using baseDir=" + baseDir);
    }

    @Test
    public void loadDriverTest() throws Exception {
        logger.debug("loadDriverTest");
        List<String> cp = new ArrayList<String>();
        cp.add("file://" + baseDir + "/src/test/resources");
        ExternalResourcesClassLoader cl = new ExternalResourcesClassLoader(getClass().getClassLoader(), cp, null);
        cl.refreshClasspath();
        
        Class driver = cl.loadClass("org.apache.derby.jdbc.ClientDriver");
    }
}
