/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.management.main.util;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SAMLR2Constants.java 1287 2009-06-16 19:19:31Z sgonzalez $
 */
public enum CertificateConstants {

    SAML_VERSION ("2.0"),

    SAML_ASSERTION_PKG ("oasis.names.tc.saml._2_0.assertion"),

    SAML_ASSERTION_NS ("urn:oasis:names:tc:SAML:2.0:assertion"),

    SAML_METADATA_PKG ("oasis.names.tc.saml._2_0.metadata"),

    SAML_METADATA_NS ("urn:oasis:names:tc:SAML:2.0:metadata"),    

    SAML_PROTOCOL_PKG ("oasis.names.tc.saml._2_0.protocol"),

    SAML_PROTOCOL_NS ("urn:oasis:names:tc:SAML:2.0:protocol"),
    
    XML_DSIG_NS ("http://www.w3.org/2000/09/xmldsig#"),
    
    XML_DSIG_PKG ("org.w3._2000._09.xmldsig_");
    
    String urn;

    CertificateConstants(String urn){
    	this.urn = urn;
    }

	public String getUrn() {
		return urn;
	}

	@Override
	public String toString() {
		return urn;
	}
    
    

}
