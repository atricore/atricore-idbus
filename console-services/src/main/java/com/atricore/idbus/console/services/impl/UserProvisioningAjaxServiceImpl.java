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
import com.atricore.idbus.console.lifecycle.main.exception.UserProvisioningAjaxException;
import com.atricore.idbus.console.services.dto.GroupDTO;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dusan Fisic
 */
public class UserProvisioningAjaxServiceImpl implements UserProvisioningAjaxService, InitializingBean {
    private static Log logger = LogFactory.getLog(UserProvisioningAjaxServiceImpl.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private String pspTargetId;

    private SpmlR2Client spmlService;

    public void afterPropertiesSet() throws Exception {

    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws UserProvisioningAjaxException {
        DeleteRequestType deleteRequest = new DeleteRequestType ();
        deleteRequest.setRequestID(uuidGenerator.generateId());
        deleteRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        PSOIdentifierType psoId = new PSOIdentifierType ();
        psoId.setID(groupRequest.getId() + "");
        psoId.setTargetID(pspTargetId);

        deleteRequest.setPsoID(psoId);
        ResponseType resp = spmlService.spmlDeleteRequest(deleteRequest);

        RemoveGroupResponse respObj = new RemoveGroupResponse();
        return respObj;
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws UserProvisioningAjaxException {
        AddRequestType addReq = new AddRequestType();
        addReq.setTargetID(pspTargetId);
        addReq.setRequestID(uuidGenerator.generateId());
        addReq.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
        GroupType group = new GroupType ();
        group.setName(groupRequest.getName());
        group.setDescription(groupRequest.getDescription());
        addReq.setData(group);

        AddResponseType resp = spmlService.spmlAddRequest(addReq);
        GroupType spmlGroup = (GroupType) resp.getPso().getData();
        AddGroupResponse respObj = new AddGroupResponse();
        respObj.setGroup(toGroupDTO(spmlGroup));

        return respObj;
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(pspTargetId);
        psoGroupId.setID(groupRequest.getId() + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType lookupRequest = new LookupRequestType();
        lookupRequest.setRequestID(uuidGenerator.generateId());
        lookupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
        lookupRequest.setPsoID(psoGroupId);

        LookupResponseType resp = spmlService.spmlLookupRequest(lookupRequest);

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
        spmlQry.setTargetID(pspTargetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if (groupRequest.getName() != null)
            qry = "/groups[name='"+groupRequest.getName()+"']";

        if (logger.isTraceEnabled())
            logger.trace("SPML Users Search query : " + qry);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);

        FindGroupByNameResponse response = new FindGroupByNameResponse();

        if (resp.getPso().size() == 1) {
            GroupType spmlGroup = (GroupType) resp.getPso().get(0).getData();
            response.setGroup(toGroupDTO(spmlGroup));
        }

        return response;
    }

    public ListGroupResponse getGroups() throws UserProvisioningAjaxException {

        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        qry = "/groups";

        if (logger.isTraceEnabled())
            logger.trace("SPML Users Search query : " + qry);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);
        GroupDTO grps[] = new GroupDTO[resp.getPso().size()];

        for (int i=0; i<grps.length; i++) {
            GroupType grp = (GroupType) resp.getPso().get(i).getData();
            grps[i] = toGroupDTO(grp);
        }

        ListGroupResponse lstGroupsResponse = new ListGroupResponse();
        lstGroupsResponse.setGroups(grps);

        return lstGroupsResponse;
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest searchGroupsRequest) throws UserProvisioningAjaxException {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
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

        if (logger.isTraceEnabled())
            logger.trace("SPML Groups Search query : " + qry);


        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);

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

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws UserProvisioningAjaxException {


        try {

            if (logger.isTraceEnabled())
                logger.trace("Processing request for group [" + groupRequest.getId() + "]");

            ModifyRequestType modifyGroupRequest = new ModifyRequestType();
            modifyGroupRequest.setRequestID(uuidGenerator.generateId());
            modifyGroupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

            PSOType psoGroup = lookupGroup(groupRequest.getId());
            if (psoGroup == null) {
                logger.error("Group not found with ID " + groupRequest.getId());
                throw new UserProvisioningAjaxException("Group not found with ID " + groupRequest.getId());
            }
            GroupType spmlGroup = (GroupType) psoGroup.getData();

            spmlGroup.setName(groupRequest.getName());
            spmlGroup.setDescription(groupRequest.getDescription());

            ModificationType mod = new ModificationType();
            mod.setModificationMode(ModificationModeType.REPLACE);
            mod.setData(spmlGroup);

            modifyGroupRequest.setPsoID(psoGroup.getPsoID());
            modifyGroupRequest.getModification().add(mod);

            ModifyResponseType resp = spmlService.spmlModifyRequest(modifyGroupRequest);

            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while updating group " + groupRequest.getId());
                throw new UserProvisioningAjaxException("Error updating Group [" + groupRequest.getId() + "]");
            }

            return new UpdateGroupResponse();

        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error updating Group " + groupRequest.getId() + " : " + e.getMessage(), e);
        }

    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws java.lang.Exception {
        DeleteRequestType userDelRequest = new DeleteRequestType();
        userDelRequest.setRequestID(uuidGenerator.generateId());
        userDelRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        PSOIdentifierType psoId = new PSOIdentifierType();
        psoId.setID(userRequest.getId() + "");
        psoId.setTargetID(pspTargetId);

        userDelRequest.setPsoID(psoId);
        ResponseType resp = spmlService.spmlDeleteRequest(userDelRequest);

        RemoveUserResponse response = new RemoveUserResponse();

        return response;
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws java.lang.Exception {
        AddRequestType addReq = new AddRequestType();
        addReq.setTargetID(pspTargetId);
        addReq.setRequestID(uuidGenerator.generateId());
        addReq.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        UserType user = toUserType(userRequest);
        addReq.setData(user);

        AddResponseType resp = spmlService.spmlAddRequest(addReq);
        UserType spmlUser = (UserType) resp.getPso().getData();
        AddUserResponse response = new AddUserResponse();
        response.setUser(toUserDTO(spmlUser));
        return response;
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws java.lang.Exception {
        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(pspTargetId);
        psoUserId.setID(userRequest.getId() + "");
        psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        LookupRequestType lookupRequest = new LookupRequestType();
        lookupRequest.setRequestID(uuidGenerator.generateId());
        lookupRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
        lookupRequest.setPsoID(psoUserId);

        LookupResponseType resp = spmlService.spmlLookupRequest(lookupRequest);

        UserType spmlUser = (UserType) resp.getPso().getData();
        FindUserByIdResponse response = new FindUserByIdResponse();
        response.setUser(toUserDTO(spmlUser));

        return response;
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws java.lang.Exception {

        if (logger.isTraceEnabled())
            logger.trace("Finding user with username ["+userRequest.getUsername()+"]");

        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if (userRequest.getUsername() != null)
            qry = "/users[username='"+userRequest.getUsername()+"']";

        if (logger.isTraceEnabled())
            logger.trace("SPML Users Search query : " + qry);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);
        FindUserByUsernameResponse response = new FindUserByUsernameResponse();

        if (resp.getPso().size() == 1) {
            UserType spmlUser = (UserType) resp.getPso().get(0).getData();
            response.setUser(toUserDTO(spmlUser));
        }

        return response;
    }

    public ListUserResponse getUsers() throws java.lang.Exception {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        qry = "/users";

        if (logger.isTraceEnabled())
            logger.trace("SPML Users Search query : " + qry);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);
        UserDTO users[] = new UserDTO[resp.getPso().size()];

        for (int i=0; i<users.length; i++) {
            UserType usr = (UserType) resp.getPso().get(i).getData();
            users[i] = toUserDTO(usr);
        }
        ListUserResponse response = new ListUserResponse();
        response.setUsers(users);
        return response;
    }

    public SearchUserResponse searchUsers(SearchUserRequest userSearchRequest) throws java.lang.Exception {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        StringBuffer sb_query = new StringBuffer("/users[");
        if ( !"".equals(userSearchRequest.getUserName()))
            sb_query.append("username=" + userSearchRequest.getUserName() + " | ");
        if ( !"".equals(userSearchRequest.getFirstName()))
            sb_query.append("firstname=" + userSearchRequest.getFirstName() + " | ");
        if ( !"".equals(userSearchRequest.getSurename()))
            sb_query.append("surname=" + userSearchRequest.getSurename() + " | ");
        if ( !"".equals(userSearchRequest.getCommonName()))
            sb_query.append("commonname=" + userSearchRequest.getCommonName() + " | ");
        if ( !"".equals(userSearchRequest.getGivenName()))
            sb_query.append("givenName=" + userSearchRequest.getGivenName() + " | ");

        String qry = sb_query.toString();
        qry = qry.substring(0,qry.length()-3)+"]";

        if (logger.isTraceEnabled())
            logger.trace("SPML Users Search query : " + qry);

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName(SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);
        ArrayList<UserDTO> users = new ArrayList<UserDTO>();

        for (PSOType psoUser : resp.getPso()) {
            UserType spmlUser = (UserType) psoUser.getData();
            users.add(toUserDTO(spmlUser));
        }

        SearchUserResponse response = new SearchUserResponse();
        response.setUsers(users);

        return response;
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws java.lang.Exception {
        ModifyRequestType modifyUserRequest = new ModifyRequestType();
        modifyUserRequest.setRequestID(uuidGenerator.generateId());
        modifyUserRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        PSOType psoUser = null;
        try {
            psoUser = lookupUser(userRequest.getId());
        } catch (IdentityMediationException e) {
            e.printStackTrace();
        }

        UserType spmlUser = toUserType(userRequest);
        spmlUser.setId( ((UserType) psoUser.getData()).getId());

        ModificationType mod = new ModificationType();
        mod.setModificationMode(ModificationModeType.REPLACE);
        mod.setData(spmlUser);

        modifyUserRequest.setPsoID(psoUser.getPsoID());
        modifyUserRequest.getModification().add(mod);

        ModifyResponseType resp = spmlService.spmlModifyRequest(modifyUserRequest);
        UpdateUserResponse response = new UpdateUserResponse();

        return response;
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        SearchRequestType searchRequest = new SearchRequestType();
        searchRequest.setRequestID(uuidGenerator.generateId());
        searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(pspTargetId);
        String qry="";

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        if (usersByGroupRequest.getGroup() != null)
            qry = "/users[group='"+usersByGroupRequest.getGroup()+"']";



        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);
        searchRequest.setQuery(spmlQry);

        SearchResponseType resp  = spmlService.spmlSearchRequest(searchRequest);
        GetUsersByGroupResponse response = new GetUsersByGroupResponse();
        UserDTO users[] = new UserDTO[resp.getPso().size()];

        for (int i=0; i<users.length; i++) {
            UserType usr = (UserType) resp.getPso().get(i).getData();
            users[i] = toUserDTO(usr);
        }
        response.setUsers(users);
        return response;
    }

    protected PSOType lookupGroup(Long id) throws IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Group lookup for " + id);

        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(pspTargetId);
        psoGroupId.setID(id + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoGroupId);

        LookupResponseType resp = spmlService.spmlLookupRequest(spmlRequest);

        if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
            logger.error("SPML Status Code " + resp.getStatus() + " received while looking for group " + id);
            throw new IdentityMediationException("SPML Status Code " + resp.getStatus() + " received while looking for group " + id);
        }

        return resp.getPso();

    }

    protected PSOType lookupUser(Long id) throws IdentityMediationException {

        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(pspTargetId);
        psoUserId.setID(id + "");
        psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoUserId);

        LookupResponseType resp = spmlService.spmlLookupRequest(spmlRequest);
        if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
            logger.error("SPML Status Code " + resp.getStatus() + " received while looking for user " + id);
            throw new IdentityMediationException("SPML Status Code " + resp.getStatus() + " received while looking for user " + id);
        }

        return resp.getPso();

    }

