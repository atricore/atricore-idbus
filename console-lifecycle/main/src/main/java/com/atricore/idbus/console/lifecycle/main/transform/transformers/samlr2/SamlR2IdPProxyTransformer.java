package com.atricore.idbus.console.lifecycle.main.transform.transformers.samlr2;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractSPChannelTransformer;
import com.atricore.idbus.console.lifecycle.main.util.MetadataUtil;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Description;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Entry;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.capabilities.sso.main.binding.logging.SSOLogMessageBuilder;
import org.atricore.idbus.capabilities.sso.main.binding.logging.SamlR2LogMessageBuilder;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSessionEventListener;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.main.sp.SamlR2SPMediator;
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
import org.atricore.idbus.kernel.main.session.SSOSessionEventManager;
import org.atricore.idbus.kernel.main.store.SSOIdentityManagerImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;
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

    @Override
    public boolean accept(TransformEvent event) {

        if (event.getData() instanceof ServiceProviderChannel) {

            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (roleA) {
                return fc.getRoleA() instanceof Saml2IdentityProvider && fc.getRoleA().isRemote();
                // TODO : Change this once the front-end supports it
                /*
                return spChannel.isOverrideProviderSetup() && fc.getRoleA() instanceof Saml2IdentityProvider
                        && fc.getRoleA().isRemote();
                        */
            } else {
                return fc.getRoleB() instanceof Saml2IdentityProvider && fc.getRoleB().isRemote();
                // TODO : Change this once the front-end supports it
                /*
                return spChannel.isOverrideProviderSetup() && fc.getRoleB() instanceof Saml2IdentityProvider
                        && fc.getRoleB().isRemote();
                        */
            }

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

        Saml2IdentityProvider identityProvider = (Saml2IdentityProvider) (roleA ? fc.getRoleA() : fc.getRoleB());
        ServiceProvider serviceProvider = (ServiceProvider) (roleA ? fc.getRoleB() : fc.getRoleA());

        Description descr = new Description();
        descr.getContent().add(identityProvider.getName() + " : IdP Proxy Configuration ("+identityProvider.getName()+"/"+serviceProvider.getName()+") generated by Atricore Identity Bus Server on " + now.toGMTString());
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
        createProxyIdPSide(event, identityProvider, serviceProvider, baseBeans, idpProxyBeans, signKs, encryptKs, idauPath);

        // Create Internal SP facing remote IDP
        createProxySpSide(event, serviceProvider, identityProvider, baseBeans, idpProxyBeans, signKs, encryptKs, idauPath);

    }

    /**
     * Creates an internal SP that will face the remote IdP
     */
    protected void createProxySpSide(TransformEvent event,
                                     ServiceProvider provider, Saml2IdentityProvider otherProvider,
                                     Beans baseBeans, Beans idpProxyBeans,
                                     Keystore signKs,
                                     Keystore encryptKs,
                                     String idauPath) throws TransformException {

        //-------------------------------
        // Service Provider
        // ----------------------------------------

        Bean sp = newBean(idpProxyBeans, normalizeBeanName(provider.getName()), ServiceProviderImpl.class.getName());

        // Name
        setPropertyValue(sp, "name", sp.getName());

        // Role
        if (!provider.getRole().equals(ProviderRole.SSOServiceProvider)) {
            logger.warn("Provider "+provider.getId()+" is not defined as SP, forcing role! ");
        }
        setPropertyValue(sp, "role", SSOMetadataConstants.SPSSODescriptor_QNAME.toString());

        // unitContainer
        setPropertyRef(sp, "unitContainer", provider.getIdentityAppliance().getName() + "-container");

        // COT Manager
        Collection<Bean> cotMgrs = getBeansOfType(baseBeans, CircleOfTrustManagerImpl.class.getName());
        if (cotMgrs.size() == 1) {
            Bean cotMgr = cotMgrs.iterator().next();
            setPropertyRef(sp, "cotManager", cotMgr.getName());
        }

        // State Manager
        setPropertyRef(sp, "stateManager", provider.getIdentityAppliance().getName() + "-state-manager");

        // ----------------------------------------
        // Service Provider Mediator
        // ----------------------------------------
        Bean spMediator = newBean(idpProxyBeans, sp.getName() + "-samlr2-mediator",
                SamlR2SPMediator.class.getName());

        /* TODO : How to se preferred IDP
        Collection<Bean> idpMds = getBeansOfType(idpBeans, ResourceCircleOfTrustMemberDescriptorImpl.class.getName());
        Bean idpMd = idpMds.iterator().next();


        */

        MetadataDefinition providerMd;
        try {
            providerMd = MetadataUtil.loadMetadataDefinition(otherProvider.getMetadata().getValue());
            String alias = MetadataUtil.findEntityId(providerMd);
            setPropertyValue(spMediator, "preferredIdpAlias", alias);
        } catch (Exception e) {
            throw new TransformException(e);
        }

        //setPropertyValue(spMediator, "preferredIdpSSOBinding", SSOBinding.SAMLR2_POST.getValue());
        //setPropertyValue(spMediator, "preferredIdpSLOBinding", SSOBinding.SAMLR2_POST.getValue());
        setPropertyValue(spMediator, "preferredIdpSSOBinding", SSOBinding.SAMLR2_POST.getValue());
        setPropertyValue(spMediator, "preferredIdpSLOBinding", SSOBinding.SAMLR2_POST.getValue());

        // TODO : [JOSSO-370] This might be null somewhere on the chain
        ExecutionEnvironment execEnv = provider.getServiceConnection().getResource().getActivation().getExecutionEnv();

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();
        IdentityApplianceDefinition applianceDef = provider.getIdentityAppliance();

        String bpLocationPath = resolveLocationPath(applianceDef.getLocation()) + "/" + execEnv.getName().toUpperCase();
        String bpLocation = resolveLocationBaseUrl(applianceDef.getLocation()) + bpLocationPath;

        setPropertyValue(spMediator, "spBindingACS", bpLocation + "/SSO/ACS/ARTIFACT");
        setPropertyValue(spMediator, "spBindingSLO", bpLocation + "/SSO/SLO/ARTIFACT");

        setPropertyValue(spMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(spMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
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
        spLogger.setName(sp.getName() + "-mediation-logger");
        setPropertyValue(spLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire." + sp.getName());
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
                    idauPath + sp.getName() + "/", signerResourceFileName,
                    "binary", signKs.getStore().getValue());
            signerResource.setClassifier("byte");

            Bean signer = newBean(idpProxyBeans, sp.getName() + "-samlr2-signer", JSR105SamlR2SignerImpl.class);
            signer.setInitMethod("init");

            Description signerDescr = new Description();
            signerDescr.getContent().add(signKs.getDisplayName());
            signer.setDescription(signerDescr);

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", signKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + sp.getName() + "/" + signerResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", signKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", signKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", signKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", signKs.getCertificateAlias());

            setPropertyBean(signer, "keyResolver", keyResolver);
            setPropertyBean(spMediator, "signer", signer);

            event.getContext().getCurrentModule().addResource(signerResource);

            // signer
            setPropertyRef(spMediator, "signer", signer.getName());

            setPropertyValue(spMediator, "signRequests", provider.isSignRequests());
            setPropertyValue(spMediator, "validateRequestsSignature", provider.isWantSignedRequests());

        } else {
            throw new TransformException("No Signer defined for " + sp.getName());
        }

        // ----------------------------------------
        // Encrypter
        // ----------------------------------------
        if (encryptKs != null) {

            String encrypterResourceFileName = encryptKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(encryptKs.getType()) ? "pkcs12" : "jks");

            IdProjectResource<byte[]> encrypterResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + sp.getName() + "/", encrypterResourceFileName,
                    "binary", encryptKs.getStore().getValue());
            encrypterResource.setClassifier("byte");

            Bean encrypter = newBean(idpProxyBeans, sp.getName() + "-samlr2-encrypter", XmlSecurityEncrypterImpl.class);

            setPropertyValue(encrypter, "symmetricKeyAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
            setPropertyValue(encrypter, "kekAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#rsa-1_5");

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", encryptKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + sp.getName() + "/" + encrypterResourceFileName);
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
            throw new TransformException("No Encrypter defined for " + sp.getName());
        }

        // accountLinkLifecycle
        Bean accountLinkLifecycle = newBean(idpProxyBeans, sp.getName() + "-account-link-lifecycle", AccountLinkLifecycleImpl.class);
        if (provider.getIdentityLookup() != null) {
            setPropertyRef(accountLinkLifecycle, "identityStore", sp.getName() + "-identity-store");
        }

        // ----------------------------------------
        // MBean
        // ----------------------------------------
        Bean mBean = newBean(idpProxyBeans, sp.getName() + "-mbean", "org.atricore.idbus.capabilities.sso.management.internal.ServiceProviderMBeanImpl");
        setPropertyRef(mBean, "serviceProvider", sp.getName());

        Bean mBeanExporter = newBean(idpProxyBeans, sp.getName() + "-mbean-exporter", "org.springframework.jmx.export.MBeanExporter");
        setPropertyRef(mBeanExporter, "server", "mBeanServer");

        // mbeans
        List<Entry> mBeans = new ArrayList<Entry>();

        Bean mBeanKey = newBean(idpProxyBeans, mBean.getName() + "-key", String.class);
        setConstructorArg(mBeanKey, 0, "java.lang.String", appliance.getNamespace() +  "." +
                event.getContext().getCurrentModule().getId() +
                ":type=ServiceProvider,name=" + applianceDef.getName() + "." + sp.getName());

        Entry mBeanEntry = new Entry();
        mBeanEntry.setKeyRef(mBeanKey.getName());
        mBeanEntry.setValueRef(mBean.getName());
        mBeans.add(mBeanEntry);

        setPropertyAsMapEntries(mBeanExporter, "beans", mBeans);

        // -------------------------------------------------------
        // Define Session Manager bean
        // -------------------------------------------------------
        Bean sessionManager = newBean(idpProxyBeans, sp.getName() + "-session-manager",
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
        setPropertyRef(sessionStore, "cacheManager", provider.getIdentityAppliance().getName() + "-cache-manager");
        setPropertyValue(sessionStore, "cacheName", provider.getIdentityAppliance().getName() +
                "-" + sp.getName() + "-sessionsCache");

        // Wiring
        setPropertyBean(sessionManager, "sessionIdGenerator", sessionIdGenerator);
        setPropertyBean(sessionManager, "sessionStore", sessionStore);

    }


    /**
     * Creates an internal IdP that will face the local SP
     */
    protected void createProxyIdPSide(TransformEvent event,
                                      Saml2IdentityProvider provider, FederatedProvider otherProvider,
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

        // Get remote MD descriptor for validation, parse : provider.getMetadata().getValue()

        Bean idpProxyBean = newBean(idpProxyBeans, normalizeBeanName(provider.getName() + "-" + otherProvider.getName() + "-idp-proxy"), IdentityProviderImpl.class);
        event.getContext().put("idpProxyBean", idpProxyBean);

        // Take location from otherProvider :
        {
            Location otherLocation = otherProvider.getLocation();

            Location location = new Location();
            location.setProtocol(otherLocation.getProtocol());
            location.setHost(otherLocation.getHost());
            location.setPort(otherLocation.getPort());
            location.setContext(otherLocation.getContext());

            location.setUri(appliance.getName().toUpperCase() + "/" + idpProxyBean.getName().toUpperCase());

            provider.setLocation(location);
            logger.warn("Location for " + idpProxyBean.getName() + ", forcing ["+location+"]");
        }

        ServiceProviderChannel fChannel = (ServiceProviderChannel) (roleA ? fc.getChannelA() : fc.getChannelB());
        {
            Location otherLocation = otherProvider.getLocation();

            Location location = new Location();
            location.setProtocol(otherLocation.getProtocol());
            location.setHost(otherLocation.getHost());
            location.setPort(otherLocation.getPort());
            location.setContext(otherLocation.getContext());

            location.setUri(appliance.getName().toUpperCase() + "/" + idpProxyBean.getName().toUpperCase() + "/" + fChannel.getName().toUpperCase());

            fChannel.setLocation(location);
            logger.warn("Focation for channel " + fChannel.getName() + ", forcing ["+location+"]");
        }

        // TODO : Takes this from console cfg !
        fChannel.getActiveProfiles().clear();
        fChannel.getActiveProfiles().add(Profile.SSO);
        fChannel.getActiveProfiles().add(Profile.SSO_SLO);

        // TODO : Takes this from console cfg !
        fChannel.getActiveBindings().clear();
        fChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        //fChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_REDIRECT);
        fChannel.getActiveBindings().add(Binding.SAMLR2_ARTIFACT);
        fChannel.getActiveBindings().add(Binding.SAMLR2_SOAP);

        if (logger.isDebugEnabled())
            logger.debug("Generating IDP Proxy " + idpProxyBean.getName() + " configuration model");

        // Name
        setPropertyValue(idpProxyBean, "name", idpProxyBean.getName());
        setPropertyValue(idpProxyBean, "description", provider.getDisplayName() + " (Proxy to "+otherProvider.getName()+")");

        // Role, set to IDP
        setPropertyValue(idpProxyBean, "role", SSOMetadataConstants.IDPSSODescriptor_QNAME.toString());

        // unitContainer
        setPropertyRef(idpProxyBean, "unitContainer", provider.getIdentityAppliance().getName() + "-container");

        // COT Manager
        Collection<Bean> cotMgrs = getBeansOfType(baseBeans, CircleOfTrustManagerImpl.class.getName());
        if (cotMgrs.size() == 1) {
            Bean cotMgr = cotMgrs.iterator().next();
            setPropertyRef(idpProxyBean, "cotManager", cotMgr.getName());
        } else if (cotMgrs.size() > 1) {
            throw new TransformException("Invalid number of COT Managers defined " + cotMgrs.size());
        }

        // State Manager
        setPropertyRef(idpProxyBean, "stateManager", provider.getIdentityAppliance().getName() + "-state-manager");

        // ----------------------------------------
        // Identity Provider Mediator
        // ----------------------------------------
        Bean idpMediator = newBean(idpProxyBeans, idpProxyBean.getName() + "-samlr2-mediator",
                SSOIDPMediator.class.getName());
        setPropertyValue(idpMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(idpMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(idpMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(idpMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

        // logger
        List<Bean> idpLogBuilders = new ArrayList<Bean>();
        idpLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(SSOLogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean idpLogger = newAnonymousBean(DefaultMediationLogger.class.getName());
        idpLogger.setName(idpProxyBean.getName() + "-mediation-logger");
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

            // Use provider name in path, to keep all resources together
            IdProjectResource<byte[]> signerResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + provider.getName() + "/", signerResourceFileName,
                    "binary", signKs.getStore().getValue());
            signerResource.setClassifier("byte");

            Bean signer = newBean(idpProxyBeans, idpProxyBean.getName() + "-samlr2-signer", JSR105SamlR2SignerImpl.class);
            signer.setInitMethod("init");

            Description signerDescr = new Description();
            signerDescr.getContent().add(signKs.getDisplayName());
            signer.setDescription(signerDescr);

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", signKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + provider.getName() + "/" + signerResourceFileName);
            setPropertyValue(keyResolver, "keystorePass", signKs.getPassword());
            setPropertyValue(keyResolver, "privateKeyAlias", signKs.getPrivateKeyName());
            setPropertyValue(keyResolver, "privateKeyPass", signKs.getPrivateKeyPassword());
            setPropertyValue(keyResolver, "certificateAlias", signKs.getCertificateAlias());

            setPropertyBean(signer, "keyResolver", keyResolver);
            setPropertyBean(idpMediator, "signer", signer);

            // TODO : Maybe we need to get this from external IDP Metadata, or from FC channel setup !?
            //setPropertyValue(idpMediator, "signRequests", provider.isSignRequests());
            //setPropertyValue(idpMediator, "validateRequestsSignature", provider.isWantSignedRequests());
            setPropertyValue(idpMediator, "signRequests", true);
            setPropertyValue(idpMediator, "validateRequestsSignature", false);

            event.getContext().getCurrentModule().addResource(signerResource);

            // signer
            setPropertyRef(idpMediator, "signer", signer.getName());
        } else {
            throw new TransformException("No Signer defined for " + provider.getName());
        }

        // ----------------------------------------
        // Encrypter
        // ----------------------------------------
        if (encryptKs != null) {

            String encrypterResourceFileName = encryptKs.getStore().getName() + "." +
                    ("PKCS#12".equalsIgnoreCase(encryptKs.getType()) ? "pkcs12" : "jks");

            IdProjectResource<byte[]> encrypterResource = new IdProjectResource<byte[]>(idGen.generateId(),
                    idauPath + provider.getName() + "/", encrypterResourceFileName,
                    "binary", encryptKs.getStore().getValue());
            encrypterResource.setClassifier("byte");

            Bean encrypter = newBean(idpProxyBeans, idpProxyBean.getName() + "-samlr2-encrypter", XmlSecurityEncrypterImpl.class);

            setPropertyValue(encrypter, "symmetricKeyAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
            setPropertyValue(encrypter, "kekAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#rsa-1_5");

            Bean keyResolver = newAnonymousBean(SSOKeystoreKeyResolver.class);
            setPropertyValue(keyResolver, "keystoreType", encryptKs.getType());
            setPropertyValue(keyResolver, "keystoreFile", "classpath:" + idauPath + provider.getName() + "/" + encrypterResourceFileName);
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
            throw new TransformException("No Encrypter defined for " + provider.getName());
        }

        // ----------------------------------------
        // MBean
        // ----------------------------------------
        // TODO : Use proxy specific bean types
        Bean mBean = newBean(idpProxyBeans, idpProxyBean.getName() + "-mbean", "org.atricore.idbus.capabilities.sso.management.internal.IdentityProviderMBeanImpl");
        setPropertyRef(mBean, "identityProvider", idpProxyBean.getName());

        Bean mBeanExporter = newBean(idpProxyBeans, idpProxyBean.getName() + "-mbean-exporter", "org.springframework.jmx.export.MBeanExporter");
        setPropertyRef(mBeanExporter, "server", "mBeanServer");

        // mbeans
        List<Entry> mBeans = new ArrayList<Entry>();

        Bean mBeanKey = newBean(idpProxyBeans, mBean.getName() + "-key", String.class);
        setConstructorArg(mBeanKey, 0, "java.lang.String", appliance.getNamespace() + "." +
                event.getContext().getCurrentModule().getId() +
                ":type=IdentityProvider,name=" + provider.getIdentityAppliance().getName() + "." + idpProxyBean.getName());

        Entry mBeanEntry = new Entry();
        mBeanEntry.setKeyRef(mBeanKey.getName());
        mBeanEntry.setValueRef(mBean.getName());
        mBeans.add(mBeanEntry);

        setPropertyAsMapEntries(mBeanExporter, "beans", mBeans);

        // -------------------------------------------------------
        // Session Manager bean
        // -------------------------------------------------------
        Bean sessionManager = newBean(idpProxyBeans, idpProxyBean.getName() + "-session-manager",
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
        //Bean sessionStore = newAnonymousBean("org.atricore.idbus.idojos.memorysessionstore.MemorySessionStore");
        Bean sessionStore = newAnonymousBean("org.atricore.idbus.idojos.ehcachesessionstore.EHCacheSessionStore");
        sessionStore.setInitMethod("init");
        setPropertyRef(sessionStore, "cacheManager", provider.getIdentityAppliance().getName() + "-cache-manager");
        setPropertyValue(sessionStore, "cacheName", provider.getIdentityAppliance().getName() +
                "-" + idpProxyBean.getName() + "-sessionsCache");

        // Wiring
        setPropertyBean(sessionManager, "sessionIdGenerator", sessionIdGenerator);
        setPropertyBean(sessionManager, "sessionStore", sessionStore);

        // If no identity manager was defined yet, create a new one based on memory

        // TODO : Support local stores ?!
        Bean identityManager = newBean(idpProxyBeans, idpProxyBean.getName() + "-identity-manager", SSOIdentityManagerImpl.class);
        setPropertyRef(identityManager, "identityStore", idpProxyBean.getName() + "-identity-store");
        setPropertyBean(identityManager, "identityStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));

        Bean identityStore = newBean(idpProxyBeans, idpProxyBean.getName() + "-identity-store", "org.atricore.idbus.idojos.memoryidentitystore.MemoryIdentityStore");



        // generate metadata for default channel
        IdProjectResource<EntityDescriptorType> idpMetadata = new IdProjectResource<EntityDescriptorType>(idGen.generateId(),
                idauPath + provider.getName() + "/", fChannel.getName(), "saml2", generateSPChannelMetadata(provider, fChannel, signKs, encryptKs));
        idpMetadata.setClassifier("jaxb");

        module.addResource(idpMetadata);


        // Now, create the SP that will face the actual Idp



    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
        Saml2IdentityProvider provider = (Saml2IdentityProvider) (roleA ? fc.getRoleA() : fc.getRoleB());
        FederatedProvider otherProvider = roleA ? fc.getRoleB() : fc.getRoleA();
        String configName = normalizeBeanName(provider.getName() + "-" + otherProvider.getName() + "-proxy");

        IdProjectModule module = event.getContext().getCurrentModule();
        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");

        Bean idpBean = getBeansOfType(idpBeans, FederatedRemoteProviderImpl.class.getName()).iterator().next();
        Bean idpProxyBean = getBeansOfType(idpProxyBeans, IdentityProviderImpl.class.getName()).iterator().next();

        // Wire provider to COT
        Collection<Bean> cots = getBeansOfType(baseBeans, CircleOfTrustImpl.class.getName());
        if (cots.size() == 1) {
            Bean cot = cots.iterator().next();
            addPropertyBeansAsRefsToSet(cot, "providers", idpProxyBean);
            String dependsOn = cot.getDependsOn();
            if (dependsOn == null || dependsOn.equals("")) {
                cot.setDependsOn(idpProxyBean.getName());
            } else {
                cot.setDependsOn(dependsOn + "," + idpProxyBean.getName());
            }
        }

        // Wire session event listener
        Collection<Bean> sessionEventManagers = getBeansOfType(baseBeans, SSOSessionEventManager.class.getName());
        if (sessionEventManagers.size() == 1) {
            Bean sessionEventManager = sessionEventManagers.iterator().next();
            Bean idpListener = newAnonymousBean(IdPSessionEventListener.class);
            setPropertyRef(idpListener, "identityProvider", idpProxyBean.getName());
            addPropertyBean(sessionEventManager, "listeners", idpListener);
        }


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

    private EntityDescriptorType generateSPChannelMetadata(Saml2IdentityProvider provider,
                                                           ServiceProviderChannel spChannel,
                                                           Keystore signKs,
                                                           Keystore encryptKs) throws TransformException {


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
        assertionIDRequestServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
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
        idpSSODescriptor.getProtocolSupportEnumeration().add(SAMLR2Constants.SAML_PROTOCOL_NS);
        if (spChannel != null)
            idpSSODescriptor.setWantAuthnRequestsSigned(spChannel.isWantAuthnRequestsSigned());
        else
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
        Set<Profile> activeProfiles = provider.getActiveProfiles();
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
        }

        // bindings
        Set<Binding> activeBindings = provider.getActiveBindings();
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
        }

        // ArtifactResolutionService must always be enabled just in case other providers support this binding
        // if (artifactEnabled)
        {
            IndexedEndpointType artifactResolutionService = new IndexedEndpointType();
            artifactResolutionService.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
            artifactResolutionService.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/ARTIFACT/SOAP");
            artifactResolutionService.setIndex(0);
            artifactResolutionService.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService);

            IndexedEndpointType artifactResolutionServiceLocal = new IndexedEndpointType();
            artifactResolutionServiceLocal.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            artifactResolutionServiceLocal.setLocation("local://" + (spChannel != null ?
                    spChannel.getLocation().getUri().toUpperCase() : provider.getLocation().getUri().toUpperCase()) + "/SAML2/ARTIFACT/LOCAL");
            artifactResolutionServiceLocal.setIndex(1);
            artifactResolutionServiceLocal.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionServiceLocal);

            IndexedEndpointType artifactResolutionService11 = new IndexedEndpointType();
            artifactResolutionService11.setBinding(SSOBinding.SAMLR11_SOAP.getValue());
            artifactResolutionService11.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML11/ARTIFACT/SOAP");
            artifactResolutionService11.setIndex(0);
            artifactResolutionService11.setIsDefault(true);
            idpSSODescriptor.getArtifactResolutionService().add(artifactResolutionService11);
        }

        // SingleLogoutService

        if (sloEnabled) {
            if (postEnabled) {
                EndpointType singleLogoutServicePost = new EndpointType();
                singleLogoutServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
                singleLogoutServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/POST");
                singleLogoutServicePost.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO_RESPONSE/POST");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServicePost);
            }

            if (artifactEnabled) {
                EndpointType singleLogoutServiceArtifact = new EndpointType();
                singleLogoutServiceArtifact.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                singleLogoutServiceArtifact.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/ARTIFACT");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceArtifact);
            }

            if (redirectEnabled) {
                EndpointType singleLogoutServiceRedirect = new EndpointType();
                singleLogoutServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                singleLogoutServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/REDIR");
                singleLogoutServiceRedirect.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO_RESPONSE/REDIR");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceRedirect);
            }

            if (soapEnabled) {
                EndpointType singleLogoutServiceSOAP = new EndpointType();
                singleLogoutServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
                singleLogoutServiceSOAP.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SLO/SOAP");
                idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceSOAP);
            }

            EndpointType singleLogoutServiceLocal = new EndpointType();
            singleLogoutServiceLocal.setBinding(SSOBinding.SAMLR2_LOCAL.getValue());
            singleLogoutServiceLocal.setLocation("local://" + (spChannel != null ?
                    spChannel.getLocation().getUri().toUpperCase() : provider.getLocation().getUri().toUpperCase()) + "/SAML2/SLO/LOCAL");
            idpSSODescriptor.getSingleLogoutService().add(singleLogoutServiceLocal);
        }

        // ManageNameIDService
        EndpointType manageNameIDServiceSOAP = new EndpointType();
        manageNameIDServiceSOAP.setBinding(SSOBinding.SAMLR2_SOAP.getValue());
        manageNameIDServiceSOAP.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceSOAP);

        EndpointType manageNameIDServicePost = new EndpointType();
        manageNameIDServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
        manageNameIDServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/RNI");
        manageNameIDServicePost.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI_RESPONSE/SOAP");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServicePost);

        EndpointType manageNameIDServiceRedirect = new EndpointType();
        manageNameIDServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
        manageNameIDServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/RNI/REDIR");
        manageNameIDServiceRedirect.setResponseLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/MNI_RESPONSE/REDIR");
        idpSSODescriptor.getManageNameIDService().add(manageNameIDServiceRedirect);

        // TODO : Make configurable
        idpSSODescriptor.getNameIDFormat().add(NameIDFormat.PERSISTENT.getValue());
        idpSSODescriptor.getNameIDFormat().add(NameIDFormat.TRANSIENT.getValue());

        // SingleSignOnService
        if (ssoEnabled) {
            if (postEnabled) {
                EndpointType singleSignOnServicePost = new EndpointType();
                singleSignOnServicePost.setBinding(SSOBinding.SAMLR2_POST.getValue());
                singleSignOnServicePost.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/POST");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServicePost);
            }

            if (redirectEnabled) {
                EndpointType singleSignOnServiceRedirect = new EndpointType();
                singleSignOnServiceRedirect.setBinding(SSOBinding.SAMLR2_REDIRECT.getValue());
                singleSignOnServiceRedirect.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/REDIR");
                idpSSODescriptor.getSingleSignOnService().add(singleSignOnServiceRedirect);
            }

            if (artifactEnabled) {
                EndpointType singleSignOnServiceArtifact = new EndpointType();
                singleSignOnServiceArtifact.setBinding(SSOBinding.SAMLR2_ARTIFACT.getValue());
                singleSignOnServiceArtifact.setLocation(resolveLocationUrl(provider, spChannel) + "/SAML2/SSO/ARTIFACT");
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
