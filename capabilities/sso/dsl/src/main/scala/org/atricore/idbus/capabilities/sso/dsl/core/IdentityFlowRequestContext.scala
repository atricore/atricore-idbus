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

package org.atricore.idbus.capabilities.sso.dsl.core

import org.atricore.idbus.capabilities.sso.dsl.{IdentityFlowResponse, IdentityFlowRequest}


/**
 * Immutable object encapsulating the identity and access control context
 * as it flows through an Identity Flow Route pipeline.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
case class IdentityFlowRequestContext(
                                        request : IdentityFlowRequest,
                                        onResponse: IdentityFlowResponse => Unit = _ => throw new IllegalStateException,
                                        onReject: Set[Rejection] => Unit = _ => throw new IllegalStateException) {

  def respond(response: IdentityFlowResponse) {
    onResponse(response)
  }

  def reject(rejections: Set[Rejection]) {
    onReject(rejections)
  }

  def withResponse(f: IdentityFlowResponse => Unit) = copy(onResponse = f)

  def withReject(f: Set[Rejection] => Unit) = copy(onReject = f)

}





