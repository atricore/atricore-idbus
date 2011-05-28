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

package org.atricore.idbus.capabilities.josso.main;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface JossoConstants {

    public static final String JOSSO_PROTOCOL_PKG = "org.josso.gateway.ws._1_2.protocol";

    public static final String JOSSO_PROTOCOL_NS = "urn:org:josso:gateway:ws:1.2:protocol";

    public static final String JOSSO_SERVICE_BASE_URI = "urn:org:atricore:idbus:capabilities:josso:services";

    public static final String JOSSO_BINDING_BASE_URI = "urn:org:atricore:idbus:capabilities:josso:bindings";

    public static final String JOSSO_BACK_TO_VAR = "josso_back_to";

    public static final String JOSSO_CMD_VAR = "josso_cmd";

    public static final String JOSSO_APPID_VAR = "josso_partnerapp_id";

    public static final String JOSSO_IDPALIAS_VAR = "josso_idp_alias";

    public static final String JOSSO_APPCTX_VAR = "josso_partnerapp_ctx";

    public static final String JOSSO_APPHOST_VAR = "josso_partnerapp_vhost";

    public static final String XHTML_PKG = "org.w3._1999.xhtml";

}
