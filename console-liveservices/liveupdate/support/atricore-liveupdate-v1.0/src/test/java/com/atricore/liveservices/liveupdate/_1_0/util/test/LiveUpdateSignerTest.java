package com.atricore.liveservices.liveupdate._1_0.util.test;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.InvalidSignatureException;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeystoreKeyResolver;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateSigner;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LiveUpdateSignerTest {

    private static LiveUpdateKeystoreKeyResolver keyResolver;
    
    @BeforeClass
    public static void setupTestSuite() throws Exception {
    }

    @Before
    public void setup() throws Exception {
        keyResolver = new LiveUpdateKeystoreKeyResolver();
        keyResolver.setKeystoreType("JKS");
        keyResolver.setKeystorePass("ATRICORE");
        keyResolver.setPrivateKeyAlias("josso-sp1");
        keyResolver.setPrivateKeyPass("ATRICORE");
        keyResolver.setCertificateAlias("josso-sp1");
        keyResolver.setKeystoreFile(resolveResource("/com/atricore/liveservices/liveupdate/_1_0/util/test/keystore.jks"));
    }

    @After
    public void tearDown() throws Exception {
    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {
    }

    @Test
    public void testSignUpdatesIndex() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-updates.xml");
        UpdatesIndexType unsigned = XmlUtils1.unmarshallUpdatesIndex(is, false);
        Assert.assertNull(unsigned.getSignature());

        UpdatesIndexType signed = LiveUpdateSigner.sign(unsigned, keyResolver);
        Assert.assertNotNull(signed.getSignature());
    }

    @Test
    public void testSignArtifactDescriptor() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-artifact-descriptor.xml");
        ArtifactDescriptorType unsigned = XmlUtils1.unmarshallArtifactDescriptor(is, false);
        Assert.assertNull(unsigned.getSignature());

        ArtifactDescriptorType signed = LiveUpdateSigner.sign(unsigned, keyResolver);
        Assert.assertNotNull(signed.getSignature());
    }

    @Test
    public void testValidateUpdatesIndex() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-updates-signed.xml");
        UpdatesIndexType signed = XmlUtils1.unmarshallUpdatesIndex(is, false);
        Assert.assertNotNull(signed.getSignature());

        boolean valid = true;
        try {
            LiveUpdateSigner.validate(signed, keyResolver);
        } catch (InvalidSignatureException e) {
            valid = false;
        }

        Assert.assertTrue(valid);
    }

    @Test
    public void testValidateArtifactDescriptor() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-artifact-descriptor-signed.xml");
        ArtifactDescriptorType signed = XmlUtils1.unmarshallArtifactDescriptor(is, false);
        Assert.assertNotNull(signed.getSignature());

        boolean valid = true;
        try {
            LiveUpdateSigner.validate(signed, keyResolver);
        } catch (InvalidSignatureException e) {
            valid = false;
        }

        Assert.assertTrue(valid);
    }

    private byte[] resolveResource(String resourceURI) throws IOException {
        byte[] resource = null;
        InputStream is = getClass().getResourceAsStream(resourceURI);
        if (is != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            byte[] buff = new byte[4096];
            int read = is.read(buff, 0, 4096);
            while (read > 0) {
                baos.write(buff, 0, read);
                read = is.read(buff, 0, 4096);
            }
            resource = baos.toByteArray();
        }
        return resource;
    }
}
