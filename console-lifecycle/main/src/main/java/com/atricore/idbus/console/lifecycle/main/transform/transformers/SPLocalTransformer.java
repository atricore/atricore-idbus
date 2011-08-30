package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.util.MetadataUtil;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Description;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.capabilities.samlr2.main.binding.logging.SSOLogMessageBuilder;
import org.atricore.idbus.capabilities.samlr2.main.binding.logging.SamlR2LogMessageBuilder;
import org.atricore.idbus.capabilities.samlr2.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.samlr2.main.binding.plans.SamlR2ArtifactToSamlR2ArtifactResolvePlan;
import org.atricore.idbus.capabilities.samlr2.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeystoreKeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.XmlSecurityEncrypterImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.JSR105SamlR2SignerImpl;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.federation.AccountLinkLifecycleImpl;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustImpl;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManagerImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannelImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPLocalTransformer extends AbstractTransformer implements InitializingBean {
    
    private static final Log logger = LogFactory.getLog(SPLocalTransformer.class);

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
        return event.getData() instanceof ServiceProvider &&
                !((ServiceProvider)event.getData()).isRemote();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        ServiceProvider provider = (ServiceProvider) event.getData();

        FederatedProvider preferredIdp = null;
        ServiceProviderChannel preferredSpChannel = null;
        for (FederatedConnection fc : provider.getFederatedConnectionsA()) {
            IdentityProviderChannel idpc = (IdentityProviderChannel) fc.getChannelA();
            if (idpc.isPreferred()) {
                preferredIdp = (FederatedProvider) fc.getRoleB();
                preferredSpChannel = (ServiceProviderChannel) fc.getChannelB();
                break;
            }
        }

        if (preferredIdp == null) {
            for (FederatedConnection fc : provider.getFederatedConnectionsB()) {
                IdentityProviderChannel idpc = (IdentityProviderChannel) fc.getChannelB();
                if (idpc.isPreferred()) {
                    preferredIdp = (FederatedProvider) fc.getRoleA();
                    preferredSpChannel = (ServiceProviderChannel) fc.getChannelA();
                    break;
                }
            }
        }

        if (preferredIdp == null) {
            logger.warn("No preferred IDP could be found for SP " + provider.getName());
        }


        Date now = new Date();

        Beans spBeans = new Beans();

        Description descr = new Description();
        descr.getContent().add(provider.getName() + " : SP Configuration generated by Atricore Identity Bus Server on " + now.toGMTString());
        descr.getContent().add(provider.getDescription());

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");
        String idauPath = (String) event.getContext().get("idauPath");

        // TODO : Can we asure that there is only one IdP and that it's the prefered one ? This should be part of SP definition
        // Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        spBeans.setDescription(descr);

        // Publish root element so that other transformers can use it.
        event.getContext().put("spBeans", spBeans);

        if (logger.isDebugEnabled())
            logger.debug("Generating SP " + provider.getName() + " configuration model");
        
        // Define all required beans! (We can break down this in the future ...)

        // ----------------------------------------
        // Service Provider
        // ----------------------------------------

        Bean sp = newBean(spBeans, normalizeBeanName(provider.getName()),
                ServiceProviderImpl.class.getName());

        // Name
        setPropertyValue(sp, "name", sp.getName());

        // Role
        if (!provider.getRole().equals(ProviderRole.SSOServiceProvider)) {
            logger.warn("Provider "+provider.getId()+" is not defined as SP, forcing role! ");
        }
        setPropertyValue(sp, "role", SAMLR2MetadataConstants.SPSSODescriptor_QNAME.toString());

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
        Bean spMediator = newBean(spBeans, sp.getName() + "-samlr2-mediator",
                SamlR2SPMediator.class.getName());

        /* TODO : How to se preferred IDP
        Collection<Bean> idpMds = getBeansOfType(idpBeans, ResourceCircleOfTrustMemberDescriptorImpl.class.getName());
        Bean idpMd = idpMds.iterator().next();


        */

        if (preferredIdp != null) {
            if (preferredIdp instanceof IdentityProvider) {
                setPropertyValue(spMediator, "preferredIdpAlias", resolveLocationUrl(preferredIdp, preferredSpChannel) + "/SAML2/MD");
            } else if (preferredIdp instanceof ExternalIdentityProvider) {
                try {
                    MetadataDefinition md = MetadataUtil.loadMetadataDefinition(preferredIdp.getMetadata().getValue());
                    setPropertyValue(spMediator, "preferredIdpAlias", MetadataUtil.findEntityId(md));
                } catch (Exception e) {
                    throw new TransformException("Error loading metadata definition for " + preferredIdp.getName());
                }
            }
        }


        //setPropertyValue(spMediator, "preferredIdpSSOBinding", SamlR2Binding.SAMLR2_POST.getValue());
        //setPropertyValue(spMediator, "preferredIdpSLOBinding", SamlR2Binding.SAMLR2_POST.getValue());
        setPropertyValue(spMediator, "preferredIdpSSOBinding", SamlR2Binding.SAMLR2_ARTIFACT.getValue());
        setPropertyValue(spMediator, "preferredIdpSLOBinding", SamlR2Binding.SAMLR2_ARTIFACT.getValue());

        ExecutionEnvironment execEnv = provider.getActivation().getExecutionEnv();

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();
        IdentityApplianceDefinition applianceDef = provider.getIdentityAppliance();

        String bpLocationPath = resolveLocationPath(applianceDef.getLocation()) + "/" + execEnv.getName().toUpperCase();
        String bpLocation = resolveLocationBaseUrl(applianceDef.getLocation()) + bpLocationPath;

        setPropertyValue(spMediator, "spBindingACS", bpLocation + "/SSO/ACS/ARTIFACT");
        setPropertyValue(spMediator, "spBindingSLO", bpLocation + "/SSO/SLO/ARTIFACT");
        
        setPropertyValue(spMediator, "logMessages", true);

        // artifactQueueManager
        setPropertyRef(spMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");

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
        setPropertyValue(spMediator, "errorUrl", resolveLocationBaseUrl(provider.getIdentityAppliance().getLocation()) + "/idbus-ui/error.do");

        // warningUrl
        setPropertyValue(spMediator, "warningUrl", resolveLocationBaseUrl(provider.getIdentityAppliance().getLocation()) + "/idbus-ui/warn/policy-enforcement.do");


        SamlR2ProviderConfig cfg = (SamlR2ProviderConfig) provider.getConfig();

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

            Bean signer = newBean(spBeans, sp.getName() + "-samlr2-signer", JSR105SamlR2SignerImpl.class);
            signer.setInitMethod("init");

            Description signerDescr = new Description();
            signerDescr.getContent().add(signKs.getDisplayName());
            signer.setDescription(signerDescr);

            Bean keyResolver = newAnonymousBean(SamlR2KeystoreKeyResolver.class);
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

            Bean encrypter = newBean(spBeans, sp.getName() + "-samlr2-encrypter", XmlSecurityEncrypterImpl.class);

            setPropertyValue(encrypter, "symmetricKeyAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
            setPropertyValue(encrypter, "kekAlgorithmURI", "http://www.w3.org/2001/04/xmlenc#rsa-1_5");

            Bean keyResolver = newAnonymousBean(SamlR2KeystoreKeyResolver.class);
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
        Bean accountLinkLifecycle = newBean(spBeans, sp.getName() + "-account-link-lifecycle", AccountLinkLifecycleImpl.class);
        if (provider.getIdentityLookup() != null) {
            setPropertyRef(accountLinkLifecycle, "identityStore", sp.getName() + "-identity-store");
        }

        // ----------------------------------------
        // MBean
        // ----------------------------------------
        Bean mBean = newBean(spBeans, sp.getName() + "-mbean", "org.atricore.idbus.capabilities.samlr2.management.internal.ServiceProviderMBeanImpl");
        setPropertyRef(mBean, "serviceProvider", sp.getName());

        Bean mBeanExporter = newBean(spBeans, sp.getName() + "-mbean-exporter", "org.springframework.jmx.export.MBeanExporter");
        setPropertyRef(mBeanExporter, "server", "mBeanServer");

        // mbeans
        List<Entry> mBeans = new ArrayList<Entry>();
        
        Bean mBeanKey = newBean(spBeans, mBean.getName() + "-key", String.class);
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
        Bean sessionManager = newBean(spBeans, sp.getName() + "-session-manager",
                "org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl");

        // Properties
        setPropertyValue(sessionManager, "maxInactiveInterval", "30");
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

    @Override
    public Object after(TransformEvent event) throws TransformException {

        ServiceProvider provider = (ServiceProvider) event.getData();
        IdProjectModule module = event.getContext().getCurrentModule();
        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans spBeans = (Beans) event.getContext().get("spBeans");
        Beans bpBeans = (Beans) event.getContext().get("bpBeans");
        
        Bean spBean = getBeansOfType(spBeans, ServiceProviderImpl.class.getName()).iterator().next();

        // Wire IdP Channels DONE IN FED.CONN. TRANSF.
        /*
        List<Bean> bc = new ArrayList<Bean>();
        Collection<Bean> channels = getBeansOfType(spBeans, IdPChannelImpl.class.getName());
        for (Bean b : channels) {
            String channelProvider = getPropertyRef(b, "provider");
            if (channelProvider != null && !channelProvider.equals(spBean.getName())) {
                bc.add(b);
            } else {
                setPropertyRef(spBean, "channel", b.getName());
            }
        }

        if (bc.size() > 0)
            setPropertyAsRefs(spBean, "channels", bc);
            */

        Bean idMgr = getBean(spBeans, spBean.getName() + "-identity-manager");
        if (idMgr != null) {
            Collection<Bean> channels = getBeansOfType(spBeans, IdPChannelImpl.class.getName());
            for (Bean b : channels) {
                setPropertyRef(b, "identityManager", idMgr.getName());
            }

        }

        // Wire provider to COT
        Collection<Bean> cots = getBeansOfType(baseBeans, CircleOfTrustImpl.class.getName());
        if (cots.size() == 1) {
            Bean cot = cots.iterator().next();
            addPropertyBeansAsRefsToSet(cot, "providers", spBean);
            String dependsOn = cot.getDependsOn();
            if (dependsOn == null || dependsOn.equals("")) {
                cot.setDependsOn(spBean.getName());
            } else {
                cot.setDependsOn(dependsOn + "," + spBean.getName());
            }
        }

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(baseBeans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            Collection<Bean> bindingChannels = getBeansOfType(spBeans, BindingChannelImpl.class.getName());
            for (Bean b : bindingChannels) {
                addPropertyBeansAsRefs(mu, "channels", b);
            }
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

        IdProjectResource<Beans> rBeans =  new IdProjectResource<Beans>(idGen.generateId(), spBean.getName(), spBean.getName(), "spring-beans", spBeans);
        rBeans.setClassifier("jaxb");
        rBeans.setNameSpace(spBean.getName());

        module.addResource(rBeans);

        return rBeans;
    }

    public Keystore getSampleKeystore() {
        return sampleKeystore;
    }

    public void setSampleKeystore(Keystore sampleKeystore) {
        this.sampleKeystore = sampleKeystore;
    }
}
