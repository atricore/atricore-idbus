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

import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMResourceSchema;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;

public class ExtendedSCIMSchemaDefinitions extends SCIMSchemaDefinitions {

    public static final SCIMAttributeSchema INTERNAL_ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.INTERNAL_ID,
                    ExtendedSCIMConstants.UserSchemaConstants.INTERNAL_ID,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.INTERNAL_ID_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema EMAIL =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.EMAIL,
                    ExtendedSCIMConstants.UserSchemaConstants.EMAIL,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.EMAIL_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema FIRST_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.FIRST_NAME,
                    ExtendedSCIMConstants.UserSchemaConstants.FIRST_NAME,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.FIRST_NAME_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema LAST_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.LAST_NAME,
                    ExtendedSCIMConstants.UserSchemaConstants.LAST_NAME,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.LAST_NAME_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema COMPANY_NAME =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.COMPANY_NAME,
                    ExtendedSCIMConstants.UserSchemaConstants.COMPANY_NAME,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.COMPANY_NAME_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema STAGE =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.STAGE,
                    ExtendedSCIMConstants.UserSchemaConstants.STAGE,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.STAGE_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);


    public static final SCIMAttributeSchema SUBSIDIARY =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.SUBSIDIARY,
                    ExtendedSCIMConstants.UserSchemaConstants.SUBSIDIARY,
                    SCIMSchemaDefinitions.DataType.INTEGER, false, null,
                    ExtendedSCIMConstants.SUBSIDIARY_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);

    public static final SCIMAttributeSchema NEW_EMAIL =
            SCIMAttributeSchema.createSCIMAttributeSchema(ExtendedSCIMConstants.NEW_EMAIL,
                    ExtendedSCIMConstants.UserSchemaConstants.NEW_EMAIL,
                    SCIMSchemaDefinitions.DataType.STRING, false, null,
                    ExtendedSCIMConstants.NEW_EMAIL_DESC,
                    ExtendedSCIMConstants.ATRICORE_SCHEMA_URI, false, true, false, null);


    public static final SCIMResourceSchema EXTENDED_SCIM_USER_SCHEMA =
            SCIMResourceSchema.createSCIMResourceSchema(SCIMConstants.USER, ExtendedSCIMConstants.ATRICORE_SCHEMA_URI,
                    SCIMConstants.USER_DESC, SCIMConstants.USER_ENDPOINT,
                    INTERNAL_ID, EMAIL, FIRST_NAME, LAST_NAME, COMPANY_NAME, STAGE, SUBSIDIARY, NEW_EMAIL);

}
