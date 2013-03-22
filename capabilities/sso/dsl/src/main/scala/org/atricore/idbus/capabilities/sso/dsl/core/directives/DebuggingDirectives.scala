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

package org.atricore.idbus.capabilities.sso.dsl.core.directives

import org.atricore.idbus.capabilities.sso.dsl.util._

import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowRequest
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse
import org.atricore.idbus.capabilities.sso.dsl.core.{Rejection, Reject}

/**
 * Identity Flow directives for debugging
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait DebuggingDirectives extends Logging {
  this: BasicIdentityFlowDirectives =>

  def logRequestResponse(mark : String = "",
                         showRequest: IdentityFlowRequest => Any = identityFunc,
                         showResponse: IdentityFlowResponse => Any = identityFunc) = {
    transformRequestContext {
      ctx =>
        val request2Show = showRequest(ctx.request)
        log.debug("Request: %s".format(request2Show))
        ctx.withOnResponseTransformed {
          f: (IdentityFlowResponse => Unit) =>
            response =>
              log.debug("Completed %s with %s".format(request2Show,showResponse(response)))
              f(response)
        }.withOnRejectTransformed {
          f: (Set[Rejection] => Unit) =>
            rejections =>
              log.debug("Rejected %s with %s".format(request2Show, rejections))
              f(rejections)
        }
    }
  }

}
