package com.atricore.idbus.console.lifecycle.main.transform.transformers.idsource;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.store.SSOIdentityManagerImpl;
import org.atricore.idbus.kernel.main.store.identity.IdentityPartitionStore;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyRef;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentitySourceTransformer  extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdentityLookupTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // Only work with Identity Source entities
        return event.getData() instanceof IdentitySource && !(event.getData() instanceof IdentityVault);
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        IdentitySource identitySource = (IdentitySource) event.getData();

        Beans idSrcBeans = newBeans(identitySource.getName() + " : ID Source Configuration generated by Atricore Identity Bus Server on " + new Date().toGMTString());
        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        String idauPath = (String) event.getContext().get("idauPath");

        // Publish root element so that other transformers can use it.
        event.getContext().put("idSourceBeans", idSrcBeans);

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for Identity Source " + identitySource.getName());

        String storeName = normalizeBeanName(identitySource.getName() + "-identity-store");

        if (identitySource instanceof DbIdentitySource) {
            Bean identityStore = null;
            // DB
            DbIdentitySource dbSource = (DbIdentitySource) identitySource;
            identityStore = newBean(idSrcBeans, storeName, "org.atricore.idbus.idojos.dbidentitystore.DynamicJDBCIdentityStore");

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
            identityStore = newBean(idSrcBeans, storeName, "org.atricore.idbus.idojos.ldapidentitystore.LDAPBindIdentityStore");
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
        } if (identitySource instanceof XmlIdentitySource) {
            throw new UnsupportedOperationException("XML Identity Source support not implemented !!!");
        }
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        IdentitySource identitySource = (IdentitySource) event.getData();
        Beans baseBeans = (Beans) event.getContext().get("beans");

        IdProjectModule module = event.getContext().getCurrentModule();
        Beans idSrcBeans = (Beans) event.getContext().get("idSourceBeans");

        String storeName = normalizeBeanName(identitySource.getName() + "-identity-store");
        String moduleName = normalizeBeanName(identitySource.getName());

        IdProjectResource<Beans> rBeans = new IdProjectResource<Beans>(idGen.generateId(),
                moduleName,
                moduleName,
                "spring-beans",
                idSrcBeans);

        rBeans.setClassifier("jaxb");
        rBeans.setNameSpace(moduleName);

        module.addResource(rBeans);

        return super.after(event);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
