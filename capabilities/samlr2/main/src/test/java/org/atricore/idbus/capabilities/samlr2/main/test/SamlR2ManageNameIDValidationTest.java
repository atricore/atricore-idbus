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

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.ManageNameIDRequestType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import oasis.names.tc.saml._2_0.protocol.TerminateType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.Samlr2AssertionEmissionException;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusDetails;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.JSR105SamlR2SignerImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.samlr2.support.core.util.NamespaceFilterXMLStreamWriter;
import org.atricore.idbus.capabilities.samlr2.support.core.util.StringSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.springframework.context.ApplicationContext;
import org.w3._1999.xhtml.Div;
import org.w3._1999.xhtml.Form;
import org.w3._1999.xhtml.Input;
import org.w3._2000._09.xmldsig_.SignatureValueType;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class SamlR2ManageNameIDValidationTest extends SamlR2TestSupport {
	
	private static final String SP_MNID_POST = "http://localhost:8181/IDBUS/SP-1/SAML2/MNI/POST";
	
	private static final Log logger = LogFactory.getLog(SamlR2ManageNameIDValidationTest.class);
	
	private ApplicationContext applicationContext;
	
	private ManageNameIDRequestType mnidRequest;
	
    private HttpClient client;
    
    private SamlR2Signer signer;
    
    private Server server;
    
    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-manage-nameid-validation-test.xml"}
        );
        
        server = (Server) applicationContext.getBean("jetty-server");
        createServlet(applicationContext, server, "/IDBUS");
        
        signer = (SamlR2Signer) applicationContext.getBean("sp-1-samlr2-signer");
        ((JSR105SamlR2SignerImpl) signer).init();

        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context.");
    
        mnidRequest = generateTestManageNameIDRequest();
        
        client = new HttpClient();
        
    }
    
    @After
    public void tearDown() throws Exception {
        String stTimeout = System.getProperty("org.atricore.josso2.test.wait", "-1");
        long timeout = Long.parseLong(stTimeout);
        if (timeout >= 0 ) {
            synchronized (this) {
                logger.info("Waiting " + (timeout > 0 ? timeout + " millis"  : "forever"));
                try { wait(timeout); } catch (InterruptedException e) { /**/}
            }
        }

        if (server != null)
            server.stop();

        if (applicationContext != null)
            ((ClassPathXmlApplicationContext)applicationContext).close();
    }    
    
    @Test
    public void testValidatingManageNameIDRequest() throws Exception {
    	signer.validate(mnidRequest);
    	validateNoDestination();
    	validateNoIssueInstant();
    	validateNoIssuer();
    	validateNoNameIDOrEncryptedID();
    	validateNoVersion();
    	validateInvalidVersion();
    	validateInvalidSignature();
    	validateNoTerminateOrNewIDOrEncryptedID();

    }

	private void validateNoDestination() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setDestination(null);
		
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.NO_DESTINATION.toString()) : "Failed to report missing Destination";
	}

	private void validateNoIssueInstant() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setIssueInstant(null);
		
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.NO_ISSUE_INSTANT.toString()) : "Failed to report missing IssueInstant";
	}

	private void validateNoIssuer() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setIssuer(null);
		
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.NO_ISSUER.toString()) : "Failed to report missing Issuer";
	}

	private void validateNoNameIDOrEncryptedID() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setNameID(null);
		mnidRequest.setEncryptedID(null);
		
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.NO_NAMEID_ENCRYPTEDID.toString()) : "Failed to report missing both NameID and EncryptedID";
	}

	private void validateNoVersion() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setVersion(null);
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.INVALID_VERSION.toString()) : "Failed to report missing Version";
	}

	private void validateInvalidVersion() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setVersion("1.1");
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.INVALID_VERSION.toString()) : "Failed to report invalid Version";
	}

	private void validateInvalidSignature() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		SignatureValueType signatureValue = mnidRequest.getSignature().getSignatureValue();
    	signatureValue.setValue("bad_signature".getBytes());
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.INVALID_REQUEST_SIGNATURE.toString()) : "Failed to report invalid Signature";
	}

	private void validateNoTerminateOrNewIDOrEncryptedID() throws Exception {
		mnidRequest = generateTestManageNameIDRequest();
		mnidRequest.setTerminate(null);
		mnidRequest.setNewEncryptedID(null);
		mnidRequest.setNewID(null);
		
		mnidRequest.setSignature(null);
		mnidRequest = signer.sign(mnidRequest);
		
    	PostMethod post = encodeAndSendRequest(mnidRequest);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;

    	String body = post.getResponseBodyAsString();
    	StatusType statusType = getStatus(body);
    	String statusCode = statusType.getStatusCode().getValue();
    	assert statusCode.equals("urn:oasis:names:tc:SAML:2.0:status:Requester") : "Bad status code";
    	assert statusType.getStatusMessage().equals(StatusDetails.NO_NEWID_NEWENCRYPTEDID_TERMINATE.toString()) : "Failed to report missing" +
    			" NewID, NewEncryptedID and Terminate";
	}
	

	private StatusType getStatus(String body) throws Exception {
		StatusType status = null;
		
		Form form = getForm(unmarshallHtml(body));
		assert form != null : "No Form received";
		
		List pOrH1OrH2 = form.getPOrH1OrH2();
		
		String samlResponse = null;
		
        for (Object obj : pOrH1OrH2) {
            if (obj instanceof Div) {
                Div div = (Div)obj;
                for (Object obj2 : div.getContent()){
                	if (obj2 instanceof Input) {
                		Input input = (Input)obj2;
		                if(input.getName().equals("SAMLResponse")){
		                	samlResponse = input.getValue();
		                	break;
		                }
                	}
                }
            }
        }
        if(samlResponse != null){
        	byte[] decodedResponse = Base64.decodeBase64(samlResponse.getBytes());
        	samlResponse = new String(decodedResponse);
        	StatusResponseType response = unmarshalManageNameIDResponse(samlResponse);
        	status = response.getStatus();
        }
		
		return status;
	}

	private StatusResponseType unmarshalManageNameIDResponse(String samlResponse) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_PROTOCOL_PKG);
		Unmarshaller u = context.createUnmarshaller();        
        JAXBElement<StatusResponseType> jaxbResponse = (JAXBElement<StatusResponseType>) u.unmarshal(new StringSource(samlResponse));
        
		return jaxbResponse.getValue();
	}

	private ManageNameIDRequestType generateTestManageNameIDRequest() throws SamlR2SignatureException {
		ManageNameIDRequestType mnid = new ManageNameIDRequestType();
		mnid.setID("test_ID");
		mnid.setVersion("2.0");
		mnid.setIssueInstant(DateUtils.toXMLGregorianCalendar(new Date()));
		
		NameIDType issuer = new NameIDType();
		issuer.setValue("test_issuer");
		mnid.setIssuer(issuer);		
		
		mnid.setDestination("test_destination");
		
		NameIDType nameID = new NameIDType();
		nameID.setValue("test_name_id");
		mnid.setNameID(nameID);
		mnid.setTerminate(new TerminateType());
		
		mnid = signer.sign(mnid);
		
		return mnid;
	}
	
	private PostMethod encodeAndSendRequest(ManageNameIDRequestType mnidRequest) throws JAXBException, XMLStreamException, IOException {
		JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
    	Writer stringWriter = new StringWriter();
    	Object o = new JAXBElement(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "ManageNameIDRequest"), ManageNameIDRequestType.class, mnidRequest);
    	Marshaller m = context.createMarshaller();
    	m.marshal(o, new NamespaceFilterXMLStreamWriter(stringWriter));
    	String responseString = stringWriter.toString();
    	byte[] encodedBytes  = Base64.encodeBase64(responseString.getBytes());
    	
        NameValuePair requestContent = new NameValuePair("SAMLRequest", new String(encodedBytes));
        return doPost(SP_MNID_POST, requestContent);
	}	

    protected PostMethod doPost(String url, NameValuePair ... params) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestBody(params);

        client.executeMethod(postMethod);

        return postMethod;
    }
	
}
