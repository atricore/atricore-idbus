/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
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

package org.atricore.idbus.proxy.rest

import cc.spray._
import http.{HttpResponse, StatusCodes}
import org.atricore.idbus.proxy.dsl.InvalidTenantSupplied

/**
 * Proxy Rejection handler for Proxy-specific rejection.
 * Proxy rejections are mapped to "Bad Gateway" HTTP responses whose content is the
 * cause of the fault encountered while processing proxy routes.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
object ProxyRejectionHandler {

  val Default: RejectionHandler = {
    case ProxyRejection(rejections) :: _ =>
      rejections.toList match {
        case InvalidTenantSupplied(tenant) :: _ =>
          HttpResponse(StatusCodes.BadGateway, "Invalid tenant supplied : " + tenant)
        // TODO: add proper handling for all proxy rejections
        case _ =>
          HttpResponse(StatusCodes.BadGateway, "Fatal Atricore Proxy Error")
      }
  }

  val fullRejectionHandler = RejectionHandler.Default orElse Default
}
