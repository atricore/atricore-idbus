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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.charon.core.attributes.*;
import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.bulk.BulkRequestContent;
import org.wso2.charon.core.objects.bulk.BulkRequestData;
import org.wso2.charon.core.schema.*;
import org.wso2.charon.core.util.AttributeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unmarshalls SCIM messages.
 */
public class ExtendedJSONDecoder implements Decoder {

    private static final Log logger = LogFactory.getLog(ExtendedJSONDecoder.class);

    /**
     * Decode the resource string sent in the SCIM request/response payload.
     *
     * @param scimResourceString
     * @param resourceSchema
     * @param scimObject         @return
     */
    public SCIMObject decodeResource(String scimResourceString,
                                     ResourceSchema resourceSchema, AbstractSCIMObject scimObject)
            throws BadRequestException, CharonException {
        try {
            //decode the string into json representation
            logger.trace("SCIM resource string [" + scimResourceString + "]");
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimResourceString));
            decodedJsonObj = decodedJsonObj.getJSONObject("Content").getJSONObject("data");
            //get the attribute schemas list from the schema that defines the given resource
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();
            //iterate through the schema and extract the attributes.
            for (AttributeSchema attributeSchema : attributeSchemas) {
                logger.trace("Looking for attribute [" + attributeSchema.getName() + "]");

                Object attributeValObj = decodedJsonObj.opt(attributeSchema.getName());

                if (attributeValObj instanceof String) {
                    logger.trace("Found attribute value [" + attributeValObj + "]");
                    //If an attribute is passed without a value, no need to save it.
                    if (((String) attributeValObj).isEmpty()) {
                        continue;
                    }
                    //if the corresponding json value object is String, it is a SimpleAttribute.
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema, attributeValObj));
                    logger.trace("Attribute [" + attributeSchema.getName() + "] set with value [" + attributeValObj + "]");

                } else if (attributeValObj instanceof Integer) {
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema, Integer.toString((Integer) attributeValObj)));
                } else if (attributeValObj instanceof Boolean) {
                    //if the corresponding json value object is String, it is a SimpleAttribute.
                    scimObject.setAttribute(buildSimpleAttribute(attributeSchema,
                            String.valueOf(attributeValObj)));
                } else if (attributeValObj instanceof JSONArray) {
                    //if the corresponding json value object is JSONArray, it is a MultiValuedAttribute.
                    scimObject.setAttribute(
                            buildMultiValuedAttribute(attributeSchema, (JSONArray) attributeValObj));
                } else if (attributeValObj instanceof JSONObject) {
                    //if the corresponding json value object is JSONObject, it is a ComplexAttribute.
                    scimObject.setAttribute(buildComplexAttribute(attributeSchema,
                            (JSONObject) attributeValObj));
                }
            }
            return scimObject;

        } catch (JSONException e) {
            logger.error("JSON string could not be decoded properly.", e);
            throw new BadRequestException();
        } catch (CharonException e) {
            throw new CharonException("Error in building resource from the JSON representation");
        }
    }

    /**
     * Decode the string sent in the SCIM response payload, which is an exception.
     * JSON encoded exception is usually like:
     *
     * {
     * Content:
     * {
     * status: “ERROR”,
     * data: null,
     * message: “Validation error”,
     * code: “400”
     * }
     * }
     *
     * @param scimExceptionString
     * @return
     */
    public AbstractCharonException decodeException(String scimExceptionString)
            throws CharonException {
        //decode the string into json representation
        try {
            logger.trace("Decoding exception [" + scimExceptionString + "]");
            JSONObject decodedJsonObj = new JSONObject(new JSONTokener(scimExceptionString));
            JSONObject errorObject = decodedJsonObj.getJSONObject("Content");

            String status = (String) errorObject.opt(ExtendedResponseCodeConstants.STATUS);
            String message = (String) errorObject.opt(ExtendedResponseCodeConstants.MESSAGE);
            String errorCode = (String) errorObject.opt(ExtendedResponseCodeConstants.CODE);
            return new AbstractCharonException(Integer.parseInt(errorCode), message);

        } catch (JSONException e) {
            throw new CharonException("Error in building exception from the JSON representation");
        }
    }

    /**
     * Decode the attribute, given that it is identified as a Simple Attribute.
     *
     * @param attributeSchema
     * @param attributeValue
     * @return
     * @throws CharonException
     */
    private SimpleAttribute buildSimpleAttribute(AttributeSchema attributeSchema,
                                                 Object attributeValue) throws CharonException {
        Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                (String) attributeValue, attributeSchema.getType());
        SimpleAttribute simpleAttribute = new SimpleAttribute(attributeSchema.getName(), attributeValueObject);
        return (SimpleAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                simpleAttribute);
    }

    /**
     * Decode the attribute, given that it is identified as a MultiValued Attribute.
     *
     * @param attributeSchema
     * @param attributeValues
     * @return
     * @throws CharonException
     */
    private MultiValuedAttribute buildMultiValuedAttribute(AttributeSchema attributeSchema,
                                                           JSONArray attributeValues)
            throws CharonException {
        try {
            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
            List<String> simpleAttributeValues = new ArrayList<String>();
            List<Attribute> complexAttributeValues = new ArrayList<Attribute>();

            //iterate through JSONArray and create the list of string values.
            for (int i = 0; i < attributeValues.length(); i++) {
                Object attributeValue = attributeValues.get(i);

                if (attributeValue instanceof String) {
                    if (((String) attributeValue).isEmpty()) {
                        continue;
                    }
                    simpleAttributeValues.add((String) attributeValues.get(i));
                } else if (attributeValue instanceof JSONObject) {
                    JSONObject complexAttributeValue = (JSONObject) attributeValue;
                    complexAttributeValues.add(buildComplexValue(attributeSchema, complexAttributeValue));
                } else {
                    //TODO:log the error.
                    String error = "Unknown JSON representation for the MultiValued attribute Value..";
                    throw new CharonException(error);
                }

            }
            multiValuedAttribute.setValuesAsStrings(simpleAttributeValues);
            multiValuedAttribute.setValuesAsSubAttributes(complexAttributeValues);

            return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                    multiValuedAttribute);
        } catch (JSONException e) {
            throw new CharonException("Error in accessing JSON value of multivalues attribute");
        }
    }

    /**
     * Decode the attribute, given that it is identified as a simple multi valued attribute.
     *
     * @param attributeSchema
     * @param attributeValues
     * @return
     */
    private MultiValuedAttribute buildSimpleMultiValuedAttribute(AttributeSchema attributeSchema,
                                                                 JSONArray attributeValues)
            throws CharonException {
        try {
            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
            List<String> simpleAttributeValues = new ArrayList<String>();

            //iterate through JSONArray and create the list of string values.
            for (int i = 0; i < attributeValues.length(); i++) {
                simpleAttributeValues.add((String) attributeValues.get(i));
            }
            multiValuedAttribute.setValuesAsStrings(simpleAttributeValues);

            return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                    multiValuedAttribute);
        } catch (JSONException e) {
            throw new CharonException("Error in accessing the value of multivalued attribute.");
        }

    }

    /**
     * Decode the attribute, given that it is identified as a complex multi-valued attribute.
     *
     * @param attributeSchema
     * @param attributeValues
     * @return
     */
    private MultiValuedAttribute buildComplexMultiValuedAttribute(AttributeSchema attributeSchema,
                                                                  JSONArray attributeValues)
            throws CharonException {
        try {
            MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(attributeSchema.getName());
            List<Attribute> complexAttributeValues = new ArrayList<Attribute>();
            //iterate through JSONArray and create the list of values as complex attributes..
            for (int i = 0; i < attributeValues.length(); i++) {
                JSONObject complexAttributeValue = (JSONObject) attributeValues.get(i);
                complexAttributeValues.add(buildComplexValue(attributeSchema, complexAttributeValue));
            }
            //set values as complex attributes
            multiValuedAttribute.setValuesAsSubAttributes(complexAttributeValues);
            //canonicalize before storing.
            //see primary is not set twice.
            return (MultiValuedAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                    multiValuedAttribute);
        } catch (JSONException e) {
            throw new CharonException("Error in accessing the value of multivalued attribute.");
        }
    }

    /**
     * Decode the attribute, given that it is identified as a complex attribute.
     *
     * @param complexAttributeSchema
     * @param jsonObject
     * @return
     */
    private ComplexAttribute buildComplexAttribute(AttributeSchema complexAttributeSchema, JSONObject jsonObject)
            throws CharonException {

        ComplexAttribute complexAttribute = new ComplexAttribute(complexAttributeSchema.getName());
        Map<String, Attribute> attributesMap = new HashMap<String, Attribute>();

        // If complex attribute has only sub attributes
        if (((SCIMAttributeSchema) complexAttributeSchema).getSubAttributes() != null) {
            List<SCIMSubAttributeSchema> subAttributeSchemas =
                    ((SCIMAttributeSchema) complexAttributeSchema).getSubAttributes();

            for (SCIMSubAttributeSchema subAttributeSchema : subAttributeSchemas) {
                Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
                if (subAttributeValue instanceof Integer) {
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(subAttributeSchema,
                                    String.valueOf(subAttributeValue));
                    attributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                }
                if (subAttributeValue instanceof String) {
                    if (((String) subAttributeValue).isEmpty()) {
                        continue;
                    }
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(subAttributeSchema,
                                    subAttributeValue);
                    // let the attribute factory to set the sub attribute of a
                    // complex attribute to detect schema violations.
                    // DefaultAttributeFactory.setSubAttribute(complexAttribute,
                    // simpleAttribute);
                    attributesMap.put(subAttributeSchema.getName(), simpleAttribute);
                } else if (subAttributeValue instanceof JSONArray) {
                    // there can be sub attributes which are multivalued: such
                    // as: Meta->attributes
                    MultiValuedAttribute multivaluedAttribute =
                            buildMultiValuedAttribute(subAttributeSchema,
                                    (JSONArray) subAttributeValue);
                    /*
					 * DefaultAttributeFactory.setSubAttribute(
					 * complexAttribute,
					 * buildMultiValuedAttribute(subAttributeSchema,
					 * (JSONArray) subAttributeValue));
					 */
                    attributesMap.put(subAttributeSchema.getName(), multivaluedAttribute);
                }
            }
            complexAttribute.setSubAttributes(attributesMap);

            // if complex attribute has only attributes
        } else if (((SCIMAttributeSchema) complexAttributeSchema).getAttributes() != null) {

            List<SCIMAttributeSchema> attributeSchemas =
                    ((SCIMAttributeSchema) complexAttributeSchema).getAttributes();
            for (SCIMAttributeSchema attribSchema : attributeSchemas) {
                Object subAttributeValue = jsonObject.opt(attribSchema.getName());
                if (subAttributeValue instanceof Integer) {
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(attribSchema,
                                    String.valueOf(subAttributeValue));
                    attributesMap.put(simpleAttribute.getName(), simpleAttribute);
                }
                if (subAttributeValue instanceof String) {
                    if (((String) subAttributeValue).isEmpty()) {
                        continue;
                    }
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(attribSchema, subAttributeValue);
                    attributesMap.put(simpleAttribute.getName(), simpleAttribute);
                } else if (subAttributeValue instanceof Boolean) {
                    SimpleAttribute simpleAttribute =
                            buildSimpleAttribute(attribSchema,
                                    String.valueOf(subAttributeValue));
                    attributesMap.put(simpleAttribute.getName(), simpleAttribute);
                } else if (subAttributeValue instanceof JSONArray) {
                    MultiValuedAttribute multivaluedAttribute =
                            buildMultiValuedAttribute(attribSchema,
                                    (JSONArray) subAttributeValue);
                    attributesMap.put(multivaluedAttribute.getName(), multivaluedAttribute);
                } else if (subAttributeValue instanceof JSONObject) {
                    ComplexAttribute complexAttri =
                            buildComplexAttribute(attribSchema,
                                    (JSONObject) subAttributeValue);
                    attributesMap.put(complexAttri.getName(), complexAttri);
                }
            }
            complexAttribute.setAttributes(attributesMap);
        }
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(complexAttributeSchema, complexAttribute);
    }

    /**
     * To build a complex type value of a Multi Valued Attribute.
     *
     * @param attributeSchema
     * @param jsonObject
     * @return
     */
    private ComplexAttribute buildComplexValue(AttributeSchema attributeSchema,
                                               JSONObject jsonObject) throws CharonException {
        ComplexAttribute complexAttribute = new ComplexAttribute();
        Map<String, Attribute> subAttributesMap = new HashMap<String, Attribute>();
        List<SCIMSubAttributeSchema> subAttributeSchemas =
                ((SCIMAttributeSchema) attributeSchema).getSubAttributes();

        for (SCIMSubAttributeSchema subAttributeSchema : subAttributeSchemas) {

            Object subAttributeValue = jsonObject.opt(subAttributeSchema.getName());
            if (subAttributeValue instanceof String) {
                SimpleAttribute simpleAttribute =
                        buildSimpleAttribute(subAttributeSchema, subAttributeValue);
                //set the URI of value according to the type if present
                if (SCIMConstants.CommonSchemaConstants.VALUE.equals(subAttributeSchema.getName())) {
                    if ((jsonObject.opt(SCIMConstants.CommonSchemaConstants.TYPE) != null)) {
                        String type = (String) jsonObject.opt(SCIMConstants.CommonSchemaConstants.TYPE);
                        String uri = attributeSchema.getURI() + "." + type;
                        simpleAttribute.setAttributeURI(uri);
                    }
                }
                //let the attribute factory to set the sub attribute of a complex attribute to detect schema violations.
                //DefaultAttributeFactory.setSubAttribute(complexAttribute, simpleAttribute);
                subAttributesMap.put(subAttributeSchema.getName(), simpleAttribute);
            } else if (subAttributeValue instanceof JSONArray) {
                //there can be sub attributes which are multivalued: such as: Meta->attributes
                /*DefaultAttributeFactory.setSubAttribute(
                        complexAttribute, buildMultiValuedAttribute(subAttributeSchema,
                                                                    (JSONArray) subAttributeValue));*/
                MultiValuedAttribute multivaluedAttribute =
                        buildMultiValuedAttribute(subAttributeSchema, (JSONArray) subAttributeValue);
                subAttributesMap.put(subAttributeSchema.getName(), multivaluedAttribute);
            }
        }
        complexAttribute.setSubAttributes(subAttributesMap);
        return (ComplexAttribute) DefaultAttributeFactory.createAttribute(attributeSchema,
                complexAttribute);
    }


    /**
     * Decode BulkRequestData Json String
     *
     * @param bulkResourceString
     * @return BulkRequestData Object
     */
    public BulkRequestData decodeBulkData(String bulkResourceString) throws BadRequestException {
        BulkRequestData bulkRequestDataObject = new BulkRequestData();
        List<BulkRequestContent> userCreatingRequestList = new ArrayList<BulkRequestContent>();
        List<BulkRequestContent> groupCreatingRequestList = new ArrayList<BulkRequestContent>();
        int failOnErrorsAttribute = 0;
        List<String> schemas = new ArrayList<String>();

        //TODO: Validation has to be done for failOnErrors,..etc
        JSONObject decodedObject = null;
        try {
            decodedObject = new JSONObject(new JSONTokener(bulkResourceString));

            //prepare the schema list
            JSONArray membersAttributeSchemas = (JSONArray) decodedObject.opt(
                    SCIMConstants.CommonSchemaConstants.SCHEMAS);
            for (int i = 0; i < membersAttributeSchemas.length(); i++) {
                schemas.add(membersAttributeSchemas.get(i).toString());
            }
            bulkRequestDataObject.setSchemas(schemas);

            //get [operations] from the Json String and prepare the request List
            JSONArray membersAttributeOperations = (JSONArray) decodedObject.opt(
                    SCIMConstants.CommonSchemaConstants.OPERATIONS);

            for (int i = 0; i < membersAttributeOperations.length(); i++) {
                JSONObject member = (JSONObject) membersAttributeOperations.get(i);
                //Request path - /Users or /Groups
                String requestType = member.optString(SCIMConstants.CommonSchemaConstants.PATH);
                //Request method  - POST,PUT..etc
                String requestMethod = member.optString(SCIMConstants.CommonSchemaConstants.METHOD);

                //only filter the post requests (user or group creating methods)
                if (requestMethod.equals("POST")) {
                    if (!member.optString(SCIMConstants.CommonSchemaConstants.BULK_ID).equals("") &&
                            !member.optString(SCIMConstants.CommonSchemaConstants.BULK_ID).equals(null)) {
                        //create user request list
                        if (requestType.equals(SCIMConstants.CommonSchemaConstants.USERS_PATH)) {
                            BulkRequestContent newRequestData = new BulkRequestContent();

                            newRequestData.setData(member.optString(SCIMConstants.CommonSchemaConstants.DATA));
                            newRequestData.setBulkID(member.optString(SCIMConstants.CommonSchemaConstants.BULK_ID));
                            newRequestData.setMethod(requestMethod);
                            newRequestData.setPath(requestType);

                            userCreatingRequestList.add(newRequestData);
                            logger.trace("User Request-" + i + "-" + newRequestData.toString());
                        }

                        //create group request list
                        if (requestType.equals(SCIMConstants.CommonSchemaConstants.GROUPS_PATH)) {
                            BulkRequestContent newRequestData = new BulkRequestContent();

                            newRequestData.setData(member.optString(SCIMConstants.CommonSchemaConstants.DATA));
                            newRequestData.setBulkID(member.optString(SCIMConstants.CommonSchemaConstants.BULK_ID));
                            newRequestData.setMethod(requestMethod);
                            newRequestData.setPath(requestType);

                            groupCreatingRequestList.add(newRequestData);
//                        logger.debug("Group Request-" + i + "-" + newRequestData.toString());
                        }
                    } else {
                        String error = "JSON string could not be decoded properly.Required " +
                                "attribute BULK_ID is missing in the request";
                        logger.error(error);
                        throw new BadRequestException();
                    }
                }

            }

            //extract [failOnErrors] attribute from Json string
            failOnErrorsAttribute = decodedObject.optInt(SCIMConstants.CommonSchemaConstants.FAIL_ON_ERRORS);
            logger.debug(failOnErrorsAttribute);

            bulkRequestDataObject.setFailOnErrors(failOnErrorsAttribute);
            bulkRequestDataObject.setUserCreatingRequests(userCreatingRequestList);
            bulkRequestDataObject.setGroupCreatingRequests(groupCreatingRequestList);

        } catch (JSONException e1) {
            logger.error("JSON string could not be decoded properly.");
            throw new BadRequestException();
        }

        return bulkRequestDataObject;
    }


}
