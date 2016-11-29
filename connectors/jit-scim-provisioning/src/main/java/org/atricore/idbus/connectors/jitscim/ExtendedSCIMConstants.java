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

import org.wso2.charon.core.schema.SCIMConstants;

public class ExtendedSCIMConstants extends SCIMConstants {
    public static final String ATRICORE_SCHEMA_URI = "urn:scim:schemas:atricore:1.0";

    public static final String INTERNAL_ID = "InternalId";
    public static final String EMAIL = "Email";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String COMPANY_NAME = "CompanyName";
    public static final String STAGE = "Stage";
    public static final String SUBSIDIARY = "Subsidiary";
    public static final String NEW_EMAIL = "NewEmail";

    public static class UserSchemaConstants extends SCIMConstants.UserSchemaConstants {
        public static final String INTERNAL_ID = "internalId";
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String COMPANY_NAME = "companyName";
        public static final String STAGE = "stage";
        public static final String SUBSIDIARY = "subsidiary";
        public static final String NEW_EMAIL = "newEMail";
    }

    public static final String INTERNAL_ID_DESC = "internalId";
    public static final String EMAIL_DESC = "Email";
    public static final String FIRST_NAME_DESC = "User's first name";
    public static final String LAST_NAME_DESC = "User's lastname";
    public static final String COMPANY_NAME_DESC = "Company name";
    public static final String STAGE_DESC = "Stage";
    public static final String SUBSIDIARY_DESC = "Unique identifier for subsidiary";
    public static final String NEW_EMAIL_DESC = "New Email";

}
