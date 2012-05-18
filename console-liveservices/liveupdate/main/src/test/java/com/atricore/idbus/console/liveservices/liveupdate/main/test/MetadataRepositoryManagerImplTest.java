package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.FileRepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.MetadataRepositoryManagerImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URI;
import java.util.Collection;

public class MetadataRepositoryManagerImplTest extends VFSTestSupport {

    private static MetadataRepositoryManagerImpl mdRepositoryManager;

    private static VFSMetadataRepositoryImpl vfsMetadataRepository1;
    private static VFSMetadataRepositoryImpl vfsMetadataRepository2;

    private static FileRepositoryTransport fileTransport;
    
    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/manager-beans.xml"}
        );

        fileTransport = (FileRepositoryTransport) applicationContext.getBean("fileRepositoryTransport");

        vfsMetadataRepository1 = (VFSMetadataRepositoryImpl) applicationContext.getBean("vfsMetadataRepository1");
        vfsMetadataRepository2 = (VFSMetadataRepositoryImpl) applicationContext.getBean("vfsMetadataRepository2");

        String baseDir = (String) applicationContext.getBean("baseDir");
        
        // copy test files to repository location
        FileObject testUpdatesSrc1 = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/repo1-updates.xml");
        FileObject testUpdatesDest1 = getFileSystemManager().resolveFile(vfsMetadataRepository1.getLocation().toString());
        testUpdatesDest1.createFile();
        testUpdatesDest1.copyFrom(testUpdatesSrc1, Selectors.SELECT_SELF);

        FileObject testUpdatesSrc2 = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/repo2-updates.xml");
        FileObject testUpdatesDest2 = getFileSystemManager().resolveFile(vfsMetadataRepository2.getLocation().toString());
        testUpdatesDest2.createFile();
        testUpdatesDest2.copyFrom(testUpdatesSrc2, Selectors.SELECT_SELF);
    }

    @Before
    public void setup() throws Exception {
        String baseDir = (String) applicationContext.getBean("baseDir");
        
        // copy test files to repository folder
        FileObject repo1 = getFileSystemManager().resolveFile(vfsMetadataRepository1.getRepoFolder().toString());
        FileObject sourceDir1 = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/repos/md/cache/repo1");
        repo1.delete(Selectors.EXCLUDE_SELF);
        repo1.copyFrom(sourceDir1, Selectors.SELECT_ALL);

        FileObject repo2 = getFileSystemManager().resolveFile(vfsMetadataRepository2.getRepoFolder().toString());
        FileObject sourceDir2 = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/repos/md/cache/repo2");
        repo2.delete(Selectors.EXCLUDE_SELF);
        repo2.copyFrom(sourceDir2, Selectors.SELECT_ALL);

        vfsMetadataRepository1.setSignatureValidationEnabled(false);

        // get new instance of mdRepositoryManager for each test (bean scope is set to "prototype"),
        // because we add a new repository in testAddRepository() and some tests could fail depending of the order
        // in which the tests are called
        mdRepositoryManager = (MetadataRepositoryManagerImpl) applicationContext.getBean("mdRepositoryManager");
        mdRepositoryManager.addRepository(vfsMetadataRepository1);
        mdRepositoryManager.addRepository(vfsMetadataRepository2);
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }

    @Test
    public void testRefreshRepositories() throws Exception {
        // assert current updates
        Collection<UpdateDescriptorType> repo1Updates = vfsMetadataRepository1.getAvailableUpdates();
        Assert.assertEquals(repo1Updates.size(), 1);
        Assert.assertEquals(repo1Updates.iterator().next().getID(), "id0000000100");

        Collection<UpdateDescriptorType> repo2Updates = vfsMetadataRepository2.getAvailableUpdates();
        Assert.assertEquals(repo2Updates.size(), 1);
        Assert.assertEquals(repo2Updates.iterator().next().getID(), "id0000000200");

        // refresh repositories
        vfsMetadataRepository1.setSignatureValidationEnabled(true);  // repo1 will not refresh because it doesn't have a valid certificate
        Collection<UpdateDescriptorType> newUpdates = mdRepositoryManager.refreshRepositories();
        Assert.assertEquals(newUpdates.size(), 1);  // repo2 refreshed (signature validation passed)

        repo2Updates = vfsMetadataRepository2.getAvailableUpdates();
        Assert.assertEquals(repo2Updates.size(), 1);
        Assert.assertEquals(repo2Updates.iterator().next().getID(), "id0000000400");  // updated ID

        // refresh repositories
        vfsMetadataRepository1.setSignatureValidationEnabled(false);
        newUpdates = mdRepositoryManager.refreshRepositories();
        Assert.assertEquals(newUpdates.size(), 1);  // repo1 refreshed

        repo1Updates = vfsMetadataRepository1.getAvailableUpdates();
        Assert.assertEquals(repo1Updates.size(), 1);
        Assert.assertEquals(repo1Updates.iterator().next().getID(), "id0000000300");  // updated ID
    }

    @Test
    public void testRefreshRepository() throws Exception {
        // assert current updates
        Collection<UpdateDescriptorType> repo2Updates = vfsMetadataRepository2.getAvailableUpdates();
        Assert.assertEquals(repo2Updates.size(), 1);
        Assert.assertEquals(repo2Updates.iterator().next().getID(), "id0000000200");

        // refresh repo2
        Collection<UpdateDescriptorType> newUpdates = mdRepositoryManager.refreshRepository("repo2");
        Assert.assertEquals(newUpdates.size(), 1);  // repo2 refreshed (signature validation passed)

        repo2Updates = vfsMetadataRepository2.getAvailableUpdates();
        Assert.assertEquals(repo2Updates.size(), 1);
        Assert.assertEquals(repo2Updates.iterator().next().getID(), "id0000000400");  // updated ID
    }

    @Test
    public void testGetUpdatesIndex() throws Exception {
        UpdatesIndexType idx = mdRepositoryManager.getUpdatesIndex("repo2", false);
        Assert.assertNotNull(idx);
        Assert.assertEquals(idx.getUpdateDescriptor().size(), 1);
        Assert.assertEquals(idx.getUpdateDescriptor().iterator().next().getID(), "id0000000200");

        idx = mdRepositoryManager.getUpdatesIndex("repo2", true);
        Assert.assertNotNull(idx);
        Assert.assertEquals(idx.getUpdateDescriptor().size(), 1);
        Assert.assertEquals(idx.getUpdateDescriptor().iterator().next().getID(), "id0000000400");
    }

    @Test
    public void testGetUpdates() throws Exception {
        Collection<UpdateDescriptorType> updates = mdRepositoryManager.getUpdates();
        Assert.assertEquals(updates.size(), 2);
    }

    @Test
    public void testGetUpdate1() throws Exception {
        UpdateDescriptorType update = mdRepositoryManager.getUpdate("id0000000100");
        Assert.assertNotNull(update);

        update = mdRepositoryManager.getUpdate("id0000000200");
        Assert.assertNotNull(update);

        update = mdRepositoryManager.getUpdate("id0000000300");
        Assert.assertNull(update);
    }

    @Test
    public void testGetUpdate2() throws Exception {
        UpdateDescriptorType update = mdRepositoryManager.getUpdate(
                "com.atricore.josso", "com.atricore.josso.ee.install", "1.2.1-SNAPSHOT");
        Assert.assertNotNull(update);

        update = mdRepositoryManager.getUpdate("com.test", "test", "1.0.0");
        Assert.assertNull(update);
    }

    @Test
    public void testAddRepository() throws Exception {
        String baseDir = (String) applicationContext.getBean("baseDir");
        String buildDir = (String) applicationContext.getBean("buildDir");

        VFSMetadataRepositoryImpl repo = new VFSMetadataRepositoryImpl();
        repo.setId("repo3");
        repo.setName("Repo 3");
        repo.setEnabled(true);
        repo.setLocation(new URI("file://" + buildDir + "/liveservices/liveupdate/repos/md/repo3/repo3-updates.xml"));
        repo.setRepoFolder(new URI("file://" + buildDir + "/liveservices/liveupdate/repos/md/cache/repo3"));

        // delete repository location and folder if they exist
        FileObject repoLocation = getFileSystemManager().resolveFile(repo.getLocation().toString());
        FileObject repoFolder = getFileSystemManager().resolveFile(repo.getRepoFolder().toString());
        if (repoLocation.exists()) {
            repoLocation.delete(Selectors.SELECT_ALL);
        }
        if (repoFolder.exists()) {
            repoFolder.delete(Selectors.SELECT_ALL);
        }

        // add file transport
        fileTransport.setBaseFolder("file://" + buildDir + "/liveservices/liveupdate/repos/md/repo3");
        mdRepositoryManager.getTransports().add(fileTransport);

        // add repository
        mdRepositoryManager.addRepository(repo);

        Assert.assertEquals(mdRepositoryManager.getRepositories().size(), 3);
        Assert.assertEquals(repo.getAvailableUpdates().size(), 0);

        repoFolder = getFileSystemManager().resolveFile(repo.getRepoFolder().toString());
        Assert.assertTrue(repoFolder.exists());
        Assert.assertEquals(repoFolder.getChildren().length, 0);

        // copy test updates index file to repository location
        FileObject testUpdatesSrc = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/repo1-updates.xml");
        FileObject testUpdatesDest = getFileSystemManager().resolveFile(repo.getLocation().toString());
        testUpdatesDest.createFile();
        testUpdatesDest.copyFrom(testUpdatesSrc, Selectors.SELECT_SELF);
        
        // refresh repo
        mdRepositoryManager.refreshRepository("repo3");

        Assert.assertEquals(repo.getAvailableUpdates().size(), 1);
    }

    @Test
    public void testClearRepoitories() throws Exception {
        // assert current updates
        Assert.assertEquals(mdRepositoryManager.getUpdates().size(), 2);
        Assert.assertEquals(vfsMetadataRepository1.getAvailableUpdates().size(), 1);
        Assert.assertEquals(vfsMetadataRepository2.getAvailableUpdates().size(), 1);

        FileObject repo1 = getFileSystemManager().resolveFile(vfsMetadataRepository1.getRepoFolder().toString());
        FileObject repo2 = getFileSystemManager().resolveFile(vfsMetadataRepository2.getRepoFolder().toString());
        Assert.assertEquals(repo1.getChildren().length, 1);
        Assert.assertEquals(repo2.getChildren().length, 1);

        // clear repositories
        mdRepositoryManager.clearRepositories();

        Assert.assertEquals(mdRepositoryManager.getUpdates().size(), 0);
        Assert.assertEquals(vfsMetadataRepository1.getAvailableUpdates().size(), 0);
        Assert.assertEquals(vfsMetadataRepository2.getAvailableUpdates().size(), 0);

        Assert.assertEquals(repo1.getChildren().length, 0);
        Assert.assertEquals(repo2.getChildren().length, 0);
    }

    @Test
    public void testClearRepoitory() throws Exception {
        // assert current updates
        Assert.assertEquals(mdRepositoryManager.getUpdates().size(), 2);
        Assert.assertEquals(vfsMetadataRepository1.getAvailableUpdates().size(), 1);
        
        FileObject repo1 = getFileSystemManager().resolveFile(vfsMetadataRepository1.getRepoFolder().toString());
        Assert.assertEquals(repo1.getChildren().length, 1);
        
        // clear repo1
        mdRepositoryManager.clearRepository("repo1");

        Assert.assertEquals(mdRepositoryManager.getUpdates().size(), 1);
        Assert.assertEquals(vfsMetadataRepository1.getAvailableUpdates().size(), 0);

        Assert.assertEquals(repo1.getChildren().length, 0);
    }
}
