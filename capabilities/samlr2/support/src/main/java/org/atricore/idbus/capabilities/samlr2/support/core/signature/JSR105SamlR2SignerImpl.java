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

package org.atricore.idbus.capabilities.samlr2.support.core.signature;

import oasis.names.tc.saml._1_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.KeyDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR11Constants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolverException;
import org.atricore.idbus.capabilities.samlr2.support.core.util.NamespaceFilterXMLStreamWriter;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlType;
import javax.xml.crypto.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.*;

/**
 * This will sign and verify saml2 identity artifact (assertion, requet, response) signatures usign a JSR 105 Provider.
 * <p/>
 * The provider can be injected or a FQCN can be specified as a system property. A default value will be used if no provider
 * is injected nor configured as system property.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 * @org.apache.xbean.XBean element="samlr2-signer"
 */
public class JSR105SamlR2SignerImpl implements SamlR2Signer {

    /**
     * The name of the system property that
     */
    public static final String JSR105_PROVIDER_PROPERTY = "jsr105Provider";

    /**
     * Default JSR 105 Provider FQCN
     */
    public static final String DEFAULT_JSR105_PROVIDER_FQCN = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";

    private static final Log logger = LogFactory.getLog(JSR105SamlR2SignerImpl.class);

    /**
     * JSR 105 Provider.
     */
    private Provider provider;

    private SamlR2KeyResolver keyResolver;

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public SamlR2KeyResolver getKeyResolver() {
        return keyResolver;
    }

    /**
     * @org.apache.xbean.Property alias="key-resolver"
     */
    public void setKeyResolver(SamlR2KeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }

    public String getProviderFQCN() {
        return System.getProperty(JSR105_PROVIDER_PROPERTY, DEFAULT_JSR105_PROVIDER_FQCN);
    }

