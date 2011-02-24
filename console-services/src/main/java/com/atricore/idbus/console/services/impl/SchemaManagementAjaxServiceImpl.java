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
import com.atricore.idbus.console.services.spi.SchemaManagementAjaxService;
import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
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
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

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

    private HashMap<Integer,AttributeDTO> attrMap;
    private Random randomGenerator = new Random();

    private UUIDGenerator uuidGenerator = new UUIDGenerator();
    private String pspTargetId;
    private SpmlR2Client spmlService;
    
    public void afterPropertiesSet() throws Exception {
        this.attrMap = new HashMap();

        AttributeDTO atu_one = new AttributeDTO(randomGenerator.nextInt(100) ,"User", "specRole", TypeDTOEnum.STRING, false, false);
        AttributeDTO atu_two = new AttributeDTO(randomGenerator.nextInt(100) ,"User", "specNUM", TypeDTOEnum.INT, false, true);
        AttributeDTO atu_three = new AttributeDTO(randomGenerator.nextInt(100) ,"User", "specDate", TypeDTOEnum.DATE, false, true);
        AttributeDTO atu_four = new AttributeDTO(randomGenerator.nextInt(100) ,"User", "specEmail", TypeDTOEnum.EMAIL, false, false);
        AttributeDTO atu_five = new AttributeDTO(randomGenerator.nextInt(100) ,"User", "specURL", TypeDTOEnum.URL, false, false);

        AttributeDTO atg_one = new AttributeDTO(randomGenerator.nextInt(100) ,"Group", "groupSpecRole", TypeDTOEnum.STRING, false, true);
        AttributeDTO atg_two = new AttributeDTO(randomGenerator.nextInt(100) ,"Group", "groupSpecNUM", TypeDTOEnum.INT, false, false);
        AttributeDTO atg_three = new AttributeDTO(randomGenerator.nextInt(100) ,"Group", "groupSpecDate", TypeDTOEnum.DATE, false, false);
        AttributeDTO atg_four = new AttributeDTO(randomGenerator.nextInt(100) ,"Group", "groupSpecEmail", TypeDTOEnum.EMAIL, false, false);
        AttributeDTO atg_five = new AttributeDTO(randomGenerator.nextInt(100) ,"Group", "groupSpecURL", TypeDTOEnum.URL, false, false);

        attrMap.put(atu_one.getId() , atu_one );
        attrMap.put(atu_two.getId() , atu_two);
        attrMap.put(atu_three.getId() , atu_three);
        attrMap.put(atu_four.getId() , atu_four);
        attrMap.put(atu_five.getId() , atu_five);

        attrMap.put(atg_one.getId() , atg_one );
        attrMap.put(atg_two.getId() , atg_two);
        attrMap.put(atg_three.getId() , atg_three);
        attrMap.put(atg_four.getId() , atg_four);
        attrMap.put(atg_five.getId() , atg_five);
    }

    public AddSchemaAttributeResponse addSchemaAttribute(AddSchemaAttributeRequest req) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing addSchemaAttribute [" + req.getAttribute().getName()+
                        "|" +
                        req.getAttribute().getEntity() +"]");

            if (req.getAttribute() != null) {
                AttributeDTO at = req.getAttribute();
                at.setId(randomGenerator.nextInt(1000));
                attrMap.put(at.getId() , at);
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
            AttributeDTO uAttr = req.getAttribute();
            attrMap.remove(uAttr.getId());
            attrMap.put(uAttr.getId(), uAttr);

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

    public ListSchemaAttributesResponse listSchemaAttributes(String entity) throws Exception {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing listSchemaAttributes for entity [" + entity +"]");

            ListSchemaAttributesResponse response = new ListSchemaAttributesResponse();
            Collection entities = new ArrayList();

            for (AttributeDTO atr : attrMap.values()) {
                if (atr.getEntity().equals(entity))
                    entities.add(atr);
            }

            response.setAttributesCollection(entities);
            return response;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new Exception("Error listing Schema Attributes for entity [" + entity +"]",e);
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

