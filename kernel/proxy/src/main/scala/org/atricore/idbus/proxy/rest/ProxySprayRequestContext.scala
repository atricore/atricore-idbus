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

import cc.spray.{RequestResponder, RequestContext}
import cc.spray.http.{HttpIp, HttpRequest}
import org.atricore.idbus.proxy.Environment

class ProxySprayRequestContext(
                           request: HttpRequest,
                           responder: RequestResponder,
                           remoteHost: HttpIp = "127.0.0.1",
                           unmatchedPath: String = "",
                           val environment: Environment)
  extends RequestContext(
    request,
    responder,
    remoteHost,
    unmatchedPath
  ) {
  override def copy(request: HttpRequest,
                    responder: RequestResponder,
                    remoteHost: HttpIp,
                    unmatchedPath: String): RequestContext =
    ProxySprayRequestContext(request, responder, remoteHost, unmatchedPath, environment)
}


object ProxySprayRequestContext {
  def apply(
             request: HttpRequest,
             responder: RequestResponder,
             remoteHost: HttpIp = "127.0.0.1",
             unmatchedPath: String = "",
             environment: Environment = Map.empty[String, Any]) =
    new ProxySprayRequestContext(request, responder, remoteHost, unmatchedPath, environment)

}
