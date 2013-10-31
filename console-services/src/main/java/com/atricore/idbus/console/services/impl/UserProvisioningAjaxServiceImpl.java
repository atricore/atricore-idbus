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
import com.atricore.idbus.console.services.dto.schema.AttributeValueDTO;
import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.AttributeValueType;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.ReplacePasswordRequestType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import oasis.names.tc.spml._2._0.password.ResetPasswordRequestType;
import oasis.names.tc.spml._2._0.password.SetPasswordRequestType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dusan Fisic
 */
public class UserProvisioningAjaxServiceImpl implements
        UserProvisioningAjaxService,
        SpmlAjaxClient,
        InitializingBean {

    private static Log logger = LogFactory.getLog(UserProvisioningAjaxServiceImpl.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private String pspTargetId;

    private SpmlR2Client spmlService;

    public void afterPropertiesSet() throws Exception {
        // Work-around for JDO CLASSLOADER issues !?
        /*
        try {
            logger.info("Initializing User Provisioning Ajax service (triggering JDO Classloader problems workaround)");
            FindGroupByNameRequest req = new FindGroupByNameRequest ();
            req.setName("Administrator");
            findGroupByName(req);
        } catch (Exception e) {
            logger.warn (e.getMessage(), e);
        } */
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws UserProvisioningAjaxException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing delete request for group [" + groupRequest.getId() + "]");

            DeleteRequestType deleteRequest = new DeleteRequestType ();
            deleteRequest.setRequestID(uuidGenerator.generateId());
            deleteRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

            PSOIdentifierType psoId = new PSOIdentifierType ();
            psoId.setID(groupRequest.getId() + "");
            psoId.setTargetID(pspTargetId);

            deleteRequest.setPsoID(psoId);
            ResponseType resp = spmlService.spmlDeleteRequest(deleteRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while deleting group " + groupRequest.getId());
                throw new UserProvisioningAjaxException("Error deleting Group [" + groupRequest.getId() + "]");
            }

            RemoveGroupResponse respObj = new RemoveGroupResponse();
            return respObj;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error deleting Group " + groupRequest.getId() + " : " + e.getMessage(), e);
        }
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws UserProvisioningAjaxException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing adding request for group [" + groupRequest.getId() + "]");

            AddRequestType addReq = new AddRequestType();
            addReq.setTargetID(pspTargetId);
            addReq.setRequestID(uuidGenerator.generateId());
            addReq.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
            GroupType group = new GroupType ();
            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());
            if (groupRequest.getExtraAttributes() != null) {
                List<AttributeValueType> attrValues = toAttributeValueTypes(
                        (List<AttributeValueDTO>) groupRequest.getExtraAttributes());
                group.getAttributeValue().addAll(attrValues);
            }
            addReq.setData(group);

            AddResponseType resp = spmlService.spmlAddRequest(addReq);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while adding group " + groupRequest.getId());
                throw new UserProvisioningAjaxException("Error adding Group [" + groupRequest.getId() + "]");
            }

            GroupType spmlGroup = (GroupType) resp.getPso().getData();
            AddGroupResponse respObj = new AddGroupResponse();
            respObj.setGroup(toGroupDTO(spmlGroup));

            return respObj;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error adding Group " + groupRequest.getId() + " : " + e.getMessage(), e);
        }
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Processing find request for group [" + groupRequest.getId() + "]");

            PSOIdentifierType psoGroupId = new PSOIdentifierType();
            psoGroupId.setTargetID(pspTargetId);
            psoGroupId.setID(groupRequest.getId() + "");
            psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

            LookupRequestType lookupRequest = new LookupRequestType();
            lookupRequest.setRequestID(uuidGenerator.generateId());
            lookupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");
            lookupRequest.setPsoID(psoGroupId);

            LookupResponseType resp = spmlService.spmlLookupRequest(lookupRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching group " + groupRequest.getId());
            }

            GroupType spmlGroup = (GroupType) resp.getPso().getData();
            FindGroupByIdResponse response = new FindGroupByIdResponse();

            response.setGroup(toGroupDTO(spmlGroup));

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new GroupNotFoundException(groupRequest.getId());
        }
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing find request for group [" + groupRequest.getName() + "]");
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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching group " + groupRequest.getName());
            }

            FindGroupByNameResponse response = new FindGroupByNameResponse();

            if (resp.getPso().size() == 1) {
                GroupType spmlGroup = (GroupType) resp.getPso().get(0).getData();
                response.setGroup(toGroupDTO(spmlGroup));
            }

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new GroupNotFoundException(groupRequest.getName());
        }
    }

    public ListGroupResponse getGroups() throws UserProvisioningAjaxException {
        try{
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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while fetching groups");
                throw new UserProvisioningAjaxException("Error while fetching groups");
            }
            GroupDTO grps[] = new GroupDTO[resp.getPso().size()];

            for (int i=0; i<grps.length; i++) {
                GroupType grp = (GroupType) resp.getPso().get(i).getData();
                grps[i] = toGroupDTO(grp);
            }

            ListGroupResponse lstGroupsResponse = new ListGroupResponse();
            lstGroupsResponse.setGroups(grps);

            return lstGroupsResponse;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error while fetching groups");
        }
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest searchGroupsRequest) throws UserProvisioningAjaxException {
        try{
            SearchRequestType searchRequest = new SearchRequestType();
            searchRequest.setRequestID(uuidGenerator.generateId());
            searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

            SearchQueryType spmlQry  = new SearchQueryType();
            spmlQry.setScope(ScopeType.ONE_LEVEL);
            spmlQry.setTargetID(pspTargetId);

            SelectionType spmlSelect = new SelectionType();
            spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

            StringBuffer sb_query = new StringBuffer("/groups[");
            if ( StringUtils.isNotBlank(searchGroupsRequest.getName()))
                sb_query.append("name='" + searchGroupsRequest.getName() + "' and ");
            if (StringUtils.isNotBlank(searchGroupsRequest.getDescription()))
                sb_query.append("description='" + searchGroupsRequest.getDescription() + "' and ");

            String qry = sb_query.toString();
            qry = qry.substring(0,qry.length()-5)+"]";

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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching group. Query: " + qry);
            }

            SearchGroupResponse srchGroupResponse = new SearchGroupResponse();
            ArrayList<GroupDTO> groups = new ArrayList<GroupDTO>();

            for (PSOType psoGroup : resp.getPso()) {
                psoGroup.getPsoID();
                GroupType spmlGroup = (GroupType) psoGroup.getData();
                groups.add(toGroupDTO(spmlGroup));
            }

            srchGroupResponse.setGroups(groups);

            return srchGroupResponse;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error while searching groups");
        }
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws UserProvisioningAjaxException {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Processing request for group [" + groupRequest.getId() + "]");

            ModifyRequestType modifyGroupRequest = new ModifyRequestType();
            modifyGroupRequest.setRequestID(uuidGenerator.generateId());
            modifyGroupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

            GroupType spmlGroup = new GroupType();
            spmlGroup.setId(groupRequest.getId());
            if (groupRequest.getName() != null)
                spmlGroup.setName(groupRequest.getName());

            if (groupRequest.getDescription() != null)
                spmlGroup.setDescription(groupRequest.getDescription());

            if (groupRequest.getExtraAttributes() != null) {
                List<AttributeValueType> attrValues = toAttributeValueTypes(
                        (List<AttributeValueDTO>) groupRequest.getExtraAttributes());
                spmlGroup.getAttributeValue().addAll(attrValues);
            }

            PSOIdentifierType psoId = new PSOIdentifierType();
            psoId.setID(groupRequest.getId() + "");
            psoId.setTargetID(pspTargetId);

            PSOType psoGroup = new PSOType();
            psoGroup.setPsoID(psoId);
            psoGroup.setData(spmlGroup);

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
        try {
            if (logger.isTraceEnabled())
                logger.trace("Processing delete request for user [" + userRequest.getId() + "]");

            DeleteRequestType userDelRequest = new DeleteRequestType();
            userDelRequest.setRequestID(uuidGenerator.generateId());
            userDelRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            PSOIdentifierType psoId = new PSOIdentifierType();
            psoId.setID(userRequest.getId() + "");
            psoId.setTargetID(pspTargetId);

            userDelRequest.setPsoID(psoId);
            ResponseType resp = spmlService.spmlDeleteRequest(userDelRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while deleting user " + userRequest.getId());
                throw new UserProvisioningAjaxException("Error deleting User [" + userRequest.getId() + "]");
            }
            RemoveUserResponse response = new RemoveUserResponse();

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error deleting User " + userRequest.getId() + " : " + e.getMessage(), e);
        }
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws java.lang.Exception {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Processing adding request for user [" + userRequest.getId() + "]");
            AddRequestType addReq = new AddRequestType();
            addReq.setTargetID(pspTargetId);
            addReq.setRequestID(uuidGenerator.generateId());
            addReq.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
            UserType spmlUser = toUserType(userRequest);
            if (userRequest.getGroups() != null) {
                spmlUser.getGroup().clear();

                for (GroupDTO grp : userRequest.getGroups()) {
                    FindGroupByNameRequest fgbr = new FindGroupByNameRequest();
                    fgbr.setName(grp.getName());
                    FindGroupByNameResponse rspGroup = findGroupByName(fgbr);
                    GroupType spmlGroup = toGroupType(rspGroup.getGroup());
                    spmlUser.getGroup().add(spmlGroup);
                }
            }

            addReq.setData(spmlUser);

            AddResponseType resp = spmlService.spmlAddRequest(addReq);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while adding user " + userRequest.getId());
                throw new UserProvisioningAjaxException("Error adding User [" + userRequest.getId() + "]");
            }

            UserType retUser = (UserType) resp.getPso().getData();
            AddUserResponse response = new AddUserResponse();
            response.setUser(toUserDTO(retUser));
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error adding User " + userRequest.getId() + " : " + e.getMessage(), e);
        }
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws java.lang.Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing find request for user [" + userRequest.getId() + "]");

            PSOIdentifierType psoUserId = new PSOIdentifierType();
            psoUserId.setTargetID(pspTargetId);
            psoUserId.setID(userRequest.getId() + "");
            psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            LookupRequestType lookupRequest = new LookupRequestType();
            lookupRequest.setRequestID(uuidGenerator.generateId());
            lookupRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");
            lookupRequest.setPsoID(psoUserId);

            LookupResponseType resp = spmlService.spmlLookupRequest(lookupRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching user " + userRequest.getId());
            }

            UserType spmlUser = (UserType) resp.getPso().getData();
            FindUserByIdResponse response = new FindUserByIdResponse();
            response.setUser(toUserDTO(spmlUser));

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error searching User " + userRequest.getId() + " : " + e.getMessage(), e);
        }
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws java.lang.Exception {

        try{
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
                qry = "/users[userName='"+userRequest.getUsername()+"']";

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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching user " + userRequest.getUsername());
            }

            FindUserByUsernameResponse response = new FindUserByUsernameResponse();

            if (resp.getPso().size() == 1) {
                UserType spmlUser = (UserType) resp.getPso().get(0).getData();
                response.setUser(toUserDTO(spmlUser));
            }

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error searching User " + userRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public ListUserResponse getUsers() throws java.lang.Exception {
        try{
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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while fetching users");
            }
            UserDTO users[] = new UserDTO[resp.getPso().size()];

            for (int i=0; i<users.length; i++) {
                UserType usr = (UserType) resp.getPso().get(i).getData();
                users[i] = toUserDTO(usr);
            }
            ListUserResponse response = new ListUserResponse();
            response.setUsers(users);
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error fetching users." , e);
        }
    }

    public SearchUserResponse searchUsers(SearchUserRequest userSearchRequest) throws java.lang.Exception {
        try{
            SearchRequestType searchRequest = new SearchRequestType();
            searchRequest.setRequestID(uuidGenerator.generateId());
            searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            SearchQueryType spmlQry  = new SearchQueryType();
            spmlQry.setScope(ScopeType.ONE_LEVEL);
            spmlQry.setTargetID(pspTargetId);
            SelectionType spmlSelect = new SelectionType();
            spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

            StringBuffer sb_query = new StringBuffer("/users[");
            if ( StringUtils.isNotBlank(userSearchRequest.getUserName()))
                sb_query.append("userName='" + userSearchRequest.getUserName() + "' and ");
            if ( StringUtils.isNotBlank(userSearchRequest.getFirstName()))
                sb_query.append("firstName='" + userSearchRequest.getFirstName() + "' and ");
            if ( StringUtils.isNotBlank(userSearchRequest.getSurename()))
                sb_query.append("surename='" + userSearchRequest.getSurename() + "' and ");
            if ( StringUtils.isNotBlank(userSearchRequest.getCommonName()))
                sb_query.append("commonName='" + userSearchRequest.getCommonName() + "' and ");
            if ( StringUtils.isNotBlank(userSearchRequest.getGivenName()))
                sb_query.append("givenName='" + userSearchRequest.getGivenName() + "' and ");

            String qry = sb_query.toString();
            qry = qry.substring(0,qry.length()-5)+"]";

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
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while searching users. Query: " + qry);
            }
            ArrayList<UserDTO> users = new ArrayList<UserDTO>();

            for (PSOType psoUser : resp.getPso()) {
                UserType spmlUser = (UserType) psoUser.getData();
                users.add(toUserDTO(spmlUser));
            }

            SearchUserResponse response = new SearchUserResponse();
            response.setUsers(users);

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error while searching users." , e);
        }
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws java.lang.Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing update request for user [" + userRequest.getId() + "]");
            ModifyRequestType modifyUserRequest = new ModifyRequestType();
            modifyUserRequest.setRequestID(uuidGenerator.generateId());
            modifyUserRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

            PSOType psoUser = lookupUser(userRequest.getId());

            UserType spmlUser = toUserType(userRequest);
            if (userRequest.getGroups() != null) {
                spmlUser.getGroup().clear();

                for (GroupDTO grp : userRequest.getGroups()) {
                    FindGroupByNameRequest fgbr = new FindGroupByNameRequest();
                    fgbr.setName(grp.getName());
                    FindGroupByNameResponse rspGroup = findGroupByName(fgbr);
                    GroupType spmlGroup = toGroupType(rspGroup.getGroup());
                    spmlUser.getGroup().add(spmlGroup);
                }
            }

            spmlUser.setId( ((UserType) psoUser.getData()).getId());

            ModificationType mod = new ModificationType();
            mod.setModificationMode(ModificationModeType.REPLACE);
            mod.setData(spmlUser);

            modifyUserRequest.setPsoID(psoUser.getPsoID());
            modifyUserRequest.getModification().add(mod);

            ModifyResponseType resp = spmlService.spmlModifyRequest(modifyUserRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while updating user " + userRequest.getId());
                throw new UserProvisioningAjaxException("Error updating User [" + userRequest.getId() + "]");
            }


            if (userRequest.getUserPassword() != null) {

                if (logger.isDebugEnabled())
                    logger.debug("Updating user password for " + psoUser.getPsoID().getTargetID());

                ReplacePasswordRequestType pwdReq = new ReplacePasswordRequestType();
                pwdReq.setPsoID(psoUser.getPsoID());
                pwdReq.setNewPassword(userRequest.getUserPassword());

                ResponseType pwdRes = spmlService.spmlReplacePasswordRequest(pwdReq);

            }

            UpdateUserResponse response = new UpdateUserResponse();
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new UserProvisioningAjaxException("Error updating User " + userRequest.getId() + " : " + e.getMessage(), e);
        }

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

    protected PSOType lookupUser(Long id) throws IdentityMediationException {

        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(pspTargetId);
        psoUserId.setID(Long.toString(id));
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

    public UserType toUserType(AddUserRequest newUser) {
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
        user.setCountryName(newUser.getCountryName());
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

        ArrayList extraAttr = newUser.getExtraAttributes();
        if (extraAttr !=null) {
            List<AttributeValueType> attrValues = toAttributeValueTypes((List<AttributeValueDTO>) extraAttr);
            user.getAttributeValue().addAll(attrValues);
        }

        return user;
    }

    public UserType toUserType(UpdateUserRequest newUser) {
        UserType user = new UserType();
        user.setId(newUser.getId());
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
        user.setCountryName(newUser.getCountryName());
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

        ArrayList extraAttr = newUser.getExtraAttributes();
        if (extraAttr !=null) {
            List<AttributeValueType> attrValues = toAttributeValueTypes((List<AttributeValueDTO>) extraAttr);
            user.getAttributeValue().addAll(attrValues);
        }

        return user;
    }

    public GroupType toGroupType(GroupDTO grp) {
        GroupType g = new GroupType();
        g.setId(grp.getId());
        g.setName(grp.getName());
        g.setDescription(grp.getDescription());
        if (grp.getExtraAttributes() != null) {
            List<AttributeValueType> attrValues = toAttributeValueTypes(
                    (List<AttributeValueDTO>) grp.getExtraAttributes());
            g.getAttributeValue().addAll(attrValues);
        }
        return g;
    }

    public GroupDTO toGroupDTO(GroupType grp) {
        GroupDTO g = new GroupDTO();
        g.setId(grp.getId());
        g.setName(grp.getName());
        g.setDescription(grp.getDescription());
        g.setExtraAttributes(toAttributeValueDTOs(grp.getAttributeValue()));
        return g;
    }

    public List<AttributeValueType> toAttributeValueTypes(List<AttributeValueDTO> attributeValues) {
        List<AttributeValueType> retList = new ArrayList<AttributeValueType>();
        for (AttributeValueDTO value : attributeValues) {
            AttributeValueType attrValue = new AttributeValueType();
            attrValue.setId(value.getId());
            attrValue.setName(value.getName());
            attrValue.setValue(value.getValue());
            retList.add(attrValue);
        }

        return retList;
    }

    public ArrayList<AttributeValueDTO> toAttributeValueDTOs(List<AttributeValueType> attrValues) {
        ArrayList<AttributeValueDTO> attributes = new ArrayList<AttributeValueDTO>();
        if (attrValues != null) {
            for (AttributeValueType attrValue : attrValues) {
                AttributeValueDTO attributeVal = new AttributeValueDTO();
                attributeVal.setId(attrValue.getId());
                attributeVal.setName(attrValue.getName());
                attributeVal.setValue(attrValue.getValue());
                attributes.add(attributeVal);
            }
        }
        return attributes;
    }

    public UserDTO toUserDTO(UserType usr) {
        UserDTO u = new UserDTO();
        u.setId(usr.getId());
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
        u.setAccountDisabled(usr.getAccountDisabled());
        u.setAccountExpires(usr.getAccountExpires());
        //u.setAccountExpirationDate(usr.getAccountExpirationDate());
        u.setLimitSimultaneousLogin(usr.getLimitSimultaneousLogin());
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

        u.setExtraAttributes(toAttributeValueDTOs(usr.getAttributeValue()));

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
