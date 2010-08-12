package org.atricore.idbus.capabilities.spmlr2.main.psp.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.search.ResultsIteratorType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.common.producers.SpmlR2Producer;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddGroupRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ListGroupsRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddGroupResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddUserResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.ListGroupsResponse;

import java.util.ArrayList;
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
        } else {

            // TODO : Send status=failure error= in response ! (use super producer or binding to build error
            // TODO : See SPMPL Section 3.1.2.2 Error (normative)

            throw new IdentityMediationFault("status='failure'",
                    null,
                    "error='unsupportedOperation'",
                    content.getClass().getName(),
                    null);
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

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();
        List<ProvisioningTarget> targets = mediator.getProvisioningTargets();
        ListTargetsResponseType spmlResponse = new ListTargetsResponseType();

        for (ProvisioningTarget target : targets) {
            TargetType spmlTarget = new TargetType();

            // TODO : Check spec
            spmlTarget.setProfile("dsml");
            spmlTarget.setTargetID(target.getIdentityPartition().getName());

            CapabilitiesListType capabilitiesList = new CapabilitiesListType();

            CapabilityType spmlSearchCap = new CapabilityType();
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:search");
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:update");

            spmlResponse.getTarget().add(spmlTarget);
        }

        return spmlResponse;

    }

    protected AddResponseType doProcessAddRequest(CamelMediationExchange exchane, AddRequestType spmlRequest) throws Exception {

        // TODO : User planning infrastructure to process requests!
        ProvisioningTarget target = lookupTarget(spmlRequest.getTargetID());
        IdentityPartition partition = target.getIdentityPartition();

        AddResponseType spmlResponse = null;

        if (spmlRequest.getData() instanceof UserType) {

            if (logger.isDebugEnabled())
                logger.debug("Processing SMPL Add request for User");
            AddUserResponse res = partition.addUser(toAddUserRequest(target, spmlRequest));
            spmlResponse = (AddResponseType) toSpmlResponse(target, res);
        } else if (spmlRequest.getData() instanceof GroupType) {

            if (logger.isDebugEnabled())
                logger.debug("Processing SMPL Add request for Group");

            AddGroupResponse res = partition.addGroup(toAddGroupRequest(target, spmlRequest));
            spmlResponse = (AddResponseType) toSpmlResponse(target, spmlRequest, res);
        }

        return spmlResponse;
    }

    protected SearchResponseType doProcessSearchRequest(CamelMediationExchange exchane, SearchRequestType spmlRequest) throws Exception {

        SearchQueryType spmlQry = spmlRequest.getQuery();
        ProvisioningTarget target = lookupTarget(spmlQry.getTargetID());
        IdentityPartition partition = target.getIdentityPartition();

        // TODO : Query support is limmited
        List<Object> any = spmlQry.getAny();

        SelectionType spmlSelect = (SelectionType) any.get(0);
        String path = spmlSelect.getPath();

        SearchResponseType spmlResponse = null;

        if (logger.isDebugEnabled())
            logger.debug("Searching with path " + path);

        if (path.startsWith("Group/")) {

            List<Group> groups = null;
            String clause = path.substring("Group/".length());
            if (clause != null && clause.length() > 0) {
                throw new UnsupportedOperationException("Search criterias not supported for Groups");

            } else {

                ListGroupsResponse res = partition.listGroups(new ListGroupsRequest());
                spmlResponse = new SearchResponseType();
                spmlResponse.setRequestID(spmlRequest.getRequestID());

                for (Group group : res.getGroups()) {
                    PSOType psoGroup = toSpmlGroup(target, group);
                    spmlResponse.getPso().add(psoGroup);
                }

                if (logger.isDebugEnabled())
                    logger.debug("Found Groups " + spmlResponse.getPso().size());

            }


        } else {
            throw new UnsupportedOperationException("Select path not supported '"+path+"'");
        }

        return spmlResponse;


    }

    // ------------------------------< Utilities >

    protected ProvisioningTarget lookupTarget(String targetId) {
        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();

        for (ProvisioningTarget target : mediator.getProvisioningTargets()) {
            if (target.getIdentityPartition().getName().equals(targetId)) {
                return target;
            }
        }
        return null;
    }

    protected AddUserRequest toAddUserRequest(ProvisioningTarget target, AddRequestType spmlRequest) {

        UserType user = (UserType) spmlRequest.getData();

        AddUserRequest req = new AddUserRequest();
        // TODO : Use dozer ?!

        return req;
    }

    protected ResponseType toSpmlResponse(ProvisioningTarget target, AddUserResponse response) {
        
        AddResponseType spmlResponse = null;
        return spmlResponse;
    }

    protected AddGroupRequest toAddGroupRequest(ProvisioningTarget target, AddRequestType spmlRequest) {

        GroupType group = (GroupType) spmlRequest.getData();

        AddGroupRequest req = new AddGroupRequest();
        req.setName(group.getName());
        req.setDescription(group.getDescription());

        return req;
    }

    protected ResponseType toSpmlResponse(ProvisioningTarget target,
                                          AddRequestType spmlRequest,
                                          AddGroupResponse response) {

        Group group = response.getGroup();


        AddResponseType spmlResponse = new AddResponseType();
        spmlResponse.setPso(toSpmlGroup(target, group));
        spmlResponse.setRequestID(spmlRequest.getRequestID());
        spmlResponse.setStatus(StatusCodeType.SUCCESS);

        return spmlResponse;
    }

    protected PSOType toSpmlGroup(ProvisioningTarget target, Group group) {
        GroupType spmlGroup = new GroupType();
        spmlGroup.setName(group.getName());
        spmlGroup.setDescription(group.getDescription());

        PSOIdentifierType psoGroupId = new PSOIdentifierType ();
        psoGroupId.setTargetID(target.getIdentityPartition().getName());
        psoGroupId.setID(group.getId() + "");

        PSOType psoGroup = new PSOType();
        psoGroup.setData(spmlGroup);
        psoGroup.setPsoID(psoGroupId);

        return psoGroup;

    }


}
