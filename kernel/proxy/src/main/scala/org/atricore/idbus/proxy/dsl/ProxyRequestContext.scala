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

import org.atricore.idbus.proxy.configuration.ProxyConfiguration


/**
 * Immutable object encapsulating the proxy context
 * as it flows through a Proxy Route pipeline.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
case class ProxyRequestContext(
  protocol : ProxyProtocol,
  action : ProxyAction,
  config : ProxyConfiguration,
  onResponse: ProxyAction => Unit = _ => throw new IllegalStateException,
  onReject: Set[Rejection] => Unit = _ => throw new IllegalStateException)
  {

    def respond(response : ProxyAction) {
      onResponse(response)
    }

    def reject(rejections : Set[Rejection]) {
      onReject(rejections)
    }

    def withResponse(f : ProxyAction => Unit) = copy(onResponse = f)

    def withReject(f: Set[Rejection] => Unit) = copy(onReject = f)
}

trait ProxyEntity
case class ServiceProvider(name : String) extends ProxyEntity

trait ProxySession
case class DefaultProxySession(id : String) extends ProxySession

trait ProxyAction
case class InitiateSSOAction(tenant : String, connection : String) extends ProxyAction
case class EndSSOAction(entity : ProxyEntity, session : ProxySession) extends ProxyAction
case class RedirectUserToIdentityProviderAction(address : String) extends ProxyAction
case class ConsumeAssertionAction(tenant : String, connection : String) extends ProxyAction
case class RedirectToSecurityContextEstablishmentResourceAction(address : String) extends ProxyAction
case class EstablishSecurityContextAction() extends ProxyAction
case class GrantAccessToResourceAction() extends ProxyAction

trait ProxyProtocol
object JOSSO1Protocol extends ProxyProtocol
object JOSSO2Protocol extends ProxyProtocol
object SAML2Protocol extends ProxyProtocol