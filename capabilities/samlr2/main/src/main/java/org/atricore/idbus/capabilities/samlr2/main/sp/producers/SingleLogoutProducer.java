/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.main.sp.producers;

import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.samlr2.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.samlr2.main.sp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2ResponseException;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusCode;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusDetails;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureValidationException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.*;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IDPSingleSignOnServiceProducer.java 1246 2009-06-05 20:30:58Z sgonzalez $
 */
public class SingleLogoutProducer extends SamlR2Producer {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog( SingleLogoutProducer.class );

    public SingleLogoutProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess ( CamelMediationExchange exchange) throws Exception {
        
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        logger.debug("Processing SLO Message : " + in.getMessage().getContent());

        if (in.getMessage().getContent() instanceof StatusResponseType) {

            // A response to a previous Logout Request
            StatusResponseType samlResponse = (StatusResponseType) in.getMessage().getContent();
            if (logger.isDebugEnabled())
                logger.debug("Received SAML2 SLO Response " + samlResponse.getID());
            doProcessStatusResponse(exchange, samlResponse);

        } else if (in.getMessage().getContent() instanceof LogoutRequestType) {
            LogoutRequestType samlSloRequest = (LogoutRequestType) in.getMessage().getContent();
            if (logger.isDebugEnabled())
                logger.debug("Received SSO SLO Request " + samlSloRequest.getID());

            doProcessLogoutRequest(exchange, samlSloRequest);

        } else {
            throw new SamlR2Exception("Unsupported message type " + in.getMessage().getContent());
        }


    }

    protected void doProcessLogoutRequest(CamelMediationExchange exchange, LogoutRequestType sloRequest)
            throws Exception {

        // TODO : Validate SLO Request
        // validateRequest()
        
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SPSecurityContext secCtx =
                (SPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

        CircleOfTrustMemberDescriptor idp = getProvider().getCotManager().loolkupMemberByAlias(sloRequest.getIssuer().getValue());
        if (secCtx == null || !idp.getAlias().equals(secCtx.getIdpAlias())) {
            // We're gettingn an SLO from an IDP that is not the one that created the current session, reject the request.
            logger.warn("Unexpected SLO Request received from IDP " + sloRequest.getIssuer().getValue());
            // TODO : Send status
        } else {

            SSOSessionManager sessionMgr = ((IdPChannel)channel).getSessionManager();
            try {
                sessionMgr.invalidate(secCtx.getSessionIndex());
            } catch (NoSuchSessionException e) {
                logger.debug("Session already invalidated " + secCtx.getSessionIndex());
            }
            secCtx.clear();
            in.getMessage().getState().removeLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        }

        EndpointDescriptor destination = resolveIdPSloEndpoint(idp.getAlias(),
                new SamlR2Binding[] {SamlR2Binding.asEnum(endpoint.getBinding()) },
                true);

        ResponseType samlResponse = buildSamlSloResponse(exchange, sloRequest, idp, destination);

        logger.debug("Sending SAML SLO Response to " + destination);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(samlResponse.getID(),
                samlResponse, "ResponseType", null, destination, in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected ResponseType buildSamlSloResponse(CamelMediationExchange exchange,
                                             LogoutRequestType sloRequest,
                                             CircleOfTrustMemberDescriptor sp, EndpointDescriptor spEndpoint) throws Exception {
        // Build sloresponse
        IdentityPlan identityPlan = findIdentityPlanOfType(SamlR2SloRequestToSamlR2RespPlan.class);
        IdentityPlanExecutionExchange idPlanExchange = createIdentityPlanExecutionExchange();

        // Publish SP springmetadata
        idPlanExchange.setProperty(VAR_DESTINATION_COT_MEMBER, sp);
        idPlanExchange.setProperty(VAR_DESTINATION_ENDPOINT_DESCRIPTOR, spEndpoint);
        idPlanExchange.setProperty(VAR_REQUEST, sloRequest);

        // Create in/out artifacts
        IdentityArtifact<LogoutRequestType> in =
            new IdentityArtifactImpl<LogoutRequestType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "LogoutRequest"),
                    sloRequest);

        idPlanExchange.setIn(in);

        IdentityArtifact<ResponseType> out =
                new IdentityArtifactImpl<ResponseType>(new QName(SAMLR2Constants.SAML_PROTOCOL_NS, "Response"),
                        new ResponseType());
        idPlanExchange.setOut(out);

        // Prepare execution
        identityPlan.prepare(idPlanExchange);

        // Perform execution
        identityPlan.perform(idPlanExchange);

        if (!idPlanExchange.getStatus().equals(IdentityPlanExecutionStatus.SUCCESS)) {
            throw new SecurityTokenEmissionException("Identity plan returned : " + idPlanExchange.getStatus());
        }

        if (idPlanExchange.getOut() == null)
            throw new SecurityTokenEmissionException("Plan Exchange OUT must not be null!");

        return (ResponseType) idPlanExchange.getOut().getContent();
    }


    protected void doProcessStatusResponse(CamelMediationExchange exchange, StatusResponseType samlResponse)
            throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        // Received SSO request from binding, we're responding to this.
        SPInitiatedLogoutRequestType ssoLogoutRequest = (SPInitiatedLogoutRequestType)
                in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedLogoutRequest");
        in.getMessage().getState().removeLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedLogoutRequest");

        // Security Context
        SPSecurityContext secCtx =
                (SPSecurityContext) in.getMessage().getState().getLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        in.getMessage().getState().removeLocalVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");

        // TODO : Validate SLO Response
        LogoutRequestType logoutRequest = (LogoutRequestType)
                in.getMessage().getState().getLocalVariable("urn:oasis:names:tc:SAML:2.0:protocol:LogoutRequest");
        in.getMessage().getState().removeLocalVariable("urn:oasis:names:tc:SAML:2.0:protocol:LogoutRequest");
        validateResponse(logoutRequest, (ResponseType) in.getMessage().getContent(), in.getMessage().getRawContent());
        // validateResponse(samlResponse);

        // Received SAML2 Response from IdP
        // TODO : Use plans ?
        SSOResponseType ssoResponse = new SSOResponseType();
        ssoResponse.setID(uuidGenerator.generateId());
        String destinationLocation = ((SamlR2SPMediator) channel.getIdentityMediator()).getSpBindingSLO();

        EndpointDescriptor destination =
                new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "SingleLogoutService",
                        SamlR2Binding.SSO_ARTIFACT.getValue(),
                        destinationLocation, null);

