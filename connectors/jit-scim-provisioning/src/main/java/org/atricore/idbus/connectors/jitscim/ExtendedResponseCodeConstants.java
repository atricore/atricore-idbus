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

import org.wso2.charon.core.protocol.ResponseCodeConstants;

/**
 * SCIM Protocol uses the response status codes defined in HTTP to indicate
 * operation success or failure. This class includes those code and relevant description as constants.
 */
public class ExtendedResponseCodeConstants {

    //when errors returned in response, this goes as the heading of the body:
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String CODE = ResponseCodeConstants.CODE;

}
