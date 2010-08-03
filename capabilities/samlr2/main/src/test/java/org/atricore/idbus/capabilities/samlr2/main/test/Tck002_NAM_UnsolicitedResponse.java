/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.main.test;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.apache.xpath.XPathAPI;
import org.atricore.idbus.capabilities.samlr2.main.SSOConstants;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.Samlr2AssertionEmissionException;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.JSR105SamlR2SignerImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.samlr2.support.core.util.NamespaceFilterXMLStreamWriter;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tck002_NAM_UnsolicitedResponse {
    private static final String NAM_ACS = "http://suse-IdP2.workgroup:8080/nidp/saml2/spassertion_consumer";

    private static final Log logger = LogFactory.getLog(Tck002_NAM_UnsolicitedResponse.class);

    protected ClassPathXmlApplicationContext applicationContext;

    protected HttpClient client;

    private AssertionType assertion;
    private ResponseType response;
    private String responseString;

    private SamlR2Signer signer;

    static final String SAML_VERSION = "2.0";

    @Before
    public void setUp() throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-nam-test.xml"}
        );

        SecurityTokenService sts = (SecurityTokenService) applicationContext.getBean("sts");

        signer = (SamlR2Signer) applicationContext.getBean("samlr2-signer");
        ((JSR105SamlR2SignerImpl) signer).init();

        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context");

        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put(SSOConstants.PARAM_SSO_USERNAME, "user1");
        credentials.put(SSOConstants.PARAM_SSO_PASSWORD, "user1pwd");
        RequestSecurityTokenType rst = buildRequestSecurityToken(credentials);
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);

        assertion = (AssertionType) token.getValue().getAny();

        assertion = signSamlAssertion();

        // marshal the assertion
        JAXBContext ascontext = JAXBContext.newInstance("oasis.names.tc.saml._2_0.assertion");
        Marshaller asm = ascontext.createMarshaller();

        Object aso = new JAXBElement(new QName(SAMLR2Constants.SAML_ASSERTION_NS, "Assertion"), AssertionType.class, assertion);
        Writer asWriter = new FileWriter("target/assertion-001.xml");
        asm.marshal(aso, new NamespaceFilterXMLStreamWriter(asWriter));

        response = buildAuthnResponse(assertion);
        response = signSamlResponse();

        // Dump the assertion as a XML document

        // Create DOM document factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        // Create JAXB Context and marshal the response
        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
        Marshaller m = context.createMarshaller();

        Writer writer = new FileWriter("target/response-001.xml");
        Writer stringWriter = new StringWriter();

        Object o = new JAXBElement(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"), ResponseType.class, response);
        
        m.marshal(o, new NamespaceFilterXMLStreamWriter(writer));
        m.marshal(o, new NamespaceFilterXMLStreamWriter(stringWriter));
        
        responseString = stringWriter.toString();

        writer.close(); 
        
        client = doMakeClient();
    }

    @Test
    public void assertionAttributesTest() throws Exception {
        logger.debug("************************************************");
        logger.debug("dummy test method");
        File file = new File("target/response-001.xml");
//        String content = FileUtils.readFileToString(file);
        String content = responseString;
        //byte[] encodedBytes  = Base64.encodeBase64(content.getBytes());

        validate(assertion);

        validate(response); 

//        NameValuePair responseContent = new NameValuePair("SAMLResponse", new String(encodedBytes));
//        PostMethod postMethod = doPost(NAM_ACS, responseContent);
//        int status = postMethod.getStatusCode();

//        assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    }

    protected RequestSecurityTokenType buildRequestSecurityToken(Map<String, String> credentials) throws Exception {
        logger.debug("generating RequestSecurityToken...");
        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

        rstRequest.getAny().add(of.createTokenType(WSTConstants.WST_SAMLR2_TOKEN_TYPE));
        rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

        JAXBElement<UsernameTokenType> usernameToken = ofwss.createUsernameToken(new UsernameTokenType());
        AttributedString usernameString = new AttributedString();
        usernameString.setValue(credentials.get(SSOConstants.PARAM_SSO_USERNAME));
        usernameToken.getValue().setUsername(usernameString);
        usernameToken.getValue().getOtherAttributes().put(new QName(SSOConstants.PARAM_SSO_PASSWORD), credentials.get(SSOConstants.PARAM_SSO_PASSWORD));

        rstRequest.getAny().add(usernameToken);

        logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
        return rstRequest;
    }

    protected ResponseType buildAuthnResponse(AssertionType assertn) {
        ResponseType authnResponse = new ResponseType();
        UUIDGenerator uuidGenerator = new UUIDGenerator();

        //add response attributes
        Date dateNow = new Date();
        authnResponse.setID(uuidGenerator.generateId());
        authnResponse.setVersion(SAML_VERSION);
        authnResponse.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));
        authnResponse.setDestination(NAM_ACS);

        //add status
        StatusType status = new StatusType();
        StatusCodeType statusCode = new StatusCodeType();
        statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:Success");
        status.setStatusCode(statusCode);
        authnResponse.setStatus(status);

        //add assertion
        authnResponse.getAssertionOrEncryptedAssertion().add(assertn);

        return authnResponse;
    }

    protected ResponseType signSamlResponse() throws Exception {
        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context");
        return (ResponseType) signer.sign(response);

    }

    protected AssertionType signSamlAssertion() throws Exception {
        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context");
        return (AssertionType) signer.sign(assertion);

    }

    protected PostMethod doPost(String url, NameValuePair... params) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Referer", "http://idp.atricore.com");
        postMethod.setRequestHeader("Pragma", "No-cache");
        postMethod.setRequestHeader("Cache-control", "no-cache, no-store");
        postMethod.setRequestBody(params);

        this.getClient().executeMethod(postMethod);

        return postMethod;
    }

    public HttpClient getClient() {
        return client;
    }

    protected HttpClient doMakeClient() {
        HttpClient c = new HttpClient();
        c.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        c.getParams().setBooleanParameter("http.protocol.allow-circular-redirects", true);
        return c;
    }

    public void validate(AssertionType assertion) throws SamlR2SignatureException {
        SamlR2Signer jsrSigner = (JSR105SamlR2SignerImpl) signer;
        jsrSigner.validate(assertion);

    }

    public void validate(StatusResponseType response) throws SamlR2SignatureException {
        SamlR2Signer jsrSigner = (JSR105SamlR2SignerImpl) signer;
        jsrSigner.validate(response);

    }

    private void validateSignatureTest() {

        boolean schemaValidate = false;
        final String signatureSchemaFile = "data/xmldsig-core-schema.xsd";
        // String signatureFileName = "data/ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-rsa.xml";
        String signatureFileName = "signature.xml";

        if (schemaValidate) {
            System.out.println("We do schema-validation");
        }

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        if (schemaValidate) {
            dbf.setAttribute("http://apache.org/xml/features/validation/schema",
                    Boolean.TRUE);
            dbf.setAttribute(
                    "http://apache.org/xml/features/dom/defer-node-expansion",
                    Boolean.TRUE);
            dbf.setValidating(true);
            dbf.setAttribute("http://xml.org/sax/features/validation",
                    Boolean.TRUE);
        }

        dbf.setNamespaceAware(true);
        dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

        if (schemaValidate) {
            dbf.setAttribute(
                    "http://apache.org/xml/properties/schema/external-schemaLocation",
                    org.apache.xml.security.utils.Constants.SignatureSpecNS + " " + signatureSchemaFile);
        }

        try {

            // File f = new File("signature.xml");
            File f = new File("target/assertion-001-signed-my.xml");

            System.out.println("Try to verify " + f.toURL().toString());

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

            db.setErrorHandler(new org.apache.xml.security.utils
                    .IgnoreAllErrorHandler());

            if (schemaValidate) {
                db.setEntityResolver(new org.xml.sax.EntityResolver() {

                    public org.xml.sax.InputSource resolveEntity(
                            String publicId, String systemId)
                            throws org.xml.sax.SAXException {

                        if (systemId.endsWith("xmldsig-core-schema.xsd")) {
                            try {
                                return new org.xml.sax.InputSource(
                                        new FileInputStream(signatureSchemaFile));
                            } catch (FileNotFoundException ex) {
                                throw new org.xml.sax.SAXException(ex);
                            }
                        } else {
                            return null;
                        }
                    }
                });
            }

            org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
            Element nscontext = createDSctx(doc, "ds",
                    org.apache.xml.security.utils.Constants.SignatureSpecNS);
            Element sigElement = (Element) XPathAPI.selectSingleNode(doc,
                    "//ds:Signature[1]", nscontext);
            org.apache.xml.security.signature.XMLSignature signature = new org.apache.xml.security.signature.XMLSignature(sigElement,
                    f.toURL().toString());

            //////signature.addResourceResolver(new OfflineResolver());

            // XMLUtils.outputDOMc14nWithComments(signature.getElement(), System.out);
            org.apache.xml.security.keys.KeyInfo ki = signature.getKeyInfo();

            if (ki != null) {
                if (ki.containsX509Data()) {
                    System.out
                            .println("Could find a X509Data element in the KeyInfo");
                }

                X509Certificate cert = signature.getKeyInfo().getX509Certificate();

                if (cert != null) {
                    /*
                    System.out.println(
                       "I try to verify the signature using the X509 Certificate: "
                       + cert);
                    */
                    System.out.println("The XML signature in file "
                            + f.toURL().toString() + " is "
                            + (signature.checkSignatureValue(cert)
                            ? "valid (good)"
                            : "invalid !!!!! (bad)"));
                } else {
                    System.out.println("Did not find a Certificate");

                    PublicKey pk = signature.getKeyInfo().getPublicKey();

                    if (pk != null) {
                        /*
                        System.out.println(
                           "I try to verify the signature using the public key: "
                           + pk);
                        */
                        System.out.println("The XML signature in file "
                                + f.toURL().toString() + " is "
                                + (signature.checkSignatureValue(pk)
                                ? "valid (good)"
                                : "invalid !!!!! (bad)"));
                    } else {
                        System.out.println(
                                "Did not find a public key, so I can't check the signature");
                    }
                }
            } else {
                System.out.println("Did not find a KeyInfo");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Element createDSctx
            (Document doc, String prefix, String namespace) {

        if ((prefix == null) || (prefix.trim().length() == 0)) {
            throw new IllegalArgumentException("You must supply a prefix");
        }

        Element ctx = doc.createElementNS(null, "namespaceContext");

        ctx.setAttributeNS
                (org.apache.xml.security.utils.Constants.NamespaceSpecNS, "xmlns:" + prefix.trim(), namespace);

        return ctx;
    }

    protected void sign(String id) throws Exception {

        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature
        String providerName = System.getProperty
                ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                (Provider) Class.forName(providerName).newInstance());

        // Create a Reference to the enveloped document (in this case we are
        // signing the whole document, so a URI of "" signifies that) and
        // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
        Reference ref = fac.newReference
                ("#" + id, fac.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList
                                (fac.newTransform
                                        (Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null, null);

        // Create the SignedInfo
        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                        (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null),
                        Collections.singletonList(ref));

        // Create a DSA KeyPair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();

        // Create a KeyValue containing the DSA PublicKey that was generated
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        KeyValue kv = kif.newKeyValue(kp.getPublic());

        // Create a KeyInfo and add the KeyValue to it
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

        // Instantiate the document to be signed
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc =
                dbf.newDocumentBuilder().parse(new FileInputStream("target/assertion-001-nonsigned.xml"));

        // Create a DOMSignContext and specify the DSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        /*
        DOMSignContext dsc = new DOMSignContext
                (kp.getPrivate(), doc.getDocumentElement());
        */
        DOMSignContext dsc = new DOMSignContext
                (kp.getPrivate(), doc.getDocumentElement(), doc.getDocumentElement().getFirstChild());


        // Create the XMLSignature (but don't sign it yet)
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate (and sign) the enveloped signature
        signature.sign(dsc);

        // output the resulting document
        OutputStream os;
        os = new FileOutputStream("target/assertion-001-signed.xml");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));
    }


}
