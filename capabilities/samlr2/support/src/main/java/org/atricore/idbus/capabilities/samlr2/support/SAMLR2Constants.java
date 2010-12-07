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

package org.atricore.idbus.capabilities.samlr2.support;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SAMLR2Constants.java 1287 2009-06-16 19:19:31Z sgonzalez $
 */
public interface SAMLR2Constants {

    static final String SAML_VERSION = "2.0";

    static final Integer SAML_ARTIFACT_TYPE = 4;

    static final String SAML_ASSERTION_PKG = "oasis.names.tc.saml._2_0.assertion";

    static final String SAML_ASSERTION_NS = "urn:oasis:names:tc:SAML:2.0:assertion";

    static final String SAML_METADATA_PKG = "oasis.names.tc.saml._2_0.metadata";

    static final String SAML_METADATA_NS = "urn:oasis:names:tc:SAML:2.0:metadata";    

    static final String SAML_PROTOCOL_PKG = "oasis.names.tc.saml._2_0.protocol";

    static final String SAML_PROTOCOL_NS = "urn:oasis:names:tc:SAML:2.0:protocol";

    static final String SAML_IDBUS_PKG = "oasis.names.tc.saml._2_0.idbus";

    static final String SAML_IDBUS_NS = "urn:oasis:names:tc:SAML:2.0:idbus";



    /**
     * TODO : move, this is josso specific
     */
    static final String SSOUSER_PROPERTY_NS= "urn:org:atricore:idbus:user:property";


}
