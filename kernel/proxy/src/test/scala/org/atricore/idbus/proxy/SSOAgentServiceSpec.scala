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

package org.atricore.idbus.proxy

import dsl.InvalidTenantSupplied
import org.specs2.mutable._
import cc.spray.http.HttpMethods.GET
import org.atricore.idbus.proxy.test._
import cc.spray._
import http.HttpHeaders.{Host, Location}
import http.{StatusCodes, HttpResponse, HttpRequest}
import rest.ProxyRejectionHandler
import service.JOSSO1AgentService

/**
 * Simple JOSSO1 "SSO-and-forget" Agent tester.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class SSOAgentServiceSpec extends Specification with ProxyTest with JOSSO1AgentService {

  "The SSO Proxy service" should {
    "handle correctly" in {
      implicit val proxyRejectionHandler: RejectionHandler = ProxyRejectionHandler.fullRejectionHandler

      "JOSSO1 SSO protocol flow" in {

        testProxyService(HttpRequest(GET, "/sso/T1/C1/initiateSSO/josso1"),
          environment = Map(MockSecurityContextEstablisher.mockHttpRequestEnvName -> MutableMockRequest())) {
            josso1AgentService
        }.response.status mustEqual StatusCodes.Found

        val res = testService(HttpRequest(GET, "/sso/T1/C1/josso_security_check?josso_assertion_id=ABCDEF123")) {
          josso1AgentService
        }

        res.response.status mustEqual StatusCodes.Found
        res.response.headers mustEqual List(
          `Location`("http://localhost:8080/t1/c1_acs.jsp?tenant=T1&connection=C1&token=6AF80DCA9B78E3DC8175BEB5756AFA77")
        )

        val mockHttpRequest = MutableMockRequest()
        testProxyService(
          HttpRequest(GET, "/t1/c1_acs.jsp?tenant=T1&connection=C1&token=6AF80DCA9B78E3DC8175BEB5756AFA77").withHeaders(
              List(Host("localhost", Some(8080)))
          ),
          environment = Map(MockSecurityContextEstablisher.mockHttpRequestEnvName -> mockHttpRequest)) {
          josso1AgentService
        }.response.status mustEqual StatusCodes.NoContent

        mockHttpRequest.outUserPrincipal mustEqual Some("user1")

      }
      "proxy service errors" in {
        testProxyService(HttpRequest(GET, "/sso/NonExistentTenant/C1/initiateSSO/josso1"),
          environment = Map(MockSecurityContextEstablisher.mockHttpRequestEnvName -> MutableMockRequest())) {
          josso1AgentService
        }.response.content.as[String] mustEqual Right("Invalid tenant supplied : NonExistentTenant")

      }
    }

  }
}