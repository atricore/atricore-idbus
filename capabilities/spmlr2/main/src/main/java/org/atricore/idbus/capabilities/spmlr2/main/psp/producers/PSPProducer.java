package org.atricore.idbus.capabilities.spmlr2.main.psp.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.*;
import oasis.names.tc.spml._2._0.batch.BatchRequestType;
import oasis.names.tc.spml._2._0.batch.BatchResponseType;
import oasis.names.tc.spml._2._0.batch.OnErrorType;
import oasis.names.tc.spml._2._0.password.*;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.common.producers.SpmlR2Producer;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.capabilities.spmlr2.main.util.XmlUtils;
import org.atricore.idbus.kernel.auditing.core.Action;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.ProvisioningChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.*;
import org.atricore.idbus.kernel.main.provisioning.exception.*;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * // TODO : Split this in several producers ...
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PSPProducer extends SpmlR2Producer {

    private static final Log logger = LogFactory.getLog(PSPProducer.class);

    public PSPProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        Object content = in.getMessage().getContent();

        ResponseType spmlResponse = doProcessRequest(exchange, (RequestType) content);

        // Send response back.
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(), endpoint.getBinding(), null, null);

        out.setMessage(new MediationMessageImpl(idGen.generateId(),
                spmlResponse,
                spmlResponse.getClass().getSimpleName(),
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected ResponseType doProcessRequest(CamelMediationExchange exchange, RequestType content) throws Exception {

        ResponseType spmlResponse = null;

        if (logger.isDebugEnabled())
            logger.debug("Processing SPML " + content.getClass().getSimpleName() + " request");

        if (content instanceof ListTargetsRequestType) {
            spmlResponse = doProcessListTargetsRequest(exchange, (ListTargetsRequestType) content);
        } else if (content instanceof AddRequestType) {
            spmlResponse = doProcessAddRequest(exchange, (AddRequestType) content);
        } else if (content instanceof SearchRequestType) {
            spmlResponse = doProcessSearchRequest(exchange, (SearchRequestType) content);
        } else if (content instanceof LookupRequestType) {
            spmlResponse = doProcessLookupRequest(exchange, (LookupRequestType) content);
        } else if (content instanceof ModifyRequestType) {
            spmlResponse = doProcessModifyRequest(exchange, (ModifyRequestType) content);
        } else if (content instanceof DeleteRequestType) {
            spmlResponse = doProcessDeleteRequest(exchange, (DeleteRequestType) content);
        } else if (content instanceof SetPasswordRequestType) {
            spmlResponse = doProcessSetPassword(exchange, (SetPasswordRequestType) content);
        } else if (content instanceof ReplacePasswordRequestType) {
            spmlResponse = doProcessReplacePassword(exchange, (ReplacePasswordRequestType) content);
        } else if (content instanceof ResetPasswordRequestType) {
            spmlResponse = doProcessResetPasswordRequest(exchange, (ResetPasswordRequestType) content);
        } else if (content instanceof VerifyResetPasswordRequestType) {
            spmlResponse = doProcessVerifyResetPasswordRequest(exchange, (VerifyResetPasswordRequestType) content);
        } else if (content instanceof BatchRequestType) {
            spmlResponse = doProcessBatchRequest(exchange, (BatchRequestType) content);
        } else {

            // TODO : Send status=failure error= in response ! (use super producer or binding to build error
            // TODO : See SPMPL Section 3.1.2.2 Error (normative)
            logger.error("Unknown SPML Request type : " + content.getClass().getName());
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            spmlResponse.setError(ErrorCode.UNSUPPORTED_OPERATION);

        }

        return spmlResponse;

    }

    protected BatchResponseType doProcessBatchRequest(CamelMediationExchange exchange, BatchRequestType spmlBatchRequest) throws Exception {


        BatchResponseType batchResponse = new BatchResponseType();
        batchResponse.setRequestID(spmlBatchRequest.getRequestID());

        for (Object spmlMsg : spmlBatchRequest.getAny()) {
            if (spmlMsg instanceof RequestType) {
                try {

                    // Process batch request

                    if (logger.isDebugEnabled())
                        logger.debug("Processing batch request " +
                                spmlMsg.getClass().getSimpleName() + ":" + ((RequestType) spmlMsg).getRequestID());

                    ResponseType response = doProcessRequest(exchange, (RequestType) spmlMsg);

                    if (response.getStatus() == StatusCodeType.FAILURE) {

                        batchResponse.setStatus(StatusCodeType.FAILURE);
                        batchResponse.setError(ErrorCode.CUSTOM_ERROR);

                        if (logger.isDebugEnabled()) {

                            String errr = response.getError().value() + ":";

                            if (response.getErrorMessage() != null) {
                                for (String errMsg : response.getErrorMessage()) {
                                    errr += ". " + errMsg;
                                }
                            }
                            logger.debug("Error executing batch request " +
                                    spmlMsg.getClass().getSimpleName() + ":" + ((RequestType) spmlMsg).getRequestID() + " " + errr);

                        }

                        if (spmlBatchRequest.getOnError() != null &&
                            spmlBatchRequest.getOnError() == OnErrorType.EXIT) {

                            batchResponse.setError(response.getError());
                            batchResponse.getErrorMessage().addAll(response.getErrorMessage());

                            return batchResponse;
                        } else {
                            batchResponse.getErrorMessage().addAll(response.getErrorMessage());
                        }

                    }
                } catch (Exception e) {

                    logger.error(e.getMessage(), e);
                    if (spmlBatchRequest.getOnError() != null &&
                            spmlBatchRequest.getOnError() == OnErrorType.EXIT) {

                        batchResponse.setStatus(StatusCodeType.FAILURE);
                        batchResponse.setError(ErrorCode.CUSTOM_ERROR);
                        batchResponse.getErrorMessage().add(e.getMessage());

                        return batchResponse;
                    }

                }
            }
        }


        batchResponse.setStatus(StatusCodeType.SUCCESS);

        return batchResponse;
    }

    protected ListTargetsResponseType doProcessListTargetsRequest(CamelMediationExchange exchange, ListTargetsRequestType spmlRequest) {
        // TODO : Use planning to convert SPML Request into kernel request
        ProvisioningServiceProvider provider = ((ProvisioningChannel)channel).getProvider();
        
        List<ProvisioningTarget> targets = provider.getProvisioningTargets();
        ListTargetsResponseType spmlResponse = new ListTargetsResponseType();

        for (ProvisioningTarget target : targets) {
            TargetType spmlTarget = new TargetType();

            // TODO : Check spec
            spmlTarget.setProfile("xsd");
            spmlTarget.setTargetID(target.getName());

            CapabilitiesListType capabilitiesList = new CapabilitiesListType();

            CapabilityType spmlSearchCap = new CapabilityType();
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:search");
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:update");

            spmlResponse.getTarget().add(spmlTarget);
        }

        return spmlResponse;

    }

    protected AddResponseType doProcessAddRequest(CamelMediationExchange exchange, AddRequestType spmlRequest) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();

        // TODO : User planning infrastructure to process requests!
        ProvisioningTarget target = lookupTarget(spmlRequest.getTargetID());

        AddResponseType spmlResponse = new AddResponseType();
        spmlResponse.setRequestID(spmlRequest.getRequestID());

        if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            UserType spmlUser = (UserType) spmlRequest.getData();

            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for User " + spmlUser.getUserName());

            try {

                lookupUserByName(target, spmlUser.getUserName());
                
                // ERROR, username already exists
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.ALREADY_EXISTS );
                spmlResponse.getErrorMessage().add("Username '" + spmlUser.getUserName()+ "' already exists.");

                if (logger.isDebugEnabled())
                    logger.debug("Username '" + spmlUser.getUserName()+ "' already exists.");

                Properties auditProps = new Properties();
                auditProps.setProperty("userExists", "true");
                recordInfoAuditTrail(Action.SPML_ADD_USER.getValue(), ActionOutcome.FAILURE, spmlUser.getUserName(), exchange, auditProps);

                return spmlResponse;
                
            } catch(UserNotFoundException e) {
                // OK!
            }
                
            AddUserRequest req = toAddUserRequest(target, spmlRequest);

            AddUserResponse res = target.addUser(req);
            spmlResponse.setPso(toSpmlUser(target, res.getUser()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_ADD_USER.getValue(), ActionOutcome.SUCCESS, spmlUser.getUserName(), exchange, null);
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr)) {

            GroupType spmlGroup = (GroupType) spmlRequest.getData();
            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for Group " + spmlGroup.getName());

            Properties auditProps = new Properties();
            auditProps.setProperty("groupName", spmlGroup.getName());

            try {

                lookupGroup(target, spmlGroup.getName());

                // ERROR, group already exists
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.ALREADY_EXISTS );
                spmlResponse.getErrorMessage().add("Group '" + spmlGroup.getName()+ "' already exists.");

                if (logger.isDebugEnabled())
                    logger.debug("Group '" + spmlGroup.getName()+ "' already exists.");

                auditProps.setProperty("groupExists", "true");
                recordInfoAuditTrail(Action.SPML_ADD_GROUP.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

                return spmlResponse;

            } catch(GroupNotFoundException e) {
                // OK!
            }

            AddGroupRequest req = toAddGroupRequest(target, spmlRequest);
            
            AddGroupResponse res = target.addGroup(req);
            spmlResponse.setPso(toSpmlGroup(target, res.getGroup()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_ADD_GROUP.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttributeAttr)) {

            UserAttributeType spmlUserAttribute = (UserAttributeType) spmlRequest.getData();
            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for UserAttribute " + spmlUserAttribute.getName());

            Properties auditProps = new Properties();
            auditProps.setProperty("userAttributeName", spmlUserAttribute.getName());

            try {

                lookupUserAttributeByName(target, spmlUserAttribute.getName());

                // ERROR, user attribute name already exists
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.ALREADY_EXISTS );
                spmlResponse.getErrorMessage().add("UserAttribute '" + spmlUserAttribute.getName()+ "' already exists.");

                if (logger.isDebugEnabled())
                    logger.debug("UserAttribute '" + spmlUserAttribute.getName()+ "' already exists.");

                auditProps.setProperty("userAttributeExists", "true");
                recordInfoAuditTrail(Action.SPML_ADD_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

                return spmlResponse;

            } catch(UserAttributeNotFoundException e) {
                // OK!
            }

            AddUserAttributeRequest req = toAddUserAttributeRequest(target, spmlRequest);
            AddUserAttributeResponse res = target.addUserAttribute(req);

            spmlResponse.setPso(toSpmlUserAttribute(target, res.getUserAttribute()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_ADD_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttributeAttr)) {

            GroupAttributeType spmlGroupAttribute = (GroupAttributeType) spmlRequest.getData();
            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for GroupAttribute " + spmlGroupAttribute.getName());

            Properties auditProps = new Properties();
            auditProps.setProperty("groupAttributeName", spmlGroupAttribute.getName());

            try {

                lookupGroupAttributeByName(target, spmlGroupAttribute.getName());

                // ERROR, group attribute name already exists
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.ALREADY_EXISTS);
                spmlResponse.getErrorMessage().add("GroupAttribute '" + spmlGroupAttribute.getName() + "' already exists.");

                if (logger.isDebugEnabled())
                    logger.debug("GroupAttribute '" + spmlGroupAttribute.getName()+ "' already exists.");

                auditProps.setProperty("groupAttributeExists", "true");
                recordInfoAuditTrail(Action.SPML_ADD_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

                return spmlResponse;

            } catch(GroupAttributeNotFoundException e) {
                // OK!
            }

            AddGroupAttributeRequest req = toAddGroupAttributeRequest(target, spmlRequest);
            AddGroupAttributeResponse res = target.addGroupAttribute(req);

            spmlResponse.setPso(toSpmlGroupAttribute(target, res.getGroupAttribute()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_ADD_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
        } else {
            logger.error("Request attribute for entity type unknown or missing");
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            spmlResponse.setError(ErrorCode.MALFORMED_REQUEST);
            spmlResponse.getErrorMessage().add("Request attribute for entity type unknown or missing");
        }

        return spmlResponse;
    }

    protected SearchResponseType doProcessSearchRequest(CamelMediationExchange exchane, SearchRequestType spmlRequest) throws Exception {

        SearchResponseType spmlResponse = new SearchResponseType ();
        spmlResponse.setRequestID(spmlRequest.getRequestID());

        SearchQueryType spmlQry = spmlRequest.getQuery();
        ProvisioningTarget target = lookupTarget(spmlQry.getTargetID());

        // TODO : Query support is limmited
        List<Object> any = spmlQry.getAny();

        Object o = any.get(0);

        if (logger.isTraceEnabled())
            logger.trace("Received SPML Selection as " + o);

        SelectionType spmlSelect = null;


        if (o instanceof SelectionType ) {
            // Already unmarshalled
            spmlSelect = (SelectionType) o;

        } else if (o instanceof Element) {
            // DOM Element
            Element e = (Element) o;
            spmlSelect = (SelectionType) XmlUtils.unmarshal(e, new String[] {SPMLR2Constants.SPML_PKG});
        } else {
            // JAXB Element
            JAXBElement e = (JAXBElement) o;
            logger.debug("SMPL JAXBElement " + e.getName() + "[" + e.getValue() + "]");
            spmlSelect = (SelectionType) e.getValue();
        }

        String uri = spmlSelect.getNamespaceURI();
        String path = spmlSelect.getPath();

        if (uri == null || !uri.equals("http://www.w3.org/TR/xpath20")) {
            logger.error("Unsupported query language " + uri);
            spmlResponse.setRequestID(spmlRequest.getRequestID());
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            return spmlResponse;
        }

        if (logger.isDebugEnabled())
            logger.debug("Searching with path " + path);

        if (path.startsWith("/group")) {
            // TODO : Improve this

            ListGroupsResponse res = target.listGroups(new ListGroupsRequest());
            Group[] groups = res.getGroups();

            if (logger.isTraceEnabled())
                logger.trace("Searching {"+path+"} among " + groups.length);

            JXPathContext jxp = JXPathContext.newContext(new TargetContainer(groups));
            Iterator it = jxp.iteratePointers(path);
            while (it.hasNext()) {
                Pointer groupPointer = (Pointer) it.next();
                Group group = (Group) groupPointer.getValue();
                PSOType psoGroup = toSpmlGroup(target, group);
                spmlResponse.getPso().add(psoGroup);
            }

            if (logger.isTraceEnabled())
                logger.trace("Found " + spmlResponse.getPso().size() + " groups");

            spmlResponse.setStatus(StatusCodeType.SUCCESS);

        } else if (path.startsWith("/user")) {

            SearchUserRequest searchUserRequest = new SearchUserRequest();
            UserSearchCriteria userSearchCriteria = new UserSearchCriteria();


            if (spmlRequest instanceof UserSearchRequestType) {

                UserSearchRequestType userSearchRequest = (UserSearchRequestType) spmlRequest;

                userSearchCriteria.setUsername(userSearchRequest.getUserSearchCriteria().getUserName());
                userSearchCriteria.setFirstName(userSearchRequest.getUserSearchCriteria().getFirstName());
                userSearchCriteria.setLastName(userSearchRequest.getUserSearchCriteria().getLastName());
                userSearchCriteria.setEmail(userSearchRequest.getUserSearchCriteria().getEmail());
                userSearchCriteria.setExactMatch(userSearchRequest.getUserSearchCriteria().getExactMatch());
                for (SearchAttributeType searchAttributeType : userSearchRequest.getUserSearchCriteria().getSearchAttribute()) {
                    SearchAttribute searchAttribute = new SearchAttribute();
                    searchAttribute.setName(searchAttributeType.getName());
                    searchAttribute.setValue(searchAttributeType.getValue());
                    searchAttribute.setType(AttributeType.fromValue(searchAttributeType.getType().name()));
                    userSearchCriteria.getAttributes().add(searchAttribute);
                }

                searchUserRequest.setFromResult(userSearchRequest.getFromResult());
                searchUserRequest.setResultCount(userSearchRequest.getResultCount());

                searchUserRequest.setSearchCriteria(userSearchCriteria);
                SearchUserResponse res = target.searchUsers(searchUserRequest);
                List<User> users = res.getUsers();

                spmlResponse = new UserSearchResponseType();
                for (User user : users) {
                    spmlResponse.getPso().add(toSpmlUser(target, user));
                }
                ((UserSearchResponseType) spmlResponse).setNumOfUsers(res.getNumOfUsers());
                spmlResponse.setStatus(StatusCodeType.SUCCESS);


            } else {
                // TODO : Just for backward compatibility /users[userName='fwadmin']

                if (path.startsWith("/users[userName='")) {
                    String username = path.substring("/users[userName='".length(), path.length() - 2);
                    userSearchCriteria.setUsername(username);
                    userSearchCriteria.setExactMatch(true);

                    //searchUserRequest.setFromResult(userSearchRequest.getFromResult());
                    //searchUserRequest.setResultCount(userSearchRequest.getResultCount());

                    searchUserRequest.setSearchCriteria(userSearchCriteria);
                    SearchUserResponse res = target.searchUsers(searchUserRequest);
                    List<User> users = res.getUsers();

                    spmlResponse = new SearchResponseType();
                    for (User user : users) {
                        spmlResponse.getPso().add(toSpmlUser(target, user));
                    }

                    spmlResponse.setStatus(StatusCodeType.SUCCESS);

                }
            }


        } else if (path.startsWith("/attrUser")) {
            // TODO : Improve this
            ListUserAttributesResponse res = target.listUserAttributes(new ListUserAttributesRequest());
            UserAttributeDefinition[] userAttributes = res.getUserAttributes();

            JXPathContext jxp = JXPathContext.newContext(new TargetContainer(userAttributes));
            Iterator it = jxp.iteratePointers("/userAttributes");
            while (it.hasNext()) {
                Pointer userAttributePointer = (Pointer) it.next();
                UserAttributeDefinition userAttribute = (UserAttributeDefinition) userAttributePointer.getValue();
                PSOType psoUserAttribute = toSpmlUserAttribute(target, userAttribute);
                spmlResponse.getPso().add(psoUserAttribute);
            }
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

        } else if (path.startsWith("/attrGroup")) {
            // TODO : Improve this
            ListGroupAttributesResponse res = target.listGroupAttributes(new ListGroupAttributesRequest());
            GroupAttributeDefinition[] groupAttributes = res.getGroupAttributes();

            JXPathContext jxp = JXPathContext.newContext(new TargetContainer(groupAttributes));
            Iterator it = jxp.iteratePointers("/groupAttributes");
            while (it.hasNext()) {
                Pointer groupAttributePointer = (Pointer) it.next();
                GroupAttributeDefinition groupAttribute = (GroupAttributeDefinition) groupAttributePointer.getValue();
                PSOType psoGroupAttribute = toSpmlGroupAttribute(target, groupAttribute);
                spmlResponse.getPso().add(psoGroupAttribute);
            }
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

        } else {
            throw new UnsupportedOperationException("Select path not supported '"+path+"'");
        }

        return spmlResponse;

    }

    protected LookupResponseType doProcessLookupRequest(CamelMediationExchange exchane, LookupRequestType spmlRequest) throws Exception {

        PSOIdentifierType psoId = spmlRequest.getPsoID();
        ProvisioningTarget target = lookupTarget(psoId.getTargetID());

        LookupResponseType spmlResponse = new LookupResponseType();
        spmlResponse.setRequestID(spmlRequest.getRequestID());

        if (psoId.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr)) {

            if (logger.isTraceEnabled())
                logger.trace("Looking for group using PSO-ID " + psoId.getID());

            // Lookup groups
            FindGroupByIdRequest req = new FindGroupByIdRequest();
            req.setId(psoId.getID());

            try {
                FindGroupByIdResponse res = target.findGroupById(req);

                spmlResponse.setPso(toSpmlGroup(target, res.getGroup()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

            } catch (GroupNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());

            }


        } else if (psoId.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            if (logger.isTraceEnabled())
                logger.trace("Looking for user using PSO-ID " + psoId.getID());

            FindUserByIdRequest req = new FindUserByIdRequest();
            req.setId(psoId.getID());

            try {
                FindUserByIdResponse res = target.findUserById(req);

                spmlResponse.setPso(toSpmlUser(target, res.getUser()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);
            } catch (UserNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());

            }
        } else if (psoId.getOtherAttributes().containsKey(SPMLR2Constants.userAttributeAttr)) {

            if (logger.isTraceEnabled())
                logger.trace("Looking for user attribute using PSO-ID " + psoId.getID());

            FindUserAttributeByIdRequest req = new FindUserAttributeByIdRequest();
            req.setId(psoId.getID());

            try {
                FindUserAttributeByIdResponse res = target.findUserAttributeById(req);

                spmlResponse.setPso(toSpmlUserAttribute(target, res.getUserAttribute()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);
            } catch (UserAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());

            }
        } else if (psoId.getOtherAttributes().containsKey(SPMLR2Constants.groupAttributeAttr)) {

            if (logger.isTraceEnabled())
                logger.trace("Looking for group attribute using PSO-ID " + psoId.getID());

            FindGroupAttributeByIdRequest req = new FindGroupAttributeByIdRequest();
            req.setId(psoId.getID());

            try {
                FindGroupAttributeByIdResponse res = target.findGroupAttributeById(req);

                spmlResponse.setPso(toSpmlGroupAttribute(target, res.getGroupAttribute()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);
            } catch (GroupAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());

            }
        } else {

            logger.error("Unknonw/Undefined PSO attribute that specifies entity type (Non-Normative)");

            spmlResponse.setStatus(StatusCodeType.FAILURE );
        }

        return spmlResponse;

    }

    protected ModifyResponseType doProcessModifyRequest(CamelMediationExchange exchange, ModifyRequestType spmlRequest) {

        if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr)) {

            ModifyResponseType spmlResponse = new ModifyResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            ModificationType spmlMod = spmlRequest.getModification().get(0);
            GroupType spmlGroup = (GroupType) spmlMod.getData();

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();
            auditProps.setProperty("groupId", spmlGroup.getId());

            try {
                UpdateGroupRequest groupRequest = toUpdateGroupRequest(target, spmlRequest); 
                UpdateGroupResponse groupResponse = target.updateGroup(groupRequest);
                Group group = groupResponse.getGroup();

                spmlResponse.setPso(toSpmlGroup(target, group));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                auditProps.setProperty("groupName", group.getName());
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (GroupNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("groupNotFound", "true");
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;

        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            ModifyResponseType spmlResponse = new ModifyResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            UpdateUserRequest userRequest = null;
            try {
                userRequest = toUpdateUserRequest(target, spmlRequest);
                UpdateUserResponse userResponse = target.updateUser(userRequest);

                spmlResponse.setPso(toSpmlUser(target, userResponse.getUser()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                recordInfoAuditTrail(Action.SPML_UPDATE_USER.getValue(), ActionOutcome.SUCCESS, userRequest.getUser().getUserName(), exchange, null);
            } catch (UserNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                Properties auditProps = new Properties();
                auditProps.setProperty("userNotFound", "true");
                recordInfoAuditTrail(Action.SPML_UPDATE_USER.getValue(), ActionOutcome.FAILURE,
                        userRequest != null ? userRequest.getUser().getUserName() : null, exchange, auditProps);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_UPDATE_USER.getValue(), ActionOutcome.FAILURE,
                        userRequest != null ? userRequest.getUser().getUserName() : null, exchange, null);
            }

            return spmlResponse;
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttributeAttr)) {

            ModifyResponseType spmlResponse = new ModifyResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();

            try {
                UpdateUserAttributeRequest userAttributeRequest = toUpdateUserAttributeRequest(target, spmlRequest);
                auditProps.setProperty("userAttributeId", userAttributeRequest.getUserAttribute().getId());
                UpdateUserAttributeResponse userAttributeResponse = target.updateUserAttribute(userAttributeRequest);

                spmlResponse.setPso(toSpmlUserAttribute(target, userAttributeResponse.getUserAttribute()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                auditProps.setProperty("userAttributeName", userAttributeResponse.getUserAttribute().getName());
                recordInfoAuditTrail(Action.SPML_UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (UserAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("userAttributeNotFound", "true");
                recordInfoAuditTrail(Action.SPML_UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttributeAttr)) {

            ModifyResponseType spmlResponse = new ModifyResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();

            try {
                UpdateGroupAttributeRequest groupAttributeRequest = toUpdateGroupAttributeRequest(target, spmlRequest);
                auditProps.setProperty("groupAttributeId", groupAttributeRequest.getGroupAttribute().getId());
                UpdateGroupAttributeResponse groupAttributeResponse = target.updateGroupAttribute(groupAttributeRequest);

                spmlResponse.setPso(toSpmlGroupAttribute(target, groupAttributeResponse.getGroupAttribute()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                auditProps.setProperty("groupAttributeName", groupAttributeResponse.getGroupAttribute().getName());
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (GroupAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("groupAttributeNotFound", "true");
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;
        } else {
            throw new UnsupportedOperationException("SPML Request not supported");
        }



    }

    protected ResponseType doProcessDeleteRequest(CamelMediationExchange exchange, DeleteRequestType spmlRequest) {
        if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr)) {

            ResponseType spmlResponse = new ResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            RemoveGroupRequest groupRequest = new RemoveGroupRequest ();
            groupRequest.setId(spmlRequest.getPsoID().getID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();
            auditProps.setProperty("groupId", groupRequest.getId());

            try {
                RemoveGroupResponse groupResponse = target.removeGroup(groupRequest);
                spmlResponse.setStatus(StatusCodeType.SUCCESS);
                spmlResponse.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr);

                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (GroupNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("groupNotFound", "true");
                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            ResponseType spmlResponse = new ResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            RemoveUserRequest userRequest = new RemoveUserRequest ();
            userRequest.setId(spmlRequest.getPsoID().getID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();
            auditProps.setProperty("userId", userRequest.getId());

            try {
                RemoveUserResponse userResponse = target.removeUser(userRequest);

                spmlResponse.setStatus(StatusCodeType.SUCCESS);
                spmlResponse.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr);

                recordInfoAuditTrail(Action.SPML_REMOVE_USER.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (UserNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("userNotFound", "true");
                recordInfoAuditTrail(Action.SPML_REMOVE_USER.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_REMOVE_USER.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;


        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttributeAttr)) {

            ResponseType spmlResponse = new ResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            RemoveUserAttributeRequest userAttributeRequest = new RemoveUserAttributeRequest();
            userAttributeRequest.setId(spmlRequest.getPsoID().getID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();
            auditProps.setProperty("userAttributeId", userAttributeRequest.getId());

            try {
                RemoveUserAttributeResponse userAttributeResponse = target.removeUserAttribute(userAttributeRequest);

                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                recordInfoAuditTrail(Action.SPML_REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (UserAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("userAttributeNotFound", "true");
                recordInfoAuditTrail(Action.SPML_REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;


        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttributeAttr)) {

            ResponseType spmlResponse = new ResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            RemoveGroupAttributeRequest groupAttributeRequest = new RemoveGroupAttributeRequest();
            groupAttributeRequest.setId(spmlRequest.getPsoID().getID());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            Properties auditProps = new Properties();
            auditProps.setProperty("groupAttributeId", groupAttributeRequest.getId());

            try {
                RemoveGroupAttributeResponse groupAttributeResponse = target.removeGroupAttribute(groupAttributeRequest);

                spmlResponse.setStatus(StatusCodeType.SUCCESS);

                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);
            } catch (GroupAttributeNotFoundException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                spmlResponse.setError(ErrorCode.NO_SUCH_IDENTIFIER);
                spmlResponse.getErrorMessage().add(e.getMessage());
                auditProps.setProperty("groupAttributeNotFound", "true");
                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
                recordInfoAuditTrail(Action.SPML_REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
            }

            return spmlResponse;


        } else {
            throw new UnsupportedOperationException("SPML Request not supported");
        }
    }

    public ResponseType doProcessSetPassword(CamelMediationExchange exchange, SetPasswordRequestType spmlRequest) {

        ResponseType spmlResponse = new ResponseType();
        try {

            String currentPwd = spmlRequest.getCurrentPassword();
            String newPwd = spmlRequest.getPassword();

            SetPasswordRequest req = new SetPasswordRequest();

            req.setUserId(spmlRequest.getPsoID().getID());
            req.setCurrentPassword(spmlRequest.getCurrentPassword());
            req.setNewPassword(spmlRequest.getPassword());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            target.setPassword(req);
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

        } catch (ProvisioningException e) {
            logger.error(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);
        }

        return spmlResponse;
    }

    public ResponseType doProcessReplacePassword(CamelMediationExchange exchange, ReplacePasswordRequestType spmlRequest) {

        ResponseType spmlResponse = new ResponseType();
        User user = null;
        Properties auditProps = new Properties();
        auditProps.setProperty("userId", spmlRequest.getPsoID().getID());
        try {

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());
            String userId = spmlRequest.getPsoID().getID();
            String newPwd = spmlRequest.getNewPassword();

            user = this.lookupUser(target, userId);

            ResetPasswordRequest req = new ResetPasswordRequest (user);
            req.setNewPassword(newPwd);
            target.resetPassword(req);
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_PWD_RESET.getValue(), ActionOutcome.SUCCESS, user.getUserName(), exchange, auditProps);
        } catch (ProvisioningException e) {
            logger.error(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            recordInfoAuditTrail(Action.SPML_PWD_RESET.getValue(), ActionOutcome.FAILURE,
                    user != null ? user.getUserName() : null, exchange, auditProps);
        }

        return spmlResponse;
    }

    public ResponseType doProcessVerifyResetPasswordRequest(CamelMediationExchange exchange, VerifyResetPasswordRequestType spmlRequest) {
        VerifyResetPasswordResponseType spmlResponse = new VerifyResetPasswordResponseType();
        Properties auditProps = new Properties();
        //auditProps.setProperty("transactionId", spmlRequest.getTransaction());
        try {

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            ConfirmResetPasswordRequest req = new ConfirmResetPasswordRequest();
            req.setTransactionId(spmlRequest.getTransaction());
            req.setCode(spmlRequest.getCode());
            req.setNewPassword(spmlRequest.getNewpassword());

            ResetPasswordResponse resp = target.confirmResetPassword(req);

            spmlResponse.setStatus(StatusCodeType.SUCCESS);

            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.SUCCESS, null, exchange, auditProps);

        } catch (TransactionExpiredException e) {
            logger.debug(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);

            PolicyEnforcementStatementType stmtType = new PolicyEnforcementStatementType();
            stmtType.setName("expiredTransactionOrCode");
            stmtType.setNs("urn:org:atricore:idbus:policy:provisioning:transaction");
            spmlResponse.getSsoPolicyEnforcements().add(stmtType);

            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

        } catch (TransactionInvalidException e) {
            logger.debug(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);

            PolicyEnforcementStatementType stmtType = new PolicyEnforcementStatementType();
            stmtType.setName("invalidTransactionOrCode");
            stmtType.setNs("urn:org:atricore:idbus:policy:provisioning:transaction");
            spmlResponse.getSsoPolicyEnforcements().add(stmtType);

            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

        } catch (IllegalPasswordException e) {
            logger.debug(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);

            if (logger.isDebugEnabled())
                logger.debug("Illegal password value ");

            // Get password statements
            if (e.getStmts() != null) {
                for (PolicyEnforcementStatement stmt : e.getStmts()) {
                    PolicyEnforcementStatementType stmtType = new PolicyEnforcementStatementType();
                    stmtType.setName(stmt.getName());
                    stmtType.setNs(stmt.getNs());
                    spmlResponse.getSsoPolicyEnforcements().add(stmtType);

                    if (logger.isDebugEnabled())
                        logger.debug("{"+stmt.getNs()+"}" + stmt.getName());

                }
            }
            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
        } catch (ProvisioningException e) {
            logger.error(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            recordInfoAuditTrail(Action.SPML_CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, null, exchange, auditProps);
        }

        return spmlResponse;
    }

    public ResponseType doProcessResetPasswordRequest(CamelMediationExchange exchange, ResetPasswordRequestType spmlRequest) {
        ResetPasswordResponseType spmlResponse = new ResetPasswordResponseType();
        User user = null;
        Properties auditProps = new Properties();
        auditProps.setProperty("userId", spmlRequest.getPsoID().getID());
        try {
            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());
            String userId = spmlRequest.getPsoID().getID();

            user = this.lookupUser(target, userId);

            ResetPasswordRequest req = new ResetPasswordRequest(user);
            PrepareResetPasswordResponse resp = target.prepareResetPassword(req);

            spmlResponse.setStatus(StatusCodeType.SUCCESS);
            spmlResponse.setTransaction(resp.getTransactionId());
            spmlResponse.setCode(resp.getCode());
            spmlResponse.setPassword(resp.getNewPassword());

            recordInfoAuditTrail(Action.SPML_PREPARE_PWD_RESET.getValue(), ActionOutcome.SUCCESS, user.getUserName(), exchange, auditProps);
        } catch (ProvisioningException e) {
            logger.error(e.getMessage(), e);
            spmlResponse.setStatus(StatusCodeType.FAILURE);
            recordInfoAuditTrail(Action.SPML_PREPARE_PWD_RESET.getValue(), ActionOutcome.FAILURE,
                    user != null ? user.getUserName() : null, exchange, auditProps);
        }

        return spmlResponse;
    }


    public class TargetContainer {

        public TargetContainer(Group[] groups) {
            this.groups = groups;
        }

        public TargetContainer(User[] users) {
            this.users = users;
        }

        public TargetContainer(UserAttributeDefinition[] userAttributes) {
            this.userAttributes = userAttributes;
        }

        public TargetContainer(GroupAttributeDefinition[] groupAttributes) {
            this.groupAttributes = groupAttributes;
        }

        public TargetContainer(Group[] groups, User[] users) {
            this.groups = groups;
            this.users = users;
        }

        private Group[] groups = new Group[0];

        private User[] users = new User[0];

        private UserAttributeDefinition[] userAttributes = new UserAttributeDefinition[0];

        private GroupAttributeDefinition[] groupAttributes = new GroupAttributeDefinition[0];
        
        public Group[] getGroups() {
            return groups;
        }

        public void setGroups(Group[] groups) {
            this.groups = groups;
        }

        public User[] getUsers() {
            return users;
        }

        public void setUsers(User[] users) {
            this.users = users;
        }

        public UserAttributeDefinition[] getUserAttributes() {
            return userAttributes;
        }

        public void setUserAttributes(UserAttributeDefinition[] userAttributes) {
            this.userAttributes = userAttributes;
        }

        public GroupAttributeDefinition[] getGroupAttributes() {
            return groupAttributes;
        }

        public void setGroupAttributes(GroupAttributeDefinition[] groupAttributes) {
            this.groupAttributes = groupAttributes;
        }
    }

}
