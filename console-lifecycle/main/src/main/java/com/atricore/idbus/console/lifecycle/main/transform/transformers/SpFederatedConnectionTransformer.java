package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyRefs;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpFederatedConnectionTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(SpFederatedConnectionTransformer.class);

    private boolean roleA;

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof FederatedConnection) {
            FederatedConnection fc = (FederatedConnection) event.getData();

            if (roleA) {
                if (fc.getRoleA() instanceof ServiceProvider) {
                    IdentityProviderChannel idpChannel = (IdentityProviderChannel) fc.getChannelA();

                    // Only accept a connection if channel overrides provider setup.
                    return idpChannel.isOverrideProviderSetup();
                }
            } else {
                if (fc.getRoleB() instanceof ServiceProvider) {
                    IdentityProviderChannel idpChannel = (IdentityProviderChannel) fc.getChannelB();

                    // Only accept a connection if channel overrides provider setup.
                    return idpChannel.isOverrideProviderSetup();
                }

            }

        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        FederatedConnection federatedConnection = (FederatedConnection) event.getData();

        ServiceProvider provider = (ServiceProvider) event.getContext().getParentNode();
        ServiceProviderChannel idpChannel = null;
        ServiceProvider roleProvider = null;
        FederatedProvider target = null;
        FederatedChannel targetChannel = null;

        if (roleA) {

            roleProvider = (ServiceProvider) federatedConnection.getRoleA();
            idpChannel = (ServiceProviderChannel) federatedConnection.getChannelA();

            target = federatedConnection.getRoleB();
            targetChannel = federatedConnection.getChannelB();

            if (!provider.getName().equals(federatedConnection.getRoleA().getName()))
                throw new IllegalStateException("Context provider " + provider +
                        " is not roleA provider in Federated Connection " + federatedConnection.getName());

        } else {

            roleProvider = (ServiceProvider) federatedConnection.getRoleB();
            idpChannel = (ServiceProviderChannel) federatedConnection.getChannelB();

            target = federatedConnection.getRoleA();
            targetChannel = federatedConnection.getChannelA();

            if (!provider.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + provider +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }

        generateSPComponents(provider, idpChannel, federatedConnection, target, targetChannel, event.getContext());

    }

    protected void generateSPComponents(ServiceProvider provider,
                                     ServiceProviderChannel idpChannel,
                                     FederatedConnection fc,
                                     FederatedProvider target,
                                     FederatedChannel targetChannel,
                                     IdApplianceTransformationContext ctx) throws TransformException {

        Beans spBeans = (Beans) ctx.get("spBeans");
        Beans beans = (Beans) ctx.get("beans");
        
        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for IdP Channel " + idpChannel.getName()  + " of SP " + provider.getName());

        Bean spBean = null;
        Collection<Bean> b = getBeansOfType(spBeans, ServiceProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + b.size());
        }
        spBean = b.iterator().next();

        boolean isDefault = false ; // TODO RETROFIT  : idpChannel.getTarget() == null || idpChannel.getTarget().equals(provider);
        // TODO RETROFIT  : String name = spBean.getName() + (isDefault ? "-default" : "-" + normalizeBeanName(idpChannel.getTarget().getName())) + "-sp-channel";
        String name  = null;
        
        Bean idpChannelBean = newBean(spBeans, name, IdPChannelImpl.class.getName());

        ctx.put("idpChannelBean", idpChannelBean);

        // name
        setPropertyValue(idpChannelBean, "name", idpChannelBean.getName());

        // description
        setPropertyValue(idpChannelBean, "description", idpChannel.getDescription());

        // location
        // TODO RETROFIT  : setPropertyValue(idpChannelBean, "location", resolveLocationUrl(provider.getBindingChannel().getLocation()) + "/SAML2");

        // provider
        // TODO RETROFIT  :
        /*
        if (idpChannel.getTarget() != null) {
            setPropertyRef(idpChannelBean, "provider", normalizeBeanName(idpChannel.getTarget().getName()));
        } else {
            setPropertyRef(idpChannelBean, "provider", spBean.getName());
        }
        */

        // sessionManager
        setPropertyRef(idpChannelBean, "sessionManager", spBean.getName() + "-session-manager");

        // identityManager
        // TODO RETROFIT  :
//        if (idpChannel.getIdentityVault() != null) {
//            setPropertyRef(idpChannelBean, "identityManager", spBean.getName() + "-identity-manager");
//        }

        // member
        setPropertyRef(idpChannelBean, "member", spBean.getName() + "-md");
        
        // identityMediator
        Bean identityMediatorBean = getBean(spBeans, spBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + spBean.getName() + "-samlr2-identity-mediator");

        setPropertyRef(idpChannelBean, "identityMediator", identityMediatorBean.getName());

        // accountLinkLifecycle
        setPropertyRef(idpChannelBean, "accountLinkLifecycle", spBean.getName() + "-account-link-lifecycle");

        // accountLinkEmitter
        setPropertyRef(idpChannelBean, "accountLinkEmitter", spBean.getName() + "-account-link-emitter");

        // identityMapper
        setPropertyRef(idpChannelBean, "identityMapper", spBean.getName() + "-identity-mapper");

        // endpoints
        List<Bean> endpoints = new ArrayList<Bean>();

        Bean sloHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpPost.setName(spBean.getName() + "-saml2-slo-http-post");
        setPropertyValue(sloHttpPost, "name", sloHttpPost.getName());
        setPropertyValue(sloHttpPost, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
        endpoints.add(sloHttpPost);

        Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpRedirect.setName(spBean.getName() + "-saml2-slo-http-redirect");
        setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
        setPropertyValue(sloHttpRedirect, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpRedirect, "binding", SamlR2Binding.SAMLR2_REDIRECT.getValue());
        endpoints.add(sloHttpRedirect);

        Bean sloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloSoap.setName(spBean.getName() + "-saml2-slo-soap");
        setPropertyValue(sloSoap, "name", sloSoap.getName());
        setPropertyValue(sloSoap, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloSoap, "binding", SamlR2Binding.SAMLR2_SOAP.getValue());
        List<Ref> plansList = new ArrayList<Ref>();
        Ref plan = new Ref();
        plan.setBean(spBean.getName() + "-spsso-samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        setPropertyRefs(sloSoap, "identityPlans", plansList);
        endpoints.add(sloSoap);

        Bean sloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloLocal.setName(spBean.getName() + "-saml2-slo-local");
        setPropertyValue(sloLocal, "name", sloLocal.getName());
        setPropertyValue(sloLocal, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloLocal, "binding", SamlR2Binding.SAMLR2_LOCAL.getValue());
        // NOTE: location doesn't exist in simple-federation example
        setPropertyValue(sloLocal, "location", "local:/" + spBean.getName() + "/SLO/LOCAL");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spBean.getName() + "-spsso-samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        setPropertyRefs(sloLocal, "identityPlans", plansList);
        endpoints.add(sloLocal);
        
        Bean acHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
        acHttpPost.setName(spBean.getName() + "-saml2-ac-http-post");
        setPropertyValue(acHttpPost, "name", acHttpPost.getName());
        setPropertyValue(acHttpPost, "type", SAMLR2MetadataConstants.AssertionConsumerService_QNAME.toString());
        setPropertyValue(acHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spBean.getName() + "-idpunsolicitedresponse-to-subject-plan");
        plansList.add(plan);
        setPropertyRefs(acHttpPost, "identityPlans", plansList);
        endpoints.add(acHttpPost);
        
        setPropertyAsBeans(idpChannelBean, "endpoints", endpoints);
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        // IdP Channel bean
        Bean idpChannelBean = (Bean) event.getContext().get("idpChannelBean");
        Beans beans = (Beans) event.getContext().get("beans");
        
        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(beans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            addPropertyBeansAsRefs(mu, "channels", idpChannelBean);
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

        return idpChannelBean;
    }
}
