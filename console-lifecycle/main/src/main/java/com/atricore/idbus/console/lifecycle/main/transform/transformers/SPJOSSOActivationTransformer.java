package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.JOSSOActivation;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SPInitiatedLogoutReqToSamlR2LogoutReqPlan;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SPSessionHeartBeatReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SPJOSSOActivationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(SPJOSSOActivationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof JOSSOActivation &&
                event.getContext().getParentNode() instanceof ServiceProvider;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        Beans spBeans = (Beans) event.getContext().get("spBeans");

        JOSSOActivation activation = (JOSSOActivation) event.getData();
        ServiceProvider sp = activation.getSp();

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for JOSSO Activation " + activation.getName()  + " of SP " + sp.getName());

        Bean spBean = null;
        Collection<Bean> b = getBeansOfType(spBeans, ServiceProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + b.size());
        }
        spBean = b.iterator().next();

        Bean bc = newBean(spBeans, normalizeBeanName(activation.getName()),
                "org.atricore.idbus.kernel.main.mediation.binding.BindingChannelImpl");

        setPropertyValue(bc, "name", bc.getName());
        setPropertyValue(bc, "description", activation.getDisplayName());

        setPropertyRef(bc, "provider", spBean.getName());
        setPropertyValue(bc, "location", resolveLocationUrl(sp) + "/" + activation.getPartnerAppId().toUpperCase());

        setPropertyRef(bc, "identityMediator", spBean.getName() + "-samlr2-mediator");

        // plans
        Bean spAuthnToSamlPlan = newBean(spBeans, spBean.getName() + "-spinitiatedauthnreq-to-samlr2autnreq-plan", SPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(spAuthnToSamlPlan, "bpmsManager", "bpms-manager");

        Bean spSloToSamlPlan = newBean(spBeans, spBean.getName() + "-spinitiatedsloreq-to-samlr2sloreq-plan", SPInitiatedLogoutReqToSamlR2LogoutReqPlan.class);
        setPropertyRef(spSloToSamlPlan, "bpmsManager", "bpms-manager");

        Bean sessionHeartBeatToSamlPlan = newBean(spBeans, spBean.getName() + "-spsessionheartbeatreq-to-samlr2autnreq-plan", SPSessionHeartBeatReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(sessionHeartBeatToSamlPlan, "bpmsManager", "bpms-manager");

        // TODO: set endpoints
        List<Bean> endpoints = new ArrayList<Bean>();

        Bean ssoHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoHttpRedirect.setName(spBean.getName() + "-sso-sso-http-redirect");
        setPropertyValue(ssoHttpRedirect, "name", ssoHttpRedirect.getName());
        setPropertyValue(ssoHttpRedirect, "type", SAMLR2MetadataConstants.SPInitiatedSingleSignOnService_QNAME.toString());
        setPropertyValue(ssoHttpRedirect, "binding", SamlR2Binding.SS0_REDIRECT.getValue());
        setPropertyValue(ssoHttpRedirect, "location", "/SSO/SSO/REDIR");
        List<Ref> plansList = new ArrayList<Ref>();
        Ref plan = new Ref();
        plan.setBean(spAuthnToSamlPlan.getName());
        plansList.add(plan);
        setPropertyRefs(ssoHttpRedirect, "identityPlans", plansList);
        endpoints.add(ssoHttpRedirect);

        Bean ssoHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ssoHttpArtifact.setName(spBean.getName() + "-sso-sso-http-artifact");
        setPropertyValue(ssoHttpArtifact, "name", ssoHttpArtifact.getName());
        setPropertyValue(ssoHttpArtifact, "type", SAMLR2MetadataConstants.SPInitiatedSingleSignOnService_QNAME.toString());
        setPropertyValue(ssoHttpArtifact, "binding", SamlR2Binding.SSO_ARTIFACT.getValue());
        setPropertyValue(ssoHttpArtifact, "location", "/SSO/SSO/ARTIFACT");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spAuthnToSamlPlan.getName());
        plansList.add(plan);
        setPropertyRefs(ssoHttpArtifact, "identityPlans", plansList);
        endpoints.add(ssoHttpArtifact);

        Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpRedirect.setName(spBean.getName() + "-sso-slo-http-redirect");
        setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
        setPropertyValue(sloHttpRedirect, "type", SAMLR2MetadataConstants.SPInitiatedSingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpRedirect, "binding", SamlR2Binding.SS0_REDIRECT.getValue());
        setPropertyValue(sloHttpRedirect, "location", "/SSO/SLO/REDIR");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spSloToSamlPlan.getName());
        plansList.add(plan);
        setPropertyRefs(sloHttpRedirect, "identityPlans", plansList);
        endpoints.add(sloHttpRedirect);

        Bean sloHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        sloHttpArtifact.setName(spBean.getName() + "-sso-slo-http-artifact");
        setPropertyValue(sloHttpArtifact, "name", sloHttpArtifact.getName());
        setPropertyValue(sloHttpArtifact, "type", SAMLR2MetadataConstants.SPInitiatedSingleLogoutService_QNAME.toString());
        setPropertyValue(sloHttpArtifact, "binding", SamlR2Binding.SSO_ARTIFACT.getValue());
        setPropertyValue(sloHttpArtifact, "location", "/SSO/SLO/ARTIFACT");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spSloToSamlPlan.getName());
        plansList.add(plan);
        setPropertyRefs(sloHttpArtifact, "identityPlans", plansList);
        endpoints.add(sloHttpArtifact);

        Bean aisAuthSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
        aisAuthSoap.setName(spBean.getName() + "-sso-aisauth-soap");
        setPropertyValue(aisAuthSoap, "name", aisAuthSoap.getName());
        setPropertyValue(aisAuthSoap, "type", SAMLR2MetadataConstants.AssertIdentityWithSimpleAuthenticationService_QNAME.toString());
        setPropertyValue(aisAuthSoap, "binding", SamlR2Binding.SSO_SOAP.getValue());
        setPropertyValue(aisAuthSoap, "location", "/SSO/IAAUTHN/SOAP");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spSloToSamlPlan.getName());
        plansList.add(plan);
        Ref plan2 = new Ref();
        plan2.setBean(sessionHeartBeatToSamlPlan.getName());
        plansList.add(plan2);
        setPropertyRefs(aisAuthSoap, "identityPlans", plansList);
        endpoints.add(aisAuthSoap);

        Bean aisAuthLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        aisAuthLocal.setName(spBean.getName() + "-sso-aisauth-local");
        setPropertyValue(aisAuthLocal, "name", aisAuthLocal.getName());
        setPropertyValue(aisAuthLocal, "type", SAMLR2MetadataConstants.AssertIdentityWithSimpleAuthenticationService_QNAME.toString());
        setPropertyValue(aisAuthLocal, "binding", SamlR2Binding.SSO_LOCAL.getValue());
        setPropertyValue(aisAuthLocal, "location", "local://" + sp.getLocation().getUri().toUpperCase() + "/SSO/IAAUTHN/LOCAL");
        plansList = new ArrayList<Ref>();
        plan = new Ref();
        plan.setBean(spSloToSamlPlan.getName());
        plansList.add(plan);
        plan2 = new Ref();
        plan2.setBean(sessionHeartBeatToSamlPlan.getName());
        plansList.add(plan2);
        setPropertyRefs(aisAuthLocal, "identityPlans", plansList);
        endpoints.add(aisAuthLocal);

        Bean shbSOAP = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbSOAP.setName(spBean.getName() + "-sso-shb-soap");
        setPropertyValue(shbSOAP, "name", shbSOAP.getName());
        setPropertyValue(shbSOAP, "type", SAMLR2MetadataConstants.SPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbSOAP, "binding", SamlR2Binding.SSO_SOAP.getValue());
        setPropertyValue(shbSOAP, "location", "/SSO/SSHB/SOAP");
        endpoints.add(shbSOAP);

        Bean shbLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbLocal.setName(spBean.getName() + "-sso-shb-local");
        setPropertyValue(shbLocal, "name", shbLocal.getName());
        setPropertyValue(shbLocal, "type", SAMLR2MetadataConstants.SPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbLocal, "binding", SamlR2Binding.SSO_LOCAL.getValue());
        setPropertyValue(shbLocal, "location", "local://" + sp.getLocation().getUri().toUpperCase() + "/SSO/SSHB/LOCAL");
        endpoints.add(shbLocal);

        setPropertyAsBeans(bc, "endpoints", endpoints);

        setPropertyRef(spBean, "bindingChannel", bc.getName());

     }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }
}

