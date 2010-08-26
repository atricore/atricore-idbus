/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.exception.GroupNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ProvisioningBusinessException;
import com.atricore.idbus.console.lifecycle.main.spi.UserProvisioningService;
import com.atricore.idbus.console.services.dto.GroupDTO;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.dozer.DozerBeanMapper;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.util.ArrayList;

/**
 * Author: Dejan Maric
 */
public class UserProvisioningAjaxServiceImpl implements UserProvisioningAjaxService {
    private static Log logger = LogFactory.getLog(UserProvisioningAjaxServiceImpl.class);

    UserProvisioningService provisioningService;

    private String targetId;
    private String pspChannel;
    private UUIDGenerator uuidGenerator = new UUIDGenerator();
    SPMLRequestPortType port;
    private DozerBeanMapper dozerMapper;


    public UserProvisioningAjaxServiceImpl(){
        Service serv = Service.create(SPMLR2MessagingConstants.SERVICE_NAME);
        serv.addPort(SPMLR2MessagingConstants.PORT_NAME,
                javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING,
                "http://localhost:8081/IDBUS/PSP-1/SPML2/SOAP");
        this.port = serv.getPort(SPMLR2MessagingConstants.PORT_NAME, SPMLRequestPortType.class);
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningBusinessException {
        DeleteRequestType deleteRequest = new DeleteRequestType ();
        deleteRequest.setRequestID(uuidGenerator.generateId());
        deleteRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        PSOIdentifierType psoId = new PSOIdentifierType ();
        psoId.setID(groupRequest.getId() + "");
        psoId.setTargetID(targetId);

        deleteRequest.setPsoID(psoId);
        ResponseType resp = port.spmlDeleteRequest(deleteRequest);

        RemoveGroupResponse respObj = new RemoveGroupResponse();
        return respObj;
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningBusinessException {
        AddRequestType addReq = new AddRequestType();
        addReq.setTargetID(targetId);
        addReq.setRequestID(uuidGenerator.generateId());
        addReq.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
        GroupType group = new GroupType ();
        group.setName(groupRequest.getName());
        group.setDescription(groupRequest.getDescription());
        addReq.setData(group);

        AddResponseType resp = port.spmlAddRequest(addReq);
        GroupType spmlGroup = (GroupType) resp.getPso().getData();
        AddGroupResponse respObj = new AddGroupResponse();
        respObj.setGroup(toGroupDTO(spmlGroup));

        return respObj;
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByIdRequest beReq =
                dozerMapper.map(groupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindGroupByIdRequest.class);

        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(targetId);
        psoGroupId.setID(groupRequest.getId() + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType lookupRequest = new LookupRequestType();
        lookupRequest.setRequestID(uuidGenerator.generateId());
        lookupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
        lookupRequest.setPsoID(psoGroupId);

        LookupResponseType resp = port.spmlLookupRequest(lookupRequest);

        GroupType spmlGroup = (GroupType) resp.getPso().getData();
        FindGroupByIdResponse response = new FindGroupByIdResponse();

        response.setGroup(toGroupDTO(spmlGroup));

        return response;
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if (groupRequest.getName() != null)
            qry = "/groups[name='"+groupRequest.getName()+"']";

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "Selection"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = port.spmlSearchRequest(searchRequest);

        FindGroupByNameResponse response = new FindGroupByNameResponse();

        if (resp.getPso().size() == 1) {
            GroupType spmlGroup = (GroupType) resp.getPso().get(0).getData();
            response.setGroup(toGroupDTO(spmlGroup));
        }

        return response;
    }

    public ListGroupResponse getGroups() throws ProvisioningBusinessException {

        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        qry = "/groups";

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "Selection"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = port.spmlSearchRequest(searchRequest);

        ListGroupResponse lstGroupsResponse = new ListGroupResponse();
        ArrayList<GroupDTO> groups = new ArrayList<GroupDTO>();

        for (PSOType psoGroup : resp.getPso()) {
            psoGroup.getPsoID();
            GroupType spmlGroup = (GroupType) psoGroup.getData();
            groups.add(toGroupDTO(spmlGroup));
        }

        GroupDTO grps[] = new GroupDTO[resp.getPso().size()];
        grps = groups.toArray(grps);

        lstGroupsResponse.setGroups(grps);

        return lstGroupsResponse;
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest searchGroupsRequest) throws ProvisioningBusinessException {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if ( !"".equals(searchGroupsRequest.getName()) &&
                !"".equals(searchGroupsRequest.getDescription()))
            qry = "/groups[name='"+searchGroupsRequest.getName()+"' | description='"+searchGroupsRequest.getDescription()+"']";
        else if (!"".equals(searchGroupsRequest.getName()) &&
                "".equals(searchGroupsRequest.getDescription())) {
            qry = "/groups[name='"+searchGroupsRequest.getName()+"']";

        }
        else if ("".equals(searchGroupsRequest.getName()) &&
                !"".equals(searchGroupsRequest.getDescription())) {
            qry = "/groups[description='"+searchGroupsRequest.getDescription()+"']";
        }

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "Selection"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = port.spmlSearchRequest(searchRequest);

        SearchGroupResponse srchGroupResponse = new SearchGroupResponse();
        ArrayList<GroupDTO> groups = new ArrayList<GroupDTO>();

        for (PSOType psoGroup : resp.getPso()) {
            psoGroup.getPsoID();
            GroupType spmlGroup = (GroupType) psoGroup.getData();
            groups.add(toGroupDTO(spmlGroup));
        }

        srchGroupResponse.setGroups(groups);

        return srchGroupResponse;
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningBusinessException {
        ModifyRequestType modifyGroupRequest = new ModifyRequestType();
        modifyGroupRequest.setRequestID(uuidGenerator.generateId());
        modifyGroupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        PSOType psoGroup = null;
        try {
            psoGroup = lookupGroup(groupRequest.getId());
        } catch (IdentityMediationException e) {
            e.printStackTrace();  
        }

        GroupType spmlGroup = (GroupType) psoGroup.getData();

        if (!"".equals(groupRequest.getName()))
            spmlGroup.setName(groupRequest.getName());

        if (!"".equals(groupRequest.getDescription()))
            spmlGroup.setDescription(groupRequest.getDescription());

        ModificationType mod = new ModificationType();

        mod.setModificationMode(ModificationModeType.REPLACE);
        mod.setData(spmlGroup);

        modifyGroupRequest.setPsoID(psoGroup.getPsoID());
        modifyGroupRequest.getModification().add(mod);

        ModifyResponseType resp = port.spmlModifyRequest(modifyGroupRequest);

        UpdateGroupResponse response = new UpdateGroupResponse();
        return response;
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.RemoveUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.RemoveUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.RemoveUserResponse beRes = provisioningService.removeUser(beReq);
        return dozerMapper.map(beRes, RemoveUserResponse.class);
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.AddUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.AddUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.AddUserResponse beRes = provisioningService.addUser(beReq);
        return dozerMapper.map(beRes, AddUserResponse.class);
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByIdRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByIdRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindUserByIdResponse beRes = provisioningService.findUserById(beReq);
        return dozerMapper.map(beRes, FindUserByIdResponse.class);
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByUsernameRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.FindUserByUsernameRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.FindUserByUsernameResponse beRes = provisioningService.findUserByUsername(beReq);
        return dozerMapper.map(beRes, FindUserByUsernameResponse.class);
    }

    public ListUserResponse getUsers() throws java.lang.Exception {
        return dozerMapper.map(provisioningService.getUsers(), ListUserResponse.class);
    }

    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.SearchUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.SearchUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.SearchUserResponse beRes = provisioningService.searchUsers(beReq);
        return dozerMapper.map(beRes, SearchUserResponse.class);
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws java.lang.Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserRequest beReq =
                dozerMapper.map(userRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UpdateUserRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UpdateUserResponse beRes = provisioningService.updateUser(beReq);
        return dozerMapper.map(beRes, UpdateUserResponse.class);
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        com.atricore.idbus.console.lifecycle.main.spi.request.GetUsersByGroupRequest beReq =
                dozerMapper.map(usersByGroupRequest, com.atricore.idbus.console.lifecycle.main.spi.request.GetUsersByGroupRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.GetUsersByGroupResponse beRes = provisioningService.getUsersByGroup(beReq);
        return dozerMapper.map(beRes, GetUsersByGroupResponse.class);
    }

    protected PSOType lookupGroup(Long id) throws IdentityMediationException {

        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(targetId);
        psoGroupId.setID(id + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoGroupId);

        LookupResponseType resp = port.spmlLookupRequest(spmlRequest);

        return resp.getPso();

    }

    protected PSOType lookupUser(Long id) throws IdentityMediationException {

        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(targetId);
        psoUserId.setID(id + "");
        psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoUserId);

        LookupResponseType resp = port.spmlLookupRequest(spmlRequest);

        return resp.getPso();

    }


    private GroupDTO toGroupDTO(GroupType grp) {
        GroupDTO g = new GroupDTO();
        g.setName(grp.getName());
        g.setDescription(grp.getDescription());
        g.setId(grp.getId());
        return g;
    }

    public void setProvisioningService(UserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }

}
