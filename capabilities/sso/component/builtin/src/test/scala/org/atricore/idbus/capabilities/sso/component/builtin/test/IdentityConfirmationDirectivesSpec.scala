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
package org.atricore.idbus.capabilities.sso.component.builtin.test

import org.specs2.mutable._
import org.atricore.idbus.capabilities.sso.dsl.core.directives.IdentityFlowDirectives
import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange
import org.atricore.idbus.capabilities.sso.dsl.core.{NoMoreIdentityConfirmationEndpoints, NoMoreClaimEndpoints, IdentityFlowRequestContext}
import org.atricore.idbus.capabilities.sso.dsl.{Redirect, IdentityFlowRequest}
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.capabilities.sso.test.dsl.IdentityFlowDSLTestSupport
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator
import org.atricore.idbus.capabilities.sso.component.builtin.directives.{MediationDirectives, IdentityConfirmationDirectives}

class IdentityConfirmationDirectivesSpec extends Specification
  with IdentityFlowDSLTestSupport
  with IdentityFlowDirectives
  with MediationDirectives
  with IdentityConfirmationDirectives
  with Logging {

  "The identity confirmation directive" should {
    "select identity confirmation endpoints" in {
      val r1 =
        logRequestResponse("") {
          pendingIdentityConfirmationRetries(1) {
            retryToConfirmIdentity
          } ~
          pickIdentityConfirmationChannel {
            confirmIdentity(_, _)
          }
        }

      val ctx : ContextBuilder = {
        exchange =>

        // define service provider
        val sp1 = newServiceProvider("sp1")

        // define identity confirmation provider
        val idcp1 = newIdentityConfirmationProvider("idcp1")

        // identity confirmation provider, channel and endpoints
        val idConfCh1 = newIdentityConfirmationChannel("idcc-1")
        val idConfCh1Ep1 = newIdentityMediationEndpoint("idcc-1-ep1", SSOBinding.SSO_LOCAL, AuthnCtxClass.UNSPECIFIED_AUTHN_CTX)
        idConfCh1.getEndpoints.add(idConfCh1Ep1)
        val idConfCh1Ep2 = newIdentityMediationEndpoint("idcc-1-ep2", SSOBinding.SSO_LOCAL, AuthnCtxClass.UNSPECIFIED_AUTHN_CTX)
        idConfCh1.getEndpoints.add(idConfCh1Ep2)

        idcp1.setChannel(idConfCh1)

        // define service provider channel
        val spc1 = newSpChannel("spc-1")
        spc1.getIdentityConfirmationProviders.add(idConfCh1)

        val req = IdentityFlowRequest(exchange.asInstanceOf[CamelMediationExchange], sp1, spc1)
        IdentityFlowRequestContext(req)
      }

      val as = new AuthenticationState

      val response1 = test(ctx, r1, newExchange( newSecurityContext("sp1"),as))
      response1.response must beSome
      response1.response.get.statusCode.isSuccess must beTrue
      response1.response.get.statusCode.value mustEqual 1
      response1.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "idconf-1"
      response1.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "idcc-1-ep1"

      val response2 = test(ctx, r1, newExchange( newSecurityContext("sp1"),as))

      response2.response must beSome
      response2.response.get.statusCode.isSuccess must beTrue
      response2.response.get.statusCode.value mustEqual 1
      response2.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "idconf-1"
      response2.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "idcc-1-ep1"

      val response3 = test(ctx, r1, newExchange( newSecurityContext("sp1"),as))
      response3.response.get.statusCode.isSuccess must beTrue
      response3.response.get.statusCode.value mustEqual 1
      response3.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "idconf-1"
      response3.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "idcc-1-ep2"

      val response4 = test(ctx, r1, newExchange( newSecurityContext("sp1"),as))
      response4.response.get.statusCode.isSuccess must beTrue
      response4.response.get.statusCode.value mustEqual 1
      response4.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "idconf-1"
      response4.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "idcc-1-ep2"

      val response5 = test(ctx, r1, newExchange( newSecurityContext("sp1"),as))
      response5.rejections must beSome
      response5.rejections must beSome(Set(NoMoreIdentityConfirmationEndpoints))

    }

  }
}


