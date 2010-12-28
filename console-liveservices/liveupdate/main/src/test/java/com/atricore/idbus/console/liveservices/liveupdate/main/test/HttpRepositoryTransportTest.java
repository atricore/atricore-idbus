package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.HttpRepositoryTransport;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URI;

public class HttpRepositoryTransportTest extends VFSTestSupport {

    private static HttpRepositoryTransport repositoryTransport;

    private static String warPath = "/liveservices/liveupdate/war";

    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml"}
        );

        repositoryTransport = (HttpRepositoryTransport) applicationContext.getBean("httpRepositoryTransport");

        // copy test files to war dir
        String baseDir = (String) applicationContext.getBean("baseDir");
        String buildDir = (String) applicationContext.getBean("buildDir");
        FileObject testUpdatesSrc = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/test-updates.xml");
        FileObject testUpdatesDest = getFileSystemManager().resolveFile(buildDir + warPath + "/test-updates.xml");
        testUpdatesDest.createFile();
        testUpdatesDest.copyFrom(testUpdatesSrc, Selectors.SELECT_SELF);
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }

    @Test
    public void testCanHandle() throws Exception {
        URI uri = new URI("http://localhost/file.xml");
        Assert.assertTrue(repositoryTransport.canHandle(uri));

        uri = new URI("https://localhost/file.xml");
        Assert.assertTrue(repositoryTransport.canHandle(uri));

        uri = new URI("file:///tmp/file.xml");
        Assert.assertFalse(repositoryTransport.canHandle(uri));
    }

    @Test
    public void testLoadContent() throws Exception {
        startJetty();
        URI uri = new URI("http://localhost:8888/test-updates.xml");
        byte[] content = repositoryTransport.loadContent(uri);
        Assert.assertNotNull(content);
    }

    private void startJetty() throws Exception {
        //XmlConfiguration configuration = new XmlConfiguration(new File("/path/to/jetty-config.xml").toURL());
        //Server server = (Server) configuration.configure();

        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setHost("localhost");
        connector.setPort(8888);
        server.addConnector(connector);

        String buildDir = (String) applicationContext.getBean("buildDir");

        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
        wac.setWar(buildDir + warPath);
        
        server.addHandler(wac);
        server.setStopAtShutdown(true);

        server.start();
    }
}
