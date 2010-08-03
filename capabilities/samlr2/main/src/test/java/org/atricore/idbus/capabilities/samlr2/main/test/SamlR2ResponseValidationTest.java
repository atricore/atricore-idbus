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

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.main.SSOConstants;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.Samlr2AssertionEmissionException;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusDetails;
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
import org.springframework.context.ApplicationContext;
import org.w3._2000._09.xmldsig_.SignatureValueType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

public class SamlR2ResponseValidationTest{

	private static final String SP_ACS_POST = "http://localhost:8181/IDBUS/SP-1/SAML2/ACS/POST";

	private static final Log logger = LogFactory.getLog(SamlR2ResponseValidationTest.class);
	
    private ApplicationContext applicationContext;

    private AssertionType assertion;

    private ResponseType response;

    private String serializedResponse;
    
    private HttpClient client;
    
    private SamlR2Signer signer;
    
    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-response-validation-test.xml"}
        );
        
        signer = (SamlR2Signer) applicationContext.getBean("sp-1-samlr2-signer");
        ((JSR105SamlR2SignerImpl) signer).init();

        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context.");

        SecurityTokenService sts = (SecurityTokenService) applicationContext.getBean( "sts" );
                
        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put( SSOConstants.PARAM_SSO_USERNAME, "user1" );
        credentials.put( SSOConstants.PARAM_SSO_PASSWORD, "user1pwd" );
        RequestSecurityTokenType rst = buildRequestSecurityToken( credentials );
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken( rst );
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get( 1 );

        assertion = (AssertionType) token.getValue().getAny();
        assertion = signSamlAssertion(assertion);
        
        response = buildAuthnResponse(assertion);
        response = signSamlResponse(response);

        // Dump the assertion as a XML document

        // Create DOM document factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        // Create JAXB Context and marshal the assertion
        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
        Marshaller m = context.createMarshaller();

        Writer writer = new FileWriter("target/validation-response-001.xml");

        Object o = new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:protocol", "Response"), ResponseType.class, response);
        m.marshal(o, writer);
        writer.close();

        StringWriter strWriter = new StringWriter();
        m.marshal(o, strWriter );

        serializedResponse = strWriter.toString();
        
        client = new HttpClient();
    }
    
    @Test
    public void testValidatingUnsolicitedResponse() throws Exception{
    	
        validate(assertion);
        validate(unmarshalResponse());     	
//  	idp-intitiated (request is null)
//    	Response validation:
    	validateBadDestination();
    	validateNoDestination();
    	validateNoIssueInstant();
    	validateNoVersion();
    	validateInvalidVersion();
    	validateInvalidSignature();
    	validateNoStatus();
    	validateNoStatusCode();
    	validateInvalidStatusCode();    
    	
//    	Assertion validation:
    	validateInvalidAssertionSignature();
    	validateNoMethod();
    	validateInvalidSubjectConfirmationData1();
    	validateInvalidSubjectConfirmationData2();
    	validateInvalidSubjectConfirmationData3();
        validateNoSubjectNoStatements();
        validateNoSubjectWithAuthnStatements();
        validateInvalidConditions1();
        validateInvalidConditions2();
        validateNoAudience1();
        validateNoAudience2();
        validateNoAuthnInstant();
        validateNoAuthnContext();        
    	
//    	TODO sp-initiated
//    	   a. Request.IssueInstant & Response.IssueInstant
//    	   b. InResponseTo
    	
    }  
    
    private void validateNoAuthnContext() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotBefore());
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotOnOrAfter());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	
    	for(Object object : invalidAssertion.getStatementOrAuthnStatementOrAuthzDecisionStatement()){
    		if(object instanceof AuthnStatementType){
    			((AuthnStatementType)object).setAuthnContext(null);
    		}
    	}
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_AUTHN_CONTEXT.toString()) > 0 : "Failed to report missing AuthnContext";
	}

	private void validateNoAuthnInstant() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotBefore());
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotOnOrAfter());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	
    	for(Object object : invalidAssertion.getStatementOrAuthnStatementOrAuthzDecisionStatement()){
    		if(object instanceof AuthnStatementType){
    			((AuthnStatementType)object).setAuthnInstant(null);
    		}
    	}
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_AUTHN_INSTANT.toString()) > 0 : "Failed to report missing AuthnInstant";
	}

	private void validateNoAudience2() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotBefore());
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotOnOrAfter());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);    	
    	
    	//remove our sp from audience list
    	List<AudienceRestrictionType> deleteList = new ArrayList<AudienceRestrictionType>();
    	for(ConditionAbstractType conditionAbs  : invalidAssertion.getConditions().getConditionOrAudienceRestrictionOrOneTimeUse()){
    		if(conditionAbs instanceof AudienceRestrictionType){
    			int index = -1;
    			for( int i = 0; i < ((AudienceRestrictionType)conditionAbs).getAudience().size(); i++){
    				String audience = ((AudienceRestrictionType)conditionAbs).getAudience().get(i);
    				if(audience.equals("http://localhost:8181/IDBUS/SP-1/SAML2/ACS/POST")){
    					index = i;
    				}
    			}
    			if(index != -1){
    				((AudienceRestrictionType)conditionAbs).getAudience().remove(index);
    			}
    		}
    	}
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_CONDITIONS.toString()) > 0 : "Failed to report missing Audience";
	}    
    
    private void validateNoAudience1() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotBefore());
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotOnOrAfter());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);    	
    	
    	//remove all audience elements
    	List<AudienceRestrictionType> deleteList = new ArrayList<AudienceRestrictionType>();
    	for(ConditionAbstractType conditionAbs  : invalidAssertion.getConditions().getConditionOrAudienceRestrictionOrOneTimeUse()){
    		if(conditionAbs instanceof AudienceRestrictionType){
    			deleteList.add((AudienceRestrictionType)conditionAbs);    			
    		}
    	}
    	
    	for(AudienceRestrictionType ar : deleteList){
    		invalidAssertion.getConditions().getConditionOrAudienceRestrictionOrOneTimeUse().remove(ar);
    	}
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_CONDITIONS.toString()) > 0 : "Failed to report missing Audience";
	}

	private void validateInvalidConditions2() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(DateUtils.toXMLGregorianCalendar(new Date().getTime() - 1000L * 60L * 4L));
    	scd.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(new Date().getTime() + 1000L * 60L * 4L));
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);    	
    	
    	invalidAssertion.getConditions().getNotOnOrAfter().setYear(1997);
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_CONDITIONS.toString()) > 0 : "Failed to report invalid Conditions.NotOnOrAfter element";
	}

	private void validateInvalidConditions1() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(DateUtils.toXMLGregorianCalendar(new Date().getTime() - 1000L * 60L * 4L));
    	scd.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(new Date().getTime() + 1000L * 60L * 4L));
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	
    	invalidAssertion.getConditions().getNotBefore().setYear(2020);
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_CONDITIONS.toString()) > 0 : "Failed to report invalid Conditions.NotBefore element";
	}

	private void validateNoSubjectWithAuthnStatements() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	invalidAssertion.setSubject(null);
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_SUBJECT.toString()) > 0 : "Failed to report missing Subject element";
	}
    
    private void validateNoSubjectNoStatements() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	invalidAssertion.setSubject(null);
    	invalidAssertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().clear();
    	
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_SUBJECT.toString()) > 0 : "Failed to report missing Subject element";
	}

	private void validateInvalidSubjectConfirmationData3() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);

    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotOnOrAfter());
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotBefore());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_SUBJECT_CONF_DATA.toString()) > 0 : "Failed to report invalid subject confirmation data";
	}

	private void validateInvalidSubjectConfirmationData2() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);

    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(invalidAssertion.getConditions().getNotBefore());
    	scd.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(new Date()));
    	scd.getNotOnOrAfter().setYear(2020);
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_SUBJECT_CONF_DATA.toString()) > 0 : "Failed to report invalid subject confirmation data";
	}

	private void validateInvalidSubjectConfirmationData1() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);

    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
    	SubjectConfirmationDataType scd = new SubjectConfirmationDataType();
    	scd.setNotBefore(DateUtils.toXMLGregorianCalendar(new Date()));
    	scd.getNotBefore().setYear(1997);
    	scd.setNotOnOrAfter(invalidAssertion.getConditions().getNotOnOrAfter());
    	sc.setSubjectConfirmationData(scd);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));
    	
    	invalidAssertion.setSubject(sub);
    	invalidResponse = prepareResponse(invalidAssertion);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_SUBJECT_CONF_DATA.toString()) > 0 : "Failed to report invalid subject confirmation data";
	}

	private void validateNoMethod() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SubjectType sub = new SubjectType();
    	SubjectConfirmationType sc = new SubjectConfirmationType();
    	sc.setMethod(null);
    	
    	sub.getContent().add(new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "SubjectConfirmation"), SubjectConfirmationType.class, sc));    
    	
    	invalidAssertion.setSubject(sub);
    	
    	invalidResponse = prepareResponse(invalidAssertion); 

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_METHOD.toString()) > 0 : "Failed to report missing Method element";
	}

	private void validateInvalidAssertionSignature() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
		
    	AssertionType invalidAssertion = (AssertionType)invalidResponse.getAssertionOrEncryptedAssertion().get(0);
    	
    	SignatureValueType signatureValue = invalidAssertion.getSignature().getSignatureValue();
    	signatureValue.setValue("bad_signature".getBytes());
    	
    	invalidAssertion = signSamlAssertion(invalidAssertion);
    	invalidResponse = buildAuthnResponse(invalidAssertion);
    	invalidResponse = signSamlResponse(invalidResponse);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_ASSERTION_SIGNATURE.toString()) > 0 : "Failed to report invalid assertion signature";
    }
	
	private ResponseType prepareResponse(AssertionType asrt) throws Exception {
		asrt.setSignature(null);
		asrt = signSamlAssertion(asrt);
    	ResponseType resp = buildAuthnResponse(asrt);
    	return signSamlResponse(resp);
	}

	private void validateInvalidStatusCode() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();  	
    	
		StatusCodeType statusCode = new StatusCodeType();
		statusCode.setValue("bad_value");
		StatusType statusType = new StatusType();
		statusType.setStatusCode(statusCode);
    	invalidResponse.setStatus(statusType);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_STATUS_CODE.toString()) > 0 : "Failed to report invalid StatusCode value";
	}

	private void validateNoStatusCode() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();  	
    	
		StatusType statusType = new StatusType();
		statusType.setStatusCode(null);
    	invalidResponse.setStatus(statusType);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_STATUS_CODE) > 0 : "Failed to report missing StatusCode element";
	}

	private void validateNoStatus() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();  	
    	
    	invalidResponse.setStatus(null);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_STATUS.toString()) > 0 : "Failed to report missing Status element";
	}

	private void validateInvalidSignature() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();	
    	
    	SignatureValueType signatureValue = invalidResponse.getSignature().getSignatureValue();
    	signatureValue.setValue("bad_signature".getBytes());

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_RESPONSE_SIGNATURE.toString()) > 0 : "Failed to report invalid signature";
	}

	private void validateInvalidVersion() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();  	
    	
    	invalidResponse.setVersion("1.1");

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_VERSION.toString()) > 0 : "Invalid_response check failed";
	}

	private void validateNoVersion() throws Exception {
		ResponseType invalidResponse = unmarshalResponse(); 	
    	
    	invalidResponse.setVersion(null);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_VERSION.toString()) > 0 : "No_validation check failed";
	}

	private void validateNoIssueInstant() throws Exception {
		ResponseType invalidResponse = unmarshalResponse();	
    	
    	invalidResponse.setIssueInstant(null);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_ISSUE_INSTANT) > 0 : "No_IssueInstant check failed";
	}

	private void validateNoDestination() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();    	
    	
    	invalidResponse.setDestination(null);

    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.NO_DESTINATION.toString()) > 0 : "No_destination check failed";
	}
    
    private void validateBadDestination() throws Exception {
    	ResponseType invalidResponse = unmarshalResponse();
    	
    	invalidResponse.setDestination("bad_destination");
    	
    	PostMethod post = encodeAndSendResponse(invalidResponse);
    	int status = post.getStatusCode(); 
    	assert status == HttpStatus.SC_OK : "Unexpected HTTP status " + status;
    	
    	String body = post.getResponseBodyAsString();
    	
    	assert body.indexOf("Error code: urn:oasis:names:tc:SAML:2.0:status:RequestDenied") > 0 : "Bad primary error code";
    	assert body.indexOf("Secondary error code: " + StatusDetails.INVALID_DESTINATION.toString()) > 0 : "Invalid_destination check failed";
	}
    
    private ResponseType unmarshalResponse() throws JAXBException, FileNotFoundException {
    	JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
    	Unmarshaller um = context.createUnmarshaller();    	
    	Object ob = um.unmarshal(new FileReader("target/validation-response-001.xml"));
    	return ((JAXBElement<ResponseType>)ob).getValue();        	
    }

	private PostMethod encodeAndSendResponse(ResponseType invalidResponse) throws JAXBException, XMLStreamException, IOException {
		JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.protocol");
    	Writer stringWriter = new StringWriter();
    	Object o = new JAXBElement(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"), ResponseType.class, invalidResponse);
    	Marshaller m = context.createMarshaller();
    	m.marshal(o, new NamespaceFilterXMLStreamWriter(stringWriter));
    	String responseString = stringWriter.toString();
    	byte[] encodedBytes  = Base64.encodeBase64(responseString.getBytes());
    	
        NameValuePair responseContent = new NameValuePair("SAMLResponse", new String(encodedBytes));
        return doPost(SP_ACS_POST, responseContent);
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
        authnResponse.setDestination(SP_ACS_POST);
        
        StatusCodeType statusCode = new StatusCodeType();
        statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:Success");
        StatusType status = new StatusType();
        status.setStatusCode(statusCode);
        authnResponse.setStatus(status);
        authnResponse.getAssertionOrEncryptedAssertion().add( assertn );

        return authnResponse;
    }
    
    protected PostMethod doPost(String url, NameValuePair ... params) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestBody(params);

        this.getClient().executeMethod(postMethod);

        return postMethod;
    }

	public HttpClient getClient() {
		return client;
	}
	
    protected AssertionType signSamlAssertion(AssertionType tmpAssertion) throws Exception {
        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context");
        return (AssertionType) signer.sign(tmpAssertion);
    }	
	
    protected ResponseType signSamlResponse(ResponseType tmpResponse) throws Exception {
        if (signer == null)
            throw new Samlr2AssertionEmissionException("Cannot find a valid Samlr2 signer in application context");
        return (ResponseType) signer.sign(tmpResponse);
    }
    
    public void validate(AssertionType assertion) throws SamlR2SignatureException {
        SamlR2Signer jsrSigner = signer;
        jsrSigner.validate(assertion);
    }

    public void validate(StatusResponseType response) throws SamlR2SignatureException {
        SamlR2Signer jsrSigner = signer;
        jsrSigner.validate(response);
    }    

}
