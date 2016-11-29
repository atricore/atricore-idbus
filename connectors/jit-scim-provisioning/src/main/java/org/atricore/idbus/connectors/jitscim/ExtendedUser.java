/*
 * Atricore IDBus
 *
 * Copyright (c) 2016, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.connectors.jitscim;

import org.wso2.charon.core.attributes.DefaultAttributeFactory;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.schema.AttributeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;

/**
 * Represents the ExtendedUser object.
 */
public class ExtendedUser extends AbstractSCIMObject {

    public ExtendedUser() {
        super();
    }

    /**
     * Set bulkID when going to do the bulk operation
     *
     * @param bulkID
     * @throws CharonException
     */
    public void setBulkID(String bulkID) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.BULK_ID, SCIMSchemaDefinitions.BULK_ID,
                bulkID, DataType.STRING);
    }

    /**
     * Get bulkID
     *
     * @return
     * @throws CharonException
     */
    public String getBulkID() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.BULK_ID);
    }

    /**
     * Set path ex - /Users or /Groups
     *
     * @param path
     * @throws CharonException
     */
    public void setPath(String path) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.PATH, SCIMSchemaDefinitions.PATH,
                path, DataType.STRING);
    }

    /**
     * Get path
     *
     * @return
     * @throws CharonException
     */
    public String getPath() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.PATH);
    }

    /**
     * Set request method ex - POST
     *
     * @param method
     * @throws CharonException
     */
    public void setMethod(String method) throws CharonException {
        setSimpleAttribute(SCIMConstants.CommonSchemaConstants.METHOD, SCIMSchemaDefinitions.METHOD,
                method, DataType.STRING);
    }

    /**
     * Get request method
     *
     * @return
     * @throws CharonException
     */
    public String getMethod() throws CharonException {
        return getSimpleAttributeStringVal(SCIMConstants.CommonSchemaConstants.METHOD);
    }

    /***********************InternalID manipulation methods*************************************/
    /**
     * Set InternalID attribute of the ExtendedUser.
     *
     * @param internalId
     * @throws CharonException
     */
    public void setInternalId(String internalId) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.INTERNAL_ID, ExtendedSCIMSchemaDefinitions.INTERNAL_ID,
                internalId, DataType.STRING);
    }

    /**
     * Get InternalID attribute of the user.
     *
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public String getInternalId() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.INTERNAL_ID);
    }

    /***********************EMail manipulation methods*************************************/
    /**
     * Set Email attribute of the ExtendedUser.
     *
     * @param email
     * @throws CharonException
     */
    public void setEmail(String email) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.EMAIL, SCIMSchemaDefinitions.EMAILS,
                email, DataType.STRING);
    }

    /**
     * Get EMail attribute of the user.
     *
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public String getEmail() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.EMAIL);
    }

    /***********************NewEMail manipulation methods*************************************/
    /**
     * Set new Email attribute of the ExtendedUser.
     *
     * @param newEmail
     * @throws CharonException
     */
    public void setNewEmail(String newEmail) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.NEW_EMAIL, SCIMSchemaDefinitions.EMAILS,
                newEmail, DataType.STRING);
    }

    /**
     * Get new Email attribute of the user.
     *
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public String getNewEmail() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.NEW_EMAIL);
    }


    /**
     * ************FirstName manipulation methods.*************************************
     */

    public String getFirstName() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.FIRST_NAME);
    }

    public void setFirstName(String firstName) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.FIRST_NAME,
                ExtendedSCIMSchemaDefinitions.FIRST_NAME, firstName, DataType.STRING);
    }

    /**
     * ************LastName manipulation methods.*************************************
     */

    public String getLastName() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.LAST_NAME);
    }

    public void setLastName(String lastName) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.LAST_NAME,
                ExtendedSCIMSchemaDefinitions.LAST_NAME, lastName, DataType.STRING);
    }

    /***********************CompanyName manipulation methods*************************************/
    /**
     * Set CompanyName attribute of the ExtendedUser.
     *
     * @param companyName
     * @throws CharonException
     */
    public void setCompanyName(String companyName) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.COMPANY_NAME, ExtendedSCIMSchemaDefinitions.COMPANY_NAME,
                companyName, DataType.STRING);
    }

    /**
     * Get Company name attribute of the user.
     *
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public String getCompanyName() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.COMPANY_NAME);
    }

    /***********************Stage manipulation methods*************************************/
    /**
     * Set Stage attribute of the ExtendedUser.
     *
     * @param stage
     * @throws CharonException
     */
    public void setStage(String stage) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.STAGE, ExtendedSCIMSchemaDefinitions.STAGE,
                stage, DataType.STRING);
    }

    /**
     * Get stage attribute of the user.
     *
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public String getStage() throws CharonException {
        return getSimpleAttributeStringVal(ExtendedSCIMConstants.UserSchemaConstants.STAGE);
    }

    /**
     * ************Subsidiary manipulation methods.*************************************
     */

    public Integer getSubsidiary() throws CharonException {
        return getSimpleAttributeIntegerVal(ExtendedSCIMConstants.UserSchemaConstants.SUBSIDIARY);
    }

    public void setSubsidiary(Integer subsidiary) throws CharonException {
        setSimpleAttribute(ExtendedSCIMConstants.UserSchemaConstants.SUBSIDIARY,
                ExtendedSCIMSchemaDefinitions.SUBSIDIARY, subsidiary, DataType.STRING);
    }

    /**
     * Take common functionality of setting a value to a simple attribute, into one place.
     *
     * @param attributeName
     * @param attributeSchema
     * @param value
     * @param dataType
     * @throws CharonException
     */
    private void setSimpleAttribute(String attributeName, AttributeSchema attributeSchema,
                                    Object value, DataType dataType) throws CharonException {
        if (isAttributeExist(attributeName)) {
            //since we check read-only aspect in service provider side, no need to check it here.
            //if (!attributeSchema.getReadOnly()) {
            ((SimpleAttribute) attributeList.get(attributeName)).updateValue(value, dataType);
            /*} else {
                //log info level log that version already set and can't set again.
                throw new CharonException(ResponseCodeConstants.ATTRIBUTE_READ_ONLY);
            }*/
        } else {
            SimpleAttribute simpleAttribute = new SimpleAttribute(
                    attributeName, value);
            /*SimpleAttribute userNameAttribute = new SimpleAttribute(
                    SCIMConstants.UserSchemaConstants.USER_NAME,
                    SCIMConstants.CORE_SCHEMA_URI, userName, DataType.STRING,
                    false, false);*/
            simpleAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                    attributeSchema, simpleAttribute);
            attributeList.put(attributeName, simpleAttribute);
        }
    }

    private String getSimpleAttributeStringVal(String attributeName) throws CharonException {
        if (isAttributeExist(attributeName)) {
            return ((SimpleAttribute) attributeList.get(attributeName)).getStringValue();
        } else {
            return null;
        }
    }

    private Integer getSimpleAttributeIntegerVal(String attributeName) throws CharonException {
        if (isAttributeExist(attributeName)) {
            return (Integer) ((SimpleAttribute) attributeList.get(attributeName)).getValue();
        } else {
            return null;
        }
    }


}
