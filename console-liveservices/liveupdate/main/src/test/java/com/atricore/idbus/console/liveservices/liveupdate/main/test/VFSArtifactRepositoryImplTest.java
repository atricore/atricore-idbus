package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSArtifactRepositoryImpl;
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
        Collection<ArtifactKeyType> artifacts = vfsArtifactRepository.getAvailableArtifacts();
        Assert.assertEquals(artifacts.size(), 2);

        boolean configArtifactFound = false;
        boolean toolingArtifactFound = false;
        for (ArtifactKeyType artifact : artifacts) {
            Assert.assertEquals(artifact.getGroup(), "com.atricore.idbus.console");
            Assert.assertEquals(artifact.getVersion(), "1.0.0-SNAPSHOT");
            if (artifact.getName().equals("console-config")) {
                configArtifactFound = true;
                Assert.assertEquals(artifact.getType(), "zip");
                Assert.assertEquals(artifact.getClassifier(), "resources");
            } else if (artifact.getName().equals("console-tooling")) {
                toolingArtifactFound = true;
                Assert.assertEquals(artifact.getType(), "jar");
                Assert.assertEquals(artifact.getClassifier(), "");
            }
        }

        Assert.assertTrue(configArtifactFound);
        Assert.assertTrue(toolingArtifactFound);
    }
}