    private UserType toUserType(AddUserRequest newUser) {
        UserType user = new UserType();
        user.setUserName(newUser.getUserName());
        user.setFirstName(newUser.getFirstName());
        user.setSurename(newUser.getSurename());
        user.setCommonName(newUser.getCommonName());
        user.setGivenName(newUser.getGivenName());
        user.setInitials(newUser.getInitials());
        user.setGenerationQualifier(newUser.getGenerationQualifier());
        user.setDistinguishedName(newUser.getDistinguishedName());
        user.setEmail(newUser.getEmail());
        user.setTelephoneNumber(newUser.getTelephoneNumber());
        user.setFacsimilTelephoneNumber(newUser.getFacsimilTelephoneNumber());
        user.setCountryName(newUser.getCommonName());
        user.setLocalityName(newUser.getLocalityName());
        user.setStateOrProvinceName(newUser.getStateOrProvinceName());
        user.setStreetAddress(newUser.getStreetAddress());
        user.setOrganizationName(newUser.getOrganizationName());
        user.setOrganizationUnitName(newUser.getOrganizationUnitName());
        user.setPersonalTitle(newUser.getPersonalTitle());
        user.setBusinessCategory(newUser.getBusinessCategory());
        user.setPostalAddress(newUser.getPostalAddress());
        user.setPostalCode(newUser.getPostalCode());
        user.setPostOfficeBox(newUser.getPostOfficeBox());
        user.setLanguage(newUser.getLanguage());
        user.setAccountDisabled(newUser.getAccountDisabled());
        user.setAccountExpires(newUser.getAccountExpires());
        //user.setAccountExpirationDate(newUser.getAccountExpirationDate());
        user.setLimitSimultaneousLogin(newUser.getLimitSimultaneousLogin());
        user.setMaximunLogins(newUser.getMaximunLogins());
        user.setTerminatePreviousSession(newUser.getTerminatePreviousSession());
        user.setPreventNewSession(newUser.getPreventNewSession());
        user.setAllowUserToChangePassword(newUser.getAllowUserToChangePassword());
        user.setForcePeriodicPasswordChanges(newUser.getForcePeriodicPasswordChanges());
        user.setDaysBetweenChanges(newUser.getDaysBetweenChanges());
        //user.setPasswordExpirationDate(newUser.getPasswordExpirationDate());
        user.setNotifyPasswordExpiration(newUser.getNotifyPasswordExpiration());
        user.setDaysBeforeExpiration(newUser.getDaysBeforeExpiration());
        user.setUserPassword(newUser.getUserPassword());
        user.setUserCertificate(newUser.getUserCertificate());
        user.setAutomaticallyGeneratePassword(newUser.getAutomaticallyGeneratePassword());
        user.setEmailNewPasword(newUser.getEmailNewPasword());

        //user.setGroup(newUser.getGroups());
        return user;
    }

