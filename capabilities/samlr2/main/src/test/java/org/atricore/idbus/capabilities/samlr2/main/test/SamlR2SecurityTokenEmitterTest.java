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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.main.SSOConstants;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.XmlSecurityEncrypterImpl;
import org.atricore.idbus.capabilities.samlr2.support.profiles.DCEPACAttributeDefinition;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.junit.Before;
import org.junit.Test;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This test will emitt a SAMLR2 Assertion and serialize it as XML in the target folder.
 * It will also verify that the assertion has all required information.
 *
 * @author <a href=mailto:ajadzinsky@atricor.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Apr 30, 2009
 */
public class SamlR2SecurityTokenEmitterTest {

    private static final Log logger = LogFactory.getLog( SamlR2SecurityTokenEmitterTest.class );

    protected ClassPathXmlApplicationContext applicationContext;

    private AssertionType assertion;

    @Before
    public void setUp () throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-emitter-test.xml"}
        );

        SecurityTokenService sts = (SecurityTokenService) applicationContext.getBean( "sts" );
        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put( SSOConstants.PARAM_SSO_USERNAME, "user1" );
        credentials.put( SSOConstants.PARAM_SSO_PASSWORD, "user1pwd" );
        RequestSecurityTokenType rst = buildRequestSecurityToken( credentials );
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken( rst );
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get( 1 );

//        assertion = (AssertionType) token.getFormat().getAny();
        EncryptedElementType encryptedAssertion = (EncryptedElementType) token.getValue().getAny();
        XmlSecurityEncrypterImpl decrypter = (XmlSecurityEncrypterImpl)applicationContext.getBean("samlr2-assertion-encrypter");
        logger.debug( "decrypting assertion..." );
        assertion = decrypter.decryptAssertion(encryptedAssertion);

        // Dump the assertion as a XML document

        // Create DOM document factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        // Create JAXB Context and marshal the assertion
        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.assertion");
        Marshaller m = context.createMarshaller();

        Writer writer = new FileWriter("target/assertion-001.xml");

        Object o = new JAXBElement(new QName("urn:oasis:names:tc:SAML:2.0:assertion", "Assertion"), AssertionType.class, assertion);
        m.marshal(o, writer);

        writer.close();



    }

    @Test
    public void assertionAttributesTest () {
        logger.debug( "************************************************" );
        logger.debug( "************************************************" );
        logger.debug( "************************************************" );
        logger.debug( "check assertion attributes" );
        List<String> roles = new ArrayList<String>( );
        logger.debug( "Assertion [" + assertion.getID() + "]" );
        List<StatementAbstractType> stmts = assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement();
        logger.debug( "Statements [" + stmts.size() + "]" );
        for ( StatementAbstractType stmt : stmts ) {
            if ( stmt instanceof AttributeStatementType ) {
                AttributeStatementType attrStmt = (AttributeStatementType) stmt;
                List attrs = attrStmt.getAttributeOrEncryptedAttribute();
                logger.debug( "Attribute Statements [" + attrs.size() + "]" );
                for ( Object attrOrEncAttr : attrs ) {
                    if ( attrOrEncAttr instanceof AttributeType ) {
                        AttributeType attr = (AttributeType) attrOrEncAttr;
                        logger.debug( "AttributeType:" + attr.getName() );

                        if ( attr.getName().equals( DCEPACAttributeDefinition.GROUPS.getValue()) ) {

                            List attrValues = attr.getAttributeValue();
                            logger.debug( "Attribute values [" + attrValues.size() + "]" );
                            for ( Object attrValue : attrValues ) {
                                logger.debug( "Attribute value [" + attrValue + "]" );
                                roles.add( attrValue.toString() );
                            }

                        }
                    }
                }
            }
        }

        assert roles.contains( "role1" ) ;
        assert roles.contains( "role2" ) ;
    }

    protected RequestSecurityTokenType buildRequestSecurityToken ( Map<String, String> credentials ) throws Exception {
        logger.debug( "generating RequestSecurityToken..." );
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

        logger.debug( "generated RequestSecurityToken [" + rstRequest + "]" );
        return rstRequest;
    }
}
