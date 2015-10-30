package org.atricore.idbus.capabilities.sso.main.idp.producers;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sso.support.profiles.slo.LogoutReason;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public class IdPInitiatedSingleLogoutProducer extends SSOProducer {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static Log logger = LogFactory.getLog(IdPInitiatedSingleLogoutProducer.class);

    public IdPInitiatedSingleLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Processing IDP Initiated Single SingOn on " + endpoint.getBinding());

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        doProcessIdPInitiatedSLO(exchange);

    }

    protected void doProcessIdPInitiatedSLO(CamelMediationExchange exchange) throws IdentityMediationException {

        try {

            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

            MediationState state = in.getMessage().getState();
            String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";
            IdPSecurityContext secCtx = (IdPSecurityContext) state.getLocalVariable(varName);
            SPChannel spChannel = (SPChannel) channel;
            String idpAlias = spChannel.getMember().getAlias();
            String spAlias = state.getTransientVariable("atricore_sp_alias");

            if (spAlias != null) {

                CircleOfTrustManager cot = spChannel.getFederatedProvider().getCotManager();
                CircleOfTrustMemberDescriptor sp = cot.lookupMemberByAlias(spAlias);

                if (sp == null) {
                    throw new IdentityMediationException("Unknown SP Alias " + spAlias);
                }

                // SP that we should
                EntityDescriptorType ed = (EntityDescriptorType) sp.getMetadata().getEntry();
                NameIDType issuer = new NameIDType();
                issuer.setFormat(NameIDFormat.ENTITY.getValue());
                issuer.setValue(ed.getEntityID());

                EndpointDescriptor slo = resolveIdPSloEndpoint(idpAlias, new SSOBinding[]{
                        SSOBinding.SAMLR2_REDIRECT, SSOBinding.SAMLR2_POST, SSOBinding.SAMLR2_ARTIFACT}, true);

                // TODO : Use a plan
                LogoutRequestType sloRequest = new LogoutRequestType();
                sloRequest.setID(uuidGenerator.generateId());
                sloRequest.setVersion(SAMLR2Constants.SAML_VERSION);

                // IssueInstant [required]
                Date dateNow = new java.util.Date();
                sloRequest.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));
                sloRequest.setIssuer(issuer);
                sloRequest.setDestination(slo.getLocation());
                sloRequest.setReason(LogoutReason.SAMLR2_USER.toString());
                Date notOnOrAfter = new java.util.Date(System.currentTimeMillis() + (1000L * 60L * 5L));
                sloRequest.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(notOnOrAfter));

                if (secCtx != null && secCtx.getSessionIndex() != null) {
                    Set<SSOUser> ssoUsers = secCtx.getSubject().getPrincipals(SSOUser.class);
                    if (ssoUsers.size() > 1) {
                        SSOUser user = ssoUsers.iterator().next();
                        NameIDType subjectNameID = new NameIDType();
                        subjectNameID.setFormat(NameIDFormat.UNSPECIFIED.getValue());
                        subjectNameID.setValue(user.getName());
                        sloRequest.setNameID(subjectNameID);
                    }
                }

                if (logger.isDebugEnabled())
                    logger.debug("Sending SAML SLO Request to " + slo);

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        sloRequest, "LogoutRequest", null, slo, in.getMessage().getState()));

                exchange.setOut(out);

            } else {

                // If no SP was requested, we just perform an SLO and send the user to the default Dashboard URL
                // (like the TOUT use case, but using front-channel)

                IDPInitiatedLogoutRequestType sloRequest = new IDPInitiatedLogoutRequestType();
                if (secCtx != null)
                    sloRequest.setSsoSessionId(secCtx.getSessionIndex());
                sloRequest.setID(uuidGenerator.generateId());

                EndpointDescriptor slo = resolveIdpSloEndpoint((SPChannel) channel);

                if (logger.isDebugEnabled())
                    logger.debug("Sending new IdP-initiated SLO Request to " + slo);

                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        sloRequest, "LogoutRequest", null, slo, in.getMessage().getState()));

                exchange.setOut(out);
            }

        } catch (Exception e) {
            throw new IdentityMediationException(e.getMessage(), e);
        }
    }

    /**
     * Looks for a SingleSignOn service using SSO ART binding.
     */
    protected EndpointDescriptor resolveIdpSloEndpoint(SPChannel spChannel) throws SSOException {
        // User default channel to signal SLO

        // Look for local SLO endpoint, it will also receive SLO IDP Initiated requests
        for (IdentityMediationEndpoint endpoint : spChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.SingleLogoutService.toString())) {

                if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                    // We need to build an endpoint descriptor descriptor now ...

                    String location = endpoint.getLocation().startsWith("/") ?
                            spChannel.getLocation() + endpoint.getLocation() :
                            endpoint.getLocation();

                    return new EndpointDescriptorImpl(spChannel.getName() + "-sso-slo-art",
                            SSOService.SingleLogoutService.toString(),
                            SSOBinding.SSO_ARTIFACT.toString(),
                            location,
                            null);

                }
            }
        }

        throw new SSOException("No IDP SLO endpoint for channel " + spChannel.getName());
    }


}
