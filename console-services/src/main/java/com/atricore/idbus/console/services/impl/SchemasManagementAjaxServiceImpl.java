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

import com.atricore.idbus.console.services.dto.schema.AttributeDTO;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;
import com.atricore.idbus.console.services.spi.SchemasManagementAjaxService;
import com.atricore.idbus.console.services.spi.request.schema.AddSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.ListSchemaAttributesRequest;
import com.atricore.idbus.console.services.spi.request.schema.RemoveSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.UpdateSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.response.schema.AddSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.ListSchemaAttributesResponse;
import com.atricore.idbus.console.services.spi.response.schema.RemoveSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.UpdateSchemaAttributeResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 1/13/11 - 2:50 PM
 */

public class SchemasManagementAjaxServiceImpl implements
        SchemasManagementAjaxService,
        InitializingBean {

    private static Log logger = LogFactory.getLog(SchemasManagementAjaxServiceImpl.class);

    private HashMap<Integer,AttributeDTO> attrMap;


    public void afterPropertiesSet() throws Exception {
        this.attrMap = new HashMap();
        attrMap.put( 1, new AttributeDTO(1,"User", "a", TypeDTOEnum.STRING, false, false));
        attrMap.put( 2, new AttributeDTO(2,"User", "specRole", TypeDTOEnum.STRING, true, false));
        attrMap.put( 3, new AttributeDTO(2,"Group", "a", TypeDTOEnum.STRING, false, false));
        attrMap.put( 4, new AttributeDTO(2,"Group", "b", TypeDTOEnum.STRING, true, false));
    }

    public AddSchemaAttributeResponse addSchemaAttribute(AddSchemaAttributeRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing addSchemaAttribute [" + req.getAttribute().getName()+
                        "|" +
                        req.getAttribute().getEntity() +"]");


            if (req.getAttribute() != null) {
                attrMap.put(req.getAttribute().getId(), req.getAttribute());
            }

            AddSchemaAttributeResponse response = new AddSchemaAttributeResponse();

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

            if (req.getAttribute() != null) {
                attrMap.put(req.getAttribute().getId(),req.getAttribute());
            }

            UpdateSchemaAttributeResponse response = new UpdateSchemaAttributeResponse();
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error adding attribute [" + req.getAttribute().getName()+
                    "|" +
                    req.getAttribute().getEntity() +"]",e);
        }
    }

    public ListSchemaAttributesResponse listSchemaAttributes(ListSchemaAttributesRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing listSchemaAttributes [" + req.getSchemaName() +"]");

            ListSchemaAttributesResponse response = new ListSchemaAttributesResponse();
            response.setAttributesCollection(attrMap.values());
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error listing Schema Attributes [" + req.getSchemaName() +"]",e);
        }
    }

    public RemoveSchemaAttributeResponse removeSchemaAttribute(RemoveSchemaAttributeRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing removeSchemaAttribute [" + req.getSchemaName()+
                        "|" +
                        req.getAttributeId() +"]");

            attrMap.remove(req.getAttributeId());

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
}