    /**
     * @org.apache.xbean.InitMethod
     */
    public void init() {

        InputStream is = null;

        try {

            // If a provider was already 'injected', use it.
            if (provider == null) {

                if (logger.isDebugEnabled())
                    logger.debug("Creating JSR 105 Provider : " + getProviderFQCN());

                this.provider = (Provider) Class.forName(getProviderFQCN()).newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        }

    }

    public AssertionType sign(AssertionType assertion) throws SamlR2SignatureException {

        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Assertion to DOM Tree [" + assertion.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.newDocument();

            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_ASSERTION_PKG,
                    assertion.getClass().getClassLoader());

            Marshaller m = context.createMarshaller();
            JAXBElement<AssertionType> jaxbAssertion = new JAXBElement<AssertionType>(new QName(SAMLR2Constants.SAML_ASSERTION_NS, "Assertion"), AssertionType.class, assertion);
            m.marshal(jaxbAssertion, doc);

            doc = sign(doc, assertion.getID());

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbAssertion = (JAXBElement<AssertionType>) u.unmarshal(doc);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Assertion from DOM Tree [" + assertion.getID() + "]");

            return jaxbAssertion.getValue();

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Assertion " + assertion.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Assertion " + assertion.getID(), e);
        }

    }


    public void validate(RoleDescriptorType md, AssertionType assertion) throws SamlR2SignatureException, SamlR2SignatureValidationException {

        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Assertion to DOM Tree [" + assertion.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_ASSERTION_PKG,
                    assertion.getClass().getClassLoader());

            Marshaller m = context.createMarshaller();
            JAXBElement<AssertionType> jaxbAssertion = new JAXBElement<AssertionType>(new QName(SAMLR2Constants.SAML_ASSERTION_NS, "Assertion"), AssertionType.class, assertion);
            StringWriter swas = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swas);
            m.marshal(jaxbAssertion, sw);

            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swas.toString().getBytes()));

            validate(md, doc);

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error verifying SAMLR2 Assertion signature " + assertion.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Assertion signature " + assertion.getID(), e);
        } catch (IOException e) {
            throw new SamlR2SignatureException("I/O Error verifying SAMLR2 Assertion signature " + assertion.getID(), e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Assertion signature " + assertion.getID(), e);
        } catch (XMLStreamException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Assertion signature " + assertion.getID(), e);
        }
    }

    public RequestAbstractType sign(RequestAbstractType request) throws SamlR2SignatureException {
        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Request to DOM Tree [" + request.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.newDocument();

            JAXBContext context = XmlUtils.createSamlR2JAXBContext(request);
            Marshaller m = context.createMarshaller();

            JAXBElement<RequestAbstractType> jaxbRequest = XmlUtils.createJAXBelement(request);

            m.marshal(jaxbRequest, doc);

            doc = sign(doc, request.getID());

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Status Response from DOM Tree [" + request.getID() + "]");

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbRequest = (JAXBElement<RequestAbstractType>) u.unmarshal(doc);

            return jaxbRequest.getValue();
        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Response " + request.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + request.getID(), e);
        }
    }

    public StatusResponseType sign(StatusResponseType response) throws SamlR2SignatureException {
        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Response to DOM Tree [" + response.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_PROTOCOL_PKG,
                    response.getClass().getClassLoader());

            Marshaller m = context.createMarshaller();

            Class<StatusResponseType> clazz = (Class<StatusResponseType>) response.getClass();

            // Remove the 'Type' suffix from the xml type name and use it as XML element!
            XmlType t = clazz.getAnnotation(XmlType.class);
            String element = t.name().substring(0, t.name().length() - 4);

            JAXBElement<StatusResponseType> jaxbResponse = new JAXBElement<StatusResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, element), clazz, response);

            // remove prefixes from signature elements of embedded signed assertion so that signature validation -
            // which removes those prefixes - doesn't fail
            StringWriter swrsp = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swrsp);
            m.marshal(jaxbResponse, sw);

            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swrsp.toString().getBytes()));

            doc = sign(doc, response.getID());

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Status Response from DOM Tree [" + response.getID() + "]");

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbResponse = (JAXBElement<StatusResponseType>) u.unmarshal(doc);

            return jaxbResponse.getValue();
        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Response " + response.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + response.getID(), e);
        } catch (XMLStreamException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + response.getID(), e);
        } catch (IOException e) {
            throw new SamlR2SignatureException("I/O Error signing SAMLR2 Response " + response.getID(), e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + response.getID(), e);
        }
    }

    public void validate(RoleDescriptorType md, StatusResponseType response) throws SamlR2SignatureException {

        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Response to DOM Tree [" + response.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);
            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(XmlUtils.marshallSamlR2Response(response, false).getBytes()));

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 response" + response.getID());
        }
    }

    public void validate(RoleDescriptorType md, AuthnRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Authn Request to DOM Tree [" + request.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);
            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(XmlUtils.marshallSamlR2Request(request, false).getBytes()));

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 response" + request.getID());
        }
    }

    public void validate(RoleDescriptorType md, LogoutRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Logout Request to DOM Tree [" + request.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);
            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(XmlUtils.marshallSamlR2Request(request, false).getBytes()));

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 response" + request.getID());
        }
    }

    public void validate(RoleDescriptorType md, ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException {

        try {
            // Marshall the ManageNameID object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 ManageNameID to DOM Tree [" + manageNameIDRequest.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);


            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_PROTOCOL_PKG,
                    manageNameIDRequest.getClass().getClassLoader());

            Marshaller m = context.createMarshaller();

            Class<ManageNameIDRequestType> clazz = (Class<ManageNameIDRequestType>) manageNameIDRequest.getClass();

            // Remove the 'Type' suffix from the xml type name and use it as XML element!
            XmlType t = clazz.getAnnotation(XmlType.class);
            String element = t.name().substring(0, t.name().length() - 4);

            JAXBElement<ManageNameIDRequestType> jaxbResponse = new JAXBElement<ManageNameIDRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, element), ManageNameIDRequestType.class, manageNameIDRequest);
            StringWriter swrsp = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swrsp);
            m.marshal(jaxbResponse, sw);

            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swrsp.toString().getBytes()));

            validate(md, doc);

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        } catch (IOException e) {
            throw new SamlR2SignatureException("I/O Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        } catch (XMLStreamException e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        }
    }

    public ManageNameIDRequestType sign(ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException {

        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 ManageNameIDRequestType to DOM Tree [" + manageNameIDRequest.getID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.newDocument();

            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_PROTOCOL_PKG,
                    manageNameIDRequest.getClass().getClassLoader());

            Class<ManageNameIDRequestType> clazz = (Class<ManageNameIDRequestType>) manageNameIDRequest.getClass();

            Marshaller m = context.createMarshaller();

            // Remove the 'Type' suffix from the xml type name and use it as XML element!
            XmlType t = clazz.getAnnotation(XmlType.class);
            String element = t.name().substring(0, t.name().length() - 4);

            JAXBElement<ManageNameIDRequestType> jaxbMnidRequest = new JAXBElement<ManageNameIDRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, element), clazz, manageNameIDRequest);
            m.marshal(jaxbMnidRequest, doc);

            doc = sign(doc, manageNameIDRequest.getID());

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbMnidRequest = (JAXBElement<ManageNameIDRequestType>) u.unmarshal(doc);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Assertion from DOM Tree [" + manageNameIDRequest.getID() + "]");

            return jaxbMnidRequest.getValue();

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Assertion " + manageNameIDRequest.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Assertion " + manageNameIDRequest.getID(), e);
        }

    }

    // SAML 1.1

    public ResponseType sign(ResponseType response) throws SamlR2SignatureException {
        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR11 Response to DOM Tree [" + response.getResponseID() + "]");

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

            JAXBContext context = JAXBContext.newInstance(SAMLR11Constants.SAML_PROTOCOL_PKG,
                    response.getClass().getClassLoader());

            Marshaller m = context.createMarshaller();

            Class<ResponseType> clazz = (Class<ResponseType>) response.getClass();

            // Remove the 'Type' suffix from the xml type name and use it as XML element!
            XmlType t = clazz.getAnnotation(XmlType.class);
            String element = t.name().substring(0, t.name().length() - 4);

            JAXBElement<ResponseType> jaxbResponse = new JAXBElement<ResponseType>(new QName(SAMLR11Constants.SAML_PROTOCOL_NS, element), clazz, response);

            // remove prefixes from signature elements of embedded signed assertion so that signature validation -
            // which removes those prefixes - doesn't fail
            StringWriter swrsp = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swrsp);
            m.marshal(jaxbResponse, sw);

            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swrsp.toString().getBytes()));

            doc = sign(doc, response.getResponseID());

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR11 Response from DOM Tree [" + response.getResponseID() + "]");

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbResponse = (JAXBElement<ResponseType>) u.unmarshal(doc);

            return jaxbResponse.getValue();
        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR11 Response " + response.getResponseID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR11 Response " + response.getResponseID(), e);
        } catch (XMLStreamException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR11 Response " + response.getResponseID(), e);
        } catch (IOException e) {
            throw new SamlR2SignatureException("I/O Error signing SAMLR11 Response " + response.getResponseID(), e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR11 Response " + response.getResponseID(), e);
        }
    }


    // Primitives

    public void validate(RoleDescriptorType md, String domStr) throws SamlR2SignatureException {
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(domStr.getBytes()));

            validate(md, doc);

        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException(e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException(e);
        } catch (IOException e) {
            throw new SamlR2SignatureException(e);
        }

    }

    /**
     * This validates XML Didgital signature for SAML 2.0 Documents (requests, responses, assertions, etc)
     *
     * @param md  The signer SAML 2.0 Metadata
     * @param doc DOM representation of the document
     * @throws SamlR2SignatureException if the signature is invalid
     */
    public void validate(RoleDescriptorType md, Document doc) throws SamlR2SignatureException {
        try {

            // Check for duplicate IDs among XML elements
            NodeList nodes = evaluateXPath(doc, "//*/@ID");
            boolean duplicateIdExists = false;
            List<String> ids = new ArrayList<String>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (ids.contains(node.getNodeValue())) {
                    duplicateIdExists = true;
                    logger.error("Duplicated Element ID in XML Document : " + node.getNodeValue());
                }
                ids.add(node.getNodeValue());
            }
            if (duplicateIdExists) {
                throw new SamlR2SignatureException("Duplicate IDs in document ");
            }

            // TODO : Check that the Signature references the root element (the one used by the application)
            // Keep in mind that signature reference might be an XPath expression ?!

            // We know that in SAML, the root element is the element used by the application, we just need to make sure that
            // the root element is the one referred by the signature
            Node root = doc.getDocumentElement();
            Node rootIdAttr = root.getAttributes().getNamedItem("ID");
            if (rootIdAttr == null)
                throw new SamlR2SignatureException("SAML document does not have an ID ");

            // Find Signature element
            NodeList signatureNodes =
                    doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (signatureNodes.getLength() == 0) {
                throw new SamlR2SignatureException("Cannot find Signature elements");
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", provider);

            // Create a DOMValidateContext and specify a KeyValue KeySelector
            // and document context

            // Validate all Signature elements
            boolean rootIdMatched = false;
            for (int k = 0; k < signatureNodes.getLength(); k++) {

                DOMValidateContext valContext = new DOMValidateContext
                        (new RawX509KeySelector(), signatureNodes.item(k));

                // unmarshal the XMLSignature
                XMLSignature signature = fac.unmarshalXMLSignature(valContext);

                // Validate the XMLSignature (generated above)
                boolean coreValidity = signature.validate(valContext);

                // Check core validation status
                if (!coreValidity) {

                    if (logger.isDebugEnabled())
                        logger.debug("Signature failed core validation");

                    boolean sv = signature.getSignatureValue().validate(valContext);

                    if (logger.isDebugEnabled())
                        logger.debug("signature validation status: " + sv);
                    // check the validation status of each Reference (should be only one!)
                    Iterator i = signature.getSignedInfo().getReferences().iterator();
                    boolean refValid = true;
                    for (int j = 0; i.hasNext(); j++) {

                        Reference ref = (Reference) i.next();
                        boolean b = ref.validate(valContext);
                        if (logger.isDebugEnabled())
                            logger.debug("ref[" + j + "] " + ref.getId() + " validity status: " + b);

                        if (!b) {
                            refValid = b;
                            logger.error("Signature failed reference validation " + ref.getId());
                        }


                    }
                    throw new SamlR2SignatureValidationException("Signature failed core validation" + (refValid ? " but passed all Reference validations" : " and some/all Reference validation"));
                }

                if (logger.isDebugEnabled())
                    logger.debug("Singnature passed Core validation");

                // The Signature must contain only one reference, and it must be the signed top element's ID.
                List<Reference> refs = signature.getSignedInfo().getReferences();
                if (refs.size() != 1) {
                    throw new SamlR2SignatureValidationException("Invalid number of 'Reference' elements in signature : "
                            + refs.size() + " [" + signature.getId() + "]");
                }

                Reference reference = refs.get(0);
                String referenceURI = reference.getURI();

                if (referenceURI == null || !referenceURI.startsWith("#"))
                    throw new SamlR2SignatureValidationException("Signature reference URI format not supported " + referenceURI);

                if (referenceURI.substring(1).equals(rootIdAttr.getNodeValue()))
                    rootIdMatched = true;

                Key key = signature.getKeySelectorResult().getKey();
                boolean certValidity = validateCertificate(md, key);
                if (!certValidity) {
                    throw new SamlR2SignatureValidationException("Signature failed Certificate validation");
                }

                if (logger.isDebugEnabled())
                    logger.debug("Signature passed Certificate validation");

            }

            // Check that any of the Signatures matched the root element ID
            if (!rootIdMatched) {
                logger.error("No Signature element refers to root element (possible signature wrapping attack)");
                throw new SamlR2SignatureValidationException("No Signature element refers to root element");
            }


        } catch (MarshalException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected boolean validateCertificate(RoleDescriptorType md, Key publicKey) {

        byte[] x509CertificateBin = null;


        if (md.getKeyDescriptor() != null && md.getKeyDescriptor().size() > 0) {

            for (KeyDescriptorType keyMd : md.getKeyDescriptor()) {

                if (keyMd.getKeyInfo() != null) {

                    // Get inside Key Info
                    List contentMd = keyMd.getKeyInfo().getContent();
                    if (contentMd != null && contentMd.size() > 0) {

                        for (Object o : contentMd) {

                            if (o instanceof JAXBElement) {
                                JAXBElement e = (JAXBElement) o;
                                if (e.getValue() instanceof X509DataType) {

                                    X509DataType x509Data = (X509DataType) e.getValue();

                                    for (Object x509Content : x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName()) {
                                        if (x509Content instanceof JAXBElement) {
                                            JAXBElement x509Certificate = (JAXBElement) x509Content;

                                            if (x509Certificate.getName().getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") &&
                                                    x509Certificate.getName().getLocalPart().equals("X509Certificate")) {

                                                x509CertificateBin = (byte[]) x509Certificate.getValue();
                                                break;
                                            }
                                        }
                                    }

                                }
                            }

                            if (x509CertificateBin != null)
                                break;
                        }
                    }
                } else {
                    logger.debug("Metadata Key Descriptor does not have KeyInfo " + keyMd.toString());
                }

                if (x509CertificateBin != null)
                    break;
            }


        } else {
            logger.debug("Metadata does not have Key Descriptors: " + md.getID());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Configured Certificate: " + Arrays.toString(publicKey.getEncoded()));
            logger.trace("Used Certificate: " + Arrays.toString(x509CertificateBin));
        }

        if (x509CertificateBin != null) {

            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate x509Cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(x509CertificateBin));

                PublicKey x509PublicKey = x509Cert.getPublicKey();

                byte[] x509PublicKeyEncoded = x509PublicKey.getEncoded();
                byte[] publicKeyEncoded = publicKey.getEncoded();

                return java.util.Arrays.equals(x509PublicKeyEncoded, publicKeyEncoded);

            } catch (CertificateException e) {
                logger.error(e.getMessage(), e);
            }

        }

        return false;

    }


    /**
     * This will sign a SAMLR2 Identity artifact (assertion, request or response) represeted as a DOM tree
     * The signature will be inserted as the first child of the root element.
     *
     * @param doc
     * @param id
     * @return
     */
    protected Document sign(Document doc, String id) throws SamlR2SignatureException {
        try {

            Certificate cert = keyResolver.getCertificate();

            // Create a DOM XMLSignatureFactory that will be used to generate the
            // enveloped signature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", provider);

            if (logger.isDebugEnabled())
                logger.debug("Creating XML DOM Digital Siganture (not signing yet!)");

            // Create a Reference to the enveloped document and
            // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
            // The URI must be the assertion ID

            List<Transform> transforms = new ArrayList<Transform>();
            transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            // Magically, this solves assertion DS validation when embedded in a signed response :)
            transforms.add(fac.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null));

            Reference ref = fac.newReference
                    ("#" + id,
                            fac.newDigestMethod(DigestMethod.SHA1, null),
                            transforms,
                            null, null);

            // Use signature method based on key algorithm.
            String signatureMethod = SignatureMethod.DSA_SHA1;
            if (keyResolver.getPrivateKey().getAlgorithm().equals("RSA"))
                signatureMethod = SignatureMethod.RSA_SHA1;

            logger.debug("Using signature method " + signatureMethod);

            // Create the SignedInfo, with the X509 Certificate
            /*
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                            (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                    (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(signatureMethod, null),
                            Collections.singletonList(ref));
             */
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                            (CanonicalizationMethod.EXCLUSIVE,
                                    (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(signatureMethod, null),
                            Collections.singletonList(ref));

            // Create a KeyInfo and add the Certificate to it
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            X509Data kv = kif.newX509Data(Collections.singletonList(cert));
            //KeyValue kv = kif.newKeyValue(keyResolver.getCertificate().getPublicKey());

            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
            javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki);

            if (logger.isDebugEnabled())
                logger.debug("Signing SAMLR2 Identity Artifact ...");

            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext
                    (keyResolver.getPrivateKey(), doc.getDocumentElement(), doc.getDocumentElement().getFirstChild());

            // Sign the assertion
            signature.sign(dsc);

            if (logger.isDebugEnabled())
                logger.debug("Signing SAMLR2 Identity Artifact ... DONE!");

            return doc;


        } catch (NoSuchAlgorithmException e) {
            throw new SamlR2SignatureException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new SamlR2SignatureException(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new SamlR2SignatureException(e.getMessage(), e);
        } catch (MarshalException e) {
            throw new SamlR2SignatureException(e.getMessage(), e);
        } catch (SamlR2KeyResolverException e) {
            throw new SamlR2SignatureException(e.getMessage(), e);
        }
    }

    protected NodeList evaluateXPath(Document doc, String expression) throws SamlR2SignatureException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(getNamespaceContext());

        NodeList nl;
        try {
            XPathExpression expr = xpath.compile(expression);

            nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new SamlR2SignatureException(e);
        }

        return nl;
    }

    protected NamespaceContext getNamespaceContext() {

        return new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                if (prefix.equals("ds"))
                    return org.apache.xml.security.utils.Constants.SignatureSpecNS;
                if (prefix.equals("p"))
                    return SAMLR2Constants.SAML_PROTOCOL_NS;
                if (prefix.equals("a"))
                    return SAMLR2Constants.SAML_ASSERTION_NS;

                return null;
            }

            // Dummy implementation - not used!
            public Iterator getPrefixes(String val) {
                return null;
            }

            // Dummy implemenation - not used!
            public String getPrefix(String uri) {
                if (uri.equals(org.apache.xml.security.utils.Constants.SignatureSpecNS))
                    return "ds";
                if (uri.equals(SAMLR2Constants.SAML_PROTOCOL_NS))
                    return "p";
                if (uri.equals(SAMLR2Constants.SAML_ASSERTION_NS))
                    return "a";

                return null;
            }

        };

    }

    /**
     * KeySelector which retrieves the public key out of the
     * KeyValue element and returns it.
     * NOTE: If the key algorithm doesn't match signature algorithm,
     * then the public key will be ignored.
     */
    private static class KeyValueKeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List list = keyInfo.getContent();

            for (Object aList : list) {
                XMLStructure xmlStructure = (XMLStructure) aList;
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue) xmlStructure).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SimpleKeySelectorResult(pk);
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        static boolean algEquals(String algURI, String algName) {
            if (algName.equalsIgnoreCase("DSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
                return true;
            } else if (algName.equalsIgnoreCase("RSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
                return true;
            } else {
                logger.error("Unsupported Key Algorithm found in signature: " + algName);
                return false;
            }
        }
    }


    /**
     * KeySelector which would retrieve the X509Certificate out of the
     * KeyInfo element and return the public key.
     * NOTE: If there is an X509CRL in the KeyInfo element, then revoked
     * certificate will be ignored.
     */
    public static class RawX509KeySelector extends KeySelector {

        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            // search for X509Data in keyinfo
            Iterator iter = keyInfo.getContent().iterator();
            while (iter.hasNext()) {
                XMLStructure kiType = (XMLStructure) iter.next();
                if (kiType instanceof X509Data) {
                    X509Data xd = (X509Data) kiType;
                    Object[] entries = xd.getContent().toArray();
                    X509CRL crl = null;
                    // Looking for CRL before finding certificates
                    for (int i = 0; (i < entries.length && crl != null); i++) {
                        if (entries[i] instanceof X509CRL) {
                            crl = (X509CRL) entries[i];
                        }
                    }
                    Iterator xi = xd.getContent().iterator();
                    boolean hasCRL = false;
                    while (xi.hasNext()) {
                        Object o = xi.next();
                        // skip non-X509Certificate entries
                        if (o instanceof X509Certificate) {
                            if ((purpose != KeySelector.Purpose.VERIFY) &&
                                    (crl != null) &&
                                    crl.isRevoked((X509Certificate) o)) {
                                continue;
                            } else {
                                return new SimpleKeySelectorResult
                                        (((X509Certificate) o).getPublicKey());
                            }
                        }
                    }
                }
            }
            throw new KeySelectorException("No X509Certificate found!");
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return pk;
        }
    }

    @Override
    public String toString
            () {
        return super.toString() + "[provider.name=" + provider.getName() +
                "provider.info=" + provider.getInfo() +
                ",keyResolver=" + keyResolver +
                "]";
    }
}




