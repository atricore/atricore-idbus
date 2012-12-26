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

package org.atricore.idbus.kernel.authz.dsl

import org.atricore.idbus.kernel.authz._


/**
 * Immutable object encapsulating the access control (i.e. PDP) context
 * as it flows through an Access Control Route pipeline.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
case class AccessControlRequestContext(
                                        request : DecisionRequest,
                                        onResponse: AccessControlAction => Unit = _ => throw new IllegalStateException,
                                        onReject: Set[Rejection] => Unit = _ => throw new IllegalStateException) {

  var response: Response = _

  def respond(response: AccessControlAction) {
    onResponse(response)
  }

  def reject(rejections: Set[Rejection]) {
    onReject(rejections)
  }

  def withResponse(f: AccessControlAction => Unit) = copy(onResponse = f)

  def withReject(f: Set[Rejection] => Unit) = copy(onReject = f)

}

trait AccessControlEntity

case class ServiceProvider(name: String) extends AccessControlEntity

trait AccessControlSession

case class DefaultAccessControlSession(id: String) extends AccessControlSession

case class AccessControlAction(decision : AccessControlDecision)

object DoPermit extends AccessControlAction(Permit)
object DoDeny extends AccessControlAction(Deny)
object DoIndeterminate extends AccessControlAction(Indeterminate)
object DoNotApplicable extends AccessControlAction(NotApplicable)

trait AccessControlDecision

object Permit extends AccessControlDecision
object Deny extends AccessControlDecision
object Indeterminate extends AccessControlDecision
object NotApplicable extends AccessControlDecision



