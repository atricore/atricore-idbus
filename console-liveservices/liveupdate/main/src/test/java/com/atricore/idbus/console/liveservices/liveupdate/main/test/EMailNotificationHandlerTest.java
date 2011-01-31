package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.EMailNotificationHandler;
import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.EMailNotificationScheme;
import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.PropertiesEMailNotificationSchemeStore;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateNatureType;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class EMailNotificationHandlerTest extends VFSTestSupport {

    protected static EMailNotificationHandler handler;
    
    protected static EMailNotificationScheme scheme;

    protected static PropertiesEMailNotificationSchemeStore store;

    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/notification-beans.xml"}
        );

        store = (PropertiesEMailNotificationSchemeStore) applicationContext.getBean("emailNotificationStore");
        scheme = (EMailNotificationScheme) applicationContext.getBean("emailNotificationScheme");
        handler = (EMailNotificationHandler) applicationContext.getBean("emailNotificationHandler");
    }

    @Before
    public void setup() throws Exception {
        // copy test files to notifications folder
        String baseDir = (String) applicationContext.getBean("baseDir");
        FileObject storeDir = getFileSystemManager().resolveFile(store.getBaseFolder().toString());
        FileObject sourceDir = getFileSystemManager().resolveFile(baseDir + "/src/test/resources/com/atricore/idbus/console/liveservices/liveupdate/notifications/email");
        storeDir.delete(Selectors.EXCLUDE_SELF);
        storeDir.copyFrom(sourceDir, Selectors.SELECT_ALL);
    }
    
    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }

    @Test
    public void testCanHandle() throws Exception {
        Assert.assertTrue(handler.canHandle(scheme));
    }

    // To test email notification, uncomment @Test and configure smtp username/password in notification-beans.xml
    //@Test
    public void testNotify() throws Exception {
        List<UpdateDescriptorType> updates = new ArrayList<UpdateDescriptorType>();

        UpdateDescriptorType update1 = new UpdateDescriptorType();
        update1.setID("id-v1_0_1");
        update1.setDescription("LiveUpdate Test 1.0.1");
        InstallableUnitType iu1 = new InstallableUnitType();
        iu1.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        iu1.setName("josso-ee-liveupdate-test");
        iu1.setVersion("1.0.1");
        iu1.setUpdateNature(UpdateNatureType.UPGRADE);
        ArtifactKeyType artifact1 = new ArtifactKeyType();
        artifact1.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        artifact1.setName("liveupdate-test");
        artifact1.setVersion("1.0.1");
        artifact1.setType("tar.gz");
        iu1.getArtifact().add(artifact1);
        ArtifactKeyType artifact2 = new ArtifactKeyType();
        artifact2.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        artifact2.setName("liveupdate-test");
        artifact2.setVersion("1.0.1");
        artifact2.setType("zip");
        iu1.getArtifact().add(artifact2);
        update1.setInstallableUnit(iu1);
        updates.add(update1);

        UpdateDescriptorType update2 = new UpdateDescriptorType();
        update2.setID("id-v1_0_2");
        update2.setDescription("LiveUpdate Test 1.0.2");
        InstallableUnitType iu2 = new InstallableUnitType();
        iu2.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        iu2.setName("josso-ee-liveupdate-test");
        iu2.setVersion("1.0.2");
        iu2.setUpdateNature(UpdateNatureType.UPGRADE);
        ArtifactKeyType artifact3 = new ArtifactKeyType();
        artifact3.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        artifact3.setName("liveupdate-test");
        artifact3.setVersion("1.0.2");
        artifact3.setType("tar.gz");
        iu2.getArtifact().add(artifact3);
        ArtifactKeyType artifact4 = new ArtifactKeyType();
        artifact4.setGroup("com.atricore.idbus.console.liveservices.liveupdate.tests");
        artifact4.setName("liveupdate-test");
        artifact4.setVersion("1.0.2");
        artifact4.setType("zip");
        iu2.getArtifact().add(artifact4);
        update2.setInstallableUnit(iu2);
        updates.add(update2);

        handler.notify(updates, scheme);
    }
}
