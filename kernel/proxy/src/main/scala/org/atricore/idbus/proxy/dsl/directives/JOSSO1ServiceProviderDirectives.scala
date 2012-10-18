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

import org.scala_tools.subcut.inject.BindingModule
import org.atricore.idbus.proxy.configuration.{Attribute, Connection, Tenant}
import org.josso.gateway.ws._1_2.wsdl.{SSOIdentityManagerWS, SSOIdentityProviderWS}
import org.josso.gateway.ws._1_2.protocol.{FindUserInSessionRequestType, ResolveAuthenticationAssertionRequestType}
import javax.xml.ws.BindingProvider
import org.atricore.idbus.proxy.dsl._

/**
 * Proxy directives for handling JOSSO1 message exchanges.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait JOSSO1ServiceProviderDirectives {
  this: BasicProxyDirectives =>

  def josso1LoginRequest(jossoGatewayEndpoint: String = "http://localhost:8080") =
    filter1 {
      ssoCtx =>

        if (ssoCtx.protocol == JOSSO1Protocol && ssoCtx.action.isInstanceOf[InitiateSSOAction]) {
          ssoCtx.action match {
            case InitiateSSOAction(tenant, connection) =>
              val jossoBackToParm = "http://localhost:8080/sso/" + tenant + "/" + connection + "/josso_security_check"
              val jossoPartnerAppIdParm = tenant
              val loginUrl = jossoGatewayEndpoint + "/josso/signon/login.do?" +
                "josso_back_to=" + jossoBackToParm + "&" +
                "josso_partnerapp_id=" + jossoPartnerAppIdParm

              Pass(
                JOSSO1LoginRequest(
                  connection,
                  loginUrl)
              )
            case _ => Reject(UnableToInitiateSSO(ssoCtx.protocol))
          }
        } else {
          Reject(UnableToInitiateSSO(ssoCtx.protocol))
        }
    }

  def josso1ResolveAssertionId(assertionId: String, requester: String,
                               jossoGatewayEndpoint: String = "http://localhost:8080",
                               bindingModule: BindingModule) = filter1 {
    ssoCtx =>

      val port = bindingModule.inject[SSOIdentityProviderWS](None).getSSOIdentityProviderSoap
      setEndpointAddress(port, jossoGatewayEndpoint + "/josso/services/SSOIdentityProviderSoap")

      val req = new ResolveAuthenticationAssertionRequestType
      req.setRequester(requester)
      req.setAssertionId(assertionId)

      try {
        Pass(port.resolveAuthenticationAssertion(req).getSsoSessionId)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Reject(UnableToResolveAssertionId(assertionId, e))
      }

  }

  def josso1ResolveSSOUser(tenant: Tenant, connection: Connection, bindingModule: BindingModule, token: String) =
    filter1 {
      ssoCtx =>

        connection.attributes.attrs.foldLeft(None: Option[Attribute]) {
          (a, b) =>
            if (b.name == "jossoGatewayEndpoint") Option(b) else a
        }.map({
          jge =>
            val port = bindingModule.inject[SSOIdentityManagerWS](None).getSSOIdentityManagerSoap
            setEndpointAddress(port, jge.value + "/josso/services/SSOIdentityManagerSoap")

            val req = new FindUserInSessionRequestType()
            req.setRequester(tenant.name)
            req.setSsoSessionId(token)

            try {
              Pass(port.findUserInSession(req).getSSOUser)
            } catch {
              case e: Exception =>
                e.printStackTrace()
                Reject(UnableToResolveSSOUser(token, e))
            }
        }).getOrElse(Reject(new UnableToResolveSSOUser(token)))

    }

  private def setEndpointAddress(port: AnyRef, newAddress: String) {
    assert(newAddress != null, "Doesn't appear to be a valid address")
    if (port.isInstanceOf[BindingProvider]) {
      val bp: BindingProvider = port.asInstanceOf[BindingProvider]
      val context: java.util.Map[String, AnyRef] = bp.getRequestContext
      context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, newAddress)
    }
  }

  case class JOSSO1LoginRequest(partnerApp : String, loginUrl : String) extends ProxyAction

}


