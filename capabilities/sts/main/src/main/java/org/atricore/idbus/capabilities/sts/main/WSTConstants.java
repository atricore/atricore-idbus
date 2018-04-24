/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.sts.main;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: WSTConstants.java 1220 2009-05-28 03:57:34Z sgonzalez $
 */
public interface WSTConstants {

    /**
     * This is not actually an oasis profile
     */
    static final String WST_OAUTH2_TOKEN_TYPE =
            "urn:docs:oasis:open:org:wss:oasis_wss_oauth2_token_profile_1_1#OAUTH2.0";

    static final String WST_OAUTH2_RM_TOKEN_TYPE =
            "urn:docs:oasis:open:org:wss:oasis_wss_oauth2_token_profile_1_1#RM_OAUTH2.0";

    static final String WST_OIDC_AUTHZ_CODE_TYPE =
            "urn:atricore:org:wss:atricore_wss_oidc_authz_code_profile_1_1#OAUTH2.0";

    static final String WST_OIDC_ID_TOKEN_TYPE =
            "urn:atricore:org:wss:atricore_wss_oidc_id_token_profile_1_1#JWT1.0";

    static final String WST_OIDC_ACCESS_TOKEN_TYPE =
            "urn:atricore:org:wss:atricore_wss_oidc_access_token_profile_1_1#JWT1.0";

    static final String WST_OIDC_REFRESH_TOKEN_TYPE =
            "urn:atricore:org:wss:atricore_wss_oidc_refresh_token_profile_1_1#JWT1.0";

    static final String WST_SAMLR2_TOKEN_TYPE =
            "urn:docs:oasis:open:org:wss:oasis_wss_saml_token_profile_1_1#SAMLV2.0";

    static final String WST_ISSUE_REQUEST =
            "http://schemas.xmlsoap.org/ws/2004/04/security/trust/Issue";

    /**
     * RST Context namespace (internal)
     */
    static final String RST_CTX ="urn:org:atricore:idbus:kernel:main:sts::RSTContext";

    /**
     *
     */
    static final String VAR_EMISSION_CTX ="urn:org:atricore:idbus:kernel:main:sts::SecurityTokenEmissionContext";

    /**
     * Request Token namespace (internal) 
     */
    static final String REQUEST_TOKEN = "urn:org:atricore:idbus:kernel:main:sts:RequestToken";

    /**
     * Subject property name
     */
    static final String SUBJECT_PROP = "urn:org:atricore:idbus:kernel:main:sts:Subject";

    static final String IDENTITY_PLAN_PROP = "urn:org:atricore:idbus:kernel:main:sts:IdentityPlan";

    static final String WST_AUTHN_SOURCE = "urn:org:atricore:idbus:kernel:main:sts:AuthnSource";
}
