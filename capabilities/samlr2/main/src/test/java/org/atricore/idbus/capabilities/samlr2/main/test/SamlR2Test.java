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
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.converter.jaxp.StringSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.main.SSOConstants;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.springframework.context.ApplicationContext;
import org.w3._1999.xhtml.Div;
import org.w3._1999.xhtml.Form;
import org.w3._1999.xhtml.Html;
import org.w3._1999.xhtml.Input;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Test.java 1492 2009-09-02 21:58:23Z sgonzalez $
 */
public class SamlR2Test {

    private static final Log logger = LogFactory.getLog(SamlR2Test.class);

    private ApplicationContext applicationContext;

    private AssertionType assertion;

    private StatusResponseType response;

    private String serializedResponse;

    private String base64EncodedResponse;

    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-sso-test.xml"}
        );

        SecurityTokenService sts = (SecurityTokenService) applicationContext.getBean( "sts" );
        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put( SSOConstants.PARAM_SSO_USERNAME, "user1" );
        credentials.put( SSOConstants.PARAM_SSO_PASSWORD, "user1pwd" );
        RequestSecurityTokenType rst = buildRequestSecurityToken( credentials );
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken( rst );
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get( 1 );

        /*
        EncryptedElementType encryptedAssertion = (EncryptedElementType) token.getFormat().getAny();
        XmlSecurityEncrypterImpl decrypter = (XmlSecurityEncrypterImpl)applicationContext.getBean("samlr2-assertion-encrypter");
        assertion = decrypter.decryptAssertion(encryptedAssertion);
        */
        assertion = (AssertionType) token.getValue().getAny();
        response = buildAuthnResponse(assertion);

        // Dump the assertion as a XML document

        // Create DOM document factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        // Create JAXB Context and marshal the assertion
        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
        Marshaller m = context.createMarshaller();

        Writer writer = new FileWriter("target/response-001.xml");

        Object o = new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:protocol", "Response"), ResponseType.class, response);
        m.marshal(o, writer);
        writer.close();

        StringWriter strWriter = new StringWriter();
        m.marshal(o, strWriter );

        serializedResponse = strWriter.toString();

        base64EncodedResponse = new String( new Base64().encode( serializedResponse.getBytes() ) );

    }

    @Test
    public void testSPIinitiatedSSO() throws Exception{

        String response = this.accessSPInitiantedSSOEndpoint();
        logger.debug("Step 1 : Response:\n" + response);

        Form form = getForm(response);
        assert form != null : "No Form received";

        response = accessIDPSingleSignOnEndpoint(form);
        logger.debug("Step 2 : Response:\n" + response);

    }

    protected String accessSPInitiantedSSOEndpoint() throws Exception{

        // Access SP, asking for identity.
        String url = "http://localhost:8181/IDBUS/SP-1/SP-SSO/REDIR";

        logger.debug( "******************************************************************************" );
        logger.debug( "SP GET : " + url);
        logger.debug( "******************************************************************************" );

        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        return get.getResponseBodyAsString();
    }

    protected String accessIDPSingleSignOnEndpoint(Form form) throws Exception {
        // Access SP, asking for identity.
        String url = form.getAction();

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP POST : " + url);
        logger.debug( "******************************************************************************" );

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);

        Div div = (Div) form.getPOrH1OrH2().get(0);

        for (Object o :div.getContent()) {
            Input input = (Input) o;
            logger.debug("Adding POST param " + input.getName() + "=" + input.getValue());
            post.addParameter(input.getName(), input.getValue());
        }


        int status = client.executeMethod( post );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        return post.getResponseBodyAsString();
    }


    @Test
    public void testUnsolicitedResponse() throws Exception{

        String sp1AcsUrl = "http://localhost:8181/IDBUS/SAML2/SP-1/ACS/POST";

        logger.debug( "******************************************************************************" );
        logger.debug( "SP POST : " + sp1AcsUrl);
        logger.debug( "******************************************************************************" );


        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(sp1AcsUrl);

        post.addParameter("SAMLResponse", base64EncodedResponse);

        int status = client.executeMethod( post );
        assert status == 200 : "expected status code 200 found [" + status + "]";

    }

    @After
    public void tearDown() {
        String stTimeout = System.getProperty("org.atricore.josso2.test.wait", "-1");

        long timeout = Long.parseLong(stTimeout);

        if (timeout >= 0 ) {

            synchronized (this) {
                logger.info("Waiting " + (timeout > 0 ? timeout + " millis"  : "forever"));
                try { wait(timeout); } catch (InterruptedException e) { /**/}
            }
        }

        if (applicationContext != null)
            ((ClassPathXmlApplicationContext)applicationContext).close();

    }


    protected RequestSecurityTokenType buildRequestSecurityToken ( Map<String, String> credentials ) throws Exception {
        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

        rstRequest.getAny().add( of.createTokenType( WSTConstants.WST_SAMLR2_TOKEN_TYPE ) );
        rstRequest.getAny().add( of.createRequestType( WSTConstants.WST_ISSUE_REQUEST ) );

        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

        JAXBElement<UsernameTokenType> usernameToken = ofwss.createUsernameToken( new UsernameTokenType() );
        AttributedString usernameString = new AttributedString();
        usernameString.setValue( credentials.get( SSOConstants.PARAM_SSO_USERNAME) );
        usernameToken.getValue().setUsername( usernameString );
        usernameToken.getValue().getOtherAttributes().put( new QName( SSOConstants.PARAM_SSO_PASSWORD), credentials.get( SSOConstants.PARAM_SSO_PASSWORD) );

        rstRequest.getAny().add( usernameToken );
        return rstRequest;
    }

    protected ResponseType buildAuthnResponse(AssertionType assertn){
        ResponseType authnResponse = new ResponseType();
        UUIDGenerator uuidGenerator = new UUIDGenerator();

        Date dateNow = new Date();
        authnResponse.setID( uuidGenerator.generateId() );
        authnResponse.setVersion("2.0");
        authnResponse.setIssueInstant( DateUtils.toXMLGregorianCalendar( dateNow ) );
        authnResponse.getAssertionOrEncryptedAssertion().add( assertn );

        return authnResponse;
    }


    protected Object unmarshal ( String msg, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext(  userPackages );
        return jaxbContext.createUnmarshaller().unmarshal( new StringSource( msg ) );
    }

    protected JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }
        return JAXBContext.newInstance( packages.toString() );
    }

    protected Form getForm(String xhtml) throws Exception {


        // Now , unmarshall the response as XHTML and POST the form.
        Html html = (Html) this.unmarshal(xhtml, new String[]{"org.w3._1999.xhtml"});
        List pOrH1OrH2 = html.getBody().getPOrH1OrH2();

        Form form = null;
        for (Object obj : pOrH1OrH2) {
            if (obj instanceof Form) {
                form = (Form) obj;
                break;
            }
        }

        return form;
    }


}