package org.atricore.idbus.capabilities.samlr2.support.test;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeystoreKeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.JSR105SamlR2SignerImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FIMXmlDsigTest {

    JSR105SamlR2SignerImpl signer = null;

    @Before
    public void setup() {

        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";

        SamlR2KeystoreKeyResolver kr = new SamlR2KeystoreKeyResolver();
        kr.setCertificateAlias(certificateAlias);
        // TODO : kr.setKeystoreFile("keystore.jks");
        kr.setKeystorePass(keystorePass);
        kr.setKeystoreType(keystoreType);
        kr.setPrivateKeyAlias(privateKeyAlias);
        kr.setPrivateKeyPass(privateKeyPass);

        signer = new JSR105SamlR2SignerImpl();
        signer.setKeyResolver( kr );
        signer.init();

    }

    @Test
    public void test() throws Exception {
        Document responseDom = loadDocument("src/test/resources/fim-idp-response-orig.xml");

        JAXBContext context = JAXBContext.newInstance( SAMLR2Constants.SAML_PROTOCOL_PKG );
        Unmarshaller u = context.createUnmarshaller();

        JAXBElement e = (JAXBElement) u.unmarshal(responseDom);
        ResponseType response = (ResponseType) e.getValue();

        List assertionLs = response.getAssertionOrEncryptedAssertion();

        assert assertionLs != null;
        assert assertionLs.size() == 1;
        assert assertionLs.get(0) instanceof AssertionType;

        AssertionType assertion = (AssertionType) assertionLs.get(0);

        signer.validate(responseDom);
    }

    private Document loadDocument (String fileName) throws Exception {
        File f = new File( fileName );
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(f);
    }

}
