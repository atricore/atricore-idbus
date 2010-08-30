package org.atricore.idbus.capabilities.spmlr2.main.psp.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
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
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.ProvisioningChannel;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBElement;
import java.util.Iterator;
import java.util.List;

/**
 * // TODO : Split this in several producers ...
 *
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
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
        } else {

            // TODO : Send status=failure error= in response ! (use super producer or binding to build error
            // TODO : See SPMPL Section 3.1.2.2 Error (normative)
            logger.error("Unknown SPML Request type : " + content.getClass().getName());

            spmlResponse.setStatus(StatusCodeType.FAILURE);

        }

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

    protected AddResponseType doProcessAddRequest(CamelMediationExchange exchane, AddRequestType spmlRequest) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();

        // TODO : User planning infrastructure to process requests!
        ProvisioningTarget target = lookupTarget(spmlRequest.getTargetID());

        AddResponseType spmlResponse = new AddResponseType();
        spmlResponse.setRequestID(spmlRequest.getRequestID());

        if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for User");

            AddUserRequest req = new AddUserRequest();
            toAddUserRequest(target, req, spmlRequest);

            // TODO : Groups

            AddUserResponse res = target.addUser(req);
            spmlResponse.setPso(toSpmlUser(target, res.getUser()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

        } if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr)) {

            if (logger.isDebugEnabled())
                logger.debug("Processing SPML Add request for Group");

            GroupType spmlGroup = (GroupType) spmlRequest.getData();
            AddGroupRequest req = new AddGroupRequest();
            req.setName(spmlGroup.getName());
            req.setDescription(spmlGroup.getDescription());

            AddGroupResponse res = target.addGroup(req);

            spmlResponse.setPso(toSpmlGroup(target, res.getGroup()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS);

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
            spmlSelect = (SelectionType) XmlUtils.unmarshal(e);
        }
        else {
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
            // TODO : Improve this
            ListUsersResponse res = target.listUsers(new ListUsersRequest());
            User[] users = res.getUsers();

            JXPathContext jxp = JXPathContext.newContext(new TargetContainer(users));
            Iterator it = jxp.iteratePointers(path);
            while (it.hasNext()) {
                Pointer userPointer = (Pointer) it.next();
                User user = (User) userPointer.getValue();
                PSOType psoUser = toSpmlUser(target, user);
                spmlResponse.getPso().add(psoUser);
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
            req.setId(Long.parseLong(psoId.getID()));

            FindGroupByIdResponse res = target.findGroupById(req);

            spmlResponse.setPso(toSpmlGroup(target, res.getGroup()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS );

        } else if (psoId.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            if (logger.isTraceEnabled())
                logger.trace("Looking for group using PSO-ID " + psoId.getID());

            FindUserByIdRequest req = new FindUserByIdRequest();
            req.setId(Long.parseLong(psoId.getID()));

            FindUserByIdResponse res = target.findUserById(req);

            spmlResponse.setPso(toSpmlUser(target, res.getUser()));
            spmlResponse.setStatus(StatusCodeType.SUCCESS );
        } else {
            // TODO
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

            UpdateGroupRequest groupRequest = new UpdateGroupRequest ();
            groupRequest.setId(spmlGroup.getId());
            groupRequest.setName(spmlGroup.getName());
            groupRequest.setDescription(spmlGroup.getDescription());

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            try {
                UpdateGroupResponse groupResponse = target.updateGroup(groupRequest);

                groupResponse.getGroup();
                spmlResponse.setPso(toSpmlGroup(target, groupResponse.getGroup()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
            }

            return spmlResponse;

        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            ModifyResponseType spmlResponse = new ModifyResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            UpdateUserRequest userRequest = new UpdateUserRequest ();
            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());


            try {
                toUpdateUserRequest(target, userRequest, spmlRequest); 

                // TODO : Groups

                UpdateUserResponse userResponse = target.updateUser(userRequest);

                spmlResponse.setPso(toSpmlUser(target, userResponse.getUser()));
                spmlResponse.setStatus(StatusCodeType.SUCCESS);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
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
            groupRequest.setId(Long.parseLong(spmlRequest.getPsoID().getID()));

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            try {
                RemoveGroupResponse groupResponse = target.removeGroup(groupRequest);

                spmlResponse.setStatus(StatusCodeType.SUCCESS);
                spmlResponse.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr);

            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
            }

            return spmlResponse;
        } else if (spmlRequest.getOtherAttributes().containsKey(SPMLR2Constants.userAttr)) {

            ResponseType spmlResponse = new ResponseType();
            spmlResponse.setRequestID(spmlRequest.getRequestID());

            RemoveUserRequest userRequest = new RemoveUserRequest ();
            userRequest.setId(Long.parseLong(spmlRequest.getPsoID().getID()));

            ProvisioningTarget target = lookupTarget(spmlRequest.getPsoID().getTargetID());

            try {
                RemoveUserResponse userResponse = target.removeUser(userRequest);

                spmlResponse.setStatus(StatusCodeType.SUCCESS);
                spmlResponse.getOtherAttributes().containsKey(SPMLR2Constants.groupAttr);

            } catch (ProvisioningException e) {
                logger.error(e.getMessage(), e);
                spmlResponse.setStatus(StatusCodeType.FAILURE);
            }

            return spmlResponse;


        } else {
            throw new UnsupportedOperationException("SPML Request not supported");
        }
    }

    public class TargetContainer {

        public TargetContainer(Group[] groups) {
            this.groups = groups;
        }

        public TargetContainer(User[] users) {
            this.users = users;
        }

        public TargetContainer(Group[] groups, User[] users) {
            this.groups = groups;
            this.users = users;
        }

        private Group[] groups = new Group[0];

        private User[] users = new User[0];

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
    }

     

}
