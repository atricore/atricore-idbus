package com.atricore.idbus.console.lifecycle.main.transform.transformers.domino;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.sso.STSTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.AESTokenEncrypter;
import org.atricore.idbus.capabilities.oauth2.common.HMACTokenSigner;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2AccessTokenEmitter;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DominoSTSTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(STSTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // Only work for Local IdPs with Domino support enabled
        return (event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote() &&
                ((IdentityProvider)event.getData()).isDominoEnabled());

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {


        FederatedProvider provider = null;
        if (event.getData() instanceof FederatedProvider) {
            provider = (FederatedProvider) event.getData();
        }

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider localIdp = null;
        if (provider instanceof IdentityProvider) {
            localIdp = (IdentityProvider) provider;
        }

        if (logger.isTraceEnabled())
            logger.trace("Generating Domino STS Beans for IdP " + provider.getName());


        // ----------------------------------------
        // Get IDP Bean
        // ----------------------------------------
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // ----------------------------------------
        // STS, must be already created
        // ----------------------------------------
        Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");

        // -------------------------------------------------------
        // Domino Authentication service must be already created
        // -------------------------------------------------------
        Bean dasBean;
        Collection<Bean> dasBeans = getBeansOfType(idpBeans, "org.atricore.idbus.capabilities.domino.main.Domino7AuthenticationScheme");
        if (dasBeans.size() != 1) {
            throw new TransformException("No Domino Authentication Service Defined : " + b.size());
        }
        dasBean = b.iterator().next();
        String dominoServer = getPropertyValue(dasBean, "dominoServer");

        // ----------------------------------------
        // Emitters
        // ----------------------------------------
        Bean dominoStsEmitter = newBean(idpBeans,
                dasBean.getName() + "-domino-accesstoken-emitter",
                "org.atricore.idbus.capabilities.domino.main.LTPASecurityTokenEmitter");
        setPropertyValue(dominoStsEmitter, "id", dominoStsEmitter.getName());

        // domino server
        setPropertyRef(dominoStsEmitter, "dominoServer", dominoServer);

        // identityPlanRegistry
        setPropertyRef(dominoStsEmitter, "identityPlanRegistry", "identity-plans-registry");

        Bean idMgr = getBean(idpBeans, dasBean.getName() + "-identity-manager");

        // Inject identity Manager
        if (idMgr != null) {
            setPropertyRef(dominoStsEmitter, "identityManager", dasBean.getName() + "-identity-manager");
        }

        // Add emitter to STS : the emitter MUST be the first in the list (or run before SAML2) // TODO : Mange dependencies ?
        insertPropertyBean(sts, "emitters", dominoStsEmitter);

        return null;
    }

}
