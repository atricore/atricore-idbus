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
import org.wso2.charon.core.client.CharonClientConfig;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.encoder.Decoder;
import org.wso2.charon.core.encoder.Encoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.ClientSideValidator;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

/**
 * Extended SCIM Client API.
 */
public class ExtendedSCIMClient extends SCIMClient {

    private static final Log logger = LogFactory.getLog(ExtendedSCIMClient.class);

    public static final int USER = 1;
    public static final int GROUP = 2;
    public static final int EXTENDED_USER = 3;

    /**
     * JSON encoder, decoder
     */
    private Encoder jsonEncoder;
    private Decoder jsonDecoder;


    public ExtendedSCIMClient() {
        jsonEncoder = new JSONEncoder();
        jsonDecoder = new ExtendedJSONDecoder();
    }

    public ExtendedSCIMClient(CharonClientConfig clientConfig) {
        //to allow to set extensions in the SCIM Clientside like: encoder/decoders, AuthHandlers etc.
        //TODO:decide how to register schema extension in client side - i.e: If an extended schema
        //is registered for User, how to access that user, when "createUser" method is called.
        //we can have user, group objects of parent type and initialize relevant type through config constructor.
    }

    public ExtendedUser createExtendedUser() {
        return createExtendedUser();
    }


    /**
     * Once the response is identified as containing exception, decode the relevant e
     *
     * @param scimResponse
     * @param format
     * @return
     * @throws CharonException
     */
    public AbstractCharonException decodeSCIMException(String scimResponse, String format)
            throws CharonException {
        if ((format.equals(SCIMConstants.JSON)) && (jsonDecoder != null)) {
            return jsonDecoder.decodeException(scimResponse);
        } else {
            throw new CharonException("Encoder in the given format is not properly initialized..");
        }
    }

    /**
     * Decode the SCIMResponse, given the format and the resource type.
     * Here we assume the resource type is of an existing SCIMObject type.
     * Int type is given as parameter rather than AbstractSCIMObject - so that API user can
     * select out of existing type without worrying about which extended type to pass.
     *
     * @param scimResponse
     * @param format
     * @return
     */
    public SCIMObject decodeSCIMResponse(String scimResponse, String format, int resourceType)
            throws BadRequestException, CharonException {
        if ((format.equals(SCIMConstants.JSON)) && (jsonDecoder != null)) {
            return decodeSCIMResponse(scimResponse, jsonDecoder, resourceType);

        } else {
            throw new CharonException("Encoder in the given format is not properly initialized..");
        }
    }

    private SCIMObject decodeSCIMResponse(String scimResponse, Decoder decoder,
                                          int resourceType)
            throws CharonException, BadRequestException {

        switch (resourceType) {
            case USER:
                User userObject = (User) decoder.decodeResource(scimResponse,
                        SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                        new User());
                ClientSideValidator.validateRetrievedSCIMObject(userObject,
                        SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
                return userObject;
            case GROUP:
                Group groupObject = (Group) decoder.decodeResource(scimResponse,
                        SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                        new Group());
                ClientSideValidator.validateRetrievedSCIMObject(groupObject,
                        SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
                return groupObject;
            case EXTENDED_USER:
                ExtendedUser extendedUserObject = (ExtendedUser) decoder.decodeResource(scimResponse,
                        ExtendedSCIMSchemaDefinitions.EXTENDED_SCIM_USER_SCHEMA,
                        new ExtendedUser());
                /*
                ClientSideValidator.validateRetrievedSCIMObject(extendedUserObject,
                        SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
                */
                return extendedUserObject;
            default:
                throw new CharonException("Resource type didn't match any existing types.");
        }
    }
}
