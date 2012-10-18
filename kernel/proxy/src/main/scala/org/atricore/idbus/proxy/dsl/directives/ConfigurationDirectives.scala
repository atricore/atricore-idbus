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

import org.atricore.idbus.proxy.configuration.{ProxyBindingModule, Connection, Tenant}
import org.atricore.idbus.proxy.dsl._

/**
 * Proxy directives for extracting proxy configuration attributes.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait ConfigurationDirectives {
  this: BasicProxyDirectives =>

  def configurationFor(tenantName: String, connectionName: String) =
    filter3[Tenant, Connection, ProxyBindingModule] {
      ssoCtx =>

        def log = akka.event.Logging(actorSystem, getClass)

        ssoCtx.config.tenant(tenantName) match {
          case Some(tenant) =>
            ssoCtx.config.connection(tenantName, connectionName) match {
              case Some(connection) =>
                try {
                  val bindingModule =
                    newInstance[ProxyBindingModule](
                      ssoCtx.config.toPlatformBindingClass,
                      Array[AnyRef](ssoCtx.config)
                    )
                  log.debug("Consuming platform binding [" + bindingModule + "]")
                  Pass(tenant, connection, bindingModule)
                } catch {
                  case ex: InstantiationException =>
                    Reject(
                      UnableToInstantiateBindingModule(
                        "org.atricore.idbus.proxy.configuration.DefaultProxyBindingModule"
                      )
                    )
                }
              case _ => Reject(InvalidConnectionSupplied(connectionName))
            }
          case _ => Reject(InvalidTenantSupplied(tenantName))
        }
    }

  def configurationForTarget(hostName: String, path: String, scer: String) =
    filter3[Tenant, Connection, ProxyBindingModule] {
      ssoCtx =>

        def log = akka.event.Logging(actorSystem, getClass)

        ssoCtx.config.tenant(hostName, path).map(tenant =>
          ssoCtx.config.connectionBySCER(tenant.name, scer).map(connection =>
            try {
              val bindingModule =
                newInstance[ProxyBindingModule](
                  ssoCtx.config.toPlatformBindingClass,
                  Array[AnyRef](ssoCtx.config)
                )
              log.debug("Consuming platform binding [" + bindingModule + "]")
              Pass(tenant, connection, bindingModule)
            } catch {
              case ex: InstantiationException =>
                Reject(
                  UnableToInstantiateBindingModule(
                    "org.atricore.idbus.proxy.configuration.DefaultProxyBindingModule"
                  )
                )
            }
          ).getOrElse(Reject(NoConnectionForSecurityContextEstablisherURI(scer)))
        ).getOrElse(Reject(NoTenantForHost(hostName)))
    }

  private def newInstance[I <: Any](clazz: Class[_], constructorArgs: Array[AnyRef])(implicit m: scala.reflect.Manifest[I]): I = {
    try {
      val constructor = clazz.getConstructors()(0)
      constructor.newInstance(constructorArgs: _*).asInstanceOf[I]
    }
    catch {
      case ex: InstantiationException =>
        throw new InstantiationException(("Unable to create injected instance of %s, " +
          "did you provide a zero-arg constructor without implicit binding module?").
          format(clazz.getName))
    }
  }

}
