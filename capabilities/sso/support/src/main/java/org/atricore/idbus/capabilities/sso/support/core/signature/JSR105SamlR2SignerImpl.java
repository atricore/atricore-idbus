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

package org.atricore.idbus.capabilities.sso.support.core.signature;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.KeyDescriptorType;
import oasis.names.tc.saml._2_0.metadata.KeyTypes;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.SAMLR11Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolverException;
import org.atricore.idbus.capabilities.sso.support.core.util.NamespaceFilterXMLStreamWriter;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.jcp.xml.dsig.internal.dom.DOMSignatureMethod;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.security.x509.KeyUsageExtension;

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
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.acl.NotOwnerException;
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

    private SSOKeyResolver keyResolver;

    // Validate certificate expiration, CA, etc.
    private boolean validateCertificate = true;

    public Provider getProvider() {
        return provider;
    }

    private String signMethodSpec;

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public SSOKeyResolver getKeyResolver() {
        return keyResolver;
    }

    public boolean isValidateCertificate() {
        return validateCertificate;
    }

    public void setValidateCertificate(boolean validateCertificate) {
        this.validateCertificate = validateCertificate;
    }

    public String getSignMethodSpec() {
        return signMethodSpec;
    }

    public void setSignMethodSpec(String signMethodSpec) {
        this.signMethodSpec = signMethodSpec;
    }

    /**
     * @org.apache.xbean.Property alias="key-resolver"
     */
    public void setKeyResolver(SSOKeyResolver keyResolver) {
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

    public AssertionType sign(AssertionType assertion, String digest) throws SamlR2SignatureException {

        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Assertion to DOM Tree [" + assertion.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2AsDom(assertion,
                    SAMLR2Constants.SAML_ASSERTION_NS,
                    "Assertion",
                    new String[]{SAMLR2Constants.SAML_ASSERTION_PKG});

            doc = sign(doc, assertion.getID(), digest);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Assertion from DOM Tree [" + assertion.getID() + "]");

            return (AssertionType) XmlUtils.unmarshal(doc, new String[]{SAMLR2Constants.SAML_ASSERTION_PKG});

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Assertion " + assertion.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Assertion " + assertion.getID(), e);
        } catch (Exception e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Assertion " + assertion.getID(), e);
        }

    }


    public void validate(RoleDescriptorType md, AssertionType assertion) throws SamlR2SignatureException, SamlR2SignatureValidationException {

        if (logger.isDebugEnabled())
            logger.debug("Marshalling SAMLR2 Assertion to DOM Tree [" + assertion.getID() + "]");


        try {
            Document doc = XmlUtils.marshalSamlR2AssertionAsDom(assertion);
            validate(md, doc);
        } catch (Exception e) {
            throw new SamlR2SignatureValidationException(e);
        }

    }

    public RequestAbstractType sign(RequestAbstractType request, String digest) throws SamlR2SignatureException {
        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Request to DOM Tree [" + request.getID() + "]");

            org.w3c.dom.Document doc = XmlUtils.marshalSamlR2RequestAsDom(request);

            doc = sign(doc, request.getID(), digest);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Status Response from DOM Tree [" + request.getID() + "]");

            // Unmarshall the assertion

            return XmlUtils.unmarshalSamlR2Request(doc);

        } catch (JAXBException e) {
            throw new SamlR2SignatureException("JAXB Error signing SAMLR2 Response " + request.getID(), e);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + request.getID(), e);
        } catch (Exception e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + request.getID(), e);
        }
    }

    public StatusResponseType sign(StatusResponseType response, String element, String digest) throws SamlR2SignatureException {
        try {

            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Response to DOM Tree [" + response.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2AsDom(response,
                    SAMLR2Constants.SAML_PROTOCOL_NS,
                    element,
                    new String[]{
                            SAMLR2Constants.SAML_PROTOCOL_PKG,
                            SAMLR2Constants.SAML_ASSERTION_PKG});

            doc = sign(doc, response.getID(), digest);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Response from DOM Tree [" + response.getID() + "]");

            // Unmarshall the response
            return XmlUtils.unmarshalSamlR2Response(doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Response " + response.getID(), e);
        }
    }

    public String signQueryString(String queryString, String digest) throws SamlR2SignatureException {

        try {

            if (queryString == null || queryString.length() == 0) {
                logger.error("SAML 2.0 Qery string null");
                throw new SamlR2SignatureException("SAML 2.0 Qery string null");
            }

            if (logger.isDebugEnabled())
                logger.debug("Received SAML 2.0 Query string [" + queryString + "] for signing");

            PrivateKey privateKey = (PrivateKey) this.getKeyResolver().getPrivateKey();

            String keyAlgorithm = privateKey.getAlgorithm();

            Signature signature = null;
            String algURI = null;

            try {
                SignMethod sm = SignMethod.fromValues(digest, keyAlgorithm);
                signature = Signature.getInstance(sm.getName());
                algURI = SignatureMethod.RSA_SHA1;

            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
                throw new SamlR2SignatureException("SAML 2.0 Signature does not support provided key's algorithm " + keyAlgorithm, e);
            }

            if (queryString.charAt(queryString.length() - 1) != '&') {
                queryString = queryString + "&";
            }

            queryString += "SigAlg=" +
                    URLEncoder.encode(algURI, "UTF-8");

            if (logger.isTraceEnabled())
                logger.trace("Signing SAML 2.0 Query string [" + queryString + "]");


            signature.initSign(privateKey);
            signature.update(queryString.getBytes());
            byte[] sigBytes = null;
            sigBytes = signature.sign();
            if (sigBytes == null || sigBytes.length == 0) {
                logger.error("Cannot generate signed query string, Signature created 'null' value.");
                throw new SamlR2SignatureException("Cannot generate signed query string, Signature created 'null' value.");
            }

            Base64 encoder = new Base64();
            String encodedSig = new String(encoder.encode(sigBytes), "UTF-8");
            queryString +=
                    "&Signature=" +
                            URLEncoder.encode(encodedSig, "UTF-8");

            if (logger.isTraceEnabled())
                logger.trace("Signed SAML 2.0 Query string [" + queryString + "]");

            return queryString;
        } catch (Exception e) {
            throw new SamlR2SignatureException("Error generating SAML 2.0 Query string signature " + e.getMessage(), e);
        }
    }

    public void validate(RoleDescriptorType md, StatusResponseType response, String element) throws SamlR2SignatureException {

        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Response to DOM Tree [" + response.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2ResponseAsDom(response, element);

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 response" + response.getID(), e);
        }
    }

    public void validate(RoleDescriptorType md, AuthnRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Status Authn Request to DOM Tree [" + request.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2RequestAsDom(request);

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 authn request " + request.getID(), e);
        }
    }

    public void validateQueryString(RoleDescriptorType md, String queryString) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {

            X509Certificate cert = getX509Certificate(md);

            if(cert == null) {
                logger.error("No Certificate found in Metadata " + md.getID());
                throw new SamlR2SignatureException("No Certificate found in Metadata " + md.getID());
            }

            if (queryString == null || queryString.length() == 0) {
                logger.error("SAML 2.0 Qery string null");
                throw new SamlR2SignatureException("SAML 2.0 Qery string null");
            }

            if (logger.isTraceEnabled())
                logger.trace("SAML 2.0 Query string to validate ["+queryString+"]");

            StringTokenizer st = new StringTokenizer(queryString, "&");
            String samlParam;

            String samlRequest = null;
            String samlResponse = null;
            String relayState = null;
            String sigAlg = null;
            String encSig = null;

            while (st.hasMoreTokens()) {
                samlParam = st.nextToken();
                if (samlParam.startsWith("SAMLRequest")) {
                    samlRequest = samlParam;
                } else if (samlParam.startsWith("SAMLResponse")) {
                    samlResponse = samlParam;
                } else if (samlParam.startsWith("RelayState")) {
                    relayState = samlParam;
                } else if (samlParam.startsWith("SigAlg")) {
                    sigAlg = samlParam;
                } else if (samlParam.startsWith("Signature")) {
                    encSig = samlParam;
                } else {
                    // Ignore this token ...
                    logger.warn("Non-SAML 2.0 parameter ignored " + samlParam);
                }
            }
            if ((samlRequest == null || samlRequest.equals("")) &&
                (samlResponse == null || samlResponse.equals("")))
                throw new SamlR2SignatureValidationException("SAML 2.0 Query string MUST contain either 'SAMLRequest' or 'SAMLResponse' parameter");

            if (sigAlg == null || sigAlg.equals(""))
                throw new SamlR2SignatureValidationException("SAML 2.0 Query string MUST contain a 'SigAlg' parameter");

            if (encSig == null || encSig.equals("")) {
                throw new SamlR2SignatureValidationException("SAML 2.0 Query string MUST contain a 'Signature' parameter");
            }

            // Re-order paramters just in case they were mixed-up while getting here.
            String newQueryString = null;
            if (samlRequest != null) {
                newQueryString = samlRequest;
            } else {
                newQueryString = samlResponse;
            }
            if (relayState != null) {
                newQueryString += "&" + relayState;
            }
            newQueryString += "&" + sigAlg;
            if (logger.isDebugEnabled())
                logger.debug("SAML 2.0 Query string signature validation for (re-arranged) [" + newQueryString + "]");

            int sigAlgValueIndex = sigAlg.indexOf('=');

            // Get Signature Algorithm
            String sigAlgValue =
                    sigAlg.substring(sigAlgValueIndex + 1);
            if (sigAlgValue == null || sigAlgValue.equals("")) {
                throw new SamlR2SignatureValidationException("SAML 2.0 Query string MUST contain a 'SigAlg' parameter value");
            }
            sigAlgValue = URLDecoder.decode(sigAlgValue, "UTF-8");
            if (logger.isTraceEnabled())
                logger.trace("SigAlg=" + sigAlgValue);

            // Get Signature value
            int encSigValueIndex = encSig.indexOf('=');
            String signatureEnc = encSig.substring(encSigValueIndex + 1);
            if (signatureEnc == null || signatureEnc.equals("")) {
                throw new SamlR2SignatureValidationException("SAML 2.0 Query string MUST contain a 'Signature' parameter value");
            }
            signatureEnc = URLDecoder.decode(signatureEnc, "UTF-8");
            if (logger.isTraceEnabled())
                logger.trace("Signature=" + signatureEnc);

            // base-64 decode the signature value
            byte[] signatureBin = null;
            Base64 decoder = new Base64();
            signatureBin = decoder.decode(signatureEnc.getBytes());

            // get Signature instance based on algorithm
            // TODO : Support SHA-256
            Signature signature = null;


            try {
                SignMethod sm = SignMethod.fromSpec(sigAlgValue);
                signature = Signature.getInstance(sm.getName());
            } catch (IllegalArgumentException e) {
                logger.error (e.getMessage(), e);
                throw new SamlR2SignatureException("SAML 2.0 Siganture does not support algorithm " + sigAlgValue);
            }

            // now verify signature
            signature.initVerify(cert);
            signature.update(newQueryString.getBytes());
            if (!signature.verify(signatureBin)) {
                // TODO : Get information about the error ?!
                throw new SamlR2SignatureValidationException("Invalid digital signature");
            }

            if (!validateCertificate(md, null)) {
                throw new SamlR2SignatureValidationException("Certificate is not valid, check logs for details");
            }

        } catch (Exception e) {
            logger.error("Cannot verify digital SAML 2.0 Query string signature " + e.getMessage(), e);
            throw new SamlR2SignatureException("Cannot verify digital SAML 2.0 Query string signature " + e.getMessage(), e);
        }
    }

    public void validateQueryString(RoleDescriptorType md, String msg, String relayState, String sigAlg, String signature, boolean isResponse) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {

            if (sigAlg == null)
                throw new SamlR2SignatureException("Cannot verify digital SAML 2.0 Query string signature: No signature algorithm");

            if (signature == null)
                throw new SamlR2SignatureException("Cannot verify digital SAML 2.0 Query string signature: No signature value");

            String queryStr = ( isResponse ? "SAMLResponse=" : "SAMLRequest=" ) +
                URLEncoder.encode(msg, "UTF-8") + "&" +
                (relayState != null && !"".equals(relayState) ? "RelayState=" + relayState + "&" : "") +
                "SigAlg="  + URLEncoder.encode(sigAlg, "UTF-8") + "&" +
                "Signature=" + URLEncoder.encode(signature, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            logger.error("Cannot verify digital SAML 2.0 Query string signature " + e.getMessage(), e);
            throw new SamlR2SignatureException("Cannot verify digital SAML 2.0 Query string signature " + e.getMessage(), e);
        }

    }

    public void validate(RoleDescriptorType md, LogoutRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException {
        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 Logout Request to DOM Tree [" + request.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2RequestAsDom(request);

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("Error verifying signature for SAMLR2 response" + request.getID(), e);
        }
    }

    public void validate(RoleDescriptorType md, ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException {

        try {
            // Marshall the ManageNameID object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 ManageNameID to DOM Tree [" + manageNameIDRequest.getID() + "]");

            Document doc = XmlUtils.marshalSamlR2RequestAsDom(manageNameIDRequest);

            validate(md, doc);

        } catch (Exception e) {
            throw new SamlR2SignatureException("XML Parser Error verifying SAMLR2 Response signature " + manageNameIDRequest.getID(), e);
        }
    }

    public ManageNameIDRequestType sign(ManageNameIDRequestType manageNameIDRequest, String digest) throws SamlR2SignatureException {

        try {
            // Marshall the Assertion object as a DOM tree:
            if (logger.isDebugEnabled())
                logger.debug("Marshalling SAMLR2 ManageNameIDRequestType to DOM Tree [" + manageNameIDRequest.getID() + "]");

            org.w3c.dom.Document doc = XmlUtils.marshalSamlR2RequestAsDom(manageNameIDRequest);

            doc = sign(doc, manageNameIDRequest.getID(), digest);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR2 Assertion from DOM Tree [" + manageNameIDRequest.getID() + "]");

            return (ManageNameIDRequestType) XmlUtils.unmarshal(doc, new String[]{SAMLR2Constants.SAML_PROTOCOL_NS});

        } catch (Exception e) {
            throw new SamlR2SignatureException("XML Parser Error signing SAMLR2 Assertion " + manageNameIDRequest.getID(), e);
        }

    }

    // SAML 1.1

    public oasis.names.tc.saml._1_0.protocol.ResponseType sign(oasis.names.tc.saml._1_0.protocol.ResponseType response, String digest) throws SamlR2SignatureException {
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

            Class<oasis.names.tc.saml._1_0.protocol.ResponseType> clazz =
                    (Class<oasis.names.tc.saml._1_0.protocol.ResponseType>) response.getClass();

            // Remove the 'Type' suffix from the xml type name and use it as XML element!
            XmlType t = clazz.getAnnotation(XmlType.class);
            String element = t.name().substring(0, t.name().length() - 4);

            JAXBElement<oasis.names.tc.saml._1_0.protocol.ResponseType> jaxbResponse =
                    new JAXBElement<oasis.names.tc.saml._1_0.protocol.ResponseType>(
                            new QName(SAMLR11Constants.SAML_PROTOCOL_NS, element),
                            clazz,
                            response);

            // remove prefixes from signature elements of embedded signed assertion so that signature validation -
            // which removes those prefixes - doesn't fail
            StringWriter swrsp = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swrsp);
            // TODO : Use XML Utils!!!!
            m.marshal(jaxbResponse, sw);
            sw.flush();

            Document doc =
                    dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swrsp.toString().getBytes()));

            doc = sign(doc, response.getResponseID(), digest);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling SAMLR11 Response from DOM Tree [" + response.getResponseID() + "]");

            // Unmarshall the assertion
            Unmarshaller u = context.createUnmarshaller();
            jaxbResponse = (JAXBElement<oasis.names.tc.saml._1_0.protocol.ResponseType>) u.unmarshal(doc);

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

    public void validateDom(RoleDescriptorType md, String domStr) throws SamlR2SignatureException {
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

    public void validateDom(RoleDescriptorType md, Document doc, String elementId) throws SamlR2SignatureException {

        NodeList nodes = evaluateXPath(doc, "//*[@ID='"+elementId+"']");
        if (nodes.getLength() > 1)
            throw new SamlR2SignatureException("Duplicate ID ["+elementId+"] in document ");

        if (nodes.getLength() < 1)
            throw new SamlR2SignatureException("Invalid element ID " + elementId);

        validate(md, doc, nodes.item(0));

    }


    public void validateDom(RoleDescriptorType md, String domStr, String elementId) throws SamlR2SignatureException {
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(domStr.getBytes()));

            validateDom(md, doc, elementId);
        } catch (ParserConfigurationException e) {
            throw new SamlR2SignatureException(e);
        } catch (SAXException e) {
            throw new SamlR2SignatureException(e);
        } catch (IOException e) {
            throw new SamlR2SignatureException(e);
        }
    }

    public void validate(RoleDescriptorType md, Document doc, Node root) throws SamlR2SignatureException {
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
                        (new RawX509KeySelector(getX509Certificate(md)), signatureNodes.item(k));

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
                logger.error("No Signature element refers to signed element (possible signature wrapping attack)");
                throw new SamlR2SignatureValidationException("No Signature element refers to signed element");
            }

        } catch (MarshalException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
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

        Node root = doc.getDocumentElement();
        validate(md, doc, root);

    }

    protected byte[] getBinCertificate(RoleDescriptorType md) {
        byte[] x509CertificateBin = null;

        if (md.getKeyDescriptor() != null && md.getKeyDescriptor().size() > 0) {

            for (KeyDescriptorType keyMd : md.getKeyDescriptor()) {

                if (!keyMd.getUse().equals(KeyTypes.SIGNING))
                    continue;

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
            logger.trace("MD Sign Certificate: " + Arrays.toString(x509CertificateBin));
        }

        return x509CertificateBin;
    }

    protected X509Certificate getX509Certificate(RoleDescriptorType md) {

        byte[] x509CertificateBin = getBinCertificate(md);
        if (x509CertificateBin == null)
            return null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate x509Cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(x509CertificateBin));

            return x509Cert;

        } catch (CertificateException e) {
            logger.error("Cannot get X509 Certificate " + e.getMessage(), e);

        }

        return null;

    }

    protected boolean validateCertificate(RoleDescriptorType md, Key publicKey) {

        X509Certificate x509Cert = getX509Certificate(md);

        if (x509Cert == null) {
            logger.error("No X509 Signing certificate found in SAML 2.0 Metadata Role " + md.getID());
            return false;
        }

        if (logger.isTraceEnabled()) {
            byte[] x509CertificateBin  =getBinCertificate(md);
            logger.trace("Configured Certificate: " + (publicKey != null ? Arrays.toString(publicKey.getEncoded()) : "<null>"));
            logger.trace("Used Certificate: " + Arrays.toString(x509CertificateBin));
        }

        PublicKey x509PublicKey = x509Cert.getPublicKey();

        byte[] x509PublicKeyEncoded = x509PublicKey.getEncoded();

        // Only compare with public key, if provided.
        if (publicKey != null) {
            byte[] publicKeyEncoded = publicKey.getEncoded();

            // Validate that the used certificate is the one configured for the entity
            if (!java.util.Arrays.equals(x509PublicKeyEncoded, publicKeyEncoded)) {
                logger.error("Certificate used for signing is not the one configured in SAML 2.0 Metadata Role " + md.getID());
                return false;
            }
        }

        // TODO : Make this verification configurable

        String verifyDatesStr = System.getProperty("org.atricore.idbus.capabiligies.sso.verifyCertificateDate", "true");
        boolean verifyDates = Boolean.parseBoolean(verifyDatesStr);

        Date now = new Date();
        if (x509Cert.getNotBefore() != null && x509Cert.getNotBefore().after(now)) {
            if (validateCertificate && verifyDates) {
                logger.error("Certificate should not be used before " + x509Cert.getNotBefore());
                return false;
            }
            logger.warn("Certificate should not be used before " + x509Cert.getNotBefore());
        }

        if (x509Cert.getNotAfter() != null && x509Cert.getNotAfter().before(now)) {
            if (validateCertificate && verifyDates) {
                logger.error("X509 Certificate has expired " + x509Cert.getNotAfter());
                return false;
            }
            logger.warn("X509 Certificate has expired " + x509Cert.getNotAfter());
        }

        Calendar aMonthFromNow = Calendar.getInstance();
        aMonthFromNow.add(Calendar.DAY_OF_MONTH, 30);

        // Just print-out that the certificate will expire soon.
        if (x509Cert.getNotAfter() != null && x509Cert.getNotAfter().before(aMonthFromNow.getTime()))
            logger.warn("X509 Certificate wil expired in less that 30 days for SAML 2.0 Metadata Role " + md.getID());

        return true;

    }


    /**
     * This will sign a SAMLR2 Identity artifact (assertion, request or response) represeted as a DOM tree
     * The signature will be inserted as the first child of the root element.
     *
     * @param doc
     * @param id
     * @return
     */
    protected Document sign(Document doc, String id, String digest) throws SamlR2SignatureException {
        try {

            Certificate cert = keyResolver.getCertificate();

            // Create a DOM XMLSignatureFactory that will be used to generate the
            // enveloped signature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", provider);

            if (logger.isDebugEnabled())
                logger.debug("Creating XML DOM Digital Signature (not signing yet!)");

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

            // Use signature method based on key algorithm, only RSA and DSA supported for now.
            Key pk = keyResolver.getPrivateKey();

            String signatureMethod = null;
            if (pk.getAlgorithm().equals("RSA")) {

                if (digest != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Using requested method " + digest + "withRSA");
                    signatureMethod = SignMethod.fromValues(digest, "RSA").getSpec();
                } else if (signMethodSpec != null) {
                    if (logger.isDebugEnabled())
                        logger.debug("Using configured method " + digest + "withRSA");
                    signatureMethod = SignMethod.fromSpec(signMethodSpec).getSpec();
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("Using default " + "SHA256withRSA");
                    signatureMethod = SignMethod.SHA256_WITH_RSA.getSpec();
                }

            } else if (pk.getAlgorithm().equals("DSA")) {
                signatureMethod = SignMethod.SHA1_WITH_DSA.getSpec();
                logger.warn("Using DSA/SHA 1 when signing! ");
            } else {
                // TODO : ECDSA ?
                logger.error("Unsupported Key algorithm : " + pk.getAlgorithm());
                throw new SamlR2SignatureException("Unsupported Key algorithm : " + pk.getAlgorithm());
            }

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
            javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki); // set ki to null to avoid KeyInfo tag

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
        } catch (SSOKeyResolverException e) {
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

        private X509Certificate defaultCert;


        public RawX509KeySelector(X509Certificate cert) {
            this.defaultCert = cert;
        }


        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                logger.debug("No key info, returning default");

                return new SimpleKeySelectorResult
                        (defaultCert.getPublicKey());

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




