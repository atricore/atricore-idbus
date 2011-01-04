package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.ArtifactRepositoryManagerImpl;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSArtifactRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;

public class ArtifactRepositoryManagerImplTest extends VFSTestSupport {

    private static ArtifactRepositoryManagerImpl artRepositoryManager;

    private static VFSArtifactRepositoryImpl vfsArtifactRepository;

    private static ArtifactKeyType testArtifactKey;

    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/manager-beans.xml"}
        );

        vfsArtifactRepository = (VFSArtifactRepositoryImpl) applicationContext.getBean("vfsArtifactRepository");

        // test artifact (for addArtifact() test)
        testArtifactKey = new ArtifactKeyType();
        testArtifactKey.setID("id0000000113");
        testArtifactKey.setGroup("com.atricore.idbus.console");
        testArtifactKey.setName("console-config");
        testArtifactKey.setVersion("1.0");
        testArtifactKey.setType("zip");
        testArtifactKey.setClassifier("resources");

        // copy test artifacts to repository location
        String baseDir = (String) applicationContext.getBean("baseDir");

        // copy artifact
        FileObject testArtifactSrc = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/console-config-1.0-resources.zip");
        FileObject testArtifactDest = getFileSystemManager().resolveFile(ArtifactsUtil.getArtifactFilePath(vfsArtifactRepository.getLocation().toString(), testArtifactKey));
        testArtifactDest.createFile();
        testArtifactDest.copyFrom(testArtifactSrc, Selectors.SELECT_SELF);

        // copy descriptor
        FileObject testArtifactDescriptorSrc = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/main/test/console-config-1.0-resources.xml");
        FileObject testArtifactDescriptorDest = getFileSystemManager().resolveFile(ArtifactsUtil.getArtifactDescriptorPath(vfsArtifactRepository.getLocation().toString(), testArtifactKey));
        testArtifactDescriptorDest.createFile();
        testArtifactDescriptorDest.copyFrom(testArtifactDescriptorSrc, Selectors.SELECT_SELF);
    }

    @Before
    public void setup() throws Exception {
        // copy test files to repository folder
        String baseDir = (String) applicationContext.getBean("baseDir");
        FileObject repo = getFileSystemManager().resolveFile(vfsArtifactRepository.getRepoFolder().toString());
        FileObject sourceDir = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/repos/artifacts/cache/repo1");
        repo.delete(Selectors.EXCLUDE_SELF);
        repo.copyFrom(sourceDir, Selectors.SELECT_ALL);

        artRepositoryManager = (ArtifactRepositoryManagerImpl) applicationContext.getBean("artRepositoryManager");
        artRepositoryManager.addRepository(vfsArtifactRepository);
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }

    @Test
    public void testGetArtifactStream() throws Exception {
        // test artifact will be first validated, downloaded and stored in repo folder
        InputStream artifactStream = artRepositoryManager.getArtifactStream(testArtifactKey);
        Assert.assertNotNull(artifactStream);
    }

    @Test
    public void testClearRepoitories() throws Exception {
        // assert current artifacts
        Assert.assertEquals(artRepositoryManager.getRepositories().iterator().next().getAvailableArtifacts().size(), 2);

        // clear repositories
        artRepositoryManager.clearRepositories();

        Assert.assertEquals(artRepositoryManager.getRepositories().iterator().next().getAvailableArtifacts().size(), 0);
    }

    @Test
    public void testClearRepoitory() throws Exception {
        // assert current artifacts
        Assert.assertEquals(artRepositoryManager.getRepositories().iterator().next().getAvailableArtifacts().size(), 2);

        // clear repo1
        artRepositoryManager.clearRepository("repo1");

        Assert.assertEquals(artRepositoryManager.getRepositories().iterator().next().getAvailableArtifacts().size(), 0);
    }
}
