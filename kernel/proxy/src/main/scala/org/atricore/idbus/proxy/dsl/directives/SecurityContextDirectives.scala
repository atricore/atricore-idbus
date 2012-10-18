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

package org.atricore.idbus.proxy.dsl.directives

import org.josso.gateway.ws._1_2.protocol.SSOUserType
import org.scala_tools.subcut.inject.BindingModule
import org.atricore.idbus.proxy._
import configuration.{Attribute, Connection, Tenant}
import dsl.{Reject, UnspecifiedSecurityContextEstablishmentResource, RedirectToSecurityContextEstablishmentResourceAction, Pass}
import spi.SecurityContextEstablisher

/**
 * Proxy directives for managing the security context.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait SecurityContextDirectives {
  this: BasicProxyDirectives =>

  def establishSecurityContext(subject: SSOUserType, bindingModule: BindingModule, env: Environment) =
    filter {
      ssoCtx =>
        def se = bindingModule.injectOptional[SecurityContextEstablisher](None)
        def log = akka.event.Logging(actorSystem, getClass)

        se.getOrElse(throw new IllegalArgumentException("No Security Context Establisher available"))(subject, env)

        Pass
    }

  def requestSecurityContextEstablishment(tenant: Tenant, connection: Connection, token: String) =
    filter1 {
      ssoCtx =>

        connection.attributes.attrs.foldLeft(None: Option[Attribute]) {
          (a, b) => if (b.name == "securityContextEstablishmentResource") Option(b) else a
        }.map(
        {
          scer =>
            Pass(
              RedirectToSecurityContextEstablishmentResourceAction(
                "http://%s/%s/%s?tenant=%s&connection=%s&token=%s".
                  format(tenant.hostName, tenant.path, scer.value, tenant.name, connection.name, token)
              )
            )
        }).getOrElse(Reject(UnspecifiedSecurityContextEstablishmentResource(tenant.name, connection.name, token)))
    }

}
