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

import javax.xml.namespace.QName;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 1242 $ $Date: 2009-06-04 23:15:49 -0300 (Thu, 04 Jun 2009) $
 */
public interface SAMLR2MessagingConstants {

    // TODO : REFACTOR !
    static final String SAMLR2_INBOUND_MSG = "urn:org:atricore:idbus:samlr2:inbound-msg";

    static final QName SERVICE_NAME = new QName("urn:oasis:names:tc:SAML:2.0:wsdl", "SAMLService");

    static final QName PORT_NAME  = new QName("urn:oasis:names:tc:SAML:2.0:wsdl", "soap");    

}

