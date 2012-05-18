package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.FileRepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSArtifactRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

public class VFSArtifactRepositoryImplTest extends VFSTestSupport {

    private static VFSArtifactRepositoryImpl vfsArtifactRepository;

    private static ArtifactKeyType testArtifactKey;

    private static FileRepositoryTransport fileTransport;
    
    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml",
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml"}
        );

        fileTransport = (FileRepositoryTransport) applicationContext.getBean("fileRepositoryTransport");
        
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

        // reload artifacts
        vfsArtifactRepository.init();
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }
    
    @Test
    public void testGetAvailableArtifacts() throws Exception {
        Collection<ArtifactDescriptorType> artifacts = vfsArtifactRepository.getAvailableArtifacts();
        Assert.assertEquals(artifacts.size(), 2);

        boolean configArtifactFound = false;
        boolean toolingArtifactFound = false;
        for (ArtifactDescriptorType artifactDescriptor : artifacts) {
            ArtifactKeyType artifact = artifactDescriptor.getArtifact();
            Assert.assertEquals(artifact.getGroup(), "com.atricore.idbus.console");
            Assert.assertEquals(artifact.getVersion(), "1.2.1-SNAPSHOT");
            if (artifact.getName().equals("console-config")) {
                configArtifactFound = true;
                Assert.assertEquals(artifact.getType(), "zip");
                Assert.assertEquals(artifact.getClassifier(), "resources");
            } else if (artifact.getName().equals("console-tooling")) {
                toolingArtifactFound = true;
                Assert.assertNull(artifact.getType());
                Assert.assertNull(artifact.getClassifier());
            }
        }

        Assert.assertTrue(configArtifactFound);
        Assert.assertTrue(toolingArtifactFound);
    }

    @Test
    public void testContainsArtifact() throws Exception {
        ArtifactKeyType artifactKey1 = new ArtifactKeyType();
        artifactKey1.setID("id0000000112");
        artifactKey1.setGroup("com.atricore.idbus.console");
        artifactKey1.setName("console-tooling");
        artifactKey1.setVersion("1.2.1-SNAPSHOT");
        boolean containsArtifact1 = vfsArtifactRepository.containsArtifact(artifactKey1);
        Assert.assertTrue(containsArtifact1);

        ArtifactKeyType artifactKey2 = new ArtifactKeyType();
        artifactKey2.setID("id0000000999");
        artifactKey2.setGroup("com.atricore.idbus.console");
        artifactKey2.setName("console-tooling");
        artifactKey2.setVersion("1.3.1-SNAPSHOT");
        boolean containsArtifact2 = vfsArtifactRepository.containsArtifact(artifactKey2);
        Assert.assertFalse(containsArtifact2);
    }

    @Test
    public void testGetArtifact() throws Exception {
        ArtifactKeyType artifactKey1 = new ArtifactKeyType();
        artifactKey1.setID("id0000000111");
        artifactKey1.setGroup("com.atricore.idbus.console");
        artifactKey1.setName("console-config");
        artifactKey1.setVersion("1.2.1-SNAPSHOT");
        artifactKey1.setType("zip");
        artifactKey1.setClassifier("resources");
        InputStream artifact1Stream = vfsArtifactRepository.getArtifact(artifactKey1);
        Assert.assertNotNull(artifact1Stream);

        ArtifactKeyType artifactKey2 = new ArtifactKeyType();
        artifactKey2.setID("id0000000112");
        artifactKey2.setGroup("com.atricore.idbus.console");
        artifactKey2.setName("console-tooling");
        artifactKey2.setVersion("1.2.1-SNAPSHOT");
        InputStream artifact2Stream = vfsArtifactRepository.getArtifact(artifactKey2);
        Assert.assertNotNull(artifact2Stream);
    }

    @Test
    public void testGetArtifactDescriptor() throws Exception {
        ArtifactKeyType artifactKey1 = new ArtifactKeyType();
        artifactKey1.setID("id0000000111");
        artifactKey1.setGroup("com.atricore.idbus.console");
        artifactKey1.setName("console-config");
        artifactKey1.setVersion("1.2.1-SNAPSHOT");
        artifactKey1.setType("zip");
        artifactKey1.setClassifier("resources");
        InputStream artifactDescriptor1Stream = vfsArtifactRepository.getArtifactDescriptor(artifactKey1);
        Assert.assertNotNull(artifactDescriptor1Stream);

        ArtifactKeyType artifactKey2 = new ArtifactKeyType();
        artifactKey2.setID("id0000000112");
        artifactKey2.setGroup("com.atricore.idbus.console");
        artifactKey2.setName("console-tooling");
        artifactKey2.setVersion("1.2.1-SNAPSHOT");
        InputStream artifactDescriptor2Stream = vfsArtifactRepository.getArtifactDescriptor(artifactKey2);
        Assert.assertNotNull(artifactDescriptor2Stream);
    }

    @Test
    public void testAddArtifact() throws Exception {
        Assert.assertEquals(vfsArtifactRepository.getAvailableArtifacts().size(), 2);

        InputStream artifactStream = fileTransport.getContentStream(
                new URI(vfsArtifactRepository.getLocation().toString() + "/" + ArtifactsUtil.getArtifactFilePath(testArtifactKey)));
        InputStream artifactDescriptorStream = fileTransport.getContentStream(
                new URI(vfsArtifactRepository.getLocation().toString() + "/" + ArtifactsUtil.getArtifactDescriptorPath(testArtifactKey)));

        vfsArtifactRepository.addArtifact(testArtifactKey, artifactStream, artifactDescriptorStream);

        Assert.assertEquals(vfsArtifactRepository.getAvailableArtifacts().size(), 3);

        boolean artifactExists = true;
        boolean artifactDescriptorExists = true;
        try {
            artifactStream = vfsArtifactRepository.getArtifact(testArtifactKey);
            artifactDescriptorStream = vfsArtifactRepository.getArtifactDescriptor(testArtifactKey);
        } catch (Exception e) {
            artifactExists = false;
            artifactDescriptorExists = false;
        }
        Assert.assertTrue(artifactExists);
        Assert.assertTrue(artifactDescriptorExists);
    }

    @Test
    public void testRemoveArtifact() throws Exception {
        ArtifactKeyType artifactKey = new ArtifactKeyType();
        artifactKey.setID("id0000000111");
        artifactKey.setGroup("com.atricore.idbus.console");
        artifactKey.setName("console-config");
        artifactKey.setVersion("1.2.1-SNAPSHOT");
        artifactKey.setType("zip");
        artifactKey.setClassifier("resources");

        InputStream artifactStream = vfsArtifactRepository.getArtifact(artifactKey);
        Assert.assertNotNull(artifactStream);

        vfsArtifactRepository.removeArtifact(artifactKey);

        boolean artifactExists = true;
        try {
            artifactStream = vfsArtifactRepository.getArtifact(artifactKey);
        } catch (Exception e) {
            artifactExists = false;
        }
        Assert.assertFalse(artifactExists);
    }

    @Test
    public void testClear() throws Exception {
        vfsArtifactRepository.clear();
        Collection<ArtifactDescriptorType> artifacts = vfsArtifactRepository.getAvailableArtifacts();
        Assert.assertEquals(artifacts.size(), 0);
    }
}
