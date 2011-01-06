package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.FileRepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URI;
import java.util.Collection;

public class VFSMetadataRepositoryImplTest extends VFSTestSupport {

    private static VFSMetadataRepositoryImpl vfsMetadataRepository;

    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml"}
        );

        vfsMetadataRepository = (VFSMetadataRepositoryImpl) applicationContext.getBean("vfsMetadataRepository1");

        // copy test files to repository location
        String baseDir = (String) applicationContext.getBean("baseDir");
        FileObject testUpdatesSrc = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/repo1-updates.xml");
        FileObject testUpdatesDest = getFileSystemManager().resolveFile(vfsMetadataRepository.getLocation().toString());
        testUpdatesDest.createFile();
        testUpdatesDest.copyFrom(testUpdatesSrc, Selectors.SELECT_SELF);
    }

    @Before
    public void setup() throws Exception {
        // copy test files to repository folder
        String baseDir = (String) applicationContext.getBean("baseDir");
        FileObject repo = getFileSystemManager().resolveFile(vfsMetadataRepository.getRepoFolder().toString());
        FileObject sourceDir = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/repos/md/cache/repo1");
        repo.delete(Selectors.EXCLUDE_SELF);
        repo.copyFrom(sourceDir, Selectors.SELECT_ALL);

        // reload descriptors
        vfsMetadataRepository.init();
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
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
        Assert.assertEquals(updates.iterator().next().getID(), "id0000000100");

        // add updates index
        FileRepositoryTransport transport = (FileRepositoryTransport) applicationContext.getBean("fileRepositoryTransport");
        byte[] idxBin = transport.loadContent(new URI(vfsMetadataRepository.getLocation().toString()));
        UpdatesIndexType idx = XmlUtils1.unmarshallUpdatesIndex(new String(idxBin), false);
        vfsMetadataRepository.addUpdatesIndex(idx);

        updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 1);
        Assert.assertEquals(updates.iterator().next().getID(), "id0000000300");
    }

    @Test
    public void testHasUpdate() throws Exception {
        boolean hasUpdate = vfsMetadataRepository.hasUpdate("id0000000100");
        Assert.assertTrue(hasUpdate);

        hasUpdate = vfsMetadataRepository.hasUpdate("id0000000111");
        Assert.assertFalse(hasUpdate);
    }

    @Test
    public void testGetUpdates() throws Exception {
        UpdatesIndexType idx = vfsMetadataRepository.getUpdates();
        Assert.assertNotNull(idx);
        Assert.assertEquals(idx.getUpdateDescriptor().size(), 1);
    }

    @Test
    public void testClear() throws Exception {
        vfsMetadataRepository.clear();
        Collection<UpdateDescriptorType> updates = vfsMetadataRepository.getAvailableUpdates();
        Assert.assertEquals(updates.size(), 0);
    }
}
