package com.atricore.liveservices.liveupdate._1_0.util.test;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.InvalidSignatureException;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeystoreKeyResolver;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateSigner;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class LiveUpdateSignerTest {

    private static final Log logger = LogFactory.getLog(LiveUpdateSignerTest.class);

    private static ApplicationContext applicationContext;

    private static LiveUpdateSigner liveUpdateSigner;
    
    private static LiveUpdateKeystoreKeyResolver keyResolver;
    
    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"classpath:com/atricore/liveservices/liveupdate/_1_0/util/test/test-beans.xml"}
        );

        liveUpdateSigner = (LiveUpdateSigner) applicationContext.getBean("liveUpdateSigner");
    }

    @Before
    public void setup() throws Exception {
        logger.debug("Setting up Key resolver ... ");
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
    public void testCertificate() throws Exception {
        Certificate cert = keyResolver.getCertificate();

        // Check if this are interoperable !
        javax.security.cert.X509Certificate x509cert = javax.security.cert.X509Certificate.getInstance(cert.getEncoded());

        OutputStream out = new FileOutputStream("target/cert.enc");

        cert.getEncoded();
        IOUtils.write(Base64.encodeBase64(cert.getEncoded()), out);
    }

    @Test
    public void testSignUpdatesIndex() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-updates.xml");
        byte[] signedBin = IOUtils.toByteArray(is);
        String signedStr = new String(signedBin);
        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + signedStr
                + "\n<---------------------- END DESCR ----------------------->");


        UpdatesIndexType unsigned = XmlUtils1.unmarshallUpdatesIndex(signedStr, false);
        Assert.assertNull(unsigned.getSignature());

        UpdatesIndexType signed = liveUpdateSigner.sign(unsigned, keyResolver);
        Assert.assertNotNull(signed.getSignature());

        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + XmlUtils1.marshalUpdatesIndex(signed, false)
                + "\n<---------------------- END DESCR ----------------------->");

    }

    @Test
    public void testSignArtifactDescriptor() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-artifact-descriptor.xml");
        byte[] signedBin = IOUtils.toByteArray(is);
        String signedStr = new String(signedBin);
        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + signedStr
                + "\n<---------------------- END DESCR ----------------------->");

        ArtifactDescriptorType unsigned = XmlUtils1.unmarshallArtifactDescriptor(signedStr, false);
        Assert.assertNull(unsigned.getSignature());

        ArtifactDescriptorType signed = liveUpdateSigner.sign(unsigned, keyResolver);
        Assert.assertNotNull(signed.getSignature());
        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + XmlUtils1.marshalArtifactDescriptor(signed, false)
                + "\n<---------------------- END DESCR ----------------------->");


    }

    @Test
    public void testValidateUpdatesIndex() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-updates-signed.xml");
        byte[] signedBin = IOUtils.toByteArray(is);
        String signedStr = new String(signedBin);
        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + signedStr
                + "\n<---------------------- END DESCR ----------------------->");

        UpdatesIndexType signed = XmlUtils1.unmarshallUpdatesIndex(signedStr, false);
        Assert.assertNotNull(signed.getSignature());

        InvalidSignatureException error = null;
        try {
            liveUpdateSigner.validate(signed, keyResolver);
        } catch (InvalidSignatureException e) {
            error = e;
        }

        assert error == null : error.getMessage();
    }

    @Test
    public void testValidateArtifactDescriptor() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/atricore/liveservices/liveupdate/_1_0/util/test/test-artifact-descriptor-signed.xml");

        byte[] signedBin = IOUtils.toByteArray(is);
        String signedStr = new String(signedBin);
        logger.debug("\n<---------------------- START DESCR --------------------->\n"
                + signedStr
                + "\n<---------------------- END DESCR ----------------------->");

        ArtifactDescriptorType signed = XmlUtils1.unmarshallArtifactDescriptor(signedStr, false);

        Assert.assertNotNull(signed.getSignature());

        InvalidSignatureException error = null;
        try {
            liveUpdateSigner.validate(signed, keyResolver);
        } catch (InvalidSignatureException e) {
            error = e;
        }
        assert error == null : error.getMessage();
    }

    protected byte[] resolveResource(String resourceURI) throws IOException {

        InputStream is = null;
        try {
            byte[] resource = null;
            is = getClass().getResourceAsStream(resourceURI);
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
        } finally {
            if (is != null) try { is.close(); } catch (IOException e) { /* Ignore it */ }
        }
    }


}
