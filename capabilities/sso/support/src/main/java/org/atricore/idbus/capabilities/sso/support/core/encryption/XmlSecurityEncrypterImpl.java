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

package org.atricore.idbus.capabilities.sso.support.core.encryption;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.assertion.ObjectFactory;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.EncryptionProperty;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.EncryptionConstants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolverException;
import org.w3._2001._04.xmlenc_.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

/**
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 2, 2009
 * @org.apache.xbean.XBean element="samlr2-encrypter"
 */
public class XmlSecurityEncrypterImpl implements SamlR2Encrypter {
    private static final Log logger = LogFactory.getLog(XmlSecurityEncrypterImpl.class);

    private String jceProviderName;
    private String symmetricKeyAlgorithmURI;
    private String kekAlgorithmURI;
    private SSOKeyResolver keyResolver;

    public void setJceProviderName(String jceProviderName) {
        this.jceProviderName = jceProviderName;
    }

    public String getSymmetricKeyAlgorithmURI() {
        return symmetricKeyAlgorithmURI;
    }

    /*
     * @org.apache.xbean.Property alias="symmetric-key-algorithm"
     */
    public void setSymmetricKeyAlgorithmURI(String symmetricKeyAlgorithmURI) {
        this.symmetricKeyAlgorithmURI = symmetricKeyAlgorithmURI;
    }

    public String getKekAlgorithmURI() {
        return kekAlgorithmURI;
    }

    public void setKekAlgorithmURI(String kekAlgorithmURI) {
        this.kekAlgorithmURI = kekAlgorithmURI;
    }

    public SSOKeyResolver getKeyResolver() {
        return keyResolver;
    }

    /**
     * @org.apache.xbean.Property alias="key-resolver" nestedType="org.atricore.idbus.capabilities.sso.SSOKeyResolver"
     */
    public void setKeyResolver(SSOKeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }

    static {
        org.apache.xml.security.Init.init();
    }

    public EncryptedElementType encrypt(AssertionType assertion) throws SamlR2EncrypterException {
        if (keyResolver != null)
            return encrypt(assertion, keyResolver);

        throw new SamlR2EncrypterException("No SSOKeyResolver found in configuration");
    }

    public EncryptedElementType encrypt(AssertionType assertion, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        // Marshall the Assertion object as a DOM tree:
        if (logger.isDebugEnabled())
            logger.debug("Marshalling SAML 2 Assertion to DOM Tree [" + assertion.getID() + "]");

        // Create DOM Document with Assertion
        org.w3c.dom.Document doc = createNewDocument();
        ObjectFactory of = new ObjectFactory();
        JAXBElement<AssertionType> jaxbAssertion =
                new JAXBElement<AssertionType>(
                        new QName(SAMLR2Constants.SAML_ASSERTION_NS, "Assertion"),
                        AssertionType.class,
                        assertion);

        marshal(jaxbAssertion, doc);

        if (logger.isDebugEnabled())
            logger.debug("Obtaining encryption Key for assertion " + assertion.getID());

        // TODO : CACHE Keys for SPs for some time to improve performance.
        Key encryptionKey = generateDataEncryptionKey();

        if (logger.isDebugEnabled())
            logger.debug("Encrypt assertion " + assertion.getID());

        EncryptedDataType encData = encryptElement(doc, encryptionKey, false);

        if (logger.isDebugEnabled())
            logger.debug("Encrypt Key " + assertion.getID());

        // Build encrypted element type
        EncryptedKeyType encKey = encryptKey(doc, encryptionKey, keyResolver);

        EncryptedElementType eet = of.createEncryptedElementType();
        eet.setEncryptedData(encData);
        eet.getEncryptedKey().add(encKey);

        return eet;
    }

    public AssertionType decryptAssertion(EncryptedElementType encryptedAssertion) throws SamlR2EncrypterException {
        if (keyResolver != null)
            return decryptAssertion(encryptedAssertion, keyResolver);

        throw new SamlR2EncrypterException("No SSOKeyResolver found in configuration");
    }

