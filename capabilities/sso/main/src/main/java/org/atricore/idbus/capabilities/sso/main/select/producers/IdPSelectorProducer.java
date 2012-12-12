package org.atricore.idbus.capabilities.sso.main.select.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.capabilities.sso.main.select.internal.EntitySelectionState;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsRequestImpl;
import org.atricore.idbus.capabilities.sso.main.select.spi.*;
import org.atricore.idbus.capabilities.sso.main.select.SSOEntitySelectorMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.StatusDetails;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.List;

/**
 *
 */
public class IdPSelectorProducer extends SSOProducer {

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static final Log logger = LogFactory.getLog(IdPSelectorProducer.class);

    public IdPSelectorProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        Object content = in.getMessage().getContent();

        try {

            // ------------------------------------------------------------------------------------------
            // Select Entity
            // ------------------------------------------------------------------------------------------
            if (content instanceof SelectEntityRequestType) {
                SelectEntityRequestType request = (SelectEntityRequestType) content;

                if (logger.isDebugEnabled())
                    logger.debug("Starting IdP selection for " + endpointRef);

                doProcessSelectEntityRequest(exchange, state, request);

            } else {
                throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                        null,
                        StatusDetails.UNKNOWN_REQUEST.getValue(),
                        content.getClass().getName(),
                        null);
            }



        } catch (Exception e) {
            throw new IdentityMediationFault(StatusCode.TOP_RESPONDER.getValue(),
                    null,
                    StatusDetails.UNKNOWN_REQUEST.getValue(),
                    content.getClass().getName(),
                    e);
        }
    }

    protected void doProcessSelectEntityRequest(CamelMediationExchange exchange, MediationState state, SelectEntityRequestType request) throws SSOException {

        // Do we need to collect more information to make a decision ?!
        SSOEntitySelectorMediator mediator = (SSOEntitySelectorMediator) channel.getIdentityMediator();
        EntitySelectorManager manager = mediator.getSelectorManager();

        // Information can be found:

        // 1. In the request (preferred IdP, requested IdP, etc)
        // 2. As provider state variables (user IP, user-agent, etc)
        // 3. Provided as additional endpoints

        EntitySelectionState selectionState = new EntitySelectionState();

        ClaimSet attrSet = new ClaimSetImpl();
        state.setLocalVariable(getProvider().getName().toUpperCase() + "_ATTR_SET", attrSet);

        if (request.getRequestAttribute() != null) {
            for (RequestAttributeType attrType : request.getRequestAttribute()) {

                if (attrType.getValue() != null) {
                    UserClaim attr = new UserClaimImpl(attrType.getName(), attrType.getValue());
                    attrSet.addClaim(attr);
                }
            }
            selectionState.setAttributes(attrSet);
        }



        List<String> endpoints = manager.resolveAttributeEndpoints(mediator.getPreferredStrategy());
        if (endpoints != null) {

            // Send Attributes Request
            Integer idx = (Integer) state.getLocalVariable(getProvider().getName().toUpperCase() + "_ATTR_SELECTION_ENDPOINT_IDX");
            if (idx == null) {
                idx = 0;
            } else {
                idx ++;
            }
            selectionState.setAttributesEndpointIdx(idx);

            if (idx < endpoints.size()) {

                // Store all state and send a 'select attrs request'

                // We need to keep track of the endpoints, for now only one supported !!!
                UserClaimsRequest attrReq = new UserClaimsRequestImpl(
                        uuidGenerator.generateId(),
                        channel,
                        endpoint,
                        state.getLocalState().getId());

                // For now, artifact binding is required.
                EndpointDescriptor ed = new EndpointDescriptorImpl(
                        "SelectAttributesEndpoint",
                        "UserClaimsRequest",
                        SSOBinding.SSO_ARTIFACT.toString(),
                        endpoints.get(idx),
                        null);

                // Send SAMLR2 Message back
                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        attrReq,
                        "UserClaimsRequest",
                        null,
                        ed,
                        state));

                exchange.setOut(out);

                selectionState.setRequest(request);
                selectionState.setAttributes(attrSet);

                state.setLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE", selectionState);

                return;
            }

            state.removeLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");
        }

        // Get selection strategies from mediator ...
        EntitySelectionContext ctx = new EntitySelectionContext(getCotManager(), attrSet, request);
        CircleOfTrustMemberDescriptor entity = manager.selectEntity(mediator.getPreferredStrategy(), ctx);

        // TODO : Do something with the outcome
        SelectEntityResponseType response = new SelectEntityResponseType();

        response.setEntityId(entity.getId());

        String location = request.getReplyTo();
        if (location == null)
            throw new SSOException("Reply-To attribute is required for select entity request " + request.getID());

        // For now, artifact binding is required.
        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "IDPSelectorResponseEndpoint",
                "EntitySelectorResponse",
                SSOBinding.SSO_ARTIFACT.toString(),
                request.getReplyTo(),
                null);

        // Send SAMLR2 Message back
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                response,
                "SelectEntityResponse",
                null,
                ed,
                state));

        exchange.setOut(out);

    }

    protected void doProcessSelectAttributesResponse(CamelMediationExchange exchange, MediationState state, UserClaimsResponse attrsResp) throws SSOException {

        // Do we need to collect more information to make a decision ?!
        SSOEntitySelectorMediator mediator = (SSOEntitySelectorMediator) channel.getIdentityMediator();
        EntitySelectorManager manager = mediator.getSelectorManager();

        EntitySelectionState selectionState = (EntitySelectionState) state.getLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");
        for (Claim c : attrsResp.getClaimSet().getClaims()) {
            selectionState.getAttributes().addClaim(c);
        }


        List<String> endpoints = manager.resolveAttributeEndpoints(mediator.getPreferredStrategy());
        if (endpoints != null) {

            // Send Attributes Request
            Integer idx = (Integer) state.getLocalVariable(getProvider().getName().toUpperCase() + "_ATTR_SELECTION_ENDPOINT_IDX");
            if (idx == null) {
                idx = 0;
            } else {
                idx ++;
            }

            selectionState.setAttributesEndpointIdx(idx);

            if (idx < endpoints.size()) {
                // Store all state and send a 'select attrs request'

                // We need to keep track of the endpoints, for now only one supported !!!
                UserClaimsRequest attrReq = new UserClaimsRequestImpl(
                        uuidGenerator.generateId(),
                        channel,
                        endpoint,
                        state.getLocalState().getId());

                // For now, artifact binding is required.
                EndpointDescriptor ed = new EndpointDescriptorImpl(
                        "SelectAttributesEndpoint",
                        "UserClaimsRequest",
                        SSOBinding.SSO_ARTIFACT.toString(),
                        endpoints.get(idx),
                        null);

                // Send SAMLR2 Message back
                CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        attrReq,
                        "UserClaimsRequest",
                        null,
                        ed,
                        state));

                exchange.setOut(out);

                state.setLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE", selectionState);

                return;

            }
        }

        SelectEntityRequestType request = (SelectEntityRequestType) state.getLocalVariable(getProvider().getName().toUpperCase() + "_ENTITY_SELECT_REQ");
        state.removeLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");

        // Get selection strategies from mediator ...
        EntitySelectionContext ctx = new EntitySelectionContext(getCotManager(), selectionState.getAttributes(), request);
        CircleOfTrustMemberDescriptor entity = manager.selectEntity(mediator.getPreferredStrategy(), ctx);

        // TODO : Do something with the outcome
        SelectEntityResponseType response = new SelectEntityResponseType();

        response.setEntityId(entity.getId());

        String location = request.getReplyTo();
        if (location == null)
            throw new SSOException("Reply-To attribute is required for select entity request " + request.getID());

        // For now, artifact binding is required.
        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "IDPSelectorResponseEndpoint",
                "EntitySelectorResponse",
                SSOBinding.SSO_ARTIFACT.toString(),
                request.getReplyTo(),
                null);

        // Send SAMLR2 Message back
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                response,
                "SelectEntityResponse",
                null,
                ed,
                state));

        exchange.setOut(out);

    }
}


