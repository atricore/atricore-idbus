package org.atricore.idbus.capabilities.sso.support.test;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;


import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils.*;
import static org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils.unmarshal;

import org.w3._2000._09.xmldsig_.ObjectType;
import org.w3._2000._09.xmldsig_.SignatureType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.FileOutputStream;
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


    public void testValidateAssertionFromResposneSignature() throws Exception {
        String responseStr = loadResource("/response-001-s.xml");
        ResponseType response = (ResponseType) XmlUtils.unmarshalSamlR2Response(responseStr, false);
        RoleDescriptorType md = getRoleDescriptorType("idp1");

        signer.validate(md, response, "Response");

        /*
        List assertions = response.getAssertionOrEncryptedAssertion();
        for (int i = 0; i < assertions.size(); i++) {
            AssertionType assertion = (AssertionType) assertions.get(i);
            logger.debug("Validating " + assertion.getID());
            signer.validate(md, assertion);

        }  */



    }

    @Test
    public void testValidateAssertionSignature() throws Exception {
        String assertionStr = loadResource("/assertion-003-s.xml");
        AssertionType assertion = (AssertionType) XmlUtils.unmarshal(assertionStr, new String[]{SAMLR2Constants.SAML_ASSERTION_PKG});
        RoleDescriptorType md = getRoleDescriptorType("idp1");

        signer.validate(md, assertion);

        /*
        List assertions = response.getAssertionOrEncryptedAssertion();
        for (int i = 0; i < assertions.size(); i++) {
            AssertionType assertion = (AssertionType) assertions.get(i);
            logger.debug("Validating " + assertion.getID());
            signer.validate(md, assertion);

        }  */



    }



    public void testSignResponse() throws Exception {
        String responseStr = loadResource("/response-001.xml");
        StatusResponseType response = XmlUtils.unmarshalSamlR2Response(responseStr, false);
        StatusResponseType signedResponse = signer.sign(response, "Response");

        String signedResponseStr = marshalSamlR2Response(signedResponse, false);
        saveResource("/tmp/r.xml", signedResponseStr);

        signedResponseStr = loadResource("/tmp/r.xml");
        logger.info(signedResponseStr);

        signedResponse = unmarshalSamlR2Response(signedResponseStr, false);
        RoleDescriptorType md = getRoleDescriptorType("idp1");

        signer.validate(md, signedResponse, "Response");
    }

    public void testWrapSignResponse() throws Exception {

        // Load response :
        String responseStr = loadResource("/response-001.xml");

        // Create valid signed response
        ResponseType response = (ResponseType) unmarshalSamlR2Response(responseStr, false);
        ResponseType signedResponse = (ResponseType) signer.sign(response, "Response");

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
        RoleDescriptorType md = getRoleDescriptorType("idp1");
        try {
            signer.validate(md, fakeResponse, "Response");
            assert false : "Signature should be invalid !";
        } catch (SamlR2SignatureException ex) {
            // OK !
        }
    }


    protected RoleDescriptorType getRoleDescriptorType(String idp) throws Exception {
        EntityDescriptorType md = loadIdPMetadata(idp);
        List<RoleDescriptorType> roles = md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor();
        for (RoleDescriptorType roleDescriptorType : roles) {
            if (roleDescriptorType instanceof IDPSSODescriptorType)
                return roleDescriptorType;
        }
        throw new RuntimeException("No IDP Role descriptor type found!");
    }

    protected EntityDescriptorType loadIdPMetadata(String idp) throws Exception {
        String mdStr = loadResource("/"+idp+"/"+idp+"-samlr2-metadata.xml");
        return (EntityDescriptorType) unmarshal(mdStr, new String[] {SAMLR2Constants.SAML_METADATA_PKG});
    }

    protected String loadResource(String name) throws Exception {
        InputStream is =  getClass().getResourceAsStream(name);
        return IOUtils.toString(is);
    }

    protected void saveResource(String name, String content) throws Exception {
        FileOutputStream fos = new FileOutputStream(name, false);
        IOUtils.write(content, fos);
    }
}