    public AssertionType decryptAssertion(EncryptedElementType encryptedAssertion, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        org.w3c.dom.Document doc = createNewDocument();

        JAXBElement<EncryptedElementType> jaxbAssertion =
                new JAXBElement<EncryptedElementType>(
                        new QName(SAMLR2Constants.SAML_ASSERTION_NS, "EncryptedAssertion"),
                        EncryptedElementType.class,
                        encryptedAssertion);

        marshal(jaxbAssertion, doc);

        Node assertionelement = decryptAssertion(doc, keyResolver);

        JAXBElement<AssertionType> assertion = (JAXBElement<AssertionType>) unmarshal(assertionelement);

        return assertion.getValue();
    }

    public EncryptedElementType encrypt(RequestAbstractType request) throws SamlR2EncrypterException {
        if (keyResolver != null)
            return encrypt(request, keyResolver);

        throw new SamlR2EncrypterException("No SSOKeyResolver found in configuration");
    }

    public EncryptedElementType encrypt(RequestAbstractType request, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public EncryptedElementType encrypt(StatusResponseType response) throws SamlR2EncrypterException {
        if (keyResolver != null)
            return encrypt(response, keyResolver);

        throw new SamlR2EncrypterException("No SSOKeyResolver found in configuration");
    }

    public EncryptedElementType encrypt(StatusResponseType response, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    protected EncryptedDataType encryptElement(Document ownerDocument, Key encryptionKey,
                                               boolean encryptContentMode) throws SamlR2EncrypterException {
        if (ownerDocument == null) {
            logger.error("Document for encryption is null");
            throw new SamlR2EncrypterException("Document is null");
        }
        if (encryptionKey == null) {
            logger.error("Encryption key for key encryption is null");
            throw new SamlR2EncrypterException("Encryption key is null");
        }

        XMLCipher xmlCipher;
        try {
            if (jceProviderName != null) {
                xmlCipher = XMLCipher.getProviderInstance(getSymmetricKeyAlgorithmURI(), jceProviderName);
            } else {
                xmlCipher = XMLCipher.getInstance(getSymmetricKeyAlgorithmURI());
            }
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, encryptionKey);
        } catch (XMLEncryptionException e) {
            logger.error("Error initializing cipher instance on key encryption", e);
            throw new SamlR2EncrypterException("Error initializing cipher instance on key encryption", e);
        }

        try {
            xmlCipher.doFinal(ownerDocument, ownerDocument.getDocumentElement(), encryptContentMode);
        } catch (Exception e) {
            logger.error("Error encrypting Document", e);
            throw new SamlR2EncrypterException("Error encrypting Docuemnt", e);
        }

        JAXBElement<EncryptedDataType> encElement = (JAXBElement<EncryptedDataType>) unmarshal(ownerDocument.getDocumentElement());
        return encElement.getValue();
    }

    protected EncryptedKeyType encryptKey(Document doc, Key symmetricKey, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        EncryptedKey encKey;
        try {
            XMLCipher keyCipher = XMLCipher.getInstance(getKekAlgorithmURI());
            keyCipher.init(XMLCipher.WRAP_MODE, keyResolver.getCertificate().getPublicKey());
            encKey = keyCipher.encryptKey(doc, symmetricKey);
        } catch (XMLEncryptionException e) {
            throw new SamlR2EncrypterException("Encryption Error generating encrypted key", e);
        } catch (SSOKeyResolverException e) {
            throw new SamlR2EncrypterException("Encryption Error retrieving private key", e);
        }


        EncryptedKeyType ekt = new EncryptedKeyType();
        ekt.setCarriedKeyName(encKey.getCarriedName());
        CipherDataType cdt = new CipherDataType();
        try {
            cdt.setCipherValue(Base64.decode(encKey.getCipherData().getCipherValue().getValue()));
        } catch (Base64DecodingException e) {
            throw new SamlR2EncrypterException("Error decoding key cipher value", e);
        }
        ekt.setCipherData(cdt);
        ekt.setEncoding(encKey.getEncoding());
        EncryptionMethodType emt = new EncryptionMethodType();
        emt.setAlgorithm(encKey.getEncryptionMethod().getAlgorithm());
        ekt.setEncryptionMethod(emt);
        if (encKey.getEncryptionProperties() != null) {
            EncryptionPropertiesType ept = new EncryptionPropertiesType();
            ept.setId(encKey.getEncryptionProperties().getId());
            for (Iterator iter = encKey.getEncryptionProperties().getEncryptionProperties(); iter.hasNext(); ) {
                EncryptionProperty prop = (EncryptionProperty) iter.next();
                EncryptionPropertyType ep = new EncryptionPropertyType();
                ep.setId(prop.getId());
                ep.setTarget(prop.getTarget());
                ept.getEncryptionProperty().add(ep);
            }
            ekt.setEncryptionProperties(ept);
        }
        ekt.setId(encKey.getId());
//        KeyInfoType kit = new KeyInfoType();
//        kit.setId( encKey.getKeyInfo().getRegion() );
//        kit.getContent().addAll( encKey.getKeyInfo(). )
//        ekt.setKeyInfo(  );
        ekt.setMimeType(encKey.getMimeType());
        ekt.setRecipient(encKey.getRecipient());
//        ekt.setReferenceList(  );
        ekt.setType(encKey.getType());
        return ekt;
    }

    protected Node decryptAssertion(Document document, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        try {
            org.w3c.dom.Element encryptedDataElement =
                    (org.w3c.dom.Element) document.getElementsByTagNameNS(
                            EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);


            Key kek = loadKeyEncryptionKey(document, keyResolver);

            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, kek);

            Document decDoc = xmlCipher.doFinal(document, encryptedDataElement);

            Node assertionNode = decDoc.getElementsByTagNameNS(
                    "urn:oasis:names:tc:SAML:2.0:assertion",
                    "Assertion").item(0);

            if (assertionNode == null)
                throw new SamlR2EncrypterException("No Assertion Node found in decrypted Document");

            return assertionNode;

        } catch (Exception e) {
            throw new SamlR2EncrypterException("Error decrypting Assertion data", e);
        }
    }

