package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.SamlR2ProviderConfig;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3._2001._04.xmlenc_.EncryptionMethodType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;

/**
 * @version $Id$
 */
public class SamlR2IdPTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(SamlR2IdPTransformer.class);

    private String baseSrcPath = "/org/atricore/idbus/examples/simplefederation/idau/";
    
    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityProvider;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        try {
            IdentityProvider provider = (IdentityProvider) event.getData();
            IdProjectModule module = event.getContext().getCurrentModule();
            String baseDestPath = (String) event.getContext().get("baseIdauDestPath");
            String providerBeanName = normalizeBeanName(provider.getName());

            // idp1-samlr2-metadata.xml
            IdProjectResource<EntityDescriptorType> idpMetadata =  new IdProjectResource<EntityDescriptorType>(idGen.generateId(),
                    baseDestPath + providerBeanName, providerBeanName, "saml2", generateIDPMetadata(provider));
            idpMetadata.setClassifier("jaxb");
            module.addResource(idpMetadata);
            
            // atricore-users.xml
            IdProjectResource<InputStream> atricoreUsers = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath, "atricore-users.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "atricore-users.xml"));
            atricoreUsers.setClassifier("copy");
            module.addResource(atricoreUsers);

            // atricore-credentials.xml
            IdProjectResource<InputStream> atricoreCredentials = new IdProjectResource<InputStream>(idGen.generateId(),
                    baseDestPath, "atricore-credentials.xml", "copy",
                    getClass().getResourceAsStream(baseSrcPath + "atricore-credentials.xml"));
            atricoreCredentials.setClassifier("copy");
            module.addResource(atricoreCredentials);
        } catch (Exception e) {
            throw new TransformException(e);
        }
    }

    private EntityDescriptorType generateIDPMetadata(IdentityProvider provider) throws TransformException {
        SamlR2ProviderConfig cfg = (SamlR2ProviderConfig) provider.getConfig();
        
        EntityDescriptorType entityDescriptor = new EntityDescriptorType();
        entityDescriptor.setID("id9uvH6lD7oa2zwey0JzQcpzJrKXY");
        entityDescriptor.setEntityID(resolveLocationUrl(provider) + "/SAML2/MD");

        // AttributeAuthorityDescriptor
        AuthnAuthorityDescriptorType attributeAuthorityDescriptor = new AuthnAuthorityDescriptorType();
        attributeAuthorityDescriptor.setID("idwGpHRH6meLr2hJ0Ko6fkMtSJ330");
        attributeAuthorityDescriptor.getProtocolSupportEnumeration().add("urn:oasis:names:tc:SAML:2.0:protocol");

        // authority signing key descriptor
        KeyDescriptorType authoritySigningKeyDescriptor = new KeyDescriptorType();
        authoritySigningKeyDescriptor.setUse(KeyTypes.SIGNING);
        KeyInfoType authoritySigningKeyInfo = new KeyInfoType();
        X509DataType authoritySigningX509Data = new X509DataType();
        String authoritySigningCertificate = ""; // TODO
        if (cfg != null && cfg.getSigner() != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(cfg.getSigner().getType()) ? "PKCS12" : "JKS");
                byte[] keystore = cfg.getSigner().getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), cfg.getSigner().getPassword().toCharArray());
                Certificate signerCertificate = ks.getCertificate(cfg.getSigner().getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, signerCertificate.getEncoded());
                authoritySigningCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        }
        JAXBElement jaxbAuthoritySigningX509Certificate = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate"),
                                    authoritySigningCertificate.getClass(), authoritySigningCertificate);
        authoritySigningX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jaxbAuthoritySigningX509Certificate);
        JAXBElement jaxbAuthoritySigningX509Data = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data"),
                                    authoritySigningX509Data.getClass(), authoritySigningX509Data);
        authoritySigningKeyInfo.getContent().add(jaxbAuthoritySigningX509Data);
        authoritySigningKeyDescriptor.setKeyInfo(authoritySigningKeyInfo);
        EncryptionMethodType authoritySigningEncryptionMethod = new EncryptionMethodType();
        authoritySigningEncryptionMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        authoritySigningKeyDescriptor.getEncryptionMethod().add(authoritySigningEncryptionMethod);
        attributeAuthorityDescriptor.getKeyDescriptor().add(authoritySigningKeyDescriptor);

        // authority encryption key descriptor
        KeyDescriptorType authorityEncryptionKeyDescriptor = new KeyDescriptorType();
        authorityEncryptionKeyDescriptor.setUse(KeyTypes.ENCRYPTION);
        KeyInfoType authorityEncryptionKeyInfo = new KeyInfoType();
        X509DataType authorityEncryptionX509Data = new X509DataType();
        String authorityEncryptionCertificate = ""; // TODO
        if (cfg != null && cfg.getEncrypter() != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(cfg.getEncrypter().getType()) ? "PKCS12" : "JKS");
                byte[] keystore = cfg.getEncrypter().getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), cfg.getEncrypter().getPassword().toCharArray());
                Certificate encrypterCertificate = ks.getCertificate(cfg.getEncrypter().getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, encrypterCertificate.getEncoded());
                authorityEncryptionCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        }
        JAXBElement jaxbAuthorityEncryptionX509Certificate = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate"),
                                    authorityEncryptionCertificate.getClass(), authorityEncryptionCertificate);
        authorityEncryptionX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jaxbAuthorityEncryptionX509Certificate);
        JAXBElement jaxbAuthorityEncryptionX509Data = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data"),
                                    authorityEncryptionX509Data.getClass(), authorityEncryptionX509Data);
        authorityEncryptionKeyInfo.getContent().add(jaxbAuthorityEncryptionX509Data);
        authorityEncryptionKeyDescriptor.setKeyInfo(authorityEncryptionKeyInfo);
        EncryptionMethodType authorityEncryptionEncryptionMethod = new EncryptionMethodType();
        authorityEncryptionEncryptionMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        authorityEncryptionKeyDescriptor.getEncryptionMethod().add(authorityEncryptionEncryptionMethod);
        attributeAuthorityDescriptor.getKeyDescriptor().add(authorityEncryptionKeyDescriptor);

        /* TODO:
        <md:AttributeService Binding="urn:oasis:names:tc:SAML:2.0:bindings:SOAP"
                             Location="http://localhost:8081/nidp/saml2/soap"/>

         */

        EndpointType assertionIDRequestServiceSOAP = new EndpointType();
        assertionIDRequestServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        assertionIDRequestServiceSOAP.setLocation(resolveLocationBaseUrl(provider) + "/nidp/saml2/soap");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceSOAP);

        EndpointType assertionIDRequestServiceURI = new EndpointType();
        assertionIDRequestServiceURI.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:URI");
        assertionIDRequestServiceURI.setLocation(resolveLocationBaseUrl(provider) + "/nidp/saml2/assertion");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceURI);

        entityDescriptor.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().add(attributeAuthorityDescriptor);

        // IDPSSODescriptor
        IDPSSODescriptorType idpSSODescriptor = new IDPSSODescriptorType();
        idpSSODescriptor.setID("idSy31Pds0meYpkaDLFG6-eWqL0WA");
        idpSSODescriptor.getProtocolSupportEnumeration().add("urn:oasis:names:tc:SAML:2.0:protocol");

        // signing key descriptor
        KeyDescriptorType signingKeyDescriptor = new KeyDescriptorType();
        signingKeyDescriptor.setUse(KeyTypes.SIGNING);
        KeyInfoType signingKeyInfo = new KeyInfoType();
        X509DataType signingX509Data = new X509DataType();
        String signingCertificate = "";
        if (cfg != null && cfg.getSigner() != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(cfg.getSigner().getType()) ? "PKCS12" : "JKS");
                byte[] keystore = cfg.getSigner().getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), cfg.getSigner().getPassword().toCharArray());
                Certificate signerCertificate = ks.getCertificate(cfg.getSigner().getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, signerCertificate.getEncoded());
                signingCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        }
        JAXBElement jaxbSigningX509Certificate = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate"),
                                    signingCertificate.getClass(), signingCertificate);
        signingX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jaxbSigningX509Certificate);
        JAXBElement jaxbSigningX509Data = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data"),
                                    signingX509Data.getClass(), signingX509Data);
        signingKeyInfo.getContent().add(jaxbSigningX509Data);
        signingKeyDescriptor.setKeyInfo(signingKeyInfo);
        EncryptionMethodType signingEncryptionMethod = new EncryptionMethodType();
        signingEncryptionMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        signingKeyDescriptor.getEncryptionMethod().add(signingEncryptionMethod);
        idpSSODescriptor.getKeyDescriptor().add(signingKeyDescriptor);

        // encryption key descriptor
        KeyDescriptorType encryptionKeyDescriptor = new KeyDescriptorType();
        encryptionKeyDescriptor.setUse(KeyTypes.ENCRYPTION);
        KeyInfoType encryptionKeyInfo = new KeyInfoType();
        X509DataType encryptionX509Data = new X509DataType();
        String encryptionCertificate = "";
        if (cfg != null && cfg.getEncrypter() != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(cfg.getEncrypter().getType()) ? "PKCS12" : "JKS");
                byte[] keystore = cfg.getEncrypter().getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), cfg.getEncrypter().getPassword().toCharArray());
                Certificate encrypterCertificate = ks.getCertificate(cfg.getEncrypter().getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, encrypterCertificate.getEncoded());
                encryptionCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        }
        JAXBElement jaxbEncryptionX509Certificate = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate"),
                                    encryptionCertificate.getClass(), encryptionCertificate);
        encryptionX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jaxbEncryptionX509Certificate);
        JAXBElement jaxbEncryptionX509Data = new JAXBElement(
                                    new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data"),
                                    encryptionX509Data.getClass(), encryptionX509Data);
        encryptionKeyInfo.getContent().add(jaxbEncryptionX509Data);
        encryptionKeyDescriptor.setKeyInfo(encryptionKeyInfo);
        EncryptionMethodType encryptionEncryptionMethod = new EncryptionMethodType();
        encryptionEncryptionMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        encryptionKeyDescriptor.getEncryptionMethod().add(encryptionEncryptionMethod);
        idpSSODescriptor.getKeyDescriptor().add(encryptionKeyDescriptor);

        // services
        IndexedEndpointType artifactResolutionService = new IndexedEndpointType();
        artifactResolutionService.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        artifactResolutionService.setLocation(resolveLocationUrl(provider) + "/SAML2/ARTIFACT/SOAP");
        artifactResolutionService.setIndex(0);
        artifactResolutionService.setIsDefault(true);
        idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService);

        EndpointType singleLogoutServicePost = new EndpointType();
        singleLogoutServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
        singleLogoutServicePost.setLocation(resolveLocationUrl(provider) + "/SAML2/SLO/POST");
        singleLogoutServicePost.setResponseLocation(resolveLocationUrl(provider) + "/SAML2/SLO_RESPONSE/POST");
        idpSSODescriptor.getSingleLogoutService().add(singleLogoutServicePost);

        EndpointType singleLogoutServiceSOAP = new EndpointType();
        singleLogoutServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        singleLogoutServiceSOAP.setLocation(resolveLocationUrl(provider) + "/SAML2/SLO/SOAP");
        idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceSOAP);

        EndpointType singleLogoutServiceLocal = new EndpointType();
        singleLogoutServiceLocal.setBinding(SamlR2Binding.SAMLR2_LOCAL.getValue());
        singleLogoutServiceLocal.setLocation("local:/" + provider.getLocation().getUri() + "/SAML2/SLO/LOCAL");
        idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceLocal);
        
        EndpointType singleLogoutServiceRedirect = new EndpointType();
        singleLogoutServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
        singleLogoutServiceRedirect.setLocation(resolveLocationUrl(provider) + "/SAML2/SLO/REDIR");
        singleLogoutServiceRedirect.setResponseLocation(resolveLocationUrl(provider) + "/SAML2/SLO_RESPONSE/REDIR");
        idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceRedirect);

        EndpointType manageNameIDServiceSOAP = new EndpointType();
        manageNameIDServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        manageNameIDServiceSOAP.setLocation(resolveLocationUrl(provider) + "/SAML2/MNI/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceSOAP);

        EndpointType manageNameIDServicePost = new EndpointType();
        manageNameIDServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
        manageNameIDServicePost.setLocation(resolveLocationUrl(provider) + "/SAML2/RNI");
        manageNameIDServicePost.setResponseLocation(resolveLocationUrl(provider) + "/SAML2/MNI_RESPONSE/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServicePost);

        EndpointType manageNameIDServiceRedirect = new EndpointType();
        manageNameIDServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
        manageNameIDServiceRedirect.setLocation(resolveLocationBaseUrl(provider) + "/" + provider.getLocation().getContext() + "/SAML2/RNI/REDIR");
        manageNameIDServiceRedirect.setResponseLocation(resolveLocationUrl(provider) + "/SAML2/MNI_RESPONSE/REDIR");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceRedirect);

        idpSSODescriptor.getNameIDFormat().add("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        idpSSODescriptor.getNameIDFormat().add("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");

        EndpointType singleSignOnServicePost = new EndpointType();
        singleSignOnServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
        singleSignOnServicePost.setLocation(resolveLocationUrl(provider) + "/SAML2/SSO/POST");
        idpSSODescriptor.getSingleSignOnService().add(singleSignOnServicePost);

        EndpointType singleSignOnServiceRedirect = new EndpointType();
        singleSignOnServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
        singleSignOnServiceRedirect.setLocation(resolveLocationUrl(provider) + "/SAML2/SSO/REDIR");
        idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceRedirect);

        EndpointType singleSignOnServiceArtifact = new EndpointType();
        singleSignOnServiceArtifact.setBinding(SamlR2Binding.SAMLR2_ARTIFACT.getValue());
        singleSignOnServiceArtifact.setLocation(resolveLocationUrl(provider) + "/SAML2/SSO/ARTIFACT");
        idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceArtifact);

        EndpointType singleSignOnServiceSOAP = new EndpointType();
        singleSignOnServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        singleSignOnServiceArtifact.setLocation(resolveLocationUrl(provider) + "/SAML2/SSO/SOAP");
        idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceSOAP);
        
        entityDescriptor.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().add(idpSSODescriptor);

        // organization
        OrganizationType organization = new OrganizationType();
        LocalizedNameType organizationName = new LocalizedNameType();
        organizationName.setLang("en");
        organizationName.setValue("Atricore IDBUs SAMLR2 JOSSO IDP Sample");
        organization.getOrganizationName().add(organizationName);
        LocalizedNameType organizationDisplayName = new LocalizedNameType();
        organizationDisplayName.setLang("en");
        organizationDisplayName.setValue("Atricore, Inc.");
        organization.getOrganizationDisplayName().add(organizationDisplayName);
        LocalizedURIType organizationURL = new LocalizedURIType();
        organizationURL.setLang("en");
        organizationURL.setValue("http://www.atricore.org");
        organization.getOrganizationURL().add(organizationURL);
        entityDescriptor.setOrganization(organization);

        // contact person
        ContactType contactPerson = new ContactType();
        contactPerson.setContactType(ContactTypeType.OTHER);
        entityDescriptor.getContactPerson().add(contactPerson);

        return entityDescriptor;
    }
}