    private UserType toUserType(UpdateUserRequest newUser) {
        UserType user = new UserType();
        user.setUserName(newUser.getUserName());
        user.setFirstName(newUser.getFirstName());
        user.setSurename(newUser.getSurename());
        user.setCommonName(newUser.getCommonName());
        user.setGivenName(newUser.getGivenName());
        user.setInitials(newUser.getInitials());
        user.setGenerationQualifier(newUser.getGenerationQualifier());
        user.setDistinguishedName(newUser.getDistinguishedName());
        user.setEmail(newUser.getEmail());
        user.setTelephoneNumber(newUser.getTelephoneNumber());
        user.setFacsimilTelephoneNumber(newUser.getFacsimilTelephoneNumber());
        user.setCountryName(newUser.getCommonName());
        user.setLocalityName(newUser.getLocalityName());
        user.setStateOrProvinceName(newUser.getStateOrProvinceName());
        user.setStreetAddress(newUser.getStreetAddress());
        user.setOrganizationName(newUser.getOrganizationName());
        user.setOrganizationUnitName(newUser.getOrganizationUnitName());
        user.setPersonalTitle(newUser.getPersonalTitle());
        user.setBusinessCategory(newUser.getBusinessCategory());
        user.setPostalAddress(newUser.getPostalAddress());
        user.setPostalCode(newUser.getPostalCode());
        user.setPostOfficeBox(newUser.getPostOfficeBox());
        user.setLanguage(newUser.getLanguage());
        user.setAccountDisabled(newUser.getAccountDisabled());
        user.setAccountExpires(newUser.getAccountExpires());
        //user.setAccountExpirationDate(newUser.getAccountExpirationDate());
        user.setLimitSimultaneousLogin(newUser.getLimitSimultaneousLogin());
        user.setMaximunLogins(newUser.getMaximunLogins());
        user.setTerminatePreviousSession(newUser.getTerminatePreviousSession());
        user.setPreventNewSession(newUser.getPreventNewSession());
        user.setAllowUserToChangePassword(newUser.getAllowUserToChangePassword());
        user.setForcePeriodicPasswordChanges(newUser.getForcePeriodicPasswordChanges());
        user.setDaysBetweenChanges(newUser.getDaysBetweenChanges());
        //user.setPasswordExpirationDate(newUser.getPasswordExpirationDate());
        user.setNotifyPasswordExpiration(newUser.getNotifyPasswordExpiration());
        user.setDaysBeforeExpiration(newUser.getDaysBeforeExpiration());
        user.setUserPassword(newUser.getUserPassword());
        user.setUserCertificate(newUser.getUserCertificate());
        user.setAutomaticallyGeneratePassword(newUser.getAutomaticallyGeneratePassword());
        user.setEmailNewPasword(newUser.getEmailNewPasword());

        //user.setGroup(newUser.getGroups());
        return user;
    }

