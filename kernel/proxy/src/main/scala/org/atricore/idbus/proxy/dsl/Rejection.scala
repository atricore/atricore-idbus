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

package org.atricore.idbus.proxy.dsl

/**
 * A rejection encapsulates a specific reason why a Proxy Route was not able to handle a request. Rejections are gathered
 * up over the course of a Proxy Route evaluation.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait Rejection

case class InvalidSecurityTokenRejection(tokenValue: String) extends Rejection

case class NotAuthorizedRejection() extends Rejection

case class UnableToHandleSSOProtocolRejection(protocol: String) extends Rejection

case class UnableToInitiateSSO(protocol : ProxyProtocol) extends Rejection

case class InvalidTenantSupplied(tenantName : String) extends Rejection

case class InvalidConnectionSupplied(connectionName : String) extends Rejection

case class UnableToResolveAssertionId(assertionId : String, e : Throwable) extends Rejection

case class UnableToResolveSSOUser(jossoSessionId : String, e : Throwable) extends Rejection {
  def this(jossoSessionId : String) = this(jossoSessionId, null)
}

case class UnableToInstantiateBindingModule(bindingModuleClass : String) extends Rejection

case class UnableToRedirectUserToOriginalResource(protocol : ProxyProtocol) extends Rejection

case class UnspecifiedSecurityContextEstablishmentResource(tenant : String, connection : String, token : String)  extends Rejection

case class NoTenantForHost(host : String) extends Rejection

case class NoConnectionForSecurityContextEstablisherURI(scer : String) extends Rejection