    protected Document createNewDocument() throws SamlR2EncrypterException {
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException e) {
            throw new SamlR2EncrypterException("Error parsing new XMLDocument", e);
        }
    }

    protected void marshal(JAXBElement elem, Document doc) throws SamlR2EncrypterException {
        try {
            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_ASSERTION_PKG);
            Marshaller m = context.createMarshaller();
            m.marshal(elem, doc);
        } catch (JAXBException e) {
            throw new SamlR2EncrypterException("Error parsing new XMLDocument", e);
        }
    }

    protected Object unmarshal(Node node) throws SamlR2EncrypterException {
        try {
            JAXBContext context = JAXBContext.newInstance(SAMLR2Constants.SAML_ASSERTION_PKG);
            Unmarshaller u = context.createUnmarshaller();
            return u.unmarshal(node);
        } catch (JAXBException e) {
            throw new SamlR2EncrypterException("Error unmarshalling node " + node.getNodeName(), e);
        }
    }

    protected SecretKey generateDataEncryptionKey() {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Using algorithm [" + getSymmetricKeyAlgorithmURI() + "]");

            String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(getSymmetricKeyAlgorithmURI());
            int keyLength = JCEMapper.getKeyLengthFromURI(getSymmetricKeyAlgorithmURI());

            if (logger.isTraceEnabled())
                logger.trace("Generating key [" + jceAlgorithmName + "] length:" + keyLength);

            KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
            keyGenerator.init(keyLength);
            SecretKey key = keyGenerator.generateKey();

            if (logger.isDebugEnabled())
                logger.debug("Generated key [" + jceAlgorithmName + "] length:" + keyLength);

            return key;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected Key loadKeyEncryptionKey(Document document, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        try {
            org.w3c.dom.Element encryptedKeyElement =
                    (org.w3c.dom.Element) document.getElementsByTagNameNS(
                            EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTEDKEY).item(0);
            assert encryptedKeyElement != null : "No " + EncryptionConstants._TAG_ENCRYPTEDKEY + " Element found in Document";

            XMLCipher keyCipher = XMLCipher.getInstance();
            keyCipher.init(XMLCipher.UNWRAP_MODE, keyResolver.getPrivateKey());
            EncryptedKey ek = keyCipher.loadEncryptedKey(document, encryptedKeyElement);
            assert ek != null : "No encryptedKey found";

            org.w3c.dom.Element encryptedDataElement =
                    (org.w3c.dom.Element) document.getElementsByTagNameNS(
                            EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);
            assert encryptedDataElement != null : "No " + EncryptionConstants._TAG_ENCRYPTEDDATA + " Element found in Document";

            org.w3c.dom.Element encryptionMethodElem =
                    (org.w3c.dom.Element) encryptedDataElement.getElementsByTagNameNS(
                            EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTIONMETHOD).item(0);
            assert encryptionMethodElem != null : "No " + EncryptionConstants._TAG_ENCRYPTIONMETHOD + " Element found in Document";

            String algoritmUri = encryptionMethodElem.getAttribute(EncryptionConstants._ATT_ALGORITHM);
            if (logger.isDebugEnabled())
                logger.debug("Encrypted Key algorithm: " + algoritmUri);

            return keyCipher.decryptKey(ek, algoritmUri);
        } catch (Exception e) {
            throw new SamlR2EncrypterException("Error loading or decrypting kek", e);
        }
    }

    public NameIDType decryptNameID(EncryptedElementType encryptedNameID) throws SamlR2EncrypterException {
        if (keyResolver != null)
            return decryptNameID(encryptedNameID, keyResolver);

        throw new SamlR2EncrypterException("No SSOKeyResolver found in configuration");
    }

    public NameIDType decryptNameID(EncryptedElementType encryptedNameID, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        org.w3c.dom.Document doc = createNewDocument();

        JAXBElement<EncryptedElementType> jaxbNameID =
                new JAXBElement<EncryptedElementType>(
                        new QName(SAMLR2Constants.SAML_ASSERTION_NS, "EncryptedID"),
                        EncryptedElementType.class,
                        encryptedNameID);

        marshal(jaxbNameID, doc);

        Node nameIDElement = decryptNameID(doc, keyResolver);

        JAXBElement<NameIDType> nameID = (JAXBElement<NameIDType>) unmarshal(nameIDElement);

        return nameID.getValue();
    }

    protected Node decryptNameID(Document document, SSOKeyResolver keyResolver) throws SamlR2EncrypterException {
        try {
            org.w3c.dom.Element encryptedDataElement =
                    (org.w3c.dom.Element) document.getElementsByTagNameNS(
                            EncryptionConstants.EncryptionSpecNS,
                            EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);


            Key kek = loadKeyEncryptionKey(document, keyResolver);

            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, kek);

            Document decDoc = xmlCipher.doFinal(document, encryptedDataElement);

            Node nameIDNode = decDoc.getElementsByTagNameNS(
                    "urn:oasis:names:tc:SAML:2.0:assertion",
                    "NameID").item(0);

            if (nameIDNode == null)
                throw new SamlR2EncrypterException("No NameID Node found in decrypted Document");

            return nameIDNode;

        } catch (Exception e) {
            throw new SamlR2EncrypterException("Error decrypting NameID data", e);
        }
    }

}