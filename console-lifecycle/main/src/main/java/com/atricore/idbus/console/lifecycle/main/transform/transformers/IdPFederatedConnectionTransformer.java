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
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan;
import org.atricore.idbus.capabilities.samlr2.main.idp.plans.SamlR2AuthnRequestToSamlR2ResponsePlan;
import org.atricore.idbus.capabilities.samlr2.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.samlr2.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdPFederatedConnectionTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdPFederatedConnectionTransformer.class);

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
                if (fc.getRoleA() instanceof IdentityProvider) {
                    ServiceProviderChannel spChannel = (ServiceProviderChannel) fc.getChannelA();

                    // Only accept a connection if channel overrides provider setup.
                    return spChannel.isOverrideProviderSetup();
                }
            } else {
                if (fc.getRoleB() instanceof IdentityProvider) {
                    ServiceProviderChannel spChannel = (ServiceProviderChannel) fc.getChannelB();

                    // Only accept a connection if channel overrides provider setup.
                    return spChannel.isOverrideProviderSetup();
                }

            }

        }

        return false;
            
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        FederatedConnection federatedConnection = (FederatedConnection) event.getData();

        IdentityProvider provider = (IdentityProvider) event.getContext().getParentNode();
        ServiceProviderChannel spChannel = null;
        IdentityProvider roleProvider = null;
        FederatedProvider target = null;
        FederatedChannel targetChannel = null;

        if (roleA) {

            roleProvider = (IdentityProvider) federatedConnection.getRoleA();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelA();

            target = federatedConnection.getRoleB();
            targetChannel = federatedConnection.getChannelB();

            if (!provider.getName().equals(federatedConnection.getRoleA().getName()))
                throw new IllegalStateException("Context provider " + provider +
                        " is not roleA provider in Federated Connection " + federatedConnection.getName());

        } else {

            roleProvider = (IdentityProvider) federatedConnection.getRoleB();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelB();

            target = federatedConnection.getRoleA();
            targetChannel = federatedConnection.getChannelA();

            if (!provider.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + provider +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }


        transformProvider(provider, spChannel, federatedConnection, target, targetChannel, event.getContext());

    }

    protected void transformProvider(IdentityProvider provider,
                                     ServiceProviderChannel spChannel,
                                     FederatedConnection fc,
                                     FederatedProvider target,
                                     FederatedChannel targetChannel,
                                     IdApplianceTransformationContext ctx) throws TransformException {

        Beans idpBeans = (Beans) ctx.get("idpBeans");


        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for SP Channel " + spChannel.getName()  + " of IdP " + provider.getName());

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        String name = idpBean.getName() +  "-" + normalizeBeanName(target.getName()) + "-sp-channel";
        
        Bean spChannelBean = newBean(idpBeans, name, SPChannelImpl.class.getName());

        ctx.put("spChannelBean", spChannelBean);

        // name
        setPropertyValue(spChannelBean, "name", spChannelBean.getName());

        // description
        setPropertyValue(spChannelBean, "description", spChannel.getDescription());

        // location
        setPropertyValue(spChannelBean, "location", resolveLocationUrl(provider));

        // provider
        setPropertyRef(spChannelBean, "provider", normalizeBeanName(target.getName()));

        // sessionManager
        setPropertyRef(spChannelBean, "sessionManager", idpBean.getName() + "-session-manager");
        
        // identityManager

        // member
        setPropertyRef(spChannelBean, "member", idpBean.getName() + "-md");

        // identityMediator
        Bean identityMediatorBean = getBean(idpBeans, idpBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + idpBean.getName() + "-samlr2-identity-mediator");

        setPropertyRef(spChannelBean, "identityMediator", identityMediatorBean.getName());
        
        // endpoints
        List<Bean> endpoints = new ArrayList<Bean>();

        Bean sloHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpPost.setName(idpBean.getName() + "-saml2-slo-http-post");
        setPropertyValue(sloHttpPost, "name", sloHttpPost.getName());
        setPropertyValue(sloHttpPost, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
        List<Ref> plansList = new ArrayList<Ref>();
        Ref plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        Ref plan2 = new Ref();
        plan2.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan2);
        setPropertyRefs(sloHttpPost, "identityPlans", plansList);
        endpoints.add(sloHttpPost);

        Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpRedirect.setName(idpBean.getName() + "-saml2-slo-http-redirect");
        setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
        setPropertyValue(sloHttpRedirect, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpRedirect, "binding", SamlR2Binding.SAMLR2_REDIRECT.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan2);
        setPropertyRefs(sloHttpRedirect, "identityPlans", plansList);
        endpoints.add(sloHttpRedirect);

        Bean sloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloSoap.setName(idpBean.getName() + "-saml2-slo-soap");
        setPropertyValue(sloSoap, "name", sloSoap.getName());
        setPropertyValue(sloSoap, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloSoap, "binding", SamlR2Binding.SAMLR2_SOAP.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan2);
        setPropertyRefs(sloSoap, "identityPlans", plansList);
        endpoints.add(sloSoap);

        Bean sloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloLocal.setName(idpBean.getName() + "-saml2-slo-local");
        setPropertyValue(sloLocal, "name", sloLocal.getName());
        setPropertyValue(sloLocal, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
        setPropertyValue(sloLocal, "binding", SamlR2Binding.SAMLR2_LOCAL.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan2);
        setPropertyRefs(sloLocal, "identityPlans", plansList);
        endpoints.add(sloLocal);
        
        Bean ssoHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoHttpPost.setName(idpBean.getName() + "-saml2-sso-http-post");
        setPropertyValue(ssoHttpPost, "name", ssoHttpPost.getName());
        setPropertyValue(ssoHttpPost, "type", SAMLR2MetadataConstants.SingleSignOnService_QNAME.toString());
        setPropertyValue(ssoHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2authnreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2authnstmt-to-samlr2assertion-plan");
        plansList.add(plan2);
        setPropertyRefs(ssoHttpPost, "identityPlans", plansList);
        endpoints.add(ssoHttpPost);
        
        Bean ssoHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoHttpArtifact.setName(idpBean.getName() + "-saml2-sso-http-artifact");
        setPropertyValue(ssoHttpArtifact, "name", ssoHttpArtifact.getName());
        setPropertyValue(ssoHttpArtifact, "type", SAMLR2MetadataConstants.SingleSignOnService_QNAME.toString());
        setPropertyValue(ssoHttpArtifact, "binding", SamlR2Binding.SAMLR2_ARTIFACT.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2authnreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2authnstmt-to-samlr2assertion-plan");
        plansList.add(plan2);
        setPropertyRefs(ssoHttpArtifact, "identityPlans", plansList);
        endpoints.add(ssoHttpArtifact);

        Bean ssoHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoHttpRedirect.setName(idpBean.getName() + "-saml2-sso-http-redirect");
        setPropertyValue(ssoHttpRedirect, "name", ssoHttpRedirect.getName());
        setPropertyValue(ssoHttpRedirect, "type", SAMLR2MetadataConstants.SingleSignOnService_QNAME.toString());
        setPropertyValue(ssoHttpRedirect, "binding", SamlR2Binding.SAMLR2_REDIRECT.getValue());
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2authnreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2authnstmt-to-samlr2assertion-plan");
        plansList.add(plan2);
        setPropertyRefs(ssoHttpRedirect, "identityPlans", plansList);
        endpoints.add(ssoHttpRedirect);

        Bean shbSOAP = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbSOAP.setName(idpBean.getName() + "-sso-shb-soap");
        setPropertyValue(shbSOAP, "name", shbSOAP.getName());
        setPropertyValue(shbSOAP, "type", SAMLR2MetadataConstants.IDPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbSOAP, "binding", SamlR2Binding.SSO_SOAP.getValue());
        setPropertyValue(shbSOAP, "location", "/SSO/SSHB/SOAP");
        endpoints.add(shbSOAP);

        Bean shbLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbLocal.setName(idpBean.getName() + "-sso-shb-local");
        setPropertyValue(shbLocal, "name", shbLocal.getName());
        setPropertyValue(shbLocal, "type", SAMLR2MetadataConstants.IDPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbLocal, "binding", SamlR2Binding.SSO_LOCAL.getValue());
        setPropertyValue(shbLocal, "location", "local:/" + idpBean.getName().toUpperCase() + "/SSO/SSHB/LOCAL");
        endpoints.add(shbLocal);
        
        Bean ssoSsoHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoSsoHttpArtifact.setName(idpBean.getName() + "-sso-sso-http-artifact");
        setPropertyValue(ssoSsoHttpArtifact, "name", ssoSsoHttpArtifact.getName());
        setPropertyValue(ssoSsoHttpArtifact, "type", SAMLR2MetadataConstants.SingleSignOnService_QNAME.toString());
        setPropertyValue(ssoSsoHttpArtifact, "binding", SamlR2Binding.SSO_ARTIFACT.getValue());
        setPropertyValue(ssoSsoHttpArtifact, "location", "/SSO/SSO/ARTIFACT");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2authnreq-to-samlr2resp-plan");
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean("samlr2authnstmt-to-samlr2assertion-plan");
        plansList.add(plan2);
        setPropertyRefs(ssoSsoHttpArtifact, "identityPlans", plansList);
        endpoints.add(ssoSsoHttpArtifact);
        
        Bean ssoSloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoSloSoap.setName(idpBean.getName() + "-sso-slo-soap");
        setPropertyValue(ssoSloSoap, "name", ssoSloSoap.getName());
        setPropertyValue(ssoSloSoap, "type", SAMLR2MetadataConstants.IDPInitiatedSingleLogoutService_QNAME.toString());
        setPropertyValue(ssoSloSoap, "binding", SamlR2Binding.SSO_SOAP.getValue());
        setPropertyValue(ssoSloSoap, "location", "/SSO/SLO/SOAP");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan);
        setPropertyRefs(ssoSloSoap, "identityPlans", plansList);
        endpoints.add(ssoSloSoap);

        Bean ssoSloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoSloLocal.setName(idpBean.getName() + "-sso-slo-local");
        setPropertyValue(ssoSloLocal, "name", ssoSloLocal.getName());
        setPropertyValue(ssoSloLocal, "type", SAMLR2MetadataConstants.IDPInitiatedSingleLogoutService_QNAME.toString());
        setPropertyValue(ssoSloLocal, "binding", SamlR2Binding.SSO_LOCAL.getValue());
        setPropertyValue(ssoSloLocal, "location", "local:/" + idpBean.getName().toUpperCase() + "/SSO/SLO/LOCAL");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean("samlr2sloreq-to-samlr2spsloreq-plan");
        plansList.add(plan);
        setPropertyRefs(ssoSloLocal, "identityPlans", plansList);
        endpoints.add(ssoSloLocal);
        
        setPropertyAsBeans(spChannelBean, "endpoints", endpoints);
        
        // plans
        Bean sloToSamlPlan = newBean(idpBeans, "samlr2sloreq-to-samlr2resp-plan", SamlR2SloRequestToSamlR2RespPlan.class);
        setPropertyRef(sloToSamlPlan, "bpmsManager", "bpms-manager");

        Bean sloToSamlSpSloPlan = newBean(idpBeans, "samlr2sloreq-to-samlr2spsloreq-plan", SamlR2SloRequestToSpSamlR2SloRequestPlan.class);
        setPropertyRef(sloToSamlSpSloPlan, "bpmsManager", "bpms-manager");
        
        Bean authnToSamlPlan = newBean(idpBeans, "samlr2authnreq-to-samlr2resp-plan", SamlR2AuthnRequestToSamlR2ResponsePlan.class);
        setPropertyRef(authnToSamlPlan, "bpmsManager", "bpms-manager");

        Bean stmtToAssertionPlan = newBean(idpBeans, "samlr2authnstmt-to-samlr2assertion-plan", SamlR2SecurityTokenToAuthnAssertionPlan.class);
        setPropertyRef(stmtToAssertionPlan, "bpmsManager", "bpms-manager");

        //Bean authnToSamlResponsePlan = newBean(idpBeans, "samlr2authnreq-to-samlr2response-plan", SamlR2AuthnReqToSamlR2RespPlan.class);
        //setPropertyRef(authnToSamlResponsePlan, "bpmsManager", "bpms-manager");
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        // SP Channel bean
        Bean spChannelBean = (Bean) event.getContext().get("spChannelBean");
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans beans = (Beans) event.getContext().get("beans");
        
        // claimsProvider
        Bean claimsChannel = getBean(idpBeans, spChannelBean.getName() + "-claims-channel");
        if (claimsChannel == null)
            throw new TransformException("No claims channel defined as " + spChannelBean.getName() + "-claim-channel");
        setPropertyRef(spChannelBean, "claimsProvider", claimsChannel.getName());

        // STS
        Bean sts = getBean(idpBeans, spChannelBean.getName() + "-sts");
        if (sts == null)
            throw new TransformException("No STS defined as " + spChannelBean.getName() + "-sts");
        setPropertyRef(spChannelBean, "securityTokenService", sts.getName());

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(beans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            addPropertyBeansAsRefs(mu, "channels", spChannelBean);
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

        return spChannelBean;
    }
}
