package com.atricore.idbus.console.lifecycle.main.transform.transformers.domino;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isDominoIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isOAuth2IdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * Transformer for issuing Domino Tokens from the IdP
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class DominoSTSTransformer extends AbstractTransformer implements InitializingBean {

    private static final Log logger = LogFactory.getLog(DominoSTSTransformer.class);

    public void afterPropertiesSet() throws Exception {

    }

    /**
     * Only enhance the IdP if there is a Domino resource defined for it
     *
     * @param event
     * @return
     */
    @Override
    public boolean accept(TransformEvent event) {
        boolean accept = false;
        if (event.getData() instanceof IdentityProvider && !((IdentityProvider)event.getData()).isRemote()) {

            IdentityProvider idp = (IdentityProvider) event.getData();

            for (FederatedConnection fc : idp.getFederatedConnectionsA()) {
                if (fc.getRoleB() instanceof InternalSaml2ServiceProvider) {
                    InternalSaml2ServiceProvider sp = (InternalSaml2ServiceProvider) fc.getRoleB();

                    if (sp.getServiceConnection().getResource() instanceof DominoResource)
                        accept = true;
                }
            }

            for (FederatedConnection fc : idp.getFederatedConnectionsB()) {
                if (fc.getRoleB() instanceof InternalSaml2ServiceProvider) {
                    InternalSaml2ServiceProvider sp = (InternalSaml2ServiceProvider) fc.getRoleB();

                    if (sp.getServiceConnection().getResource() instanceof DominoResource)
                        accept = true;
                }
            }

            if (accept)
                if (logger.isTraceEnabled())
                    logger.trace("Required Domino STS components for local IdP " + ((IdentityProvider)event.getData()).getName());

            return accept;
        }

        if (event.getData() instanceof ServiceProviderChannel) {
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            boolean proxy = isDominoIdPProxyRequired(fc);

            if (proxy)
                if (logger.isTraceEnabled())
                    logger.trace("Required Domino STS components for proxied IdP between " + fc.getRoleA().getName() + ":" + fc.getRoleB().getName());

            return proxy;
        }

        return false;

    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        boolean isProxy = false;

        FederatedProvider idp = null;
        FederatedProvider sp = null;
        DominoResource resource = null;

        if (event.getData() instanceof FederatedProvider) {
            idp = (FederatedProvider) event.getData();
            isProxy = false;

            if (logger.isTraceEnabled())
                logger.trace("Creating Domino STS components for local IdP " + ((IdentityProvider)event.getData()).getName());

        } else if (isDominoIdPProxyRequired((FederatedConnection) event.getContext().getParentNode())) {
            // Since this is a proxy, it must be an internal SAML 2.0 SP
            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();

            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (logger.isTraceEnabled())
                logger.trace("Creating Domino STS components for proxied IdP between " + fc.getRoleA().getName() + ":" + fc.getRoleB().getName());

            isProxy = true;

            if (fc.getRoleA() instanceof ExternalSaml2IdentityProvider && fc.getRoleA().isRemote()) {
                idp = fc.getRoleA();
                sp = fc.getRoleB();
                resource = (DominoResource) ((InternalSaml2ServiceProvider)fc.getRoleB()).getServiceConnection().getResource();
            } else if (fc.getRoleB() instanceof ExternalSaml2IdentityProvider && fc.getRoleB().isRemote()) {
                resource = (DominoResource) ((InternalSaml2ServiceProvider)fc.getRoleA()).getServiceConnection().getResource();
                idp = fc.getRoleB();
                sp = fc.getRoleA();
            } else {
                // ERROR !
                logger.error("External IdP not supported in federated connection " + fc.getName() +
                        ", available providers A: " + fc.getRoleA().getName() + ", B:" + fc.getRoleB().getName());
                throw new TransformException("External IdP type not found in federated connection " + fc.getName());
            }
        } else {
            logger.error("Accepted invalid node : " + event.getData());
        }

        Beans idpBeans = isProxy ? (Beans) event.getContext().get("idpProxyBeans") : (Beans) event.getContext().get("idpBeans");

        if (logger.isDebugEnabled())
            logger.debug("Enhancing IdP's STS " + idp.getName() + " for Domino Support");

        // Take Idp beans from context, previous transformer created them.

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        String idauPath = (String) event.getContext().get("idauPath");

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        // ----------------------------------------
        // Get IDP Bean
        // ----------------------------------------
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // TODO : Support multiple Domino servers/resources ?!  (it may require an authentication mechanism definition/selection)
        for (FederatedConnection fc : idp.getFederatedConnectionsB()) {
            if (fc.getRoleA() instanceof InternalSaml2ServiceProvider) {
                InternalSaml2ServiceProvider localSp = (InternalSaml2ServiceProvider) fc.getRoleA();
                if (localSp.getServiceConnection().getResource() instanceof DominoResource) {

                    DominoResource dominoResource = (DominoResource) localSp.getServiceConnection().getResource();

                    if (logger.isDebugEnabled())
                        logger.debug("Defining IDP components for resource " + dominoResource.getName());

                    // ----------------------------------------
                    // STS, must be already created
                    // ----------------------------------------
                    Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");

                    // ----------------------------------------
                    // Emitters
                    // ----------------------------------------
                    Bean dominoStsEmitter = newBean(idpBeans,
                            dominoResource.getName() + "-domino-accesstoken-emitter",
                            "org.atricore.idbus.capabilities.domino.main.LTPASecurityTokenEmitter");
                    setPropertyValue(dominoStsEmitter, "id", dominoStsEmitter.getName());

                    // domino server
                    setPropertyValue(dominoStsEmitter, "dominoServer", dominoResource.getServerUrl());

                    // identityPlanRegistry
                    setPropertyRef(dominoStsEmitter, "identityPlanRegistry", "identity-plans-registry");

                    Bean idMgr = getBean(idpBeans, idpBean.getName() + "-identity-manager");
                    // Inject identity Manager TODO : When using proxy, take identity manager from SP !
                    if (idMgr != null) {
                        setPropertyRef(dominoStsEmitter, "identityManager", idMgr.getName()); // Should we use the bean name ?!
                    } else if (isProxy) {
                        // Link with SP Identity manager, unfortunately we can't get spBeans here to make sure that it exists
                        setPropertyRef(dominoStsEmitter, "identityManager", sp.getName() + "-identity-manager");
                    }

                    insertPropertyBean(sts, "emitters", dominoStsEmitter);

                }
            }
        }

        for (FederatedConnection fc : idp.getFederatedConnectionsA()) {
            if (fc.getRoleB() instanceof InternalSaml2ServiceProvider) {
                InternalSaml2ServiceProvider localSp = (InternalSaml2ServiceProvider) fc.getRoleB();
                if (localSp.getServiceConnection().getResource() instanceof DominoResource) {

                    DominoResource dominoResource = (DominoResource) localSp.getServiceConnection().getResource();

                    if (logger.isDebugEnabled())
                        logger.debug("Defining IDP components for resource " + dominoResource.getName());

                    // ----------------------------------------
                    // STS, must be already created
                    // ----------------------------------------
                    Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");

                    // ----------------------------------------
                    // Emitters
                    // ----------------------------------------
                    Bean dominoStsEmitter = newBean(idpBeans,
                            dominoResource.getName() + "-domino-accesstoken-emitter",
                            "org.atricore.idbus.capabilities.domino.main.LTPASecurityTokenEmitter");
                    setPropertyValue(dominoStsEmitter, "id", dominoStsEmitter.getName());

                    // domino server
                    setPropertyValue(dominoStsEmitter, "dominoServer", dominoResource.getServerUrl());

                    // identityPlanRegistry
                    setPropertyRef(dominoStsEmitter, "identityPlanRegistry", "identity-plans-registry");

                    Bean idMgr = getBean(idpBeans, idpBean.getName() + "-identity-manager");

                    // Inject identity Manager TODO : When using proxy, take identity manager from SP !
                    if (idMgr != null) {
                        setPropertyRef(dominoStsEmitter, "identityManager", sp.getName() + "-identity-manager");
                    } else if (isProxy) {
                        // Link with SP Identity manager, unfortunately we can't get spBeans here to make sure that it exists
                        setPropertyRef(dominoStsEmitter, "identityManager", sp.getName() + "-identity-manager");
                    }

                    insertPropertyBean(sts, "emitters", dominoStsEmitter);
                }
            }
        }


    }

}
