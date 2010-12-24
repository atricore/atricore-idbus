package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.FileRepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URI;
import java.util.Collection;

public class VFSMetadataRepositoryImplTest {

    private VFSMetadataRepositoryImpl vfsMetadataRepository;
    
    private ApplicationContext applicationContext;
    
    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml", 
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml"}
        );

        vfsMetadataRepository = (VFSMetadataRepositoryImpl) applicationContext.getBean("vfsMetadataRepository");
    }

    @Test
    public void testGetAvailableUpdates() throws Exception {
        Collection<UpdateDescriptorType> updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 1);
    }

    @Test
    public void testAddUpdatesIndex() throws Exception {
        Collection<UpdateDescriptorType> updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 1);

        // add updates index
        FileRepositoryTransport transport = (FileRepositoryTransport) applicationContext.getBean("fileRepositoryTransport");
        byte[] idxBin = transport.loadContent(new URI(transport.getBaseFolder() + "/test-updates.xml"));
        UpdatesIndexType idx = XmlUtils1.unmarshallUpdatesIndex(new String(idxBin), false);
        vfsMetadataRepository.addUpdatesIndex(idx);

        updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 2);
    }

    @Test
    public void testHasUpdate() throws Exception {
        boolean hasUpdate = vfsMetadataRepository.hasUpdate("id0000000100");
        Assert.assertTrue(hasUpdate);

        hasUpdate = vfsMetadataRepository.hasUpdate("id0000000111");
        Assert.assertFalse(hasUpdate);
    }

    //s@Test
    public void testClear() throws Exception {
        vfsMetadataRepository.clear();
        Collection<UpdateDescriptorType> updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 0);
    }
}
