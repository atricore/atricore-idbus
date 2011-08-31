package org.atricore.idbus.capabilities.samlr2.main.claims.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsMediator;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsRequest;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsResponse;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.SamlR2PlanningConstants;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.samlr2.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.CredentialType;
import org.atricore.idbus.common.sso._1_0.protocol.SPCredentialsCallbackRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPCredentialsCallbackResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpUsernamePasswordClaimsProducer extends SamlR2Producer
        implements SAMLR2Constants, SAMLR2MessagingConstants, SamlR2PlanningConstants {

    private static final Log logger = LogFactory.getLog(SpUsernamePasswordClaimsProducer.class);

    public SpUsernamePasswordClaimsProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("Collecting Password claims from SP");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SamlR2ClaimsRequest claimsRequest = (SamlR2ClaimsRequest) in.getMessage().getContent();

        SamlR2ClaimsMediator mediator = ((SamlR2ClaimsMediator ) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        Channel issuer = claimsRequest.getIssuerChannel();

        // Get credentials for claims request
        // Resolve SP callback endpoint



        //CircleOfTrustMemberDescriptor sp = resolveSp(channel, claimsRequest.getSpAlias());

        FederatedLocalProvider sp = resolveSp(channel, claimsRequest.getSpAlias());

        // IDP That issued the claims request
        FederationChannel idpChannel = (FederationChannel) claimsRequest.getIssuerChannel();

        // SP To callback for claims
        FederationChannel spChannel = resolveIdPChannel(sp, idpChannel.getMember().getAlias());

        // SP Callback endpoint
        EndpointDescriptor spCallbackEd = resolveSpCallbackEndpoint(spChannel);

        SPCredentialsCallbackRequestType callbackReq = new SPCredentialsCallbackRequestType();
        callbackReq.setID(claimsRequest.getId());
        callbackReq.setRelayStateReference(claimsRequest.getTargetRelayState());

        if (logger.isTraceEnabled())
            logger.trace("Collecting claims using callback to " + spCallbackEd);

        SPCredentialsCallbackResponseType callbackResp =
                (SPCredentialsCallbackResponseType) channel.getIdentityMediator().sendMessage(callbackReq, spCallbackEd, spChannel);

        List<CredentialType> credentials = callbackResp.getCredentials();

        if (!callbackResp.getInReplayTo().equals(callbackReq.getID())) {
            logger.warn("Invalid callback response 'inReplayTo' : " + callbackResp);
        }

        if (logger.isTraceEnabled())
            logger.trace("Collected claims " + callbackResp.getID());

        ClaimSet claims = new ClaimSetImpl();

        // Addapt received simple claims to SAMLR Required token
        if (credentials != null) {

            if (credentials.size() == 1) {

                CredentialType credential = credentials.get(0);

                if (credential.getAny() instanceof UsernameTokenType) {

                    // Build a SAMLR2 Compatible Security token for Username/PasswordU
                    UsernameTokenType usernameToken = (UsernameTokenType) credential.getAny();
                    usernameToken.getOtherAttributes().put(new QName(AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue()), "TRUE");

                    Claim claim = new ClaimImpl(AuthnCtxClass.ATC_SP_PASSWORD_AUTHN_CTX.getValue(), usernameToken);
                    claims.addClaim(claim);

                } else {
                    logger.error("Unsupported token type " + credential.getAny());
                }
            } else {
                logger.error("Unsupported number of received tokens " + credentials.size());
            }
        } else {
            logger.error("No tokens found in local variable!");
        }

        SamlR2ClaimsResponse claimsResponse = new SamlR2ClaimsResponse (claimsRequest.getId() /* TODO : Generate new ID !*/,
                channel, claimsRequest.getId(), claims, claimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        // Use null location since this only supports is a 'local' bindings
        if (!endpoint.getBinding().equals(SamlR2Binding.SSO_LOCAL.toString())) {
            logger.error("Unsupported binding : " + endpoint.getBinding());
        }


        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getBinding(),
                "ClaimsResponseService", endpoint.getBinding(), null, null);


        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }


    protected FederatedLocalProvider resolveSp(Channel c, String spAlias) {
        FederatedLocalProvider channelProvider = null;
        if (c instanceof ClaimChannel) {
            channelProvider = ((ClaimChannel)c).getProvider();
        } else if (c instanceof IdPChannel) {
            channelProvider = ((IdPChannel)c).getProvider();
        } else {
            logger.error("Cannot resolve spAlias [" + spAlias + "] for channel [" + c.getName() + "], unknown channel type : " + c);
            return null;
        }

        CircleOfTrust cot = channelProvider.getCotManager().getCot();

        for (FederatedProvider fp : cot.getProviders()) {
            for (CircleOfTrustMemberDescriptor fpDescr : fp.getMembers()) {
                if (fpDescr.getAlias().equals(spAlias)) {
                    FederatedLocalProvider flp = (FederatedLocalProvider) fp;
                    if (logger.isTraceEnabled())
                        logger.trace("Selected SP " + flp.getName());
                    return flp;
                }
            }
        }

        return null;

    }

    protected FederationChannel resolveIdPChannel(FederatedLocalProvider sp, String targetAlias) {

        for (FederationChannel fChannel : sp.getChannels()) {
            FederatedProvider target = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : target.getMembers()) {
                if (member.getAlias().equals(targetAlias)) {

                    if (logger.isTraceEnabled())
                        logger.trace("Selected IDP Channel " + fChannel.getName());
                    return fChannel;
                }
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("Selected default IDP Channel " + sp.getChannel().getName());

        return sp.getChannel();

    }


    protected EndpointDescriptor resolveSpCallbackEndpoint(FederationChannel spChannel) {


        for (IdentityMediationEndpoint endpoint : spChannel.getEndpoints()) {

            if (endpoint.getType().equals(SamlR2Service.SPCredentialsCallbackService.toString()) &&
                endpoint.getBinding().equals(SamlR2Binding.SSO_LOCAL.getValue())) {

                EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint);

                if (logger.isDebugEnabled())
                    logger.debug("Selected SP Callback endpoint : " + ed);

                return ed;
            }
        }

        logger.warn("No SP Callback endpoint found for SP Channel " + spChannel.getName());

        return null;

    }

    protected FederationChannel resolveSpChannel(CircleOfTrustMemberDescriptor spDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        ClaimChannel cChannel = (ClaimChannel) channel;

        FederatedLocalProvider idp = cChannel.getProvider();

        FederationChannel spChannel = idp.getChannel();
        for (FederationChannel fChannel : idp.getChannels()) {

            FederatedProvider sp = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : sp.getMembers()) {
                if (member.getAlias().equals(spDescriptor.getAlias())) {

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP channel " + fChannel.getName() + " for provider " + sp.getName());
                    spChannel = fChannel;
                    break;
                }

            }

        }
        return spChannel;
    }

}