    private GroupDTO toGroupDTO(GroupType grp) {
        GroupDTO g = new GroupDTO();
        g.setName(grp.getName());
        g.setDescription(grp.getDescription());
        g.setId(grp.getId());
        return g;
    }

    private UserDTO toUserDTO(UserType usr) {
        UserDTO u = new UserDTO();
        u.setUserName(usr.getUserName());
        u.setFirstName(usr.getFirstName());
        u.setSurename(usr.getSurename());
        u.setCommonName(usr.getCommonName());
        u.setGivenName(usr.getGivenName());
        u.setInitials(usr.getInitials());
        u.setGenerationQualifier(usr.getGenerationQualifier());
        u.setDistinguishedName(usr.getDistinguishedName());
        u.setEmail(usr.getEmail());
        u.setTelephoneNumber(usr.getTelephoneNumber());
        u.setFacsimilTelephoneNumber(usr.getFacsimilTelephoneNumber());
        u.setCountryName(usr.getCommonName());
        u.setLocalityName(usr.getLocalityName());
        u.setStateOrProvinceName(usr.getStateOrProvinceName());
        u.setStreetAddress(usr.getStreetAddress());
        u.setOrganizationName(usr.getOrganizationName());
        u.setOrganizationUnitName(usr.getOrganizationUnitName());
        u.setPersonalTitle(usr.getPersonalTitle());
        u.setBusinessCategory(usr.getBusinessCategory());
        u.setPostalAddress(usr.getPostalAddress());
        u.setPostalCode(usr.getPostalCode());
        u.setPostOfficeBox(usr.getPostOfficeBox());
        u.setLanguage(usr.getLanguage());
        //u.setAccountDisabled(usr.getAccountDisabled());
        //u.setAccountExpires(usr.getAccountExpires());
        //u.setAccountExpirationDate(usr.getAccountExpirationDate());
        //u.setLimitSimultaneousLogin(usr.getLimitSimultaneousLogin());
        u.setMaximunLogins(usr.getMaximunLogins());
        //u.setTerminatePreviousSession(usr.getTerminatePreviousSession());
        //u.setPreventNewSession(usr.getPreventNewSession());
        //u.setAllowUserToChangePassword(usr.getAllowUserToChangePassword());
        //u.setForcePeriodicPasswordChanges(usr.getForcePeriodicPasswordChanges());
        u.setDaysBetweenChanges(usr.getDaysBetweenChanges());
        //u.setPasswordExpirationDate(newUser.getPasswordExpirationDate());
        //u.setNotifyPasswordExpiration(usr.getNotifyPasswordExpiration());
        u.setDaysBeforeExpiration(usr.getDaysBeforeExpiration());
        u.setUserPassword(usr.getUserPassword());
        u.setUserCertificate(usr.getUserCertificate());
        //u.setAutomaticallyGeneratePassword(usr.getAutomaticallyGeneratePassword());
        //u.setEmailNewPasword(usr.getEmailNewPasword());

        List<GroupType> grps = usr.getGroup();
        GroupDTO groups[] = new GroupDTO[grps.size()];
        for (int i=0;i<grps.size();i++) {
            groups[i] = toGroupDTO(grps.get(i));
        }

        u.setGroups(groups);

        return u;
    }

    public String getPspTargetId() {
        return pspTargetId;
    }

    public void setPspTargetId(String pspTargetId) {
        this.pspTargetId = pspTargetId;
    }

    public SpmlR2Client getSpmlService() {
        return spmlService;
    }

    public void setSpmlService(SpmlR2Client spmlService) {
        this.spmlService = spmlService;
    }
}
