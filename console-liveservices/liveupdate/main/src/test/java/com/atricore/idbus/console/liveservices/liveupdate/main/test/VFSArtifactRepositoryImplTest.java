package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSArtifactRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

public class VFSArtifactRepositoryImplTest {

    private VFSArtifactRepositoryImpl vfsArtifactRepository;
    
    private ApplicationContext applicationContext;
    
    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/transport-beans.xml", 
                             "classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/repository-beans.xml"}
        );

        vfsArtifactRepository = (VFSArtifactRepositoryImpl) applicationContext.getBean("vfsArtifactRepository");
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
            Assert.assertEquals(artifact.getVersion(), "1.0.0-SNAPSHOT");
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
    public void testGetArtifact() throws Exception {
        ArtifactKeyType artifactKey1 = new ArtifactKeyType();
        artifactKey1.setID("id0000000111");
        artifactKey1.setGroup("com.atricore.idbus.console");
        artifactKey1.setName("console-config");
        artifactKey1.setVersion("1.0.0-SNAPSHOT");
        artifactKey1.setType("zip");
        artifactKey1.setClassifier("resources");
        byte[] artifact1 = vfsArtifactRepository.getArtifact(artifactKey1);
        Assert.assertNotNull(artifact1);

        ArtifactKeyType artifactKey2 = new ArtifactKeyType();
        artifactKey2.setID("id0000000112");
        artifactKey2.setGroup("com.atricore.idbus.console");
        artifactKey2.setName("console-tooling");
        artifactKey2.setVersion("1.0.0-SNAPSHOT");
        byte[] artifact2 = vfsArtifactRepository.getArtifact(artifactKey2);
        Assert.assertNotNull(artifact2);
    }

    @Test
    public void testClear() throws Exception {
        vfsArtifactRepository.clear();
        Collection<ArtifactDescriptorType> artifacts = vfsArtifactRepository.getAvailableArtifacts();
        Assert.assertEquals(artifacts.size(), 0);
    }
}
