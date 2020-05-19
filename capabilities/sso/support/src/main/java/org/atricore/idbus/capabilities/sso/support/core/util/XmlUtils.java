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

package org.atricore.idbus.capabilities.sso.support.core.util;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.SAMLR11Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SSOConstants;
import org.atricore.idbus.capabilities.sso.support.core.InvalidXMLException;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.databinding.JAXBUtils;
import org.w3._2001._04.xmlenc_.EncryptedDataType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.*;
import javax.xml.ws.Holder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: XmlUtils.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class XmlUtils {

    /**
     * List of tokens that may be in an xpath expression.
     */
    private static String[] xpath = {
            "/", "..", "@", "*", "[", "]", "(", "(", "{", "}", "?", "$", "#", "|", "*", "=", "!=", "<", "<=", ">", ">=",
            "node", "ancestor", "descendant", "following", "attribute", "child", "namespace", "parent", "preceding", "self",
            "document-node", "text", "comment", "namespace-code", "processing-instruction",
    };


    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    private static final TreeSet<String> samlContextPackages = new TreeSet<String>();

    private static final Holder<JAXBUtils.CONSTRUCTION_TYPE> constructionType = new Holder<JAXBUtils.CONSTRUCTION_TYPE>();

    private static final XMLInputFactory staxIF = XMLInputFactory.newInstance();

    private static final XMLOutputFactory staxOF = XMLOutputFactory.newInstance();

    static {
        samlContextPackages.add(SAMLR2Constants.SAML_PROTOCOL_PKG);
        samlContextPackages.add(SAMLR2Constants.SAML_ASSERTION_PKG);
        samlContextPackages.add(SAMLR2Constants.SSO_COMMON_PKG);
        samlContextPackages.add(SAMLR2Constants.SAML_IDBUS_PKG);
        samlContextPackages.add(SAMLR2Constants.SAML_METADATA_PKG);

        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        javax.xml.parsers.SAXParserFactory saxf =
                SAXParserFactory.newInstance();

        String FEATURE = null;

        try {
            // -----------------------------------------------------------------------------
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all
            // XML entity attacks are prevented
            // -----------------------------------------------------------------------------
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);

            // -----------------------------------------------------------------------------
            // If you can't completely disable DTDs, then at least do the following:
            // -----------------------------------------------------------------------------
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // -----------------------------------------------------------------------------
            // Disable external DTDs as well
            // -----------------------------------------------------------------------------
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);

            // -----------------------------------------------------------------------------
            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            // -----------------------------------------------------------------------------
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            // And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."

            logger.debug("DocumentBuilder = " + dbf.newDocumentBuilder());
            logger.debug("SAXParser = " + saxf.newSAXParser());
            logger.debug("XMLEventReader = " + staxIF.createXMLEventReader(new StringSource("<a>Hello</a>")));
            logger.debug("XMLEventWriter = " + staxOF.createXMLEventWriter(new ByteArrayOutputStream()));
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
        } catch (SAXException e) {
            logger.error(e.getMessage(), e);
        } catch (XMLStreamException e) {
            logger.error(e.getMessage(), e);
        }

    }

    //  -----------------------------------------------------------
    // JAXBUtils
    //  -----------------------------------------------------------

    // SAML 2.0 MD

    public static String masrhalSamlR2Metadata(EntityDescriptorType metadata, boolean encode) throws Exception {
        String type = metadata.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return masrhalSamlR2Metadata(metadata, type, encode);
    }

    public static String masrhalSamlR2Metadata(EntityDescriptorType metadata, String metadataType, boolean encode) throws Exception {

        String marshaledMd;
        marshaledMd = marshalSamlR2(
                metadata,
                SAMLR2Constants.SAML_METADATA_NS,
                metadataType
        );
        return encode ? new String(new Base64().encode(marshaledMd.getBytes())) : marshaledMd;
    }

    // SAML 2.0 Request

    public static String marshalSamlR2Request(RequestAbstractType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSamlR2Request(request, type, encode);
    }

    public static Document marshalSamlR2RequestAsDom(RequestAbstractType request) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSamlR2RequestAsDom(request, type);
    }


    public static Document marshalSamlR2RequestAsDom(RequestAbstractType request, String requestType) throws Exception {

        // Support IDBus SAMLR2 Extentions when marshalling
        Document doc = null;
        if (request.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

            doc = marshalSamlR2AsDom(
                    request,
                    SAMLR2Constants.SAML_IDBUS_NS,
                    requestType,
                    new String[]{SAMLR2Constants.SAML_IDBUS_PKG}
            );

        } else {

            doc = marshalSamlR2AsDom(
                    request,
                    SAMLR2Constants.SAML_PROTOCOL_NS,
                    requestType,
                    new String[]{SAMLR2Constants.SAML_PROTOCOL_PKG}
            );
        }

        return doc;

    }

    public static String marshalSamlR2Request(RequestAbstractType request, String requestType, boolean encode) throws Exception {

        String marshaledRequest;
        // Support IDBus SAMLR2 Extentions when marshalling
        if (request.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

            marshaledRequest = marshalSamlR2(
                    request,
                    SAMLR2Constants.SAML_IDBUS_NS,
                    requestType
            );

        } else {

            marshaledRequest = marshalSamlR2(
                    request,
                    SAMLR2Constants.SAML_PROTOCOL_NS,
                    requestType
            );
        }

        return encode ? new String(new Base64().encode(marshaledRequest.getBytes())) : marshaledRequest;
    }


    public static RequestAbstractType unmarshalSamlR2Request(String request, boolean decode) throws Exception {
        if (decode)
            request = decode(request);

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(request)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);


        RequestAbstractType samlRequest = null;
        if (o instanceof JAXBElement)
            samlRequest = (RequestAbstractType) ((JAXBElement) o).getValue();
        else
            samlRequest = (RequestAbstractType) o;

        verifyRequest(samlRequest);

        return samlRequest;
    }


    /**
     * This unmarshalls a Base64 SAML Request
     */
    public static RequestAbstractType unmarshalSamlR2Request(String base64Request) throws Exception {
        return unmarshalSamlR2Request(base64Request, true);
    }

    public static RequestAbstractType unmarshalSamlR2Request(Document doc) throws Exception {
        RequestAbstractType samlRequest = (RequestAbstractType) unmarshal(doc, new String[]{SAMLR2Constants.SAML_PROTOCOL_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG});

        verifyRequest(samlRequest);

        return samlRequest;

    }


    // SAML 2.0 Response

    public static Document marshalSamlR2ResponseAsDom(StatusResponseType response) throws Exception {

        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSamlR2ResponseAsDom(response, type);
    }


    public static String marshalSamlR2Response(StatusResponseType response,
                                               boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSamlR2Response(response, type, encode);
    }

    public static Document marshalSamlR2ResponseAsDom(StatusResponseType response, String responseType) throws Exception {
        // Support IDBus SAMLR2 Extentions when marshalling
        if (response.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

            return XmlUtils.marshalSamlR2AsDom(
                    response,
                    SAMLR2Constants.SAML_IDBUS_NS,
                    responseType,
                    new String[]{SAMLR2Constants.SAML_IDBUS_PKG}
            );
        } else {
            return XmlUtils.marshalSamlR2AsDom(
                    response,
                    SAMLR2Constants.SAML_PROTOCOL_NS,
                    responseType,
                    new String[]{SAMLR2Constants.SAML_PROTOCOL_PKG}
            );

        }

    }

    public static String marshalSamlR2Response(StatusResponseType response,
                                               String responseType, boolean encode) throws Exception {

        String marshaledResponse;
        // Support IDBus SAMLR2 Extentions when marshalling
        if (response.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG)) {

            marshaledResponse = XmlUtils.marshalSamlR2(
                    response,
                    SAMLR2Constants.SAML_IDBUS_NS,
                    responseType
            );
        } else {
            marshaledResponse = XmlUtils.marshalSamlR2(
                    response,
                    SAMLR2Constants.SAML_PROTOCOL_NS,
                    responseType
            );

        }

        return encode ? new String(new Base64().encode(marshaledResponse.getBytes())) : marshaledResponse;
    }

    /**
     * This unmarshalls a Base64 SAML Response
     */
    public static StatusResponseType unmarshalSamlR2Response(String response, boolean decode) throws Exception {
        if (decode)
            response = decode(response);

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(response)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        StatusResponseType statusResponse = null;
        if (o instanceof JAXBElement)
            statusResponse = (StatusResponseType) ((JAXBElement) o).getValue();
        else
            statusResponse = (StatusResponseType) o;

        verifyStatusResponse(statusResponse);

        return statusResponse;
    }

    public static StatusResponseType unmarshalSamlR2Response(String base64Response) throws Exception {
        return unmarshalSamlR2Response(base64Response, true);
    }

    public static StatusResponseType unmarshalSamlR2Response(Document doc) throws Exception {
        StatusResponseType statusResponse = (StatusResponseType) unmarshal(doc,
                new String[]{SAMLR2Constants.SAML_PROTOCOL_PKG, SAMLR2Constants.SAML_IDBUS_PKG});

        verifyStatusResponse(statusResponse);

        return statusResponse;


    }

    // SAML EX Request

    public static String marshalSSORequest(SSORequestAbstractType request, boolean encode) throws Exception {
        String type = request.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSSORequest(request, type, encode);
    }

    public static String marshalSSORequest(SSORequestAbstractType request, String requestType, boolean encode) throws Exception {

        String marshaledRequest = marshalSamlR2(
                request,
                SSOConstants.SSO_PROTOCOL_NS,
                requestType
        );

        return encode ? new String(new Base64().encode(marshaledRequest.getBytes())) : marshaledRequest;
    }

    public static SSORequestAbstractType unmarshalSSORequest(String request, boolean decode) throws Exception {
        if (decode)
            request = decode(request);

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(request)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        SSORequestAbstractType ssoRequest = null;
        if (o instanceof JAXBElement)
            ssoRequest = (SSORequestAbstractType) ((JAXBElement) o).getValue();
        else
            ssoRequest = (SSORequestAbstractType) o;

        verifySSORequest(ssoRequest);

        return ssoRequest;
    }


    /**
     * This unmarshals a Base64 SAML Request
     */
    public static SSORequestAbstractType unmarshalSSORequest(String base64Request) throws Exception {
        return unmarshalSSORequest(base64Request, true);
    }


    // SAML EX Response

    public static String marshalSSOResponse(SSOResponseType response,
                                            boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSSOResponse(response, type, encode);
    }

    public static String marshalSSOResponse(SSOResponseType response,
                                            String responseType, boolean encode) throws Exception {
        String marshaledResponse = XmlUtils.marshalSamlR2(
                response,
                SSOConstants.SSO_PROTOCOL_NS,
                responseType
        );

        return encode ? new String(new Base64().encode(marshaledResponse.getBytes())) : marshaledResponse;
    }

    /**
     * This unmarshalls a Base64 SAML Response
     */
    public static SSOResponseType unmarshalSSOResponse(String response, boolean decode) throws Exception {
        if (decode)
            response = decode(response);

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(response)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);


        SSOResponseType ssoResponse = null;
        if (o instanceof JAXBElement)
            ssoResponse = (SSOResponseType) ((JAXBElement) o).getValue();
        else
            ssoResponse = (SSOResponseType) o;

        verifySSOResponse(ssoResponse);

        return ssoResponse;
    }

    public static StatusResponseType unmarshalSSOResponse(String base64Response) throws Exception {
        return unmarshalSamlR2Response(base64Response, true);

    }

    // SAML 1.1 Response
    public static String marshalSamlR11Response(oasis.names.tc.saml._1_0.protocol.ResponseType response,
                                                boolean encode) throws Exception {
        String type = response.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshalSamlR11Response(response, type, encode);
    }

    public static String marshalSamlR11Response(oasis.names.tc.saml._1_0.protocol.ResponseType response,
                                                String responseType, boolean encode) throws Exception {

        String marshaledResponse;
        marshaledResponse = XmlUtils.marshalSamlR2(
                response,
                SAMLR11Constants.SAML_PROTOCOL_NS,
                responseType
        );


        return encode ? new String(new Base64().encode(marshaledResponse.getBytes())) : marshaledResponse;
    }

    public static String decode(String content) {
        return new String(new Base64().decode(content.getBytes()));
    }


    // SAML

    public static JAXBElement<RequestAbstractType> createJAXBelement(RequestAbstractType request) {

        Class<RequestAbstractType> clazz = (Class<RequestAbstractType>) request.getClass();

        // Remove the 'Type' suffix from the xml type name and use it as XML element!
        XmlType t = clazz.getAnnotation(XmlType.class);
        String element = t.name().substring(0, t.name().length() - 4);

        if (request.getClass().getPackage().getName().equals(SAMLR2Constants.SAML_IDBUS_PKG))
            return new JAXBElement<RequestAbstractType>(new QName(SAMLR2Constants.SAML_IDBUS_NS, element), clazz, request);
        else
            return new JAXBElement<RequestAbstractType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, element), clazz, request);
    }

    // JAXB Generic

    public static String marshalSamlR2(Object msg,
                                       String msgQName,
                                       String msgLocalName) throws Exception {

        //JAXBContext jaxbContext = createJAXBContext(userPackages);
        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller m = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );

        Writer writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new NamespaceFilterXMLStreamWriter(writer);

        // Support XMLDsig

        // TODO : What about non-sun XML Bind stacks!
        m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
                new NamespacePrefixMapper() {

                    @Override
                    public String[] getPreDeclaredNamespaceUris() {
                        return new String[]{
                                SAMLR2Constants.SAML_PROTOCOL_NS,
                                SAMLR2Constants.SAML_ASSERTION_NS,
                                "http://www.w3.org/2000/09/xmldsig#",
                                "http://www.w3.org/2001/04/xmlenc#",
                                "http://www.w3.org/2001/XMLSchema"
                        };
                    }

                    @Override
                    public String getPreferredPrefix(String nsUri, String suggestion, boolean requirePrefix) {

                        if (nsUri.equals(SAMLR2Constants.SAML_PROTOCOL_NS))
                            return "samlp";
                        else if (nsUri.equals(SAMLR2Constants.SAML_ASSERTION_NS))
                            return "saml";
                        else if (nsUri.equals("http://www.w3.org/2000/09/xmldsig#"))
                            return "ds";
                        else if (nsUri.equals("http://www.w3.org/2001/04/xmlenc#"))
                            return "enc";
                        else if (nsUri.equals("http://www.w3.org/2001/XMLSchema"))
                            return "xsd";


                        return suggestion;
                    }
                });

        m.marshal(jaxbRequest, xmlStreamWriter);
        xmlStreamWriter.flush();
        JAXBUtils.releaseJAXBMarshaller(jaxbContext, m);

        return writer.toString();
    }

    public static String marshal(Object msg,
                                 final String msgQName,
                                 final String msgLocalName,
                                 String[] userPackages) throws Exception {


        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller marshaller = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                msg.getClass(),
                msg
        );

        Writer writer = new StringWriter();

        // Support XMLDsig
        XMLEventWriter xmlWriter = staxOF.createXMLEventWriter(writer);
        marshaller.marshal(jaxbRequest, xmlWriter);
        xmlWriter.flush();
        JAXBUtils.releaseJAXBMarshaller(jaxbContext, marshaller);

        return writer.toString();

    }

    public static Document marshalSamlR2AsDom(Object msg,
                                              String msgQName,
                                              String msgLocalName,
                                              String[] userPackages) throws Exception {

        // JAXB Element
        JAXBElement jaxbMsg = new JAXBElement(new QName(msgQName, msgLocalName), msg.getClass(), msg);

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(samlContextPackages, constructionType,
                samlContextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller marshaller = JAXBUtils.getJAXBMarshaller(jaxbContext);

        // Marshal as string and then parse with DOM ...
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new NamespaceFilterXMLStreamWriter(writer);

        // TODO : What about non-sun XML Bind stacks!

        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
                new NamespacePrefixMapper() {

                    @Override
                    public String[] getPreDeclaredNamespaceUris() {
                        return new String[]{
                                SAMLR2Constants.SAML_PROTOCOL_NS,
                                SAMLR2Constants.SAML_ASSERTION_NS,
                                "http://www.w3.org/2000/09/xmldsig#",
                                "http://www.w3.org/2001/04/xmlenc#",
                                "http://www.w3.org/2001/XMLSchema"
                        };
                    }

                    @Override
                    public String getPreferredPrefix(String nsUri, String suggestion, boolean requirePrefix) {

                        if (nsUri.equals(SAMLR2Constants.SAML_PROTOCOL_NS))
                            return "samlp";
                        else if (nsUri.equals(SAMLR2Constants.SAML_ASSERTION_NS))
                            return "saml";
                        else if (nsUri.equals("http://www.w3.org/2000/09/xmldsig#"))
                            return "ds";
                        else if (nsUri.equals("http://www.w3.org/2001/04/xmlenc#"))
                            return "enc";
                        else if (nsUri.equals("http://www.w3.org/2001/XMLSchema"))
                            return "xsd";


                        return suggestion;
                    }
                });

        marshaller.marshal(jaxbMsg, xmlStreamWriter);
        xmlStreamWriter.flush();

        // Instantiate the document to be signed
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        // XML Signature needs to be namespace aware
        dbf.setNamespaceAware(true);

        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(writer.toString().getBytes()));

        JAXBUtils.releaseJAXBMarshaller(jaxbContext, marshaller);
        return doc;


    }


    public static Object unmarshal(String msg, String userPackages[]) throws Exception {
        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(staxIF.createXMLEventReader(new StringSource(msg)));
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;

    }

    public static Object unmarshal(Document doc, String userPackages[]) throws Exception {

        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(doc);
        JAXBUtils.releaseJAXBUnmarshaller(jaxbContext, unmarshaller);

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;
    }

    public static Document marshalSamlR2AssertionAsDom(AssertionType assertion) throws Exception {
        return marshalSamlR2AsDom(assertion, SAMLR2Constants.SAML_ASSERTION_NS, "Assertion", new String[]{SAMLR2Constants.SAML_ASSERTION_PKG});
    }

    public static Document marshalSamlR2EncryptedAssertionAsDom(EncryptedElementType assertion) throws Exception {
        return marshalSamlR2AsDom(assertion, SAMLR2Constants.SAML_ASSERTION_NS, "EncryptedAssertion", new String[]{SAMLR2Constants.SAML_ASSERTION_PKG});
    }

    public static AssertionType unmarshalSamlR2Assertion(Document doc) throws Exception {
        AssertionType samlAssertion = (AssertionType) unmarshal(doc, new String[]{SAMLR2Constants.SAML_ASSERTION_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG});

        verifyAssertion(samlAssertion);

        return samlAssertion;
    }

    public static EncryptedDataType unmarshalSamlR2EncryptedAssertion(Document doc) throws Exception {
        EncryptedDataType encryptedType = (EncryptedDataType) unmarshal(doc, new String[]{SAMLR2Constants.SAML_ASSERTION_PKG,
                SAMLR2Constants.SAML_IDBUS_PKG});

        verifyEncryptedType(encryptedType);

        return encryptedType;
    }

    public static void verifyRequest(RequestAbstractType samlRequest) throws Exception {
        verifyID(samlRequest.getID());
    }

    public static void verifyAssertion(AssertionType samlAssertion) throws Exception {
        verifyID(samlAssertion.getID());
    }

    public static void verifyEncryptedType(EncryptedDataType encryptedData) throws Exception {
        verifyID(encryptedData.getId());
    }

    public static void verifyStatusResponse(StatusResponseType statusResponse) throws Exception {
        verifyID(statusResponse.getID());

        if (statusResponse instanceof ResponseType) {
            ResponseType assertionResponse = ((ResponseType) statusResponse);
            for (Object o : assertionResponse.getAssertionOrEncryptedAssertion()) {
                if (o instanceof AssertionType) {
                    verifyAssertion((AssertionType) o);
                } else if (o instanceof EncryptedDataType) {
                    verifyEncryptedType((EncryptedDataType) o);
                }
            }
        }
    }

    public static void verifySSORequest(SSORequestAbstractType ssoRequest) throws Exception {
        verifyID(ssoRequest.getID());
    }

    public static void verifySSOResponse(SSOResponseType ssoResponse) throws Exception {
        verifyID(ssoResponse.getID());
    }

    /**
     * Verifh that IDs do not have an XPath expression that the digital signature tool may try to resolve.
     *
     * @param ID
     * @throws Exception
     */
    public static void verifyID(String ID) throws Exception {

        for (String s : xpath) {
            if (ID.contains(s))
                throw new InvalidXMLException("Invalid ID " + ID + " [" + s + "]");

        }

    }
}
