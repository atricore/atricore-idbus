package com.atricore.idbus.console.liveservices.liveupdate.main.test;

import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.EMailNotificationScheme;
import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.NotificationScheme;
import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.PropertiesEMailNotificationSchemeStore;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.Properties;

public class PropertiesEMailNotificationSchemeStoreTest extends VFSTestSupport {

    protected static PropertiesEMailNotificationSchemeStore store;
    
    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/idbus/console/liveservices/liveupdate/main/test/notification-beans.xml"}
        );

        store = (PropertiesEMailNotificationSchemeStore) applicationContext.getBean("emailNotificationStore");
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
    public void testLoadAll() throws Exception {
        Collection<NotificationScheme> schemes = store.loadAll();
        Assert.assertEquals(schemes.size(), 1);
    }

    @Test
    public void testLoad() throws Exception {
        EMailNotificationScheme scheme = (EMailNotificationScheme) store.load("Test scheme");
        Assert.assertNotNull(scheme);
        Assert.assertEquals(scheme.getName(), "Test scheme");
        Assert.assertEquals(scheme.getThreshold(), "UPGRADE");
        Assert.assertEquals(scheme.getSmtpHost(), "localhost");
        Assert.assertEquals(scheme.getSmtpUsername(), "user");
        Assert.assertEquals(scheme.getSmtpPassword(), "pass");
        Assert.assertEquals(scheme.getSmtpPort(), 25);
        Assert.assertEquals(scheme.getAddresses().length, 2);
        Assert.assertEquals(scheme.getAddresses()[0], "gnastov@atricore.com");
        Assert.assertEquals(scheme.getAddresses()[1], "dfisic@atricore.com");
    }

    @Test
    public void testStore() throws Exception {
        EMailNotificationScheme scheme = new EMailNotificationScheme();
        scheme.setName("Test scheme 2");
        scheme.setThreshold("BUGFIX");
        scheme.setSmtpHost("host2");
        scheme.setSmtpUsername("user2");
        scheme.setSmtpPassword("pass2");
        scheme.setSmtpPort(123);
        scheme.setAddresses(new String[] {"test1@test.com","test2@test.com"});

        store.store(scheme);

        FileObject storeDir = getFileSystemManager().resolveFile(store.getBaseFolder().toString());
        FileObject schemePropertiesFile = storeDir.resolveFile("test_scheme_2.properties");
        FileObject schemeProcessedPropertiesFile = storeDir.resolveFile("test_scheme_2-processed.properties");

        Assert.assertTrue(schemePropertiesFile.exists());
        Assert.assertTrue(schemeProcessedPropertiesFile.exists());
    }

    @Test
    public void testRemove() throws Exception {
        FileObject storeDir = getFileSystemManager().resolveFile(store.getBaseFolder().toString());
        Assert.assertEquals(storeDir.getChildren().length, 2);

        store.remove("Test scheme");

        storeDir = getFileSystemManager().resolveFile(store.getBaseFolder().toString());
        Assert.assertEquals(storeDir.getChildren().length, 0);
    }

    @Test
    public void testMarshall() throws Exception {
        EMailNotificationScheme scheme = new EMailNotificationScheme();
        scheme.setName("Test scheme 2");
        scheme.setThreshold("BUGFIX");
        scheme.setSmtpHost("host2");
        scheme.setSmtpUsername("user2");
        scheme.setSmtpPassword("pass2");
        scheme.setSmtpPort(123);
        scheme.setAddresses(new String[] {"test1@test.com","test2@test.com"});

        Properties props = store.marshall(scheme);

        Assert.assertNotNull(props);
        Assert.assertEquals(props.getProperty("name"), "Test scheme 2");
        Assert.assertEquals(props.getProperty("threshold"), "BUGFIX");
        Assert.assertEquals(props.getProperty("smtp.host"), "host2");
        Assert.assertEquals(props.getProperty("smtp.username"), "user2");
        Assert.assertEquals(props.getProperty("smtp.password"), "pass2");
        Assert.assertEquals(props.getProperty("smtp.port"), "123");
        Assert.assertEquals(props.getProperty("addresses"), "test1@test.com,test2@test.com");
    }

    @Test
    public void testUnmarshall() throws Exception {
        Properties props = new Properties();
        props.setProperty("name", "Test scheme 2");
        props.setProperty("threshold", "BUGFIX");
        props.setProperty("smtp.host", "host2");
        props.setProperty("smtp.username", "user2");
        props.setProperty("smtp.password", "pass2");
        props.setProperty("smtp.port", "123");
        props.setProperty("addresses", "test1@test.com,test2@test.com");

        EMailNotificationScheme scheme = (EMailNotificationScheme) store.unmarshall(props);

        Assert.assertNotNull(scheme);
        Assert.assertEquals(scheme.getName(), "Test scheme 2");
        Assert.assertEquals(scheme.getThreshold(), "BUGFIX");
        Assert.assertEquals(scheme.getSmtpHost(), "host2");
        Assert.assertEquals(scheme.getSmtpUsername(), "user2");
        Assert.assertEquals(scheme.getSmtpPassword(), "pass2");
        Assert.assertEquals(scheme.getSmtpPort(), 123);
        Assert.assertEquals(scheme.getAddresses().length, 2);
        Assert.assertEquals(scheme.getAddresses()[0], "test1@test.com");
        Assert.assertEquals(scheme.getAddresses()[1], "test2@test.com");
    }

    @Test
    public void testGetProcessedUpdates() throws Exception {
        String[] processedUpdates = store.getProcessedUpdates("Test scheme");
        Assert.assertEquals(processedUpdates.length, 2);
        Assert.assertEquals(processedUpdates[0], "id-v1_0_1-100");
        Assert.assertEquals(processedUpdates[1], "id-v1_0_2-100");
    }

    @Test
    public void testAddProcessedUpdates() throws Exception {
        String[] processedUpdates = store.getProcessedUpdates("Test scheme");
        Assert.assertEquals(processedUpdates.length, 2);
        Assert.assertEquals(processedUpdates[0], "id-v1_0_1-100");
        Assert.assertEquals(processedUpdates[1], "id-v1_0_2-100");

        store.addProcessedUpdates("Test scheme", new String[] {"id-v1_0_3-100", "id-v1_0_4-100"});

        processedUpdates = store.getProcessedUpdates("Test scheme");
        Assert.assertEquals(processedUpdates.length, 4);
        Assert.assertEquals(processedUpdates[0], "id-v1_0_1-100");
        Assert.assertEquals(processedUpdates[1], "id-v1_0_2-100");
        Assert.assertEquals(processedUpdates[2], "id-v1_0_3-100");
        Assert.assertEquals(processedUpdates[3], "id-v1_0_4-100");
    }
}