        if (ssoLogoutRequest != null) {
            logger.debug("SLO Response in reply to " + ssoLogoutRequest.getID());
            ssoResponse.setInReplayTo(ssoLogoutRequest.getID());
            if (ssoLogoutRequest.getReplyTo() != null) {

                logger.debug("Using requested reply destination : " + ssoLogoutRequest.getReplyTo());

                destination = new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "SingleLogoutService",
                        SamlR2Binding.SSO_ARTIFACT.getValue(),
                        ssoLogoutRequest.getReplyTo(), null);
            }
        }

        destroySPSecurityContext(exchange, secCtx);

        logger.debug("Sending JOSSO SLO Response to " + destination);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPLogoutResponse", null, destination, in.getMessage().getState()));

        exchange.setOut(out);

    }

   // TODO : Reuse basic SAML request validations ....
    protected ResponseType validateResponse(LogoutRequestType request,
                                            ResponseType response,
                                            String originalResponse)
            throws SamlR2ResponseException, SamlR2Exception {

        SamlR2SPMediator mediator = (SamlR2SPMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();
        SamlR2Encrypter encrypter = mediator.getEncrypter();

        // Metadata from the IDP
        String idpAlias = null;
        IDPSSODescriptorType idpMd = null;
        try {
            idpAlias = response.getIssuer().getValue();
            MetadataEntry md = getCotManager().findEntityMetadata(idpAlias);
            EntityDescriptorType saml2Md = (EntityDescriptorType) md.getEntry();
            boolean found = false;
            for (RoleDescriptorType roleMd : saml2Md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (roleMd instanceof IDPSSODescriptorType) {
                    idpMd = (IDPSSODescriptorType) roleMd;
                }
            }

        } catch (CircleOfTrustManagerException e) {
            throw new SamlR2ResponseException(response,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.NO_SUPPORTED_IDP,
                    null,
                    response.getIssuer().getValue(),
                    e);
        }

        // Request can be null for IDP initiated SSO
    	EndpointDescriptor epointDesc;
		try {
			epointDesc = channel.getIdentityMediator().resolveEndpoint(channel, endpoint);
		} catch (IdentityMediationException e1) {
			throw new SamlR2ResponseException(response,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.RESOURCE_NOT_RECOGNIZED,
                    StatusDetails.INTERNAL_ERROR,
                    "Cannot resolve endpoint descriptor", e1);
		}

        // --------------------------------------------------------
        // Validate response:
        // --------------------------------------------------------

        // Destination
    	//saml2 core, section 3.2.2
    	//saml2 binding, sections 3.4.5.2 & 3.5.5.2
    	if(response.getDestination() != null){

    		if(!epointDesc.getLocation().equals(response.getDestination())){
    			throw new SamlR2ResponseException(response,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_DESTINATION);
    		}

    	} else if(response.getSignature() != null &&
                (epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST") ||
    			epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"))) {
    		throw new SamlR2ResponseException(response,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.NO_DESTINATION);
    	}

        // IssueInstant
		/*
		   -  required
		   -  check that the response time is not before request time (use UTC)
		   -  check that time difference is not bigger than X
		   */
    	if(response.getIssueInstant() == null){
    		throw new SamlR2ResponseException(response,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_ISSUE_INSTANT);

    	} else if(request != null) {

            // Request can be null for IDP initiated SSO
    		if(response.getIssueInstant().compare(request.getIssueInstant()) <= 0){
    			throw new SamlR2ResponseException(response,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                        StatusDetails.INVALID_ISSUE_INSTANT,
                        response.getIssueInstant().toGregorianCalendar().toString() +
                                    " earlier than request issue instant.");

    		} else {

                long ttl = mediator.getRequestTimeToLive();

                long res = response.getIssueInstant().toGregorianCalendar().getTime().getTime();
                long req = request.getIssueInstant().toGregorianCalendar().getTime().getTime();

                if (logger.isDebugEnabled())
                    logger.debug("TTL : " + res + " - " +  req + " = " + (res - req));

    			if(response.getIssueInstant().toGregorianCalendar().getTime().getTime()
    					- request.getIssueInstant().toGregorianCalendar().getTime().getTime() > ttl) {

    				throw new SamlR2ResponseException(response,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                            StatusDetails.INVALID_ISSUE_INSTANT,
                            response.getIssueInstant().toGregorianCalendar().toString() +
                                    " expired after " + ttl + "ms");
    			}
    		}
    	}

        // Version, saml2 core, section 3.2.2
    	if(response.getVersion() == null) {
    		throw new SamlR2ResponseException(response,
                    StatusCode.TOP_VERSION_MISSMATCH,
                    null,
                    StatusDetails.INVALID_VERSION);
    	}

        if (!response.getVersion().equals(SAML_VERSION)){

            throw new SamlR2ResponseException(response,
                    StatusCode.TOP_VERSION_MISSMATCH,
                    null, // TODO : Check version!
                    StatusDetails.UNSUPPORTED_VERSION,
                    response.getVersion());
        }

        // InResponseTo, saml2 core, section 3.2.2
    	// Request can be null for IDP initiated SSO


    	if(request != null) {
            if (response.getInResponseTo() == null) {
                throw new SamlR2ResponseException(response,
                        StatusCode.TOP_REQUESTER,
                        null,
                        StatusDetails.NO_IN_RESPONSE_TO);

            } else if (!request.getID().equals(response.getInResponseTo())) {
                throw new SamlR2ResponseException(response,
                        StatusCode.TOP_REQUESTER,
                        null,
                        StatusDetails.INVALID_RESPONSE_ID,
                        request.getID() + "/ " + response.getInResponseTo());
            }

    	}

        // Status.StatusDetails
    	if(response.getStatus() != null) {
    		if(response.getStatus().getStatusCode() != null) {

    			if(StringUtils.isEmpty(response.getStatus().getStatusCode().getValue())
    					|| !isStatusCodeValid(response.getStatus().getStatusCode().getValue())){

    				throw new SamlR2ResponseException(response,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                            StatusDetails.INVALID_STATUS_CODE,
                            response.getStatus().getStatusCode().getValue());
    			}
    		} else {
    			throw new SamlR2ResponseException(response,
                        StatusCode.TOP_REQUESTER,
                        StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                        StatusDetails.NO_STATUS_CODE);
    		}
    	} else {
    		throw new SamlR2ResponseException(response,
                    StatusCode.TOP_REQUESTER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_STATUS);
    	}

		// XML Signature, saml2 core, section 5
        if (mediator.isEnableSignatureValidation()) {



            if(response.getSignature() != null && mediator.isEnableSignatureValidation()) {
                try {

                    if (originalResponse != null)
                        signer.validate(idpMd, originalResponse);
                    else
                        signer.validate(idpMd, response);

                } catch (SamlR2SignatureValidationException e) {
                    throw new SamlR2ResponseException(response,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                } catch (SamlR2SignatureException e) {
                    //other exceptions like JAXB, xml parser...
                    throw new SamlR2ResponseException(response,
                            StatusCode.TOP_REQUESTER,
                            StatusCode.REQUEST_DENIED,
                            StatusDetails.INVALID_RESPONSE_SIGNATURE, e);
                }
            }
        }

        return response;
    }

    protected void destroySPSecurityContext(CamelMediationExchange exchange,
                                            SPSecurityContext secCtx) throws SamlR2Exception {

        CircleOfTrustMemberDescriptor idp = getCotManager().loolkupMemberByAlias(secCtx.getIdpAlias());
        IdPChannel idpChannel = (IdPChannel) resolveIdpChannel(idp);
        SSOSessionManager ssoSessionManager = idpChannel.getSessionManager();
        secCtx.clear();

        try {
            ssoSessionManager.invalidate(secCtx.getSessionIndex());
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            in.getMessage().getState().removeRemoteVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        } catch (NoSuchSessionException e) {
            logger.debug("SSO Session already invalidated " + secCtx.getSessionIndex());
        } catch (Exception e) {
            throw new SamlR2Exception(e);
        }

    }

    /**
     * @return
     */
    protected FederationChannel resolveIdpChannel(CircleOfTrustMemberDescriptor idpDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        FederatedLocalProvider sp = getProvider();

        FederationChannel idpChannel = sp.getChannel();
        for (FederationChannel fChannel : sp.getChannels()) {

            FederatedProvider idp = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : idp.getMembers()) {
                if (member.getAlias().equals(idpDescriptor.getAlias())) {

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP channel " + fChannel.getName() + " for provider " + idp.getName());
                    idpChannel = fChannel;
                    break;
                }
            }
        }

        return idpChannel;
    }

}
