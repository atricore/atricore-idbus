package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.FileRepositoryTransport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URI;

public class FileRepositoryTransportTest {

    private FileRepositoryTransport repositoryTransport;

    private ApplicationContext applicationContext;

    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/beans.xml"}
        );

        repositoryTransport = (FileRepositoryTransport) applicationContext.getBean("fileRepositoryTransport");
    }
    
    @Test
    public void testCanHandle() throws Exception {
        URI uri = new URI("file:///tmp/file.xml");
        Assert.assertTrue(repositoryTransport.canHandle(uri));

        uri = new URI("http://localhost/file.xml");
        Assert.assertFalse(repositoryTransport.canHandle(uri));
    }

    @Test
    public void testLoadContent() throws Exception {
        URI uri = new URI(repositoryTransport.getBaseFolder() + "/updates.xml");
        byte[] content = repositoryTransport.loadContent(uri);
        Assert.assertNotNull(content);
    }
}
