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

package org.atricore.idbus.capabilities.sso.main.common.plans;

import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;

/**
 * Definition of variables used during plans execution.
 *
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2PlansConstants.java 1342 2009-06-26 16:21:27Z sgonzalez $
 */
public interface SSOPlanningConstants {

    static final String VAR_COT_MEMBER = "urn:org:atricore:idbus:cot-member";

    static final String VAR_COT = "urn:org:atricore:idbus:cot";

    static final String VAR_CHANNEL = "urn:org:atricore:idbus:channel";

    static final String VAR_RESPONSE_CHANNEL = "urn:org:atricore:idbus:response-channel";

    static final String VAR_ENDPOINT = "urn:org:atricore:idbus:endpoint";

    static final String VAR_DESTINATION_COT_MEMBER = "urn:org:atricore:idbus:dest-cot-member";

    static final String VAR_DESTINATION_ENDPOINT_DESCRIPTOR = "urn:org:atricore:idbus:dest-endpoint-descr";

    static final String VAR_LOCAL_METADATA_ENTRY  = "urn:org:atricore:idbus:local-metadata-entry";

    static final String VAR_RESPONSE = "urn:org:atricore:idbus:response";

    static final String VAR_RESPONSE_MODE = "urn:org:atricore:idbus:response-mode";

    static final String VAR_REQUEST = "urn:org:atricore:idbus:request";

    static final String VAR_SAMLR2_AUTHN_STATE = "urn:org:atricore:idbus:samlr2:authn:AuthenticationState";

    static final String VAR_SAMLR2_AUTHN_REQUEST = SAMLR2Constants.SAML_PROTOCOL_NS + ":AuthnRequest";

    static final String VAR_SAMLR2_ASSERTION = SAMLR2Constants.SAML_ASSERTION_NS + ":Assertion";

    static final String VAR_SAMLR2_RESPONSE =  SAMLR2Constants.SAML_PROTOCOL_NS + ":Response";

    static final String VAR_SAMLR2_REQUEST =  SAMLR2Constants.SAML_PROTOCOL_NS + ":Request";

    static final String VAR_SAMLR2_SIGNER = "urn:org:atricore:idbus:samlr2:support:Encrypter";

    static final String VAR_SAMLR2_ARTIFACT =  "urn:org:atricore:idbus:samlr2:support:SamlArtifact";

    static final String VAR_SAMLR2_ENCRYPTER = "urn:org:atricore:idbus:samlr2:support:Signer";

    static final String VAR_SAMLR2_EMITTER_CXT  = "urn:org:atricore:idbus:samlr2:support:EmitterContext";

    static final String VAR_SUBJECT = WSTConstants.SUBJECT_PROP;

    static final String VAR_IDENTITY_PLAN_NAME = WSTConstants.IDENTITY_PLAN_PROP;

    static final String VAR_SECURITY_CONTEXT = "urn:org:atricore:idbus:security-context";

    static final String VAR_LOCAL_SUBJECT = "urn:org:atricore:idbus:local-subject";

    static final String VAR_NAMEID_BUILDERS = "urn:org:atricore:idbus:samlr2:emitter:NameIDBuilders";

    static final String VAR_DEFAULT_NAMEID_BUILDER = "urn:org:atricore:idbus:samlr2:emitter:DefaultNameIDBuilder";

    static final String VAR_IGNORE_REQUESTED_NAMEID_POLICY = "urn:org:atricore:idbus:samlr2:emitter:IgnoreRequestedNameIDPolicy";

}
