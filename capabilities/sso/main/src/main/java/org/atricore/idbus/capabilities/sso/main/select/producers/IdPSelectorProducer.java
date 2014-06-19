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
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
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

            // TODO : Keep track of Selected IdP (session)
            // TODO : Remember Selected IdP (persistente cookie)

            // ------------------------------------------------------------------------------------------
            // Select Entity
            // ------------------------------------------------------------------------------------------
            if (content instanceof SelectEntityRequestType) {
                SelectEntityRequestType request = (SelectEntityRequestType) content;

                if (logger.isDebugEnabled())
                    logger.debug("Starting IdP selection for " + endpointRef);

                doProcessSelectEntityRequest(exchange, state, request);

            } else if (content instanceof UserClaimsResponse) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing claims response for " + endpointRef);

                // Claims collected from the user, to make a selection decision.
                doProcessUserClaimsResponse(exchange, state, (UserClaimsResponse) content);

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
        EntitySelectorManager entitySelectorMgr = mediator.getSelectorManager();

        CircleOfTrustMemberDescriptor previouslySelectedCotMember = (CircleOfTrustMemberDescriptor) state.getLocalVariable(getProvider().getName().toUpperCase() + "_SELECTED_ENTITY");

        if (logger.isDebugEnabled())
            logger.debug("Found previously selected entity " + previouslySelectedCotMember);

        // Information to make a decision can be obtain in the following ways:

        // 1. In the request (preferred IdP, requested IdP, etc)
        // 2. As provider state variables (user IP, user-agent, etc)
        // 3. Provided by additional endpoints as User Claims

        EntitySelectionState selectionState = new EntitySelectionState();
        ClaimSet userClaims = new ClaimSetImpl();

        if (request.getRequestAttribute() != null) {
            for (RequestAttributeType attrType : request.getRequestAttribute()) {

                if (attrType.getValue() != null) {
                    UserClaim attr = new UserClaimImpl(attrType.getName(), attrType.getValue());
                    userClaims.addClaim(attr);
                }
            }
            selectionState.setUserClaims(userClaims);
            if (previouslySelectedCotMember != null)
                selectionState.setPreviousCotMember(previouslySelectedCotMember.getAlias());
        }

        selectionState.setRequest(request);

        EntitySelectionContext ctx = new EntitySelectionContext(state,
                selectionState,
                getCotManager(),
                selectionState.getUserClaims(),
                request);

        // We already have some attributes in the selection state
        //List<EndpointDescriptor> endpoints = entitySelectorMgr.resolveUserClaimsEndpoints(ctx, (SelectorChannel) channel, mediator.getPreferredStrategy());

        List<EntitySelector> selectors = entitySelectorMgr.resolveSelectors(ctx, (SelectorChannel) channel, mediator.getPreferredStrategy());

        // Store selection state
        state.setLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE", selectionState);

        // Try to find an IdP
        CircleOfTrustMemberDescriptor entity = null;

        EntitySelector selector = processNextSelector(exchange, selectors, ctx);
        while (selector != null && entity == null) {

            if (logger.isDebugEnabled())
                logger.debug("Processing Selector: " + selector);

            // This will process the next selector endpoint, if it returns true, it means that a claims endpoint was used and we'll wait for a response
            if (processNextSelectorEndpoint(exchange, selector, ctx))
                return;

            // If we get here, it means that there are now more endpoints to process for this selector, try to select
            // an entity now.

            entity = entitySelectorMgr.selectEntity(mediator.getPreferredStrategy(), selector, ctx, (SelectorChannel) channel);

            selector = processNextSelector(exchange, selectors, ctx);
        }

        if (logger.isDebugEnabled())
            logger.debug("Selected IdP " + (entity != null ? entity.getAlias() : "NULL"));

        // Send the selected entity, if any
        sendSelectionResponse(exchange, state, selectionState.getRequest(), entity);

        // Clear selection state
        state.removeLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");
        if (entity != null) {
            if (logger.isDebugEnabled())
                logger.debug("Storing selected entity " + entity.getAlias());
            state.setLocalVariable(getProvider().getName().toUpperCase() + "_SELECTED_ENTITY", entity);
        }

    }

    protected void doProcessUserClaimsResponse(CamelMediationExchange exchange, MediationState state, UserClaimsResponse userClaimsResp) throws SSOException {

        // Do we need to collect more information to make a decision ?!
        SSOEntitySelectorMediator mediator = (SSOEntitySelectorMediator) channel.getIdentityMediator();

        EntitySelectorManager entitySelectorMgr = mediator.getSelectorManager();

        // Get selection state
        EntitySelectionState selectionState = (EntitySelectionState) state.getLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");

        for (Claim c : userClaimsResp.getClaimSet().getClaims()) {
            selectionState.getUserClaims().addClaim(c);
        }

        EntitySelectionContext ctx = new EntitySelectionContext(state,
                selectionState,
                getCotManager(),
                selectionState.getUserClaims(),
                selectionState.getRequest());

        CircleOfTrustMemberDescriptor entity = null;

        List<EntitySelector> selectors = entitySelectorMgr.resolveSelectors(ctx, (SelectorChannel) channel, mediator.getPreferredStrategy());

        // Current selector
        EntitySelector selector = selectors.get(selectionState.getNextSelectorIdx() - 1);
        while (selector != null && entity == null) {

            // This will process the next selector endpoint, if it returs true, it means that an endpoint was used
            if (processNextSelectorEndpoint(exchange, selector, ctx))
                return;

            // If we get here, it means that there are now more endpoints to process for this selector, try to select
            // an entity now.

            entity = entitySelectorMgr.selectEntity(mediator.getPreferredStrategy(), selector, ctx, (SelectorChannel) channel);

            selector = processNextSelector(exchange, selectors, ctx);
        }

        if (logger.isDebugEnabled())
            logger.debug("Selected IdP " + (entity != null ? entity.getAlias() : "NULL"));

        // Send the selected entity, if any
        sendSelectionResponse(exchange, state, selectionState.getRequest(), entity);

        // Clear selection state
        state.removeLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");
        if (entity != null) {
            if (logger.isDebugEnabled())
                logger.debug("Storing selected entity " + entity.getAlias());
            state.setLocalVariable(getProvider().getName().toUpperCase() + "_SELECTED_ENTITY", entity);
        }

    }


    protected EntitySelector processNextSelector(CamelMediationExchange exchange, List<EntitySelector> selectors, EntitySelectionContext ctx) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        EntitySelectionState selectionState = (EntitySelectionState) state.getLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");

        int selectorIdx = selectionState.getNextSelectorIdx();

        if (logger.isDebugEnabled())
            logger.debug("Processing selector [" + selectorIdx + "] ");

        EntitySelector selector = null;
        if (selectorIdx < selectors.size()) {

            selector = selectors.get(selectorIdx);

            while (selector != null && !selector.canHandle(ctx)) {
                selectorIdx ++;
                if (selectorIdx < selectors.size())
                    selector = selectors.get(selectorIdx);
                else
                    selector = null;
            }
        }

        selectionState.setNextSelectorIdx(selectorIdx + 1);
        // Reset endpoints index
        selectionState.setNextSelectorEndpointIdx(0);

        if (logger.isDebugEnabled())
            logger.debug("Using selector [" + selectorIdx + "] " + selector);

        return selector;

    }

    protected boolean processNextSelectorEndpoint(CamelMediationExchange exchange,
                                                  EntitySelector selector,
                                                  EntitySelectionContext ctx) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        EntitySelectionState selectionState = (EntitySelectionState) state.getLocalVariable(getProvider().getName().toUpperCase() + "_SELECTION_STATE");

        List<EndpointDescriptor> endpoints = selector.getUserClaimsEndpoints(ctx, (SelectorChannel) channel);

        if (endpoints == null) {
            if (logger.isDebugEnabled())
                logger.debug("No endpoints for selector " + selector);
            return false;
        }

        // Send Attributes Request
        int selectorEndpointIdx = selectionState.getNextSelectorEndpointIdx();

        if (logger.isDebugEnabled())
            logger.debug("Processing IDX ["+selectorEndpointIdx+"] for Endpoints [" + endpoints.size() + "] from Selector: " + selector);

        if (selectorEndpointIdx < endpoints.size()) {

            EndpointDescriptor ed = endpoints.get(selectorEndpointIdx);

            selectionState.setNextSelectorEndpointIdx(selectorEndpointIdx + 1);

            if (logger.isDebugEnabled())
                logger.debug("Sending User Claims Request to " + ed);

            // We need to keep track of the endpoints, for now only one supported !!!
            UserClaimsRequest userClaimsReq = new UserClaimsRequestImpl(
                    uuidGenerator.generateId(),
                    channel,
                    endpoint,
                    state.getLocalState().getId());

            userClaimsReq.setAttribute("ServiceProvider", ctx.getRequest().getIssuer());

            // Send User Claims request
            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    userClaimsReq,
                    "UserClaimsRequest",
                    null,
                    ed,
                    state));

            exchange.setOut(out);

            return true;

        }

        if (logger.isDebugEnabled())
            logger.debug("No more endpoints found for " + selector);

        return false;
    }

    protected void sendSelectionResponse(CamelMediationExchange exchange ,
                                         MediationState state,
                                         SelectEntityRequestType request,
                                         CircleOfTrustMemberDescriptor selectedCotMembery) throws SSOException {

        if (logger.isDebugEnabled())
            logger.debug("Sending selection response with entity " + selectedCotMembery != null ? (selectedCotMembery.getId() + " " + selectedCotMembery.getAlias()) : "NULL");

        // Do something with the outcome
        SelectEntityResponseType response = new SelectEntityResponseType();
        response.setInReplayTo(request.getID());
        response.setID(uuidGenerator.generateId());
        if (selectedCotMembery != null)
            response.setEntityId(selectedCotMembery.getId());

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


