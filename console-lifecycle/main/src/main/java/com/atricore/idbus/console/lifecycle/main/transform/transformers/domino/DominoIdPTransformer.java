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
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Client;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2BindingFactory;
import org.atricore.idbus.capabilities.oauth2.main.binding.logging.OAuth2LogMessageBuilder;
import org.atricore.idbus.capabilities.oauth2.main.util.JasonUtils;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * Transformer for issuing Domino Tokens from the IdP
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class DominoIdPTransformer extends AbstractTransformer implements InitializingBean {

    private static final Log logger = LogFactory.getLog(DominoIdPTransformer.class);

    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote();
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {


        IdentityProvider provider = (IdentityProvider) event.getData();

        // Take Idp beans from context, previous transformer created them.
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        String idauPath = (String) event.getContext().get("idauPath");

        // Publish root element so that other transformers can use it.
        event.getContext().put("idpBeans", idpBeans);

        if (logger.isDebugEnabled())
            logger.debug("Enhancing IDP " + provider.getName() + " for Domino Support");

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        // ----------------------------------------
        // Identity Provider
        // ----------------------------------------
        Bean idpBean = getBean(idpBeans, normalizeBeanName(provider.getName()));

        for (FederatedConnection fc : provider.getFederatedConnectionsB()) {
            if (fc.getRoleA() instanceof InternalSaml2ServiceProvider) {
                InternalSaml2ServiceProvider localSp = (InternalSaml2ServiceProvider) fc.getRoleA();
                if (localSp.getServiceConnection().getResource() instanceof DominoResource) {

                    DominoResource dominoResource = (DominoResource) localSp.getServiceConnection().getResource();

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

                    // Inject identity Manager
                    if (idMgr != null) {
                        setPropertyRef(dominoStsEmitter, "identityManager", dominoResource.getName() + "-identity-manager");
                    }

                    insertPropertyBean(sts, "emitters", dominoStsEmitter);

                }
            }
        }

        for (FederatedConnection fc : provider.getFederatedConnectionsA()) {
            if (fc.getRoleB() instanceof InternalSaml2ServiceProvider) {
                InternalSaml2ServiceProvider localSp = (InternalSaml2ServiceProvider) fc.getRoleB();
                if (localSp.getServiceConnection().getResource() instanceof DominoResource) {

                    DominoResource dominoResource = (DominoResource) localSp.getServiceConnection().getResource();

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

                    // Inject identity Manager
                    if (idMgr != null) {
                        setPropertyRef(dominoStsEmitter, "identityManager", idpBean.getName() + "-identity-manager");
                    }

                    insertPropertyBean(sts, "emitters", dominoStsEmitter);
                }
            }
        }

        return null;

    }

}
