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

package org.atricore.idbus.proxy.service

import cc.spray.Directives

import akka.actor.ActorSystem
import cc.spray.http.{StatusCodes, HttpResponse}
import cc.spray.directives.PathElement
import org.atricore.idbus.proxy.dsl.directives.ProxyDirectives
import org.atricore.idbus.proxy.configuration.ProxyConfiguration
import org.atricore.idbus.proxy.dsl._
import org.atricore.idbus.proxy.Environment
import org.atricore.idbus.proxy.rest.{ProxyRejection, ProxyPathElement, ProxySprayRequestContext, ProxySprayDirectives}

/**
 * A Simple "sso-and-forget" Agent service for the JOSSO1 protocol fully built on top of Spray and the Proxy DSL.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait JOSSO1AgentService extends Directives with ProxySprayDirectives with ProxyDirectives {

  import JOSSO1AgentService._

  def actorSystem: ActorSystem

  val log = akka.event.Logging(actorSystem, getClass)

  val proxyConfig = {
    ProxyConfiguration.fromResource(proxyConfigName, getClass.getClassLoader)
  }

  val josso1AgentService = {

    path(ssoRequestUri / ProxyPathElement / ProxyPathElement / "initiateSSO" / "josso1") {
      (tenantName, connectionName) =>
        get {
          proxy({
            () =>
              ProxyRequestContext(
                JOSSO1Protocol,
                InitiateSSOAction(tenantName, connectionName),
                proxyConfig
              )
          }) {
            ssoCtx =>
              ctx =>
                log.info("Initiate SSO Action")
                (configurationFor(tenantName, connectionName) {
                  (tenant, conn, bindingModule) =>
                    josso1LoginRequest(
                      (ssoCtx.config.attribute(conn, "jossoGatewayEndpoint")).
                        getOrElse(throw new IllegalArgumentException("No JOSSO Gateway endpoint specified")).value) {
                      loginReq =>
                        ssoCtx =>
                          ssoCtx.respond(loginReq)
                    }
                })(ssoCtx.withResponse({
                  response =>
                    val josso1Response = response.asInstanceOf[JOSSO1LoginRequest]
                    log.debug("Redirecting to : " + josso1Response.loginUrl)
                    ctx.redirect(josso1Response.loginUrl)
                }).withReject({
                  rejections => ctx.reject(ProxyRejection(rejections))
                })
                )

          }
        }
    } ~
      path(ssoRequestUri / ProxyPathElement / ProxyPathElement / "josso_security_check") {
        (tenantName, connectionName) =>
          parameter("josso_assertion_id") {
            jossoAssertionId =>
              get {
                proxy({
                  () =>
                    ProxyRequestContext(
                      JOSSO1Protocol,
                      ConsumeAssertionAction(tenantName, connectionName),
                      proxyConfig)
                }) {
                  ssoCtx =>
                    ctx =>
                      log.info("Resolving Assertion")
                      (configurationFor(tenantName, connectionName) {
                        (tenant, conn, bindingModule) =>
                          josso1ResolveAssertionId(
                            jossoAssertionId,
                            tenantName,
                            (ssoCtx.config.attribute(conn, "jossoGatewayEndpoint")).
                              getOrElse(throw new IllegalArgumentException("No JOSSO Gateway endpoint specified")).value,
                            bindingModule
                          ) {
                            jossoSessionId =>
                              requestSecurityContextEstablishment(tenant, conn, jossoSessionId) {
                                redirectRequest =>
                                  ssoCtx =>
                                    ssoCtx.respond(redirectRequest)
                              }
                          }
                      }
                        )(ssoCtx.withResponse({
                        response =>
                          ctx.redirect(response.asInstanceOf[RedirectToSecurityContextEstablishmentResourceAction].address)
                      }).withReject({
                        rejections => ctx.reject(ProxyRejection(rejections))
                      })
                      )
                }
              }
          }
      } ~
      path(PathElement / PathElement) {
        (contextPath, resourceUri) =>
          parameters('tenant, 'connection, 'token) {
            (tenantName, connectionName, token) =>
              host() {
                host =>
                  get {
                    proxy({
                      () =>
                        ProxyRequestContext(
                          JOSSO1Protocol,
                          EstablishSecurityContextAction(),
                          proxyConfig)
                    }) {
                      ssoCtx =>
                        ctx =>
                          log.info("Accessing Security Context Establishment Resource")
                          val env: Environment = ctx.asInstanceOf[ProxySprayRequestContext].environment

                          (configurationForTarget(host, contextPath, resourceUri) {
                            (tenant, connection, bindingModule) =>
                              josso1ResolveSSOUser(
                                tenant,
                                connection,
                                bindingModule,
                                token
                              ) {
                                jossoUser =>
                                  establishSecurityContext(jossoUser, bindingModule, env) {
                                    ssoCtx =>
                                      ssoCtx.respond(GrantAccessToResourceAction())
                                      log.info("Established Security Context for user : " + jossoUser.getName)
                                  }

                              }
                          })(ssoCtx.withResponse({
                            response =>
                              ctx.complete(HttpResponse(status = StatusCodes.NoContent))
                          }).withReject({
                            rejections => ctx.reject(ProxyRejection(rejections))
                          })
                          )
                    }
                  }
              }
          }
      }

  }

}

object JOSSO1AgentService {


  val proxyConfigName = "atricore.conf"

  // Single Sign-On Service URIs
  val ssoRequestUri = "sso"
  val actionParamName = "action"


}
