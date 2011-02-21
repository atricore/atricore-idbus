package com.atricore.josso2.licensing._1_0.util;

import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.*;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;

/**
 * Author: Dejan Maric
 */
public class LicenseSigner {


    private static final Log logger = LogFactory.getLog(LicenseSigner.class);

    /**
     * JSR 105 Provider name.
     */
    public static final String JSR105_PROVIDER_PROPERTY = "jsr105Provider";

    /**
     * Default JSR 105 Provider FQCN.
     */
    public static final String DEFAULT_JSR105_PROVIDER_FQCN = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";

    /**
     * JSR 105 Provider.
     */
    private Provider provider;

    public void init() {
        try {
            // If a provider was already 'injected', use it.
            if (provider == null) {

                if (logger.isDebugEnabled())
                    logger.debug("Creating JSR 105 Provider : " + getProviderFQCN());

                provider = (Provider) Class.forName(getProviderFQCN()).newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error creating default provider: " + getProviderFQCN(), e);
        }
    }

    public LicenseType sign(LicenseType unsigned, LicenseKeyResolver keyResolver) throws LicenseSignatureException {
        try{
            Document doc = XmlUtils.marshalLicenseToDOM(unsigned);
            doc = sign(doc, unsigned.getID(), keyResolver);

            if (logger.isDebugEnabled())
                logger.debug("Unmarshalling LicenseType from DOM Tree");

            return XmlUtils.unmarshallLicense(doc);            
        }catch (Exception e){
            throw new LicenseSignatureException("Error signing license", e);
        }
    }

    public Document sign(Document doc, String id, LicenseKeyResolver keyResolver) throws LicenseSignatureException {
        try {
            // Create a DOM XMLSignatureFactory that will be used to generate the
            // enveloped signature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());

            if (logger.isDebugEnabled())
                logger.debug("Creating XML DOM Digital Signature (not signing yet!)");

            // Create a Reference to the enveloped document and
            // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
            // The URI must be the assertion ID
            Reference ref = fac.newReference
                    ("#" + id, fac.newDigestMethod(DigestMethod.SHA1, null),
                            Collections.singletonList
                                    (fac.newTransform
                                            (Transform.ENVELOPED, (TransformParameterSpec) null)),
                            null, null);

            // Use signature method based on key algorithm.
            String signatureMethod = SignatureMethod.DSA_SHA1;
            if (keyResolver.getPrivateKey().getAlgorithm().equals("RSA"))
                signatureMethod = SignatureMethod.RSA_SHA1;

            logger.debug("Using signature method " + signatureMethod);

            // Create the SignedInfo, with the X509 Certificate
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                            (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                    (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(signatureMethod, null),
                            Collections.singletonList(ref));

            // Create a KeyInfo and add the Certificate to it
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            X509Data kv = kif.newX509Data(Collections.singletonList(keyResolver.getCertificate()));
            //KeyValue kv = kif.newKeyValue(keyResolver.getCertificate().getPublicKey());

            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
            javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki);

            if (logger.isDebugEnabled())
                logger.debug("Signing...");

            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext
                    (keyResolver.getPrivateKey(), doc.getDocumentElement(), doc.getDocumentElement().getFirstChild());

            // Sign the assertion
            signature.sign(dsc);

            if (logger.isDebugEnabled())
                logger.debug("Signing... DONE!");

            return doc;


        } catch (NoSuchAlgorithmException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        } catch (MarshalException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        } catch (LicenseKeyResolverException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        }
    }

    public void validate(LicenseType signed, LicenseKeyResolver keyResolver) throws LicenseSignatureException {
        try {

            if (logger.isDebugEnabled())
                logger.debug("Marshalling LicenseType to DOM Tree");

            Document doc = XmlUtils.marshalLicenseToDOM(signed);

            // validate
            validate(doc, keyResolver);

        } catch (Exception e) {
            throw new LicenseSignatureException("Error validating signature for LicenseType", e);
        }
    }

    protected void validate(Document doc, LicenseKeyResolver keyResolver) throws LicenseSignatureException {
        try {
            // Find Signature element
            NodeList nl =
                    doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                throw new LicenseSignatureException("Cannot find Signature element");
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", provider);

            // Create a DOMValidateContext and specify a KeyValue KeySelector
            // and document context         
            // Validate all Signature elements
            for (int k = 0; k < nl.getLength(); k++) {

                DOMValidateContext valContext = new DOMValidateContext
                        (new RawX509KeySelector(), nl.item(k));

                // unmarshal the XMLSignature
                XMLSignature signature = fac.unmarshalXMLSignature(valContext);

                // Validate the XMLSignature (generated above)
                boolean coreValidity = signature.validate(valContext);

                // Check core validation status
                if (!coreValidity) {
                    logger.error("Signature failed core validation");
                    boolean sv = signature.getSignatureValue().validate(valContext);
                    logger.debug("signature validation status: " + sv);
                    // check the validation status of each Reference
                    Iterator i = signature.getSignedInfo().getReferences().iterator();
                    boolean refValid = true;
                    for (int j = 0; i.hasNext(); j++) {
                        boolean b = ((Reference) i.next()).validate(valContext);
                        if (!b) refValid = b;
                        logger.debug("ref[" + j + "] validity status: " + b);
                    }
                    logger.error("Signature failed core validation" + (refValid ? " but passed all Reference validations" : " and some/all Reference validation"));
                    throw new LicenseSignatureException("Signature failed core validation" + (refValid ? " but passed all Reference validations" : " and some/all Reference validation"));
                }

                logger.debug("Signature passed core validation");

                Key key = signature.getKeySelectorResult().getKey();
                boolean certValidity = validateCertificate(keyResolver, key);
                if (!certValidity) {
                    throw new LicenseSignatureException("Signature failed Certificate validation");
                }

                logger.debug("Signature passed Certificate validation");
            }
        } catch (MarshalException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected boolean validateCertificate(LicenseKeyResolver keyResolver, Key publicKey) throws LicenseSignatureException {
        try {
            PublicKey x509PublicKey = keyResolver.getCertificate().getPublicKey();
            byte[] x509PublicKeyEncoded = x509PublicKey.getEncoded();
            byte[] publicKeyEncoded = publicKey.getEncoded();
            return java.util.Arrays.equals(x509PublicKeyEncoded, publicKeyEncoded);
        } catch (LicenseKeyResolverException e) {
            throw new LicenseSignatureException(e.getMessage(), e);
        }
    }

    /**
     * KeySelector which would retrieve the X509Certificate out of the
     * KeyInfo element and return the public key.
     * NOTE: If there is an X509CRL in the KeyInfo element, then revoked
     * certificate will be ignored.
     */
    private static class RawX509KeySelector extends KeySelector {

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

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public static String getProviderFQCN() {
        return System.getProperty(JSR105_PROVIDER_PROPERTY, DEFAULT_JSR105_PROVIDER_FQCN);
    }    
}
