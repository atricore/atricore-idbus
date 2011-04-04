package org.atricore.idbus.capabilities.samlr2.support.test;

import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2Signer;


import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils.*;

import org.w3._2000._09.xmldsig_.ObjectType;
import org.w3._2000._09.xmldsig_.SignatureType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlR2DsigTest {

    private static final Log logger = LogFactory.getLog(SamlR2DsigTest.class);

    private static ApplicationContext applicationContext;

    private static SamlR2Signer signer;

    @BeforeClass
    public static void setupTestSuite() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("samlr2-ds-test-beans.xml");
        signer = (SamlR2Signer) applicationContext.getBean("test-samlr2-signer");
    }

    @Before
    public void setupTest() throws Exception {

    }

    @After
    public void tearDownTest() throws Exception {

    }

    @AfterClass
    public static void tearDownTestSuite() throws Exception {

    }


    @Test
    public void testSignResponse() throws Exception {
        String responseStr = loadResource("/response-001.xml");
        StatusResponseType response = unmarshalSamlR2Response(responseStr, false);
        StatusResponseType signedResponse = signer.sign(response);


        String signedResponseStr = marshalSamlR2Response(signedResponse, false);

        signedResponse = unmarshalSamlR2Response(signedResponseStr, false);
        RoleDescriptorType md = getRoleDescriptorType();

        signer.validate(md, signedResponse);
    }

    @Test
    public void testWrapSignResponse() throws Exception {

        // Load response :
        String responseStr = loadResource("/response-001.xml");

        // Create valid signed response
        ResponseType response = (ResponseType) unmarshalSamlR2Response(responseStr, false);
        ResponseType signedResponse = (ResponseType) signer.sign(response);

        // Get signature
        SignatureType signature = signedResponse.getSignature();

        // Create fake response (with wrapped object)
        ResponseType fakeResponse = (ResponseType) unmarshalSamlR2Response(responseStr, false);
        fakeResponse.setID("id666");
        fakeResponse.setDestination("New Destination!");
        fakeResponse.setSignature(signature);

        logger.debug("FAKE RESPONSE (1):\n" + marshalSamlR2Response(fakeResponse, false));

        // Wrap valid response in signature object:
        String signedResponseStr = marshalSamlR2Response(signedResponse, false);
        JAXBElement e = new JAXBElement(
                new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"),
                ResponseType.class,
                signedResponse);
        ObjectType obj = new ObjectType();
        obj.getContent().add(e);
        signedResponse.setSignature(null);

        // Add object to original signature
        signature.getObject().add(obj);

        // Add signature to fake response
        fakeResponse.setSignature(signature);

        logger.debug("FAKE RESPONSE (2):\n" + marshalSamlR2Response(fakeResponse, false));

        // Validate FAKE Response
        RoleDescriptorType md = getRoleDescriptorType();
        signer.validate(md, fakeResponse);
    }


    protected RoleDescriptorType getRoleDescriptorType() throws Exception {
        EntityDescriptorType md = loadSamlR2Metadata();
        List<RoleDescriptorType> roles = md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor();
        for (RoleDescriptorType roleDescriptorType : roles) {
            if (roleDescriptorType instanceof IDPSSODescriptorType)
                return roleDescriptorType;
        }
        throw new RuntimeException("No IDP Role descriptor type found!");
    }

    protected EntityDescriptorType loadSamlR2Metadata() throws Exception {
        String mdStr = loadResource("/idp1/idp1-samlr2-metadata.xml");
        JAXBElement e = (JAXBElement) unmarshal(mdStr, new String[] {SAMLR2Constants.SAML_METADATA_PKG});
        return (EntityDescriptorType) e.getValue();
    }

    protected String loadResource(String name) throws Exception {
        InputStream is =  getClass().getResourceAsStream(name);
        return IOUtils.toString(is);
    }
}


