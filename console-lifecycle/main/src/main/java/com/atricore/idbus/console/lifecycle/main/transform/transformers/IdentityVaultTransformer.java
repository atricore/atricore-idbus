package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.atricore.idbus.kernel.main.authn.scheme.BindUsernamePasswordAuthScheme;
import org.atricore.idbus.kernel.main.authn.scheme.UsernamePasswordAuthScheme;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.store.SSOIdentityManagerImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyRef;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityVaultTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdentityVaultTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // add methods in TransformerVisitor for IdentitySource?
        //return (event.getData() instanceof IdentitySource &&
        //        event.getContext().getParentNode() instanceof Channel);

        return (event.getData() instanceof ServiceProviderChannel ||
                event.getData() instanceof IdentityProviderChannel);
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Channel channel = (Channel) event.getData();
        //IdentitySource identitySource = (IdentitySource) event.getData();
        //Channel channel = (Channel) event.getContext().getParentNode();
        Provider provider = null; // TODO RETROFIT : channel.getTarget();

        IdentitySource identitySource = null;
        if (channel instanceof IdentityProviderChannel) {
            // TODO RETROFIT : identitySource = ((IdentityProviderChannel) channel).getIdentityLookup();
        } else if (channel instanceof ServiceProviderChannel) {
            // TODO RETROFIT : identitySource = ((ServiceProviderChannel) channel).getIdentityLookup();
        }

        if (identitySource != null) {
            String baseSamlDestPath = (String) event.getContext().get("baseSamlDestPath");

            if (logger.isTraceEnabled())
                logger.trace("Generating Beans for Identity Vault " + identitySource.getName()  + " of provider " + provider.getName());

            Beans providerBeans = null;
            Bean providerBean = null;
            Collection<Bean> b = null;
            if (channel instanceof IdentityProviderChannel) {
                providerBeans = (Beans) event.getContext().get("spBeans");
                b = getBeansOfType(providerBeans, ServiceProviderImpl.class.getName());
            } else if (channel instanceof ServiceProviderChannel) {
                providerBeans = (Beans) event.getContext().get("idpBeans");
                b = getBeansOfType(providerBeans, IdentityProviderImpl.class.getName());
            }
            if (b.size() != 1) {
                throw new TransformException("Invalid provider definition count : " + b.size());
            }
            providerBean = b.iterator().next();

            // auth scheme
            if (channel instanceof ServiceProviderChannel) {
                // bind auth scheme
                if (identitySource instanceof LdapIdentitySource) {
                    Bean basicAuthn = newBean(providerBeans, "basic-authentication", BindUsernamePasswordAuthScheme.class);
                    setPropertyValue(basicAuthn, "name", basicAuthn.getName());
                    setPropertyRef(basicAuthn, "credentialStore", providerBean.getName() + "-identity-store");
                    setPropertyBean(basicAuthn, "credentialStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));
                } else {  // username-password auth scheme
                    Bean basicAuthn = newBean(providerBeans, "basic-authentication", UsernamePasswordAuthScheme.class);
                    setPropertyValue(basicAuthn, "name", basicAuthn.getName());
                    setPropertyValue(basicAuthn, "hashAlgorithm", "MD5");
                    setPropertyValue(basicAuthn, "hashEncoding", "HEX");
                    setPropertyValue(basicAuthn, "ignorePasswordCase", false);
                    setPropertyValue(basicAuthn, "ignoreUserCase", false);
                    setPropertyRef(basicAuthn, "credentialStore", providerBean.getName() + "-identity-store");
                    setPropertyBean(basicAuthn, "credentialStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));
                }
            }
            
            // identityManager
            Bean identityManager = newBean(providerBeans, providerBean.getName() + "-identity-manager", SSOIdentityManagerImpl.class);
            setPropertyRef(identityManager, "identityStore", providerBean.getName() + "-identity-store");
            setPropertyBean(identityManager, "identityStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));

            // identity store
            Bean identityStore = null;

            if (identitySource instanceof DbIdentitySource) {
                // DB
                DbIdentitySource dbIdentityVault = (DbIdentitySource) identitySource;
                identityStore = newBean(providerBeans, providerBean.getName() + "-identity-store", "org.atricore.idbus.idojos.dbidentitystore.JDBCIdentityStore");
                if (dbIdentityVault.isEmbedded()) {
                    setPropertyValue(identityStore, "driverName", "org.apache.derby.jdbc.ClientDriver");
                    setPropertyValue(identityStore, "connectionURL", "jdbc:derby://localhost:" + dbIdentityVault.getPort() + "/" + dbIdentityVault.getSchema() + ";create=false");
                    setPropertyValue(identityStore, "connectionName", dbIdentityVault.getAdmin());
                    setPropertyValue(identityStore, "connectionPassword", dbIdentityVault.getPassword());
                    setPropertyValue(identityStore, "userQueryString", "SELECT USERNAME AS NAME FROM \"IB_USER\" WHERE USERNAME = ?");
                    setPropertyValue(identityStore, "rolesQueryString", "SELECT G.NAME FROM \"IB_USER\" U JOIN \"IB_USERGROUPS\" UG ON U.id = UG.user_id JOIN \"IB_GROUP\" G ON UG.GROUP_ID = G.id WHERE U.USERNAME = ?");
                    setPropertyValue(identityStore, "credentialsQueryString", "SELECT USERNAME, USERPASSWORD AS PASSWORD FROM \"IB_USER\" WHERE USERNAME = ?");
                    setPropertyValue(identityStore, "userPropertiesQueryString", "SELECT FIRSTNAME, SURENAME FROM \"IB_USER\" WHERE USERNAME = ?");
                    setPropertyValue(identityStore, "resetCredentialDml", "UPDATE \"IB_USER\" SET USERPASSWORD = ? WHERE USERNAME = ?");
                    setPropertyValue(identityStore, "relayCredentialQueryString", "SELECT USERNAME FROM \"IB_USER\" WHERE #?# = ?");
                } else {
                    setPropertyValue(identityStore, "driverName", dbIdentityVault.getDriverName());
                    setPropertyValue(identityStore, "connectionURL", dbIdentityVault.getConnectionUrl());
                    setPropertyValue(identityStore, "connectionName", dbIdentityVault.getAdmin());
                    setPropertyValue(identityStore, "connectionPassword", dbIdentityVault.getPassword());
                    /* TODO RETROFIT  :
                    setPropertyValue(identityStore, "userQueryString", dbIdentityVault.getUserInformationLookup().getUserQueryString());
                    setPropertyValue(identityStore, "rolesQueryString", dbIdentityVault.getUserInformationLookup().getRolesQueryString());
                    setPropertyValue(identityStore, "credentialsQueryString", dbIdentityVault.getUserInformationLookup().getCredentialsQueryString());
                    setPropertyValue(identityStore, "userPropertiesQueryString", dbIdentityVault.getUserInformationLookup().getUserPropertiesQueryString());
                    setPropertyValue(identityStore, "resetCredentialDml", dbIdentityVault.getUserInformationLookup().getResetCredentialDml());
                    setPropertyValue(identityStore, "relayCredentialQueryString", dbIdentityVault.getUserInformationLookup().getRelayCredentialQueryString());
                    */
                    
                    IdProjectResource<byte[]> driverResource = new IdProjectResource<byte[]>(idGen.generateId(),
                            "lib/", dbIdentityVault.getDriver().getName(),
                            "binary", dbIdentityVault.getDriver().getValue());
                    driverResource.setClassifier("byte");
                    event.getContext().getCurrentModule().addResource(driverResource);
                    event.getContext().getCurrentModule().addEmbeddedDependency(
                            driverResource.getNameSpace() + driverResource.getName());
                }
            } else if (identitySource instanceof LdapIdentitySource) {
                // LDAP
                LdapIdentitySource ldapIdentityVault = (LdapIdentitySource) identitySource;
                identityStore = newBean(providerBeans, providerBean.getName() + "-identity-store", "org.atricore.idbus.idojos.ldapidentitystore.LDAPBindIdentityStore");
                setPropertyValue(identityStore, "initialContextFactory", ldapIdentityVault.getInitialContextFactory());
                setPropertyValue(identityStore, "providerUrl", ldapIdentityVault.getProviderUrl());
                setPropertyValue(identityStore, "securityPrincipal", ldapIdentityVault.getSecurityPrincipal());
                setPropertyValue(identityStore, "securityCredential", ldapIdentityVault.getSecurityCredential());
                setPropertyValue(identityStore, "securityAuthentication", ldapIdentityVault.getSecurityAuthentication());
                setPropertyValue(identityStore, "ldapSearchScope", ldapIdentityVault.getLdapSearchScope());
                setPropertyValue(identityStore, "usersCtxDN", ldapIdentityVault.getUsersCtxDN());
                setPropertyValue(identityStore, "principalUidAttributeID", ldapIdentityVault.getPrincipalUidAttributeID());
                setPropertyValue(identityStore, "roleMatchingMode", ldapIdentityVault.getRoleMatchingMode());
                setPropertyValue(identityStore, "uidAttributeID", ldapIdentityVault.getUidAttributeID());
                setPropertyValue(identityStore, "rolesCtxDN", ldapIdentityVault.getRolesCtxDN());
                setPropertyValue(identityStore, "roleAttributeID", ldapIdentityVault.getRoleAttributeID());
                setPropertyValue(identityStore, "credentialQueryString", ldapIdentityVault.getCredentialQueryString());
                setPropertyValue(identityStore, "updateableCredentialAttribute", ldapIdentityVault.getUpdateableCredentialAttribute());
                setPropertyValue(identityStore, "userPropertiesQueryString", ldapIdentityVault.getUserPropertiesQueryString());
            } else {
                // memory store
                identityStore = newBean(providerBeans, providerBean.getName() + "-identity-store", "org.atricore.idbus.idojos.memoryidentitystore.MemoryIdentityStore");
                setPropertyValue(identityStore, "usersFileName", "classpath:" + baseSamlDestPath + "atricore-users.xml");
                setPropertyValue(identityStore, "credentialsFileName", "classpath:" + baseSamlDestPath + "atricore-credentials.xml");
            }
        }
    }
}
