package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceSpringMarshallerVisitor extends AbstractApplianceDefinitionVisitor {

    private static final Log logger = LogFactory.getLog(ApplianceSpringMarshallerVisitor.class);

    private IdentityAppliance appliance;

    private Beans beans;

    private Bean applianceBean;
    
    private Bean applianceDefBean;

    public ApplianceSpringMarshallerVisitor(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

    @Override
    public void arrive(IdentityApplianceDefinition node) throws Exception {
        beans = newBeans(node.getDisplayName());
        
        applianceBean = newBean(beans, node.getName(), IdentityAppliance.class.getName());
        setBeanDescription(applianceBean, appliance.toString());

        setPropertyValue(applianceBean, "id", appliance.getId() + "");
        setPropertyValue(applianceBean, "state", appliance.getState());
        setPropertyValue(applianceBean, "namespace", appliance.getNamespace());
        
        applianceDefBean = newBean(beans, node.getName() + "applianceDefBean", node.getClass());
        setBeanDescription(applianceDefBean, node.toString());

        setPropertyValue(applianceDefBean, "id", node.getId() + "");
        setPropertyValue(applianceDefBean, "name", node.getName());
        setPropertyValue(applianceDefBean, "displayName", node.getDisplayName());
        setPropertyValue(applianceDefBean, "description", node.getDescription());
        setLocationPropertyValue(applianceDefBean, "location", node.getLocation());
        setPropertyValue(applianceDefBean, "revision" , node.getRevision() + "");

        // Wire Beans
        setPropertyRef(applianceBean, "idApplianceDefinition", applianceDefBean.getName());

    }

    @Override
    public Object[] leave(IdentityApplianceDefinition node, Object[] results) throws Exception {
        return new Object[] {beans};
    }

    @Override
    public void arrive(IdentityProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Active bindings
        if (node.getActiveBindings() != null) {
            Set<String> abs = new HashSet<String>(node.getActiveBindings().size());
            for (Binding ab : node.getActiveBindings()) {
                abs.add(ab.toString());
            }
            setPropertyAsValues(providerBean, "activeBindings", abs);
        }

        // Active profiles
        if (node.getActiveProfiles() != null) {
            Set<String> profiles = new HashSet<String>(node.getActiveProfiles().size());
            for (Profile profile : node.getActiveProfiles()) {
                profiles.add(profile.toString());
            }
            setPropertyAsValues(providerBean, "activeProfiles", profiles);
        }

        // Auth mechanism
        if (node.getAuthenticationMechanisms() != null) {
            for (AuthenticationMechanism authn : node.getAuthenticationMechanisms()) {
                addPropertyRefsToSet(providerBean, "authenticationMechanisms", authn.getName());
            }
        }

        // Federated Connections

        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Identity Lookup
        if (node.getIdentityLookup() != null) {
            setPropertyRef(providerBean, "identityLookup", node.getIdentityLookup().getName());
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());


        // TODO : node.getEmissionPolicy();
        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);
    }

    @Override
    public Object[] leave(IdentityProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(InternalSaml2ServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));
        
        // Active bindings
        if (node.getActiveBindings() != null) {
            Set<String> abs = new HashSet<String>(node.getActiveBindings().size());
            for (Binding ab : node.getActiveBindings()) {
                abs.add(ab.toString());
            }
            setPropertyAsValues(providerBean, "activeBindings", abs);
        }

        // Active profiles
        if (node.getActiveProfiles() != null) {
            Set<String> profiles = new HashSet<String>(node.getActiveProfiles().size());
            for (Profile profile : node.getActiveProfiles()) {
                profiles.add(profile.toString());
            }
            setPropertyAsValues(providerBean, "activeProfiles", profiles);
        }

        // Activation
        // TODO [JOSSO-370]
        /*
        if (node.getActivation() != null)
            setPropertyRef(providerBean, "activation", node.getActivation().getName());
            */

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Identity Lookup
        if (node.getIdentityLookup() != null) {
            setPropertyRef(providerBean, "identityLookup", node.getIdentityLookup().getName());
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // Config (assume saml)
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());


        // TODO : node.getAccountLinkagePolicy();
        // TODO : node.getAuthenticationContract();
        // TODO : node.getAuthenticationMechanisms();
        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);


    }

    @Override
    public Object[] leave(InternalSaml2ServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public Object[] leave(ExternalSaml2IdentityProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(ExternalSaml2ServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // Config
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);
    }

    @Override
    public Object[] leave(ExternalSaml2ServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(ExternalOpenIDIdentityProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // TODO: Config not saml?
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);
    }

    @Override
    public Object[] leave(ExternalOpenIDIdentityProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(ExternalOAuth2IdentityProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // TODO: Config not saml?
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);
    }

    @Override
    public Object[] leave(ExternalOAuth2IdentityProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(OAuth2ServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // TODO: Config not saml?
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);
    }

    @Override
    public Object[] leave(OAuth2ServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(SalesforceServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // Config (assume saml)
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);


    }

    @Override
    public Object[] leave(SalesforceServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(GoogleAppsServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());
        setPropertyValue(providerBean, "domain", node.getDomain());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));

        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // Config (assume saml)
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);


    }

    @Override
    public Object[] leave(GoogleAppsServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(SugarCRMServiceProvider node) throws Exception {

        Bean providerBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(providerBean, node.toString());

        setPropertyValue(providerBean, "id", node.getId() + "");
        setPropertyValue(providerBean, "name", node.getName());
        setPropertyValue(providerBean, "displayName", node.getDisplayName());
        setPropertyValue(providerBean, "description", node.getDescription());
        setPropertyRef(providerBean, "identityAppliance", applianceDefBean.getName());
        setPropertyValue(providerBean, "remote", node.isRemote());

        setPropertyValue(providerBean, "x", String.valueOf(node.getX()));
        setPropertyValue(providerBean, "y", String.valueOf(node.getY()));
        
        // Federated Connections
        if (node.getFederatedConnectionsA() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsA()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsA", fc.getName() );
            }
        }

        if (node.getFederatedConnectionsB() != null) {
            for (FederatedConnection fc : node.getFederatedConnectionsB()) {
                addPropertyRefsToSet(providerBean, "federatedConnectionsB", fc.getName() );
            }
        }

        // Location
        if (node.getLocation() != null)
            setLocationPropertyValue(providerBean, "location", node.getLocation());

        // Config (assume saml)
        if (node.getConfig() != null)
            setSamlR2ConfigurationPropertyValue(providerBean, "config", (SamlR2ProviderConfig) node.getConfig());

        // TODO : node.getMetadata();

        addPropertyBeansAsRefsToSet(applianceDefBean, "providers", providerBean);


    }

    @Override
    public Object[] leave(SugarCRMServiceProvider node, Object[] results) throws Exception {
        return null;
    }

    @Override
    public void arrive(ServiceConnection node) throws Exception {

        Bean oldServiceConnectionBean = getBean(beans, node.getName());
        // Is it the exact same bean !?
        if (oldServiceConnectionBean != null && getBeanDescription(oldServiceConnectionBean).equals(node.toString()))
            return;

        Bean serviceConnectionBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(serviceConnectionBean, node.toString());

        setPropertyValue(serviceConnectionBean, "id", node.getId() + "");
        setPropertyValue(serviceConnectionBean, "name", node.getName());
        setPropertyValue(serviceConnectionBean, "displayName", node.getDisplayName());
        setPropertyValue(serviceConnectionBean, "description", node.getDescription());

        if (node.getResource() != null)
            setPropertyRef(serviceConnectionBean, "resource", node.getResource().getName());

        if (node.getSp() != null)
            setPropertyRef(serviceConnectionBean, "sp", node.getSp().getName());
    }

    @Override
    public void arrive(Activation node) throws Exception {
        Bean oldActivationBean = getBean(beans, node.getName());
        // Is it the exact same bean !?
        if (oldActivationBean != null && getBeanDescription(oldActivationBean).equals(node.toString()))
            return;

        Bean activationBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(activationBean, node.toString());

        setPropertyValue(activationBean, "id", node.getId() + "");
        setPropertyValue(activationBean, "name", node.getName());
        //setPropertyValue(activationBean, "displayName", node.getDisplayName());
        setPropertyValue(activationBean, "description", node.getDescription());

    }

    @Override
    public void arrive(JOSSO1Resource node) throws Exception {


        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));

        // TODO : [JOSSO-370] What's the resource here ?!
        if (node.getServiceConnection().getResource() != null)
            setPropertyRef(idSourceBean, "resource", node.getServiceConnection().getResource().getName());

        if (node.getServiceConnection().getSp() != null)
            setPropertyRef(idSourceBean, "sp", node.getServiceConnection().getSp().getName());

        if (node.getIgnoredWebResources() != null) {
            setPropertyAsValues(idSourceBean, "ignoredWebResources", node.getIgnoredWebResources());
        }

        if (node.getPartnerAppLocation() != null)
            setLocationPropertyValue(idSourceBean, "partnerAppLocation", node.getPartnerAppLocation());


    }

    @Override
    public void arrive(MicroStrategyResource node) throws Exception {


        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));

        // TODO : [JOSSO-370] What's the resource here ?!
        if (node.getServiceConnection().getResource() != null)
            setPropertyRef(idSourceBean, "resource", node.getServiceConnection().getResource().getName());

        if (node.getServiceConnection().getSp() != null)
            setPropertyRef(idSourceBean, "sp", node.getServiceConnection().getSp().getName());

    }



    @Override
    public void arrive(FederatedConnection node) throws Exception {
        // Check if bean is already defined!

        Bean oldFcBean = getBean(beans, node.getName());
        // Is it the exact same bean !?
        if (oldFcBean != null && getBeanDescription(oldFcBean).equals(node.toString()))
            return;

        Bean fcBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(fcBean, node.toString());

        setPropertyValue(fcBean, "id", node.getId() + "");
        setPropertyValue(fcBean, "name", node.getName());
        setPropertyValue(fcBean, "displayName", node.getDisplayName());
        setPropertyValue(fcBean, "description", node.getDescription());

        if (node.getRoleA() != null)
            setPropertyRef(fcBean, "roleA", node.getRoleA().getName());

        if (node.getRoleB() != null)
            setPropertyRef(fcBean, "roleB", node.getRoleA().getName());

        if (node.getChannelA() != null)
            setChannelPropertyValue(fcBean, "channelA", node.getChannelA());

        if (node.getChannelB() != null)
            setChannelPropertyValue(fcBean, "channelB", node.getChannelB());


    }

    @Override
    public void arrive(IdentityLookup node) throws Exception {
        Bean oldIdLBean = getBean(beans, node.getName());
        // Is it the exact same bean !?
        if (oldIdLBean != null && getBeanDescription(oldIdLBean).equals(node.toString()))
            return;

        Bean idLBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idLBean, node.toString());

        setPropertyValue(idLBean, "id", node.getId() + "");
        setPropertyValue(idLBean, "name", node.getName());
        setPropertyValue(idLBean, "displayName", node.getDisplayName());
        setPropertyValue(idLBean, "description", node.getDescription());

        if (node.getIdentitySource() != null)
            setPropertyRef(idLBean, "identitySource", node.getIdentitySource().getName());

        if (node.getProvider() != null)
            setPropertyRef(idLBean, "provider", node.getProvider().getName());

    }

    @Override
    public void arrive(DelegatedAuthentication node) throws Exception {
        Bean oldDelegatedAuthnBean = getBean(beans, node.getName());
        // Is it the exact same bean !?
        if (oldDelegatedAuthnBean != null && getBeanDescription(oldDelegatedAuthnBean).equals(node.toString()))
            return;

        Bean delegatedAuthnBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(delegatedAuthnBean, node.toString());

        setPropertyValue(delegatedAuthnBean, "id", node.getId() + "");
        setPropertyValue(delegatedAuthnBean, "name", node.getName());
        setPropertyValue(delegatedAuthnBean, "displayName", node.getDisplayName());
        setPropertyValue(delegatedAuthnBean, "description", node.getDescription());

        if (node.getIdp() != null)
            setPropertyRef(delegatedAuthnBean, "idp", node.getIdp().getName());

        if (node.getAuthnService() != null)
            setPropertyRef(delegatedAuthnBean, "authnService", node.getAuthnService().getName());
    }

    @Override
    public void arrive(EmbeddedIdentitySource node) throws Exception {
        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "displayName", node.getDisplayName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "idau", node.getIdau());
        setPropertyValue(idSourceBean, "psp", node.getPsp());
        setPropertyValue(idSourceBean, "pspTarget", node.getPspTarget());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));
    }

    @Override
    public void arrive(LdapIdentitySource node) throws Exception {
        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "displayName", node.getDisplayName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "initialContextFactory", node.getInitialContextFactory());
        setPropertyValue(idSourceBean, "providerUrl", node.getProviderUrl());
        setPropertyValue(idSourceBean, "securityPrincipal", node.getSecurityPrincipal());
        setPropertyValue(idSourceBean, "securityCredential", node.getSecurityCredential());
        setPropertyValue(idSourceBean, "securityAuthentication", node.getSecurityAuthentication());
        setPropertyValue(idSourceBean, "ldapSearchScope", node.getLdapSearchScope());
        setPropertyValue(idSourceBean, "usersCtxDN", node.getUsersCtxDN());
        setPropertyValue(idSourceBean, "principalUidAttributeID", node.getPrincipalUidAttributeID());
        setPropertyValue(idSourceBean, "roleMatchingMode", node.getRoleMatchingMode());
        setPropertyValue(idSourceBean, "uidAttributeID", node.getUidAttributeID());
        setPropertyValue(idSourceBean, "rolesCtxDN", node.getRolesCtxDN());
        setPropertyValue(idSourceBean, "roleAttributeID", node.getRoleAttributeID());
        setPropertyValue(idSourceBean, "credentialQueryString", node.getCredentialQueryString());
        setPropertyValue(idSourceBean, "updateableCredentialAttribute", node.getUpdateableCredentialAttribute());
        setPropertyValue(idSourceBean, "userPropertiesQueryString", node.getUserPropertiesQueryString());
        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));
    }

    @Override
    public void arrive(DbIdentitySource node) throws Exception {
        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "displayName", node.getDisplayName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "admin", node.getAdmin());
        setPropertyValue(idSourceBean, "password", node.getPassword());
        setPropertyValue(idSourceBean, "ConnectionUrl", node.getConnectionUrl());

        setPropertyValue(idSourceBean, "driverName", node.getDriverName());

        setPropertyValue(idSourceBean, "userQueryString", node.getUserQueryString());
        setPropertyValue(idSourceBean, "rolesQueryString", node.getRolesQueryString());
        setPropertyValue(idSourceBean, "credentialsQueryString", node.getCredentialsQueryString());
        setPropertyValue(idSourceBean, "userPropertiesQueryString", node.getUserPropertiesQueryString());
        setPropertyValue(idSourceBean, "resetCredentialDml", node.getResetCredentialDml());
        setPropertyValue(idSourceBean, "relayCredentialQueryString", node.getRelayCredentialQueryString());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));

        // Resource driver;

    }

    @Override
    public void arrive(XmlIdentitySource node) throws Exception {
        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "displayName", node.getDisplayName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyRef(idSourceBean, "xmlUrl", node.getXmlUrl());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));
    }

    public void arrive(JOSSO2Resource node) throws Exception {
        Bean idSourceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(idSourceBean, node.toString());

        setPropertyValue(idSourceBean, "id", node.getId() + "");
        setPropertyValue(idSourceBean, "name", node.getName());
        setPropertyValue(idSourceBean, "description", node.getDescription());

        setPropertyValue(idSourceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(idSourceBean, "y", String.valueOf(node.getY()));
    }

    @Override
    public void arrive(ExecutionEnvironment node) throws Exception {
        Bean execEnvBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(execEnvBean, node.toString());

        setPropertyValue(execEnvBean, "id", node.getId() + "");
        setPropertyValue(execEnvBean, "name", node.getName());
        setPropertyValue(execEnvBean, "displayName", node.getDisplayName());
        setPropertyValue(execEnvBean, "description", node.getDescription());

        setPropertyValue(execEnvBean, "installUri", node.getInstallUri());
        setPropertyValue(execEnvBean, "platformId", node.getPlatformId());
        setPropertyValue(execEnvBean, "active", node.isActive());

        setPropertyValue(execEnvBean, "x", String.valueOf(node.getX()));
        setPropertyValue(execEnvBean, "y", String.valueOf(node.getY()));

        if (node instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbNode = (JBossExecutionEnvironment) node;
            setPropertyValue(execEnvBean, "instance", jbNode.getInstance());

        } else if (node instanceof  WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlNode = (WeblogicExecutionEnvironment) node;
            setPropertyValue(execEnvBean, "domain", wlNode.getDomain());

        } // TODO : Export specific attributes for other exec envs!

        if (node.getActivations() != null) {
            for (Activation a : node.getActivations()) {
                addPropertyRefsToSet(execEnvBean, "activations", a.getName());
            }
        }

    }

    @Override
    public void arrive(AuthenticationService node) throws Exception {
        Bean authnServiceBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(authnServiceBean, node.toString());

        setPropertyValue(authnServiceBean, "id", node.getId() + "");
        setPropertyValue(authnServiceBean, "name", node.getName());
        setPropertyValue(authnServiceBean, "displayName", node.getDisplayName());
        setPropertyValue(authnServiceBean, "description", node.getDescription());

        setPropertyValue(authnServiceBean, "x", String.valueOf(node.getX()));
        setPropertyValue(authnServiceBean, "y", String.valueOf(node.getY()));

        if (node instanceof WikidAuthenticationService) {
            WikidAuthenticationService wikidAuthnService = (WikidAuthenticationService) node;
            setPropertyValue(authnServiceBean, "serverHost", wikidAuthnService.getServerHost());
            setPropertyValue(authnServiceBean, "serverPort", String.valueOf(wikidAuthnService.getServerPort()));
            setPropertyValue(authnServiceBean, "serverCode", wikidAuthnService.getServerCode());

            if (wikidAuthnService.getCaStore() != null)
                setKeystorePropertyValue(authnServiceBean, "caStore", wikidAuthnService.getCaStore());

            if (wikidAuthnService.getWcStore() != null)
                setKeystorePropertyValue(authnServiceBean, "wcStore", wikidAuthnService.getWcStore());
        }

        if (node.getDelegatedAuthentications() != null) {
            for (DelegatedAuthentication da : node.getDelegatedAuthentications()) {
                addPropertyRefsToSet(authnServiceBean, "delegatedAuthentications", da.getName());
            }
        }
    }

    /**
     * Because we use a reflexive walker, even if the arrive is not part of the Visitor interface, the method is invoked :)
     *
     * @param node
     * @throws Exception
     */
    public void arrive(BasicAuthentication node) throws Exception {
        Bean basicAuthnBean = newBean(beans, node.getName(), node.getClass());
        setBeanDescription(basicAuthnBean, node.toString());

        setPropertyValue(basicAuthnBean, "id", node.getId() + "");
        setPropertyValue(basicAuthnBean, "name", node.getName());
        setPropertyValue(basicAuthnBean, "hashAlgorithm", node.getHashAlgorithm());
        setPropertyValue(basicAuthnBean, "hashEncoding", node.getHashEncoding());
        setPropertyValue(basicAuthnBean, "ignoreUsernameCase", node.isIgnoreUsernameCase());


    }

    //----------------------------------------------------< UTILS >

    protected void setSamlR2ConfigurationPropertyValue(Bean bean, String propertyName, SamlR2ProviderConfig config) {
        Bean pCfgBean = newAnonymousBean(config.getClass());
        setBeanDescription(pCfgBean, config.toString());

        setPropertyValue(pCfgBean, "id", config.getId() + "");
        setPropertyValue(pCfgBean, "name", config.getName());
        setPropertyValue(pCfgBean, "description", config.getDescription());

        if (config.getSigner() != null)
            setKeystorePropertyValue(pCfgBean, "signer", config.getSigner());

        if (config.getEncrypter() != null)
            setKeystorePropertyValue(pCfgBean, "encrypter", config.getEncrypter());

        setPropertyBean(bean, propertyName, pCfgBean);

    }

    protected void setChannelPropertyValue(Bean bean, String propertyName, Channel c) {

        Bean cBean = newAnonymousBean(c.getClass());
        setBeanDescription(cBean, c.toString());

        setPropertyValue(cBean, "id", c.getId() + "");
        setPropertyValue(cBean, "name", c.getName());
        setPropertyValue(cBean, "displayName", c.getDisplayName());
        setPropertyValue(cBean, "description", c.getDescription());

        if (c.getLocation() != null)
            setLocationPropertyValue(cBean, "location", c.getLocation());

        setPropertyValue(cBean, "overrideProviderSetup", c.isOverrideProviderSetup());

        // Active bindings
        if (c.getActiveBindings() != null) {
            Set<String> abs = new HashSet<String>(c.getActiveBindings().size());
            for (Binding ab : c.getActiveBindings()) {
                abs.add(ab.toString());
            }
            setPropertyAsValues(cBean, "activeBindings", abs);
        }

        // Active profiles
        if (c.getActiveProfiles() != null) {
            Set<String> profiles = new HashSet<String>(c.getActiveProfiles().size());
            for (Profile profile : c.getActiveProfiles()) {
                profiles.add(profile.toString());
            }
            setPropertyAsValues(cBean, "activeProfiles", profiles);
        }

        if (c instanceof ServiceProviderChannel) {
            ServiceProviderChannel spc = (ServiceProviderChannel) c;


            // TODO : spc.getAttributeProfile();
            // TODO : spc.getAuthenticationContract();
            // TODO : spc.getAuthenticationMechanism()

        } else if (c instanceof IdentityProviderChannel) {
            IdentityProviderChannel idpc = (IdentityProviderChannel) c;
            setPropertyValue(cBean, "preferred", idpc.isPreferred());

            if (idpc.getAccountLinkagePolicy() != null)
                setAccountLinkagePolicyPropertyValue(cBean, "accountLinkagePolicy", idpc.getAccountLinkagePolicy());
        }

        setPropertyBean(bean, propertyName, cBean);

    }

    protected void setAccountLinkagePolicyPropertyValue(Bean bean, String propertyName, AccountLinkagePolicy policy) {

        Bean policyBean = newAnonymousBean(policy.getClass());
        setBeanDescription(policyBean, policy.toString());

        setPropertyValue(policyBean, "id", policy.getId() + "");
        setPropertyValue(policyBean, "name", policy.getName());
        //setPropertyValue(policyBean, "name", policy.getCustomMapper());
        setPropertyValue(policyBean, "mappingType", policy.getLinkEmitterType().toString());

        setPropertyBean(bean, propertyName, policyBean);

    }

    protected void setLocationPropertyValue(Bean bean, String propertyName, Location location) {
        Bean locationBean = buildLocationBean(location);
        setPropertyBean(bean, propertyName, locationBean);
    }

    protected void setKeystorePropertyValue(Bean bean, String propertyName, Keystore keystore) {

        Bean ksBean = newAnonymousBean(keystore.getClass());
        setBeanDescription(ksBean, keystore.toString());

        setPropertyValue(ksBean, "id", keystore.getId() + "");
        setPropertyValue(ksBean, "type", keystore.getType());
        setPropertyValue(ksBean, "password", keystore.getPassword());
        setPropertyValue(ksBean, "privateKeyName", keystore.getPrivateKeyName());
        setPropertyValue(ksBean, "privateKeyPassword", keystore.getPrivateKeyPassword());
        setPropertyValue(ksBean, "certificateAlias", keystore.getCertificateAlias());

        setResourcePropertyValue(ksBean, "store", keystore.getStore());

        setPropertyBean(bean, propertyName, ksBean);


    }

    protected void setResourcePropertyValue(Bean bean, String propertyName, Resource resource) {
        Bean resourceBean = newAnonymousBean(resource.getClass());
        setBeanDescription(resourceBean, resource.toString());

        setPropertyValue(resourceBean, "id", resource.getId() + "");
        setPropertyValue(resourceBean, "name", resource.getName());
        setPropertyValue(resourceBean, "displayName", resource.getDisplayName());
        setPropertyValue(resourceBean, "uri", resource.getUri());
        // TODO !!!
        setPropertyValue(resourceBean, "value", resource.getValue().toString());

        setPropertyBean(bean, propertyName, resourceBean);

    }


    
    protected Bean buildLocationBean(Location location) {
        
        Bean locationBean = newAnonymousBean(location.getClass().getName());
        setBeanDescription(locationBean, location.toString());
        setPropertyValue(locationBean, "protocol", location.getProtocol());
        setPropertyValue(locationBean, "host", location.getHost());
        setPropertyValue(locationBean, "port", location.getPort() + "");
        setPropertyValue(locationBean, "context", location.getContext());
        setPropertyValue(locationBean, "uri", location.getUri());
        return locationBean;

    }
}

