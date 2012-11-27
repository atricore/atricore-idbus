package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.SSOIdentityManagerImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * TODO : Split into LDAP, DB, EMBEDDED, etc TRANSFOMER ...
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityLookupTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdentityLookupTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // add methods in TransformerVisitor for IdentitySource?
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
                logger.trace("Generating Beans for Identity Source " + identitySource.getName()  + ", provider " + provider.getName());

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

            // identityManager
            Bean identityManager = newBean(providerBeans, providerBean.getName() + "-identity-manager", SSOIdentityManagerImpl.class);
            setPropertyRef(identityManager, "identityStore", providerBean.getName() + "-identity-store");
            setPropertyBean(identityManager, "identityStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));

            // identity store (TODO : Move to specific transformers)
            if (identitySource instanceof DbIdentitySource) {
                Bean identityStore = null;
                // DB
                DbIdentitySource dbSource = (DbIdentitySource) identitySource;
                identityStore = newBean(providerBeans, providerBean.getName() + "-identity-store", "org.atricore.idbus.idojos.dbidentitystore.DynamicJDBCIdentityStore");

                setPropertyRef(identityStore, "manager", "jdbc-manager");
                
                setPropertyValue(identityStore, "driverName", dbSource.getDriverName());
                setPropertyValue(identityStore, "connectionURL", dbSource.getConnectionUrl());
                setPropertyValue(identityStore, "connectionUser", dbSource.getAdmin());
                setPropertyValue(identityStore, "connectionPassword", dbSource.getPassword());

                setPropertyValue(identityStore, "userQueryString", dbSource.getUserQueryString());
                setPropertyValue(identityStore, "rolesQueryString", dbSource.getRolesQueryString());
                setPropertyValue(identityStore, "credentialsQueryString", dbSource.getCredentialsQueryString());
                setPropertyValue(identityStore, "userPropertiesQueryString", dbSource.getUserPropertiesQueryString());
                setPropertyValue(identityStore, "resetCredentialDml", dbSource.getResetCredentialDml());
                setPropertyValue(identityStore, "relayCredentialQueryString", dbSource.getRelayCredentialQueryString());

            } else if (identitySource instanceof LdapIdentitySource) {
                Bean identityStore = null;
                // LDAP
                LdapIdentitySource ldapSource = (LdapIdentitySource) identitySource;
                identityStore = newBean(providerBeans, providerBean.getName() + "-identity-store", "org.atricore.idbus.idojos.ldapidentitystore.LDAPBindIdentityStore");
                setPropertyValue(identityStore, "initialContextFactory", ldapSource.getInitialContextFactory());
                setPropertyValue(identityStore, "providerUrl", ldapSource.getProviderUrl());
                setPropertyValue(identityStore, "securityPrincipal", ldapSource.getSecurityPrincipal());
                setPropertyValue(identityStore, "securityCredential", ldapSource.getSecurityCredential());
                setPropertyValue(identityStore, "securityAuthentication", ldapSource.getSecurityAuthentication());
                setPropertyValue(identityStore, "ldapSearchScope", ldapSource.getLdapSearchScope());
                setPropertyValue(identityStore, "usersCtxDN", ldapSource.getUsersCtxDN());
                setPropertyValue(identityStore, "principalUidAttributeID", ldapSource.getPrincipalUidAttributeID());
                setPropertyValue(identityStore, "roleMatchingMode", ldapSource.getRoleMatchingMode());
                setPropertyValue(identityStore, "uidAttributeID", ldapSource.getUidAttributeID());
                setPropertyValue(identityStore, "rolesCtxDN", ldapSource.getRolesCtxDN());
                setPropertyValue(identityStore, "roleAttributeID", ldapSource.getRoleAttributeID());
                setPropertyValue(identityStore, "credentialQueryString", ldapSource.getCredentialQueryString());
                setPropertyValue(identityStore, "updateableCredentialAttribute", ldapSource.getUpdateableCredentialAttribute());
                setPropertyValue(identityStore, "userPropertiesQueryString", ldapSource.getUserPropertiesQueryString());
            } else if (identitySource instanceof EmbeddedIdentitySource) {

                EmbeddedIdentitySource embeddedSource = (EmbeddedIdentitySource) identitySource;
                // TODO : For now only default PSP is supported : String pspName = embeddedSource.getPsp();

                Reference identityStoreOsgi = new Reference();
                identityStoreOsgi.setId(providerBean.getName() + "-identity-store");
                identityStoreOsgi.setInterface(AbstractStore.class.getName());
                identityStoreOsgi.setCardinality("1..1");
                identityStoreOsgi.setTimeout(60L);

                providerBeans.getImportsAndAliasAndBeen().add(identityStoreOsgi);

            } else if (identitySource instanceof XmlIdentitySource) {
                throw new UnsupportedOperationException("XML Identity Source support not implemented !!!");
            }
        } else {
            throw new TransformException("No IdentitySource defined for " + idLookup.getName());
        }
    }
}
