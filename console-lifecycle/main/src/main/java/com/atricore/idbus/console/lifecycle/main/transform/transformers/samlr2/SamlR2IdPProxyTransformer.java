package com.atricore.idbus.console.lifecycle.main.transform.transformers.samlr2;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.sso.AbstractSPChannelTransformer;
import com.atricore.idbus.console.lifecycle.main.util.MetadataUtil;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Description;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Entry;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Service;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.capabilities.sso.main.binding.logging.SSOLogMessageBuilder;
import org.atricore.idbus.capabilities.sso.main.binding.logging.SamlR2LogMessageBuilder;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSessionEventListener;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.main.sp.SSOSPMediator;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeystoreKeyResolver;
import org.atricore.idbus.capabilities.sso.support.core.encryption.XmlSecurityEncrypterImpl;
import org.atricore.idbus.capabilities.sso.support.core.signature.JSR105SamlR2SignerImpl;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.federation.AccountLinkLifecycleImpl;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustImpl;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManagerImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedRemoteProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
//import org.atricore.idbus.kernel.main.session.SSOSessionEventManager;
import org.atricore.idbus.kernel.main.session.SSOSessionEventListener;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3._2001._04.xmlenc_.EncryptionMethodType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.*;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;

/**
 * Generates IdP proxy components for remote SAML 2.0 IdP definitions having 'override' defaults set to true.
 *
 * [Local SP] <--> [IdP-pxy/Sp-pxy] <--> [Remote IdP]
 *
 * They all expose an internal SAML 2.0 IdP that talks to our SAML 2.0 SPs.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlR2IdPProxyTransformer extends AbstractSPChannelTransformer implements InitializingBean {

    private static final Log logger = LogFactory.getLog(SamlR2IdPProxyTransformer.class);

    private UUIDGenerator idGenerator = new UUIDGenerator();

    private boolean roleA;

    private Keystore sampleKeystore;

    public SamlR2IdPProxyTransformer() {

    }


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


    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    /**
     * Internal SAML 2.0 SPs connected to external SAML 2.0 IdPs and using a resource that requires special functionallity (OAuth, Domino, etc).
     */
    @Override
    public boolean accept(TransformEvent event) {

        if (event.getData() instanceof ServiceProviderChannel) {
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            boolean requireProxy = isIdPProxyRequired(fc, roleA);

            if (requireProxy)
                if (logger.isDebugEnabled())
                    logger.debug("Required IdP proxy (role " + (roleA ? "A" : "B") + ") between "  + fc.getRoleA().getName() + ":" + fc.getRoleB().getName());

            return requireProxy;
        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Date now = new Date();
        Beans baseBeans = (Beans) event.getContext().get("beans");
        //Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        Beans idpProxyBeans = new Beans();

        FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

        ExternalSaml2IdentityProvider identityProvider = (ExternalSaml2IdentityProvider) (roleA ? fc.getRoleA() : fc.getRoleB());
        InternalSaml2ServiceProvider internalSaml2ServiceProvider = (InternalSaml2ServiceProvider) (roleA ? fc.getRoleB() : fc.getRoleA());
        IdentityProviderChannel idpChannel = (IdentityProviderChannel) (roleA ? fc.getChannelB() : fc.getChannelA());

        Description descr = new Description();
        descr.getContent().add(identityProvider.getName() + " : IdP Proxy Configuration ("+identityProvider.getName()+"/"+ internalSaml2ServiceProvider.getName()+") generated by Atricore Identity Bus Server on " + now.toGMTString());
        descr.getContent().add(identityProvider.getDescription());

        // Publish root element so that other transformers can use it.
        event.getContext().put("idpProxyBeans", idpProxyBeans);
        String idauPath = (String) event.getContext().get("idauPath");

        // Get the proper keystore

        Keystore signKs = null;
        Keystore encryptKs = null;

        // Take Keystores from local SP
        SamlR2ProviderConfig spCfg = (SamlR2ProviderConfig) (roleA ? fc.getRoleB().getConfig() : fc.getRoleA().getConfig());
        if (spCfg != null) {
            signKs = spCfg.getSigner();
            if (signKs == null && spCfg.isUseSampleStore()) {
                logger.warn("Using Sample keystore for signing : " + spCfg.getName());
                signKs = sampleKeystore;
            }
            encryptKs = spCfg.getEncrypter();
            if (encryptKs == null && spCfg.isUseSampleStore()) {
                logger.warn("Using Sample keystore for encryption : " + spCfg.getName());
                encryptKs = sampleKeystore;
            }
        } else {
            // Use sample
            logger.warn("Using Sample keystore for signing : proxy " + identityProvider.getName());
            signKs = sampleKeystore;
            logger.warn("Using Sample keystore for encryption : proxy " + identityProvider.getName());
            encryptKs = sampleKeystore;
        }

        // Create Internal IDP facing local SP
        createProxyIdPSide(event, identityProvider, internalSaml2ServiceProvider, baseBeans, idpProxyBeans, signKs, encryptKs, idauPath);

        // Create Internal SP facing remote IDP
        createProxySpSide(event, internalSaml2ServiceProvider, idpChannel, identityProvider, baseBeans, idpProxyBeans, signKs, encryptKs, idauPath);

    }

    /**
     * @serviceProvider local SAML 2.0 SP
     * @identityProvider remote SAML 2.0 IdP to be proxied
     * Creates an internal SP that will face the remote IdP
     */
    protected void createProxySpSide(TransformEvent event,
                                     InternalSaml2ServiceProvider providerInternalSaml2,
                                     IdentityProviderChannel idpChannel,
                                     ExternalSaml2IdentityProvider identityProvider,
                                     Beans baseBeans,
                                     Beans idpProxyBeans,
                                     Keystore signKs,
                                     Keystore encryptKs,
                                     String idauPath) throws TransformException {

        //-------------------------------
        // Service Provider
        // ----------------------------------------

        // Name
        IdProjectModule module = event.getContext().getCurrentModule();
        String spProxyName = normalizeBeanName(identityProvider.getName() + "-" + providerInternalSaml2.getName() + "-sp-proxy");
        String spName = normalizeBeanName(providerInternalSaml2.getName());
        Bean spProxyBean = newBean(idpProxyBeans, spProxyName, ServiceProviderImpl.class.getName());
        event.getContext().put("spProxyBean", spProxyBean);
        setPropertyValue(spProxyBean, "name", spProxyName);
        setPropertyValue(spProxyBean, "description", providerInternalSaml2.getDescription() + "(SP Proxy)");

        // Role
        if (!providerInternalSaml2.getRole().equals(ProviderRole.SSOServiceProvider)) {
            logger.warn("Provider "+ providerInternalSaml2.getId()+" is not defined as SP, forcing role! ");
        }
        setPropertyValue(spProxyBean, "role", SSOMetadataConstants.SPSSODescriptor_QNAME.toString());

        // unitContainer
        setPropertyRef(spProxyBean, "unitContainer", providerInternalSaml2.getIdentityAppliance().getName() + "-container");

        // COT Manager
        Collection<Bean> cotMgrs = getBeansOfType(baseBeans, CircleOfTrustManagerImpl.class.getName());
        if (cotMgrs.size() == 1) {
            Bean cotMgr = cotMgrs.iterator().next();
            setPropertyRef(spProxyBean, "cotManager", cotMgr.getName());
        }

        // State Manager
        setPropertyRef(spProxyBean, "stateManager", providerInternalSaml2.getIdentityAppliance().getName() + "-state-manager");

        // ----------------------------------------
        // IDP Proxy Provider Mediator
        // ----------------------------------------
        Bean spMediator = newBean(idpProxyBeans, spProxyName + "-samlr2-mediator",
                SSOSPMediator.class.getName());

        MetadataDefinition providerMd;
        try {
            providerMd = MetadataUtil.loadMetadataDefinition(identityProvider.getMetadata().getValue());
            String alias = MetadataUtil.findEntityId(providerMd);
            setPropertyValue(spMediator, "preferredIdpAlias", alias);
        } catch (Exception e) {
            throw new TransformException(e);
        }


        //setPropertyValue(spMediator, "preferredIdpSSOBinding", SSOBinding.SAMLR2_POST.getValue());
        //setPropertyValue(spMediator, "preferredIdpSLOBinding", SSOBinding.SAMLR2_POST.getValue());
        setPropertyValue(spMediator, "preferredIdpSSOBinding", SSOBinding.SAMLR2_ARTIFACT.getValue());
        setPropertyValue(spMediator, "preferredIdpSLOBinding", SSOBinding.SAMLR2_ARTIFACT.getValue());

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();
        IdentityApplianceDefinition applianceDef = providerInternalSaml2.getIdentityAppliance();



        // Take IDP Proxy location and create ACS endpoints ...

        Bean idpProxyBean = (Bean) event.getContext().get("idpProxyBean");
        String bpLocationPath = "/IDBUS/" + appliance.getName().toUpperCase() + "/" + idpProxyBean.getName().toUpperCase();
        String bpLocation = resolveLocationBaseUrl(applianceDef.getLocation()) + bpLocationPath;

        // This is actually an IDP-Proxy endpoint IDBUS/DIAGEO/PROXY1-IDP
        setPropertyValue(spMediator, "spBindingACS", bpLocation + "/SSO/ACSPROXY/ARTIFACT");
        setPropertyValue(spMediator, "spBindingSLO", bpLocation + "/SSO/SLOPROXY/ARTIFACT");
        setPropertyValue(spMediator, "idpSelector", resolveLocationUrl(appliance.getIdApplianceDefinition().getLocation()) + "/SSO/SELECTOR/IDP");

        setPropertyValue(spMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(spMediator, "artifactQueueManager", identityProvider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(spMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(spMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

        // logger

        List<Bean> spLogBuilders = new ArrayList<Bean>();
        spLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
        spLogBuilders.add(newAnonymousBean(SSOLogMessageBuilder.class));
        spLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        spLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean spLogger = newAnonymousBean(DefaultMediationLogger.class.getName());
        spLogger.setName(spProxyName + "-mediation-logger");
        setPropertyValue(spLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire." + spProxyName);
        setPropertyAsBeans(spLogger, "messageBuilders", spLogBuilders);
        setPropertyBean(spMediator, "logger", spLogger);

        // errorUrl
        setPropertyValue(spMediator, "errorUrl", resolveUiErrorLocation(appliance));

        // warningUrl
        setPropertyValue(spMediator, "warningUrl", resolveUiWarningLocation(appliance));

        // ----------------------------------------
        // Signer
        // ----------------------------------------
        if (signKs != null) {

            String signerResourceFileName = signKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(signKs.getType()) ? "pkcs12" : "jks");

            IdProjectResource<byte[]> signerResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + identityProvider.getName() + "/", signerResourceFileName,
                    "binary", signKs.getStore().getValue());
            signerResource.setClassifier("byte");

            Bean signer = newBean(idpProxyBeans, spProxyName + "-samlr2-signer", JSR105SamlR2SignerImpl.class);
            signer.setInitMethod("init");

            Description signerDescr = new Description();
            signerDescr.getContent().add(signKs.getDisplayName());
            signer.setDescription(signerDescr);

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", signKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + identityProvider.getName() + "/" + signerResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", signKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", signKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", signKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", signKs.getCertificateAlias());

            setPropertyBean(signer, "keyResolver", keyResolver);
            setPropertyBean(spMediator, "signer", signer);

            event.getContext().getCurrentModule().addResource(signerResource);

            // signer
            setPropertyRef(spMediator, "signer", signer.getName());

            setPropertyValue(spMediator, "signRequests", providerInternalSaml2.isSignRequests());
            setPropertyValue(spMediator, "validateRequestsSignature", providerInternalSaml2.isWantSignedRequests());

        } else {
            throw new TransformException("No Signer defined for " + spProxyName);
        }

        // ----------------------------------------
        // Encrypter
        // ----------------------------------------
        if (encryptKs != null) {

            String encrypterResourceFileName = encryptKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(encryptKs.getType()) ? "pkcs12" : "jks");

            IdProjectResource<byte[]> encrypterResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + identityProvider.getName() + "/", encrypterResourceFileName,
                    "binary", encryptKs.getStore().getValue());
            encrypterResource.setClassifier("byte");

            Bean encrypter = newBean(idpProxyBeans, spProxyName + "-samlr2-encrypter", XmlSecurityEncrypterImpl.class);

            setPropertyValue(encrypter, "symmetricKeyAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
            setPropertyValue(encrypter, "kekAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#rsa-1_5");

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", encryptKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + identityProvider.getName() + "/" + encrypterResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", encryptKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", encryptKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", encryptKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", encryptKs.getCertificateAlias());

            setPropertyBean(encrypter, "keyResolver", keyResolver);
            setPropertyBean(spMediator, "encrypter", encrypter);

            event.getContext().getCurrentModule().addResource(encrypterResource);

            // encrypter
            setPropertyRef(spMediator, "encrypter", encrypter.getName());
        } else {
            throw new TransformException("No Encrypter defined for " + spProxyName);
        }

        // accountLinkLifecycle
        Bean accountLinkLifecycle = newBean(idpProxyBeans, spProxyName + "-account-link-lifecycle", AccountLinkLifecycleImpl.class);

        // ----------------------------------------
        // MBean
        // ----------------------------------------
        Bean mBean = newBean(idpProxyBeans, spProxyName + "-mbean", "org.atricore.idbus.capabilities.sso.management.internal.ServiceProviderMBeanImpl");
        setPropertyRef(mBean, "serviceProvider", spProxyName);

        Bean mBeanExporter = newBean(idpProxyBeans, spProxyName + "-mbean-exporter", "org.springframework.jmx.export.MBeanExporter");
        setPropertyRef(mBeanExporter, "server", "mBeanServer");

        // mbeans
        List<Entry> mBeans = new ArrayList<Entry>();

        Bean mBeanKey = newBean(idpProxyBeans, mBean.getName() + "-key", String.class);
        setConstructorArg(mBeanKey, 0, "java.lang.String", appliance.getNamespace() +  "." +
                event.getContext().getCurrentModule().getId() +
                ":type=InternalSaml2ServiceProvider,name=" + applianceDef.getName() + "." + spProxyName);

        Entry mBeanEntry = new Entry();
        mBeanEntry.setKeyRef(mBeanKey.getName());
        mBeanEntry.setValueRef(mBean.getName());
        mBeans.add(mBeanEntry);

        setPropertyAsMapEntries(mBeanExporter, "beans", mBeans);

        // -------------------------------------------------------
        // Define Session Manager bean
        // -------------------------------------------------------
        Bean sessionManager = newBean(idpProxyBeans, spProxyName + "-session-manager",
                "org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl");

        // Properties (take from config!)
        // FOR SPs, the session timeout should be long enough ...
        setPropertyValue(sessionManager, "maxInactiveInterval", "500");
        setPropertyValue(sessionManager, "maxSessionsPerUser", "-1");
        setPropertyValue(sessionManager, "invalidateExceedingSessions", "false");
        setPropertyValue(sessionManager, "sessionMonitorInterval", "10000");

        // Session ID Generator
        Bean sessionIdGenerator = newAnonymousBean("org.atricore.idbus.kernel.main.session.service.SessionIdGeneratorImpl");
        setPropertyValue(sessionIdGenerator, "algorithm", "MD5");

        // Session Store
        //Bean sessionStore = newAnonymousBean("org.atricore.idbus.idojos.memorysessionstore.MemorySessionStore");
        Bean sessionStore = newAnonymousBean("org.atricore.idbus.idojos.ehcachesessionstore.EHCacheSessionStore");
        sessionStore.setInitMethod("init");
        setPropertyRef(sessionStore, "cacheManager", providerInternalSaml2.getIdentityAppliance().getName() + "-cache-manager");
        setPropertyValue(sessionStore, "cacheName", providerInternalSaml2.getIdentityAppliance().getName() +
                "-" + spProxyName + "-sessionsCache");

        // Wiring
        setPropertyBean(sessionManager, "sessionIdGenerator", sessionIdGenerator);
        setPropertyBean(sessionManager, "sessionStore", sessionStore);

        // Generate SP metadata for default idp channel
        IdProjectResource<EntityDescriptorType> spMetadata = new IdProjectResource<EntityDescriptorType>(idGen.generateId(),
                idauPath + identityProvider.getName() + "/", idpChannel.getName(), "saml2",
                generateIDPChannelMetadata(appliance, spProxyBean, providerInternalSaml2, idpChannel, signKs, encryptKs, idauPath));

        spMetadata.setClassifier("jaxb");

        module.addResource(spMetadata);

    }


    /**
     * @identityProvider remote SAML 2.0 IdP to be proxied
     * @serviceProvider local SAML 2.0 SP
     *
     * Creates an internal IdP that will face the local SP
     */
    protected void createProxyIdPSide(TransformEvent event,
                                      ExternalSaml2IdentityProvider remoteIdentityProvider,
                                      InternalSaml2ServiceProvider localInternalSaml2ServiceProvider,
                                      Beans baseBeans, Beans idpProxyBeans,
                                      Keystore signKs,
                                      Keystore encryptKs,
                                      String idauPath) throws TransformException {

        IdProjectModule module = event.getContext().getCurrentModule();
        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();
        FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

        // --------------------------------------------------------------
        // IDP Proxy side
        // --------------------------------------------------------------

        // Get remote MD descriptor for validation, parse : identityProvider.getMetadata().getValue()

        String idpName =  normalizeBeanName(remoteIdentityProvider.getName() + "-" + localInternalSaml2ServiceProvider.getName() + "-idp-proxy");

        // TODO : Takes this from console cfg !
        ServiceProviderChannel spChannel = (ServiceProviderChannel) (roleA ? fc.getChannelA() : fc.getChannelB());
        IdentityProviderChannel idpChannel = (IdentityProviderChannel) (roleA ? fc.getChannelB() : fc.getChannelA());

        spChannel.getActiveProfiles().clear();
        spChannel.getActiveProfiles().add(Profile.SSO);
        spChannel.getActiveProfiles().add(Profile.SSO_SLO);

        // TODO : Takes this from console cfg !
        spChannel.getActiveBindings().clear();
        spChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        //fChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_REDIRECT);
        spChannel.getActiveBindings().add(Binding.SAMLR2_ARTIFACT);
        spChannel.getActiveBindings().add(Binding.SAMLR2_SOAP);
        // ---------------------------------------------------------------------------------------------------

        Bean idpProxyBean = newBean(idpProxyBeans, idpName, IdentityProviderImpl.class);

        // Name
        setPropertyValue(idpProxyBean, "name", idpProxyBean.getName());
        setPropertyValue(idpProxyBean, "description", remoteIdentityProvider.getDescription() + "(IDP Proxy)");
        event.getContext().put("idpProxyBean", idpProxyBean);

        if (logger.isDebugEnabled())
            logger.debug("Generating IDP Proxy " + idpProxyBean.getName() + " configuration model");

        // Role, set to IDP
        setPropertyValue(idpProxyBean, "role", SSOMetadataConstants.IDPSSODescriptor_QNAME.toString());

        // unitContainer
        setPropertyRef(idpProxyBean, "unitContainer", remoteIdentityProvider.getIdentityAppliance().getName() + "-container");

        // COT Manager
        Collection<Bean> cotMgrs = getBeansOfType(baseBeans, CircleOfTrustManagerImpl.class.getName());
        if (cotMgrs.size() == 1) {
            Bean cotMgr = cotMgrs.iterator().next();
            setPropertyRef(idpProxyBean, "cotManager", cotMgr.getName());
        } else if (cotMgrs.size() > 1) {
            throw new TransformException("Invalid number of COT Managers defined " + cotMgrs.size());
        }

        // State Manager
        setPropertyRef(idpProxyBean, "stateManager", remoteIdentityProvider.getIdentityAppliance().getName() + "-state-manager");

        // ----------------------------------------
        // Identity Provider Mediator
        // ----------------------------------------
        Bean idpMediator = newBean(idpProxyBeans, idpName + "-samlr2-mediator",
                SSOIDPMediator.class.getName());
        setPropertyValue(idpMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(idpMediator, "artifactQueueManager", identityProvider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(idpMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(idpMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

        // TODO : setPropertyBean(idpMediator, "setPreferredSpAlias", ???????);

        // logger
        List<Bean> idpLogBuilders = new ArrayList<Bean>();
        idpLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(SSOLogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean idpLogger = newAnonymousBean(DefaultMediationLogger.class.getName());
        idpLogger.setName(idpName + "-mediation-logger");
        setPropertyValue(idpLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire." + idpProxyBean.getName());
        setPropertyAsBeans(idpLogger, "messageBuilders", idpLogBuilders);
        setPropertyBean(idpMediator, "logger", idpLogger);

        // errorUrl
        setPropertyValue(idpMediator, "errorUrl", resolveUiErrorLocation(appliance));

        // warningUrl
        setPropertyValue(idpMediator, "warningUrl", resolveUiWarningLocation(appliance));

        // ----------------------------------------
        // Signer
        // ----------------------------------------
        if (signKs != null) {

            String signerResourceFileName = signKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(signKs.getType()) ? "pkcs12" : "jks");

            // Use identityProvider name in path, to keep all resources together
            IdProjectResource<byte[]> signerResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + remoteIdentityProvider.getName() + "/", signerResourceFileName,
                    "binary", signKs.getStore().getValue());
            signerResource.setClassifier("byte");

            Bean signer = newBean(idpProxyBeans, idpName + "-samlr2-signer", JSR105SamlR2SignerImpl.class);
            signer.setInitMethod("init");

            Description signerDescr = new Description();
            signerDescr.getContent().add(signKs.getDisplayName());
            signer.setDescription(signerDescr);

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", signKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + remoteIdentityProvider.getName() + "/" + signerResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", signKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", signKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", signKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", signKs.getCertificateAlias());

            setPropertyBean(signer, "keyResolver", keyResolver);
            setPropertyBean(idpMediator, "signer", signer);

            // TODO : Maybe we need to get this from external IDP Metadata, or from FC channel setup !?
            //setPropertyValue(idpMediator, "signRequests", identityProvider.isSignRequests());
            //setPropertyValue(idpMediator, "validateRequestsSignature", identityProvider.isWantSignedRequests());
            setPropertyValue(idpMediator, "signRequests", true);
            setPropertyValue(idpMediator, "validateRequestsSignature", false);

            event.getContext().getCurrentModule().addResource(signerResource);

            // signer
            setPropertyRef(idpMediator, "signer", signer.getName());
        } else {
            throw new TransformException("No Signer defined for " + remoteIdentityProvider.getName());
        }

        // ----------------------------------------
        // Encrypter
        // ----------------------------------------
        if (encryptKs != null) {

            String encrypterResourceFileName = encryptKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(encryptKs.getType()) ? "pkcs12" : "jks");

            IdProjectResource<byte[]> encrypterResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + remoteIdentityProvider.getName() + "/", encrypterResourceFileName,
                    "binary", encryptKs.getStore().getValue());
            encrypterResource.setClassifier("byte");

            Bean encrypter = newBean(idpProxyBeans, idpName + "-samlr2-encrypter", XmlSecurityEncrypterImpl.class);

            setPropertyValue(encrypter, "symmetricKeyAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
            setPropertyValue(encrypter, "kekAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#rsa-1_5");

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", encryptKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + remoteIdentityProvider.getName() + "/" + encrypterResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", encryptKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", encryptKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", encryptKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", encryptKs.getCertificateAlias());

            setPropertyBean(encrypter, "keyResolver", keyResolver);
            setPropertyBean(idpMediator, "encrypter", encrypter);

            event.getContext().getCurrentModule().addResource(encrypterResource);

            // encrypter
            setPropertyRef(idpMediator, "encrypter", encrypter.getName());
        } else {
            throw new TransformException("No Encrypter defined for " + remoteIdentityProvider.getName());
        }

        // ----------------------------------------
        // MBean
        // ----------------------------------------
        // TODO : Use proxy specific bean types
        Bean mBean = newBean(idpProxyBeans, idpName + "-mbean", "org.atricore.idbus.capabilities.sso.management.internal.IdentityProviderMBeanImpl");
        setPropertyRef(mBean, "identityProvider", idpName);

        Bean mBeanExporter = newBean(idpProxyBeans, idpName + "-mbean-exporter", "org.springframework.jmx.export.MBeanExporter");
        setPropertyRef(mBeanExporter, "server", "mBeanServer");

        // mbeans
        List<Entry> mBeans = new ArrayList<Entry>();

        Bean mBeanKey = newBean(idpProxyBeans, mBean.getName() + "-key", String.class);
        setConstructorArg(mBeanKey, 0, "java.lang.String", appliance.getNamespace() + "." +
                event.getContext().getCurrentModule().getId() +
                ":type=IdentityProvider,name=" + remoteIdentityProvider.getIdentityAppliance().getName() + "." + idpProxyBean.getName());

        Entry mBeanEntry = new Entry();
        mBeanEntry.setKeyRef(mBeanKey.getName());
        mBeanEntry.setValueRef(mBean.getName());
        mBeans.add(mBeanEntry);

        setPropertyAsMapEntries(mBeanExporter, "beans", mBeans);

        // -------------------------------------------------------
        // Session Manager bean
        // -------------------------------------------------------
        Bean sessionManager = newBean(idpProxyBeans, idpName + "-session-manager",
                "org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl");

        // Properties (take from config!)

        // This will be overridden on runtime, using assertion expiration times
        int ssoSessionTimeout = 30;
        if (ssoSessionTimeout < 1) {
            logger.warn("Invalid SSO Session Timeout " + ssoSessionTimeout + ", forcing a new value");
            ssoSessionTimeout = 30;
        }
        setPropertyValue(sessionManager, "maxInactiveInterval", ssoSessionTimeout + "");
        setPropertyValue(sessionManager, "maxSessionsPerUser", "-1");
        setPropertyValue(sessionManager, "invalidateExceedingSessions", "false");
        setPropertyValue(sessionManager, "sessionMonitorInterval", "10000");

        // Session ID Generator
        Bean sessionIdGenerator = newAnonymousBean("org.atricore.idbus.kernel.main.session.service.SessionIdGeneratorImpl");
        setPropertyValue(sessionIdGenerator, "algorithm", "MD5");

        // Session Store
        Bean sessionStore = newAnonymousBean("org.atricore.idbus.idojos.ehcachesessionstore.EHCacheSessionStore");
        sessionStore.setInitMethod("init");
        setPropertyRef(sessionStore, "cacheManager", remoteIdentityProvider.getIdentityAppliance().getName() + "-cache-manager");
        setPropertyValue(sessionStore, "cacheName", remoteIdentityProvider.getIdentityAppliance().getName() +
                "-" + idpName + "-sessionsCache");

        // Wiring
        setPropertyBean(sessionManager, "sessionIdGenerator", sessionIdGenerator);
        setPropertyBean(sessionManager, "sessionStore", sessionStore);

        // generate IDP metadata for default SP channel
        IdProjectResource<EntityDescriptorType> idpMetadata = new IdProjectResource<EntityDescriptorType>(idGen.generateId(),
                idauPath + remoteIdentityProvider.getName() + "/", spChannel.getName(), "saml2",
                generateSPChannelMetadata(appliance, idpProxyBean, localInternalSaml2ServiceProvider, remoteIdentityProvider, signKs, encryptKs));
        idpMetadata.setClassifier("jaxb");

        module.addResource(idpMetadata);


    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
        ExternalSaml2IdentityProvider provider = (ExternalSaml2IdentityProvider) (roleA ? fc.getRoleA() : fc.getRoleB());
        FederatedProvider otherProvider = roleA ? fc.getRoleB() : fc.getRoleA();
        String configName = normalizeBeanName(provider.getName() + "-" + otherProvider.getName() + "-proxy");

        IdProjectModule module = event.getContext().getCurrentModule();
        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");

        Bean idpBean = getBeansOfType(idpBeans, FederatedRemoteProviderImpl.class.getName()).iterator().next();
        Bean idpProxyBean = (Bean) event.getContext().get("idpProxyBean");
        Bean spProxyBean = (Bean) event.getContext().get("spProxyBean");

        // Wire identityProvider to COT
        Collection<Bean> cots = getBeansOfType(baseBeans, CircleOfTrustImpl.class.getName());
        if (cots.size() == 1) {
            Bean cot = cots.iterator().next();
            addPropertyBeansAsRefsToSet(cot, "providers", idpProxyBean);
            addPropertyBeansAsRefsToSet(cot, "providers", spProxyBean);
            String dependsOn = cot.getDependsOn();
            if (dependsOn == null || dependsOn.equals("")) {
                cot.setDependsOn(idpProxyBean.getName() + "," + spProxyBean.getName());
            } else {
                cot.setDependsOn(dependsOn + "," + idpProxyBean.getName() + "," + spProxyBean.getName());
            }

        }

        // Wire session event listener

        Bean idpListener = newBean(idpProxyBeans, idpProxyBean.getName() + "-session-listener", IdPSessionEventListener.class);
        setPropertyRef(idpListener, "identityProvider", idpProxyBean.getName());

        Service idpListenerSvc = new Service();
        idpListenerSvc.setId(idpListener.getName() + "-exporter");
        idpListenerSvc.setRef(idpListener.getName());
        idpListenerSvc.setInterface(SSOSessionEventListener.class.getName());
        idpBeans.getImportsAndAliasAndBeen().add(idpListenerSvc);

        //Collection<Bean> sessionEventManagers = getBeansOfType(baseBeans, SSOSessionEventManager.class.getName());
        //if (sessionEventManagers.size() == 1) {
        //    Bean sessionEventManager = sessionEventManagers.iterator().next();
        //    Bean idpListener = newAnonymousBean(IdPSessionEventListener.class);
        //    setPropertyRef(idpListener, "identityProvider", idpProxyBean.getName());
        //    addPropertyBean(sessionEventManager, "listeners", idpListener);
        //}


        IdProjectResource<Beans> rBeans =  new IdProjectResource<Beans>(idGen.generateId(), idpBean.getName(), configName, "spring-beans", idpProxyBeans);
        rBeans.setClassifier("jaxb");
        rBeans.setNameSpace(idpBean.getName());

        Set<IdProjectResource<Beans>> rBeansSet = (Set<IdProjectResource<Beans>>) event.getContext().get("bean-projects");
        if (rBeansSet == null) {
            rBeansSet = new HashSet<IdProjectResource<Beans>>();
            event.getContext().put("bean-projects", rBeansSet);
        }

        rBeansSet.add(rBeans);
        module.addResource(rBeans);

        return rBeans;
    }

    private EntityDescriptorType generateIDPChannelMetadata(IdentityAppliance appliance,
                                                            Bean spProxyBean,
                                                            InternalSaml2ServiceProvider providerInternalSaml2,
                                                            IdentityProviderChannel idpChannel,
                                                            Keystore signKs,
                                                            Keystore encryptKs,
                                                            String idauPath) throws TransformException {

        SamlR2ProviderConfig cfg = (SamlR2ProviderConfig) providerInternalSaml2.getConfig();

        // Build a location for this channel, we use SP location as base
        Location idpChannelLocation = null;
        {
            Location spLocation = providerInternalSaml2.getLocation();

            idpChannelLocation = new Location();
            idpChannelLocation.setProtocol(spLocation.getProtocol());
            idpChannelLocation.setHost(spLocation.getHost());
            idpChannelLocation.setPort(spLocation.getPort());
            idpChannelLocation.setContext(spLocation.getContext());

            // Don't use channel name since it's the default channel
            idpChannelLocation.setUri(appliance.getName().toUpperCase() + "/" + spProxyBean.getName().toUpperCase());

        }


        EntityDescriptorType entityDescriptor = new EntityDescriptorType();
        // TODO : Take ID from remoteIdentityProvider entityId attribute (To be created)
        entityDescriptor.setID(idGenerator.generateId());

        entityDescriptor.setEntityID(idpChannelLocation.toString() + "/SAML2/MD");

        // SPSSODescriptor
        SPSSODescriptorType spSSODescriptor = new SPSSODescriptorType();
        // TODO : Take ID from remoteIdentityProvider entityId attribute (To be screated)
        spSSODescriptor.setID(idGenerator.generateId());
        spSSODescriptor.getProtocolSupportEnumeration().add(SAMLR2Constants.SAML_PROTOCOL_NS);

        spSSODescriptor.setAuthnRequestsSigned(false);
        spSSODescriptor.setWantAssertionsSigned(false);


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
        spSSODescriptor.getKeyDescriptor().add(signingKeyDescriptor);

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
        } else {
            throw new TransformException("No Encrypter defined for " + providerInternalSaml2.getName());
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
        spSSODescriptor.getKeyDescriptor().add(encryptionKeyDescriptor);

        // services

        // profiles
        Set<Profile> activeProfiles = providerInternalSaml2.getActiveProfiles();
        if (idpChannel != null) {
            activeProfiles = idpChannel.getActiveProfiles();
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
        Set<Binding> activeBindings = providerInternalSaml2.getActiveBindings();
        if (idpChannel != null) {
            activeBindings = idpChannel.getActiveBindings();
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

        // ArtifactResolutionService must alwasy be enabled
        // if (artifactEnabled)
        {
            IndexedEndpointType artifactResolutionService0 = new IndexedEndpointType();
            artifactResolutionService0.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
            artifactResolutionService0.setLocation(idpChannelLocation + "/SAML2/ARTIFACT/SOAP");
            artifactResolutionService0.setIndex(0);
            artifactResolutionService0.setIsDefault(true);
            spSSODescriptor.getArtifactResolutionService().add(artifactResolutionService0);

            IndexedEndpointType artifactResolutionService1 = new IndexedEndpointType();
            artifactResolutionService1.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            artifactResolutionService1.setLocation("local://" + idpChannelLocation.getUri().toUpperCase() + "/SAML2/ARTIFACT/LOCAL");
            artifactResolutionService1.setIndex(1);
            //artifactResolutionService1.setIsDefault(true);
            spSSODescriptor.getArtifactResolutionService().add(artifactResolutionService1);
        }

        // SingleLogoutService

        if (sloEnabled) {
            if (postEnabled) {
                EndpointType singleLogoutServicePost = new EndpointType();
                singleLogoutServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
                singleLogoutServicePost.setLocation(idpChannelLocation + "/SAML2/SLO/POST");
                singleLogoutServicePost.setResponseLocation(idpChannelLocation + "/SAML2/SLO_RESPONSE/POST");
                spSSODescriptor.getSingleLogoutService().add(singleLogoutServicePost);
            }

            if (artifactEnabled) {
                EndpointType singleLogoutServiceArtifact = new EndpointType();
                singleLogoutServiceArtifact.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                singleLogoutServiceArtifact.setLocation(idpChannelLocation + "/SAML2/SLO/ARTIFACT");
                spSSODescriptor.getSingleLogoutService().add(singleLogoutServiceArtifact);
            }

            if (redirectEnabled) {
                EndpointType singleLogoutServiceRedirect = new EndpointType();
                singleLogoutServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                singleLogoutServiceRedirect.setLocation(idpChannelLocation + "/SAML2/SLO/REDIR");
                singleLogoutServiceRedirect.setResponseLocation(idpChannelLocation + "/SAML2/SLO_RESPONSE/REDIR");
                spSSODescriptor.getSingleLogoutService().add(singleLogoutServiceRedirect);
            }

            if (soapEnabled) {
                EndpointType singleLogoutServiceSOAP = new EndpointType();
                singleLogoutServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
                singleLogoutServiceSOAP.setLocation(idpChannelLocation + "/SAML2/SLO/SOAP");
                spSSODescriptor.getSingleLogoutService().add(singleLogoutServiceSOAP);
            }

            EndpointType singleLogoutServiceLocal = new EndpointType();
            singleLogoutServiceLocal.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            singleLogoutServiceLocal.setLocation("local://" + idpChannelLocation.getUri().toUpperCase() + "/SAML2/SLO/LOCAL");
            spSSODescriptor.getSingleLogoutService().add(singleLogoutServiceLocal);
        }

        // ManageNameIDService
        EndpointType manageNameIDServiceSOAP = new EndpointType();
        manageNameIDServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
        manageNameIDServiceSOAP.setLocation(idpChannelLocation + "/SAML2/MNI/SOAP");
        spSSODescriptor.getManageNameIDService().add(manageNameIDServiceSOAP);

        EndpointType manageNameIDServicePost = new EndpointType();
        manageNameIDServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
        manageNameIDServicePost.setLocation(idpChannelLocation + "/SAML2/MNI/POST");
        manageNameIDServicePost.setResponseLocation(idpChannelLocation + "/SAML2/MNI_RESPONSE/POST");
        spSSODescriptor.getManageNameIDService().add(manageNameIDServicePost);

        EndpointType manageNameIDServiceRedirect = new EndpointType();
        manageNameIDServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
        manageNameIDServiceRedirect.setLocation(idpChannelLocation + "/SAML2/MNI/REDIR");
        manageNameIDServiceRedirect.setResponseLocation(idpChannelLocation + "/SAML2/MNI_RESPONSE/REDIR");
        spSSODescriptor.getManageNameIDService().add(manageNameIDServiceRedirect);

        // TODO : Make configurable
        spSSODescriptor.getNameIDFormat().add(NameIDFormat.PERSISTENT.getValue());
        spSSODescriptor.getNameIDFormat().add(NameIDFormat.TRANSIENT.getValue());

        // AssertionConsumerService
        if (ssoEnabled) {
            int index = 0;
            if (artifactEnabled) {
                IndexedEndpointType assertionConsumerService0 = new IndexedEndpointType();
                assertionConsumerService0.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                assertionConsumerService0.setLocation(idpChannelLocation + "/SAML2/ACS/ARTIFACT");
                assertionConsumerService0.setIndex(index++);
                assertionConsumerService0.setIsDefault(true);
                spSSODescriptor.getAssertionConsumerService().add(assertionConsumerService0);
            }

            if (postEnabled) {
                IndexedEndpointType assertionConsumerService1 = new IndexedEndpointType();
                assertionConsumerService1.setBinding(SSOBinding.SAMLR2_POST.getValue());
                assertionConsumerService1.setLocation(idpChannelLocation + "/SAML2/ACS/POST");
                assertionConsumerService1.setIndex(index++);
                //assertionConsumerService1.setIsDefault(false);
                spSSODescriptor.getAssertionConsumerService().add(assertionConsumerService1);
            }

            if (redirectEnabled) {
                IndexedEndpointType assertionConsumerService2 = new IndexedEndpointType();
                assertionConsumerService2.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                assertionConsumerService2.setLocation(idpChannelLocation + "/SAML2/ACS/REDIR");
                assertionConsumerService2.setResponseLocation(idpChannelLocation + "/SAML2/ACS_RESPONSE/REDIR");
                assertionConsumerService2.setIndex(index++);
                //assertionConsumerService1.setIsDefault(false);
                spSSODescriptor.getAssertionConsumerService().add(assertionConsumerService2);
            }
        }

        entityDescriptor.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().add(spSSODescriptor);

        // organization
        OrganizationType organization = new OrganizationType();
        LocalizedNameType organizationName = new LocalizedNameType();
        organizationName.setLang("en");
        organizationName.setValue("Atricore IDBUs SAMLR2 JOSSO SP Sample");
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

    private EntityDescriptorType generateSPChannelMetadata(IdentityAppliance appliance,
                                                           Bean idpProxyBean,
                                                           InternalSaml2ServiceProvider internalSaml2ServiceProvider,
                                                           ExternalSaml2IdentityProvider remoteIdentityProvider,
                                                           Keystore signKs,
                                                           Keystore encryptKs) throws TransformException {

        // Build a location for this channel, we use SP location as base
        Location spChannelLocation = null;
        {
            // Take location from local service remoteIdentityProvider
            Location spLocation = internalSaml2ServiceProvider.getLocation();

            spChannelLocation = new Location();
            spChannelLocation.setProtocol(spLocation.getProtocol());
            spChannelLocation.setHost(spLocation.getHost());
            spChannelLocation.setPort(spLocation.getPort());
            spChannelLocation.setContext(spLocation.getContext());

            // Don't use channel name since it's the default channel
            spChannelLocation.setUri(appliance.getName().toUpperCase() + "/" + idpProxyBean.getName().toUpperCase());

        }


        EntityDescriptorType entityDescriptor = new EntityDescriptorType();
        // TODO : Take ID from identityProvider entityId attribute (To be created)
        entityDescriptor.setID(idGenerator.generateId());

        entityDescriptor.setEntityID(spChannelLocation.toString() + "/SAML2/MD");

        // AttributeAuthorityDescriptor
        AuthnAuthorityDescriptorType attributeAuthorityDescriptor = new AuthnAuthorityDescriptorType();
        // TODO : Take ID from identityProvider entityId attribute (To be created)
        attributeAuthorityDescriptor.setID(idGenerator.generateId());
        attributeAuthorityDescriptor.getProtocolSupportEnumeration().add("urn:oasis:names:tc:SAML:2.0:protocol");

        // authority signing key descriptor
        KeyDescriptorType authoritySigningKeyDescriptor = new KeyDescriptorType();
        authoritySigningKeyDescriptor.setUse(KeyTypes.SIGNING);
        KeyInfoType authoritySigningKeyInfo = new KeyInfoType();
        X509DataType authoritySigningX509Data = new X509DataType();
        String authoritySigningCertificate = ""; // TODO

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
            throw new TransformException("No Signer defined for " + remoteIdentityProvider.getName());
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
            throw new TransformException("No Encrypter defined for " + remoteIdentityProvider.getName());
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
        assertionIDRequestServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
        assertionIDRequestServiceSOAP.setLocation(resolveLocationBaseUrl(identityProvider) + "/nidp/saml2/soap");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceSOAP);

        EndpointType assertionIDRequestServiceURI = new EndpointType();
        assertionIDRequestServiceURI.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:URI");
        assertionIDRequestServiceURI.setLocation(resolveLocationBaseUrl(identityProvider) + "/nidp/saml2/assertion");
        attributeAuthorityDescriptor.getAssertionIDRequestService().add(assertionIDRequestServiceURI);
        */

        entityDescriptor.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().add(attributeAuthorityDescriptor);

        // IDPSSODescriptor
        IDPSSODescriptorType idpSSODescriptor = new IDPSSODescriptorType();
        idpSSODescriptor.setID("idSy31Pds0meYpkaDLFG6-eWqL0WA");
        idpSSODescriptor.getProtocolSupportEnumeration().add(SAMLR2Constants.SAML_PROTOCOL_NS);
        idpSSODescriptor.setWantAuthnRequestsSigned(false);

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

        /* TODO : Take this from externa IDP Metadata ?!
        Set<Profile> activeProfiles = remoteIdentityProvider.getActiveProfiles();
        if (spChannel != null) {
            activeProfiles = spChannel.getActiveProfiles();
        }
        boolean ssoEnabled = false;
        boolean sloEnabled = false;
        for (Profile profile : activeProfiles) {
            if (profile.equals(Profile.SSO)) {
                ssoEnabled = true;
            } else if (profile.equals(Profile.SSO_SLO)) {
                sloEnabled = true;
            }
        } */
        boolean ssoEnabled = true;
        boolean sloEnabled = true;

        /*
        // bindings
        Set<Binding> activeBindings = remoteIdentityProvider.getActiveBindings();
        if (spChannel != null) {
            activeBindings = spChannel.getActiveBindings();
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
        } */
        boolean postEnabled = true;
        boolean redirectEnabled = true;
        boolean artifactEnabled = true;
        boolean soapEnabled = true;

        // ArtifactResolutionService must always be enabled just in case other providers support this binding
        // if (artifactEnabled)
        {
            IndexedEndpointType artifactResolutionService = new IndexedEndpointType();
            artifactResolutionService.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
            artifactResolutionService.setLocation(spChannelLocation + "/SAML2/ARTIFACT/SOAP");
            artifactResolutionService.setIndex(0);
            artifactResolutionService.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService);

            IndexedEndpointType artifactResolutionServiceLocal = new IndexedEndpointType();
            artifactResolutionServiceLocal.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            artifactResolutionServiceLocal.setLocation("local://" + spChannelLocation.getUri().toUpperCase()  + "/SAML2/ARTIFACT/LOCAL");
            artifactResolutionServiceLocal.setIndex(1);
            artifactResolutionServiceLocal.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionServiceLocal);

            IndexedEndpointType artifactResolutionService11 = new IndexedEndpointType();
            artifactResolutionService11.setBinding(SSOBinding.SAMLR11_SOAP.getValue());
            artifactResolutionService11.setLocation(spChannelLocation + "/SAML11/ARTIFACT/SOAP");
            artifactResolutionService11.setIndex(0);
            artifactResolutionService11.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService11);
        }

        // SingleLogoutService

        if (sloEnabled) {
            if (postEnabled) {
                EndpointType singleLogoutServicePost = new EndpointType();
                singleLogoutServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
                singleLogoutServicePost.setLocation(spChannelLocation + "/SAML2/SLO/POST");
                singleLogoutServicePost.setResponseLocation(spChannelLocation + "/SAML2/SLO_RESPONSE/POST");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServicePost);
            }

            if (artifactEnabled) {
                EndpointType singleLogoutServiceArtifact = new EndpointType();
                singleLogoutServiceArtifact.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                singleLogoutServiceArtifact.setLocation(spChannelLocation + "/SAML2/SLO/ARTIFACT");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceArtifact);
            }

            if (redirectEnabled) {
                EndpointType singleLogoutServiceRedirect = new EndpointType();
                singleLogoutServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                singleLogoutServiceRedirect.setLocation(spChannelLocation + "/SAML2/SLO/REDIR");
                singleLogoutServiceRedirect.setResponseLocation(spChannelLocation + "/SAML2/SLO_RESPONSE/REDIR");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceRedirect);
            }

            if (soapEnabled) {
                EndpointType singleLogoutServiceSOAP = new EndpointType();
                singleLogoutServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
                singleLogoutServiceSOAP.setLocation(spChannelLocation + "/SAML2/SLO/SOAP");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceSOAP);
            }

            EndpointType singleLogoutServiceLocal = new EndpointType();
            singleLogoutServiceLocal.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            singleLogoutServiceLocal.setLocation("local://" + spChannelLocation.getUri().toUpperCase() + "/SAML2/SLO/LOCAL");
            idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceLocal);
        }

        // ManageNameIDService
        EndpointType manageNameIDServiceSOAP = new EndpointType();
        manageNameIDServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
        manageNameIDServiceSOAP.setLocation(spChannelLocation + "/SAML2/MNI/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceSOAP);

        EndpointType manageNameIDServicePost = new EndpointType();
        manageNameIDServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
        manageNameIDServicePost.setLocation(spChannelLocation + "/SAML2/RNI");
        manageNameIDServicePost.setResponseLocation(spChannelLocation + "/SAML2/MNI_RESPONSE/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServicePost);

        EndpointType manageNameIDServiceRedirect = new EndpointType();
        manageNameIDServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
        manageNameIDServiceRedirect.setLocation(spChannelLocation + "/SAML2/RNI/REDIR");
        manageNameIDServiceRedirect.setResponseLocation(spChannelLocation + "/SAML2/MNI_RESPONSE/REDIR");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceRedirect);

        // TODO : Make configurable
        idpSSODescriptor.getNameIDFormat().add(NameIDFormat.PERSISTENT.getValue());
        idpSSODescriptor.getNameIDFormat().add(NameIDFormat.TRANSIENT.getValue());

        // SingleSignOnService
        if (ssoEnabled) {
            if (postEnabled) {
                EndpointType singleSignOnServicePost = new EndpointType();
                singleSignOnServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
                singleSignOnServicePost.setLocation(spChannelLocation + "/SAML2/SSO/POST");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServicePost);
            }

            if (redirectEnabled) {
                EndpointType singleSignOnServiceRedirect = new EndpointType();
                singleSignOnServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                singleSignOnServiceRedirect.setLocation(spChannelLocation + "/SAML2/SSO/REDIR");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceRedirect);
            }

            if (artifactEnabled) {
                EndpointType singleSignOnServiceArtifact = new EndpointType();
                singleSignOnServiceArtifact.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                singleSignOnServiceArtifact.setLocation(spChannelLocation + "/SAML2/SSO/ARTIFACT");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceArtifact);
            }
        }

        entityDescriptor.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().add(idpSSODescriptor);

        // organization
        OrganizationType organization = new OrganizationType();
        LocalizedNameType organizationName = new LocalizedNameType();
        organizationName.setLang("en");
        // TODO : Take this from appliance/system setup/license
        organizationName.setValue("Atricore JOSSO 2 IDP");
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
