package com.atricore.idbus.console.lifecycle.main.transform.transformers.idsource;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.store.SSOIdentityManagerImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.ArrayList;
import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * TODO : Split into LDAP, DB, EMBEDDED, etc TRANSFORMER ...
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityLookupTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdentityLookupTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityLookup &&
            event.getContext().getParentNode() instanceof Provider;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        //Channel channel = (Channel) event.getData();
        IdentityLookup idLookup = (IdentityLookup) event.getData();
        IdentitySource identitySource = idLookup.getIdentitySource();
        Provider provider = idLookup.getProvider();

        if (provider != event.getContext().getParentNode()) {
            throw new TransformException("Lookup service provider is not parent node for " + idLookup.getName());
        }

        if (identitySource != null) {

            String idauPath = (String) event.getContext().get("idauPath");

            if (logger.isTraceEnabled())
                logger.trace("Generating Beans for Identity Lookup " + identitySource.getName()  + ", provider " + provider.getName());

            Beans providerBeans = null;
            Bean providerBean = null;
            Collection<Bean> b = null;

            if (provider instanceof InternalSaml2ServiceProvider) {
                providerBeans = (Beans) event.getContext().get("spBeans");
                b = getBeansOfType(providerBeans, ServiceProviderImpl.class.getName());
            } else if (provider instanceof IdentityProvider) {
                providerBeans = (Beans) event.getContext().get("idpBeans");
                b = getBeansOfType(providerBeans, IdentityProviderImpl.class.getName());

            }                           

            if (b == null || b.size() != 1) {
                throw new TransformException("Invalid provider definition count : " + (b != null ? b.size() : "<null>"));
            }
            providerBean = b.iterator().next();

            String storeName = normalizeBeanName(identitySource.getName() + "-identity-store");
            String virtualStoreName = normalizeBeanName(providerBean.getName() + "-identity-store");

            Bean visb = null;
            if (provider.getIdentityLookups().size() > 1) {

                // Multiple stores detected
                Collection<Bean> virtualIdentityStore = getBeansOfType(providerBeans, "org.atricore.idbus.idojos.virtualidentitystore.VirtualIdentityStore");

                // Virtual store not defined yet, create it
                if (virtualIdentityStore.isEmpty()) {
                    visb = newBean(providerBeans, virtualStoreName, "org.atricore.idbus.idojos.virtualidentitystore.VirtualIdentityStore");

                    // property used to store embedded identity sources
                    setPropertyAsBeans(visb, "identitySources", new ArrayList<Bean>());

                    // data mapping definition
                    Bean mappingPolicy = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.RuleBasedIdentityDataMappingPolicy");
                    setPropertyBean(visb, "identityDataMappingPolicy", mappingPolicy);

                    Bean selectAllUsersRule = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.SelectAllUsers");
                    addPropertyBean(mappingPolicy, "userMappingRules", selectAllUsersRule);

                    Bean mergePropertiesRule = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.MergeProperties");
                    addPropertyBean(mappingPolicy, "userMappingRules", mergePropertiesRule);

                    Bean selectAllRoles = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.SelectAllRoles");
                    addPropertyBean(mappingPolicy, "roleMappingRules", selectAllRoles);

                    Bean mergeRoles = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.MergeRoles");
                    addPropertyBean(mappingPolicy, "roleMappingRules", mergeRoles);

                    Bean selectAllCredentials = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.SelectAllCredentials");
                    addPropertyBean(mappingPolicy, "credentialMappingRules", selectAllCredentials);

                    Bean mergeCredentials = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.MergeCredentials");
                    addPropertyBean(mappingPolicy, "credentialMappingRules", mergeCredentials);

                    Bean userExistsMappingRule = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.rule.UserExistsOnAnySource");
                    addPropertyBean(mappingPolicy, "userExistsMappingRules", userExistsMappingRule);
                } else {
                  visb = virtualIdentityStore.iterator().next();
                }


                Bean isrc = newAnonymousBean("org.atricore.idbus.idojos.virtualidentitystore.IdentitySourceImpl");
                setPropertyValue(isrc, "alias", normalizeBeanName(providerBean.getName() + "-" + storeName + "-identity-source"));
                setPropertyRef(isrc, "backingIdentityStore", storeName);
                // Add source to VIS
                addPropertyBean(visb, "identitySources", isrc);

            }

            // Identity Manager
            if (getBeansOfType(providerBeans, SSOIdentityManagerImpl.class.getName()).size() < 1 ) {
                // identityManager
                Bean identityManager = newBean(providerBeans, providerBean.getName() + "-identity-manager", SSOIdentityManagerImpl.class);
                setPropertyRef(identityManager, "identityStore", visb != null ? virtualStoreName : storeName);
                setPropertyBean(identityManager, "identityStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));
            }

            if (identitySource instanceof IdentityVault) {
                // We have provisioning support with vaults
                setPropertyRef(providerBean, "provisioningTarget", normalizeBeanName(identitySource.getName()) + "-pst");
            }


        } else {
            throw new TransformException("No IdentitySource defined for " + idLookup.getName());
        }
    }
}
