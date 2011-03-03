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

import com.atricore.idbus.console.lifecycle.main.exception.UserProvisioningAjaxException;
import com.atricore.idbus.console.services.dto.schema.AttributeDTO;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;
import com.atricore.idbus.console.services.spi.SchemaManagementAjaxService;
import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.exceptions.SchemaManagementAjaxException;
import com.atricore.idbus.console.services.spi.request.schema.AddSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.RemoveSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.UpdateSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.response.schema.AddSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.ListSchemaAttributesResponse;
import com.atricore.idbus.console.services.spi.response.schema.RemoveSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.UpdateSchemaAttributeResponse;
import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.AttributeType;
import oasis.names.tc.spml._2._0.atricore.GroupAttributeType;
import oasis.names.tc.spml._2._0.atricore.UserAttributeType;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 1/13/11 - 2:50 PM
 */

public class SchemaManagementAjaxServiceImpl implements
        SchemaManagementAjaxService,
        SpmlAjaxClient,
        InitializingBean {

    private static Log logger = LogFactory.getLog(SchemaManagementAjaxServiceImpl.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();
    private String pspTargetId;
    private SpmlR2Client spmlService;

    public void afterPropertiesSet() throws Exception {

    }

    public AddSchemaAttributeResponse addSchemaAttribute(AddSchemaAttributeRequest req) throws Exception {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Processing addSchemaAttribute [" + req.getAttribute().getName()+
                        "|" +
                        req.getAttribute().getEntity() +"]");

            AddSchemaAttributeResponse response = new AddSchemaAttributeResponse();

            AttributeDTO attribute = req.getAttribute();
            if (attribute != null) {
                AddRequestType addReq = new AddRequestType();
                addReq.setTargetID(pspTargetId);
                addReq.setRequestID(uuidGenerator.generateId());

                if (attribute.getEntity().equals("User")) {
                    addReq.getOtherAttributes().put(SPMLR2Constants.userAttributeAttr, "true");
                    UserAttributeType userAttribute = new UserAttributeType();
                    userAttribute.setName(attribute.getName());
                    userAttribute.setDescription(attribute.getDescription());
                    userAttribute.setRequired(attribute.isRequired());
                    userAttribute.setMultivalued(attribute.isMultivalued());
                    userAttribute.setType(AttributeType.fromValue(attribute.getType().name()));
                    addReq.setData(userAttribute);
                } else if (attribute.getEntity().equals("Group")) {
                    addReq.getOtherAttributes().put(SPMLR2Constants.groupAttributeAttr, "true");
                    GroupAttributeType groupAttribute = new GroupAttributeType();
                    groupAttribute.setName(attribute.getName());
                    groupAttribute.setDescription(attribute.getDescription());
                    groupAttribute.setRequired(attribute.isRequired());
                    groupAttribute.setMultivalued(attribute.isMultivalued());
                    groupAttribute.setType(AttributeType.fromValue(attribute.getType().name()));
                    addReq.setData(groupAttribute);
                }

                AddResponseType resp = spmlService.spmlAddRequest(addReq);
                if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                    logger.error("SPML Status Code " + resp.getStatus() + " received while adding attribute " + req.getAttribute().getName());
                    throw new SchemaManagementAjaxException("Error adding attribute [" + req.getAttribute().getName() + "]");
                }

                if (attribute.getEntity().equals("User")) {
                    UserAttributeType spmlUserAttribute = (UserAttributeType) resp.getPso().getData();
                    response.setAttribute(toAttributeDTO(spmlUserAttribute));
                } else if (attribute.getEntity().equals("Group")) {
                    GroupAttributeType spmlGroupAttribute = (GroupAttributeType) resp.getPso().getData();
                    response.setAttribute(toAttributeDTO(spmlGroupAttribute));
                }
            }

            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error adding attribute [" + req.getAttribute().getName()+
                    "|" +
                    req.getAttribute().getEntity() +"]",e);
        }
    }

    public UpdateSchemaAttributeResponse updateSchemaAttribute(UpdateSchemaAttributeRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing addSchemaAttribute [" + req.getAttribute().getName()+
                        "|" +
                        req.getAttribute().getEntity() +"]");

            AttributeDTO attribute = req.getAttribute();
            ModifyRequestType modifyGroupRequest = new ModifyRequestType();
            modifyGroupRequest.setRequestID(uuidGenerator.generateId());
            ModificationType mod = new ModificationType();

            if (attribute != null) {
                PSOIdentifierType psoId = new PSOIdentifierType();
                psoId.setTargetID(pspTargetId);
                PSOType psoType = new PSOType();
                psoType.setPsoID(psoId);
                modifyGroupRequest.setPsoID(psoType.getPsoID());
                mod.setModificationMode(ModificationModeType.REPLACE);

                if (attribute.getEntity().equals("User")) {
                    modifyGroupRequest.getOtherAttributes().put(SPMLR2Constants.userAttributeAttr, "true");
                    UserAttributeType userAttribute = new UserAttributeType();
                    userAttribute.setId(attribute.getId());
                    userAttribute.setName(attribute.getName());
                    userAttribute.setDescription(attribute.getDescription());
                    userAttribute.setRequired(attribute.isRequired());
                    userAttribute.setMultivalued(attribute.isMultivalued());
                    userAttribute.setType(AttributeType.fromValue(attribute.getType().name()));
                    psoId.setID(userAttribute.getId()+"");
                    psoType.setData(userAttribute);
                    mod.setData(userAttribute);
                } else if (attribute.getEntity().equals("Group")) {
                    modifyGroupRequest.getOtherAttributes().put(SPMLR2Constants.groupAttributeAttr, "true");
                    GroupAttributeType groupAttribute = new GroupAttributeType();
                    groupAttribute.setId(attribute.getId());
                    groupAttribute.setName(attribute.getName());
                    groupAttribute.setDescription(attribute.getDescription());
                    groupAttribute.setRequired(attribute.isRequired());
                    groupAttribute.setMultivalued(attribute.isMultivalued());
                    groupAttribute.setType(AttributeType.fromValue(attribute.getType().name()));
                    psoId.setID(groupAttribute.getId() + "");
                    psoType.setData(groupAttribute);
                    mod.setData(groupAttribute);
                }
            }

            modifyGroupRequest.getModification().add(mod);
            ModifyResponseType resp = spmlService.spmlModifyRequest(modifyGroupRequest);

            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while updating attribute " + req.getAttribute().getName());
                throw new UserProvisioningAjaxException("Error updating attribute [" + req.getAttribute().getName() + "]");
            }

            return new UpdateSchemaAttributeResponse();
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error updating attribute [" + req.getAttribute().getName()+
                    "|" +
                    req.getAttribute().getEntity() +"]",e);
        }
    }

    public ListSchemaAttributesResponse listSchemaAttributes(String entity) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing listSchemaAttributes for entity [" + entity +"]");
            SearchRequestType searchRequest = new SearchRequestType();
            searchRequest.setRequestID(uuidGenerator.generateId());
            SearchQueryType spmlQry  = new SearchQueryType();
            spmlQry.setScope(ScopeType.ONE_LEVEL);
            spmlQry.setTargetID(pspTargetId);
            String qry="";

            if (entity.equals("User")) {
                searchRequest.getOtherAttributes().put(SPMLR2Constants.userAttributeAttr, "true");
                qry="/attrUser";
            }
            else {
                searchRequest.getOtherAttributes().put(SPMLR2Constants.groupAttributeAttr, "true");
                qry="/attrGroup";
            }

            SelectionType spmlSelect = new SelectionType();
            spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

            if (logger.isTraceEnabled())
                logger.trace("SPML Attributes Search query : " + qry);

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
                logger.error("SPML Status Code " + resp.getStatus() + " received while fetching list of attributes");
                throw new Exception("Error while fetching list of attributes");
            }

            ArrayList<AttributeDTO> attrs = new  ArrayList();
            for (int i=0; i<resp.getPso().size(); i++) {
                if (entity.equals("User")) {
                    UserAttributeType userAttrType = (UserAttributeType) resp.getPso().get(i).getData();
                    attrs.add(toAttributeDTO(userAttrType));
                }
                else {
                    GroupAttributeType groupAttrType = (GroupAttributeType) resp.getPso().get(i).getData();
                    attrs.add(toAttributeDTO(groupAttrType));
                }
            }

            ListSchemaAttributesResponse listSchemaAttributesResponse = new ListSchemaAttributesResponse();
            listSchemaAttributesResponse.setAttributesCollection(attrs);

            return listSchemaAttributesResponse;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error while fetching list of attributes.");
        }
    }

    public RemoveSchemaAttributeResponse removeSchemaAttribute(RemoveSchemaAttributeRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing removeSchemaAttribute [" + req.getSchemaName()+
                        "|" +
                        req.getAttributeId() +"]");

            DeleteRequestType attrDelRequest = new DeleteRequestType();
            attrDelRequest.setRequestID(uuidGenerator.generateId());
            if (req.getEntity().equals("User"))
                attrDelRequest.getOtherAttributes().put(SPMLR2Constants.userAttributeAttr, "true");
            else
                attrDelRequest.getOtherAttributes().put(SPMLR2Constants.groupAttributeAttr, "true");

            PSOIdentifierType psoId = new PSOIdentifierType();
            psoId.setID(req.getAttributeId() + "");
            psoId.setTargetID(pspTargetId);

            attrDelRequest.setPsoID(psoId);
            ResponseType resp = spmlService.spmlDeleteRequest(attrDelRequest);
            if (!resp.getStatus().equals(StatusCodeType.SUCCESS)) {
                logger.error("SPML Status Code " + resp.getStatus() + " received while deleting attribute " + req.getAttributeId());
                throw new UserProvisioningAjaxException("Error deleting attribute [" +  req.getAttributeId() + "]");
            }
            RemoveSchemaAttributeResponse response = new RemoveSchemaAttributeResponse();
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error removing schema attribute [" + req.getSchemaName() +
                    "|" +
                    req.getAttributeId() +"]",e);
        }
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

    public AttributeDTO toAttributeDTO(UserAttributeType attr) {
        AttributeDTO aDto = new AttributeDTO();
        aDto.setEntity("User");
        aDto.setId((int) attr.getId());
        aDto.setRequired(attr.isRequired());
        aDto.setDescription(attr.getDescription());
        aDto.setMultivalued(attr.isMultivalued());
        aDto.setName(attr.getName());
        aDto.setType(TypeDTOEnum.valueOf(attr.getType().value()));

        return aDto;
    }

    public AttributeDTO toAttributeDTO(GroupAttributeType attr) {
        AttributeDTO aDto = new AttributeDTO();
        aDto.setEntity("Group");
        aDto.setId((int) attr.getId());
        aDto.setRequired(attr.isRequired());
        aDto.setDescription(attr.getDescription());
        aDto.setMultivalued(attr.isMultivalued());
        aDto.setName(attr.getName());
        aDto.setType(TypeDTOEnum.valueOf(attr.getType().value()));
        return aDto;
    }
}

