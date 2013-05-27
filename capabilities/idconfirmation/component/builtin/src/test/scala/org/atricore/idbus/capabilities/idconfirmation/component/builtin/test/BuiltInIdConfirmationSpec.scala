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
package org.atricore.idbus.capabilities.idconfirmation.component.builtin.test

import org.specs2.mutable._
import org.atricore.idbus.capabilities.sso.dsl.core.directives.IdentityFlowDirectives
import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange
import org.atricore.idbus.capabilities.sso.dsl.core.{NoMoreClaimEndpoints, IdentityFlowRequestContext}
import org.atricore.idbus.capabilities.sso.dsl.{IdentityFlowRequest}
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.capabilities.sso.test.dsl.{MockCamelMediationMessage, MockCamelMediationExchange, IdentityFlowDSLTestSupport}
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.{TokenAuthenticationRequest, IdentityConfirmationState, BasicIdentityConfirmationDirectives}
import org.atricore.idbus.kernel.main.mediation.confirmation.{IdentityConfirmationRequest, IdentityConfirmationRequestImpl}
import org.atricore.idbus.kernel.main.mediation.{MediationStateImpl, MediationMessageImpl}
import org.atricore.idbus.kernel.main.mediation.claim.{UserClaimImpl, UserClaim}
import org.atricore.idbus.kernel.main.mediation.state.LocalStateImpl
import org.atricore.idbus.capabilities.sso.component.builtin.directives.MediationDirectives

/**
 * Identity Confirmation route tester.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class BuiltInIdConfirmationSpec
  extends Specification
  with IdentityFlowDSLTestSupport
  with BasicIdentityConfirmationDirectives
  with IdentityFlowDirectives
  with MediationDirectives
  with Logging {

  "The identity flow definition" should {
    "handle correctly" in {

      val r1 =
        logRequestResponse("") {
          onConfirmationRequest {
            _ =>
              fromUnknownIpAddress {
                issueSecret(10) {
                  secret =>
                    shareSecretByEmail(secret) {
                      notifyTokenShared
                    }
                }
              }
          } ~
            onConfirmationTokenAuthenticationRequest {
              _ =>
                forReceivedSecret {
                  (receivedSecret) =>
                    forIssuedSecret {
                      (issuedSecret) =>
                        verifyToken(receivedSecret, issuedSecret) {
                          notifyCompletion
                        }

                    }
                }
            }
        }


      val ctx: ContextBuilder = {
        exchange =>
        // define service provider
          val provider = newIdentityConfirmationProvider("idcp1")

          // claim channel and endpoints
          val idConfChannel1 = newIdentityConfirmationChannel("idcc-1")
          val idConfCh1Ep1 = newIdentityMediationEndpoint("idcc-1-ep1", SSOBinding.SSO_LOCAL, AuthnCtxClass.UNSPECIFIED_AUTHN_CTX)
          idConfChannel1.getEndpoints.add(idConfCh1Ep1)
          provider.setChannel(idConfChannel1)

          val req = IdentityFlowRequest(exchange.asInstanceOf[CamelMediationExchange], provider, idConfChannel1)
          IdentityFlowRequestContext(req)
      }

      val response1 = test(ctx, r1,
        newExchange(
          newIdentityConfirmationRequest(
            Map( "principal" -> "fooUser",
                 "sourceIpAddress" -> "192.168.1.1",
                 "lastSourceIpAddress" -> "192.168.1.2",
                 "emailAddress" -> "foo@acme.com")),
            ""
        )
      )
      response1.response must beSome
      response1.response.get.statusCode.isSuccess must beTrue
      response1.response.get.statusCode.value mustEqual 2
      //response1.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "cc-1"
      //response1.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "cc-1-ep1"

      val response2 = test(ctx, r1,
        newExchange(
          newIdentityConfirmationTokenAuthenticationRequest("ABCDEF1"),
          "ABCDEF1"
        )
      )
      response2.response must beSome
      response2.response.get.statusCode.isSuccess must beTrue
      response2.response.get.statusCode.value mustEqual 2
      //response2.response.get.statusCode.asInstanceOf[Redirect].channel.getName mustEqual "cc-1"
      //response2.response.get.statusCode.asInstanceOf[Redirect].endpoint.getName mustEqual "cc-1-ep1"

    }


  }

  protected def newExchange(reqPayload : Any, token : String) : MockCamelMediationExchange = {
    val ex = super.newExchange(Map.empty[String,Nothing])
    val state = new MediationStateImpl(new LocalStateImpl("mocked-up-mediation-state"))
    val in = new MediationMessageImpl("123", reqPayload, null, null, null, state)
    val msg = new MockCamelMediationMessage
    state.setLocalVariable("urn:org:atricore:idbus:idconf-state", IdentityConfirmationState(Some(token)))
    msg.setMessage(in)
    ex.setIn(msg)
    ex
  }

  protected def newIdentityConfirmationRequest(userClaims : Map[String,String]) : IdentityConfirmationRequest = {
    val icr = new IdentityConfirmationRequestImpl
    userClaims.foreach( { case(k, v) =>
      icr.addUserClaim(new UserClaimImpl("", k, v))
    })
    icr
  }

  protected def newIdentityConfirmationTokenAuthenticationRequest(token : String) : TokenAuthenticationRequest =
    TokenAuthenticationRequest(token)


}

