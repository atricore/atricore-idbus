package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3._2001._04.xmlenc_.EncryptionMethodType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @version $Id$
 */
public class SamlR2IdPTransformer extends AbstractTransformer implements InitializingBean {

    private static final Log logger = LogFactory.getLog(SamlR2IdPTransformer.class);

    private String baseSrcPath = "/org/atricore/idbus/examples/simplefederation/idau/";

    private UUIDGenerator idGenerator = new UUIDGenerator();

    private Keystore sampleKeystore;

    public void afterPropertiesSet() throws Exception {

        if (sampleKeystore.getStore() != null &&
                (sampleKeystore.getStore().getValue() == null ||
                sampleKeystore.getStore().getValue().length == 0)) {
            resolveResource(sampleKeystore.getStore());
        }

        if (sampleKeystore.getStore() == null &&
            sampleKeystore.getStore().getValue() == null ||
                sampleKeystore.getStore().getValue().length == 0) {
            logger.debug("Sample Keystore invalid or not found!");
        } else {
            logger.debug("Sample Keystore size " + sampleKeystore.getStore());
        }

    }

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityProvider;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        try {
            IdentityProvider provider = (IdentityProvider) event.getData();
            IdProjectModule module = event.getContext().getCurrentModule();
            String baseDestPath = (String) event.getContext().get("idauPath");
            String providerBeanName = normalizeBeanName(provider.getName());

            boolean defaultChannelAdded = false;
            List<ServiceProviderChannel> spChannels = new ArrayList<ServiceProviderChannel>();

            for (FederatedConnection fedConn : provider.getFederatedConnectionsA()) {
                ServiceProviderChannel spChannel = (ServiceProviderChannel) fedConn.getChannelA();
                if (spChannel.isOverrideProviderSetup() || !defaultChannelAdded) {
                    spChannels.add(spChannel);
                }
                if (!spChannel.isOverrideProviderSetup()) {
                    defaultChannelAdded = true;
                }
            }

            for (FederatedConnection fedConn : provider.getFederatedConnectionsB()) {
                ServiceProviderChannel spChannel = (ServiceProviderChannel) fedConn.getChannelB();
                if (spChannel.isOverrideProviderSetup() || !defaultChannelAdded) {
                    spChannels.add(spChannel);
                }
                if (!spChannel.isOverrideProviderSetup()) {
                    defaultChannelAdded = true;
                }
            }

            for (ServiceProviderChannel spChannel : spChannels) {
                String resourceName = providerBeanName;
                if (spChannel.isOverrideProviderSetup()) {
                    resourceName = normalizeBeanName(spChannel.getName());
                }
                IdProjectResource<EntityDescriptorType> idpMetadata = new IdProjectResource<EntityDescriptorType>(idGen.generateId(),
                    baseDestPath + providerBeanName, resourceName, "saml2", generateSPChannelMetadata(provider, spChannel));
                idpMetadata.setClassifier("jaxb");
                module.addResource(idpMetadata);
            }
        } catch (Exception e) {
            throw new TransformException(e);
        }
    }

    private EntityDescriptorType generateSPChannelMetadata(IdentityProvider provider, ServiceProviderChannel spChannel) throws TransformException {
        SamlR2ProviderConfig cfg = (SamlR2ProviderConfig) provider.getConfig();

        EntityDescriptorType entityDescriptor = new EntityDescriptorType();
        // TODO : Take ID from provider entityId attribute (To be created)
        entityDescriptor.setID(idGenerator.generateId());
        entityDescriptor.setEntityID(resolveLocationUrl(provider, spChannel) + "/SAML2/MD");

        // AttributeAuthorityDescriptor
        AuthnAuthorityDescriptorType attributeAuthorityDescriptor = new AuthnAuthorityDescriptorType();
        // TODO : Take ID from provider entityId attribute (To be created)
        attributeAuthorityDescriptor.setID(idGenerator.generateId());
        attributeAuthorityDescriptor.getProtocolSupportEnumeration().add("urn:oasis:names:tc:SAML:2.0:protocol");

        // authority signing key descriptor
        KeyDescriptorType authoritySigningKeyDescriptor = new KeyDescriptorType();
        authoritySigningKeyDescriptor.setUse(KeyTypes.SIGNING);
        KeyInfoType authoritySigningKeyInfo = new KeyInfoType();
        X509DataType authoritySigningX509Data = new X509DataType();
        String authoritySigningCertificate = ""; // TODO

        Keystore signKs = null;
        Keystore encryptKs = null;

        if (cfg != null) {

            signKs = cfg.getSigner();

            if (signKs == null && cfg.isUseSampleStore()) {
                logger.warn("Using Sample keystore for signing : " + cfg.getName());
                signKs = sampleKeystore;
            }

            encryptKs = cfg.getEncrypter();
            if (encryptKs == null && cfg.isUseSampleStore()) {
                logger.warn("Using Sample keystore for encryption : " + cfg.getName());
                encryptKs = sampleKeystore;
            }

        }
            
        if (signKs != null) {
            try {

                byte[] keystore = signKs.getStore().getValue();
                if (logger.isTraceEnabled())
                    logger.trace("Keystore [" + signKs.getStore().getName() + "] length " + keystore.length);

                KeyStore jks = KeyStore.getInstance("PKCS#12".equals(signKs.getType()) ? "PKCS12" : "JKS");
                jks.load(new ByteArrayInputStream(keystore), signKs.getPassword().toCharArray());

                Certificate signerCertificate = jks.getCertificate(signKs.getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, signerCertificate.getEncoded());
                authoritySigningCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        } else {
            throw new TransformException("No Signer defined for " + provider.getName());
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
        if (encryptKs != null) {

            try {
                byte[] keystore = encryptKs.getStore().getValue();
                if (logger.isTraceEnabled())
                    logger.trace("Keystore [" + encryptKs.getStore().getName() + "] length " + keystore.length);

                KeyStore jks = KeyStore.getInstance("PKCS#12".equals(encryptKs.getType()) ? "PKCS12" : "JKS");
                jks.load(new ByteArrayInputStream(keystore), encryptKs.getPassword().toCharArray());

                Certificate encrypterCertificate = jks.getCertificate(encryptKs.getCertificateAlias());
                StringWriter writer = new StringWriter();
                CipherUtil.writeBase64Encoded(writer, encrypterCertificate.getEncoded());
                authorityEncryptionCertificate = writer.toString();
            } catch (Exception e) {
                throw new TransformException(e);
            }
        } else {
            throw new TransformException("No Encrypter defined for " + provider.getName());
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

        /* TODO : When serivce is supported, use proper Locations!
        EndpointType assertionIDRequestServiceSOAP = new EndpointType();
        assertionIDRequestServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        assertionIDRequestServiceSOAP.setLocation(resolveLocationBaseUrl(provider) + "/nidp/saml2/soap");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceSOAP);

        EndpointType assertionIDRequestServiceURI = new EndpointType();
        assertionIDRequestServiceURI.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:URI");
        assertionIDRequestServiceURI.setLocation(resolveLocationBaseUrl(provider) + "/nidp/saml2/assertion");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceURI);
        */

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
        if (signKs != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(signKs.getType()) ? "PKCS12" : "JKS");
                byte[] keystore = signKs.getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), signKs.getPassword().toCharArray());
                Certificate signerCertificate = ks.getCertificate(signKs.getCertificateAlias());
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
        if (encryptKs != null) {
            try {
                KeyStore ks = KeyStore.getInstance("PKCS#12".equals(encryptKs.getType()) ? "PKCS12" : "JKS");
                byte[] keystore = encryptKs.getStore().getValue();
                ks.load(new ByteArrayInputStream(keystore), encryptKs.getPassword().toCharArray());
                Certificate encrypterCertificate = ks.getCertificate(encryptKs.getCertificateAlias());
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

        // profiles
        Set<Profile> activeProfiles = spChannel.getActiveProfiles();
        if (!spChannel.isOverrideProviderSetup()) {
            activeProfiles = provider.getActiveProfiles();
        }
        boolean ssoEnabled = false;
        boolean sloEnabled = false;
        for (Profile profile : activeProfiles) {
            if (profile.equals(Profile.SSO)) {
                ssoEnabled = true;
            } else if (profile.equals(Profile.SSO_SLO)) {
                sloEnabled = true;
            }
        }

        // bindings
        Set<Binding> activeBindings = spChannel.getActiveBindings();
        if (!spChannel.isOverrideProviderSetup()) {
            activeBindings = provider.getActiveBindings();
        }
        boolean postEnabled = false;
        boolean redirectEnabled = false;
        boolean artifactEnabled = false;
        boolean soapEnabled = false;
        for (Binding binding : activeBindings) {
            if (binding.equals(Binding.SAMLR2_HTTP_POST)) {
                postEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_HTTP_REDIRECT)) {
                redirectEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_ARTIFACT)) {
                artifactEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_SOAP)) {
                soapEnabled = true;
            }
        }

        // ArtifactResolutionService
        if (artifactEnabled) {
            IndexedEndpointType artifactResolutionService = new IndexedEndpointType();
            artifactResolutionService.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
            artifactResolutionService.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/ARTIFACT/SOAP");
            artifactResolutionService.setIndex(0);
            artifactResolutionService.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService);

            IndexedEndpointType artifactResolutionServiceLocal = new IndexedEndpointType();
            artifactResolutionServiceLocal.setBinding(SamlR2Binding.SAMLR2_LOCAL.getValue());
            artifactResolutionServiceLocal.setLocation("local://" + spChannel.getLocation().getUri() + "/SAML2/ARTIFACT/LOCAL");
            artifactResolutionServiceLocal.setIndex(1);
            artifactResolutionServiceLocal.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionServiceLocal);

            IndexedEndpointType artifactResolutionService11 = new IndexedEndpointType();
            artifactResolutionService11.setBinding(SamlR2Binding.SAMLR11_SOAP.getValue());
            artifactResolutionService11.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML11/ARTIFACT/SOAP");
            artifactResolutionService11.setIndex(0);
            artifactResolutionService11.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService11);
        }

        // SingleLogoutService

        if (sloEnabled) {
            if (postEnabled) {
                EndpointType singleLogoutServicePost = new EndpointType();
                singleLogoutServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
                singleLogoutServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/POST");
                singleLogoutServicePost.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO_RESPONSE/POST");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServicePost);
            }

            if (artifactEnabled) {
                EndpointType singleLogoutServiceArtifact = new EndpointType();
                singleLogoutServiceArtifact.setBinding(SamlR2Binding.SAMLR2_ARTIFACT.getValue());
                singleLogoutServiceArtifact.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/ARTIFACT");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceArtifact);
            }

            if (redirectEnabled) {
                EndpointType singleLogoutServiceRedirect = new EndpointType();
                singleLogoutServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
                singleLogoutServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/REDIR");
                singleLogoutServiceRedirect.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO_RESPONSE/REDIR");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceRedirect);
            }

            if (soapEnabled) {
                EndpointType singleLogoutServiceSOAP = new EndpointType();
                singleLogoutServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
                singleLogoutServiceSOAP.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/SOAP");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceSOAP);
            }

            EndpointType singleLogoutServiceLocal = new EndpointType();
            singleLogoutServiceLocal.setBinding(SamlR2Binding.SAMLR2_LOCAL.getValue());
            singleLogoutServiceLocal.setLocation("local://" + spChannel.getLocation().getUri() + "/SAML2/SLO/LOCAL");
            idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceLocal);
        }

        // ManageNameIDService
        EndpointType manageNameIDServiceSOAP = new EndpointType();
        manageNameIDServiceSOAP.setBinding(SamlR2Binding.SAMLR2_SOAP.getValue());
        manageNameIDServiceSOAP.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceSOAP);

        EndpointType manageNameIDServicePost = new EndpointType();
        manageNameIDServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
        manageNameIDServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/RNI");
        manageNameIDServicePost.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI_RESPONSE/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServicePost);

        EndpointType manageNameIDServiceRedirect = new EndpointType();
        manageNameIDServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
        manageNameIDServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/RNI/REDIR");
        manageNameIDServiceRedirect.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI_RESPONSE/REDIR");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceRedirect);

        // TODO : Make configurable
        idpSSODescriptor.getNameIDFormat().add("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        idpSSODescriptor.getNameIDFormat().add("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");

        // SingleSignOnService
        if (ssoEnabled) {
            if (postEnabled) {
                EndpointType singleSignOnServicePost = new EndpointType();
                singleSignOnServicePost.setBinding(SamlR2Binding.SAMLR2_POST.getValue());
                singleSignOnServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/POST");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServicePost);
            }

            if (redirectEnabled) {
                EndpointType singleSignOnServiceRedirect = new EndpointType();
                singleSignOnServiceRedirect.setBinding(SamlR2Binding.SAMLR2_REDIRECT.getValue());
                singleSignOnServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/REDIR");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceRedirect);
            }

            if (artifactEnabled) {
                EndpointType singleSignOnServiceArtifact = new EndpointType();
                singleSignOnServiceArtifact.setBinding(SamlR2Binding.SAMLR2_ARTIFACT.getValue());
                singleSignOnServiceArtifact.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/ARTIFACT");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceArtifact);
            }
        }

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

    public Keystore getSampleKeystore() {
        return sampleKeystore;
    }

    public void setSampleKeystore(Keystore sampleKeystore) {
        this.sampleKeystore = sampleKeystore;
    }
}