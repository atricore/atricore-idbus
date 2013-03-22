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

package org.atricore.idbus.capabilities.sso.dsl.core.directives

import org.atricore.idbus.capabilities.sso.dsl.core._
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationExchange}
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import oasis.names.tc.saml._2_0.protocol.RequestedAuthnContextType
import scala.Option
import scala.collection.JavaConversions._
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.{Channel, MediationState}
import org.atricore.idbus.capabilities.sso.dsl.{Redirect, IdentityFlowSuccess, IdentityFlowResponse}

/**
 * Identity Flow directives for handling
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait MediationDirectives {
  this: BasicIdentityFlowDirectives =>

  def mediationState =
    filter1 {
      ctx =>
        Option(ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getState) match {
          case Some(state) =>
            Pass(state)
          case _ =>
            Reject(NoStateAvailable)
        }

    }

  def securityContext = {
    filter1 {
      ctx =>
        val securityContextVarName = ctx.request.provider.getName.toUpperCase + "_SECURITY_CTX"

        mediationState.filter(ctx) match {
          case Pass(values, transform) =>
            Option(values._1.getLocalVariable(securityContextVarName)) match {
              case Some(sc) =>
                Pass(sc.asInstanceOf[IdPSecurityContext])
              case _ =>
                Reject(NoSecurityContextAvailable)
            }
        }
    }
  }

  def sessionManager = {
    filter1 {
      ctx =>
        Option(ctx.request.channel.asInstanceOf[SPChannel].getSessionManager) match {
          case Some(sm) => Pass(sm)
          case _ => Reject(NoSessionManagerAvailable)
        }

    }
  }

  def withValidSession = {
    filter1 {
      ctx =>
        securityContext.filter(ctx).flatMap(
          secCtx =>
            Option(secCtx._1.getSessionIndex) match {
              case Some(sessionIndex) =>
                sessionManager.filter(ctx).flatMap(
                {
                  sm =>
                    try {
                      sm._1.accessSession(sessionIndex)
                      Pass(sessionIndex)
                    } catch {
                      case e: NoSuchSessionException =>
                        Reject(InvalidSession)
                    }
                }
                )
              case _ =>
                Reject(InvalidSession)
            }
        )
    }
  }

  def withNoSession = {
    filter {
      ctx =>
        securityContext.filter(ctx).flatMap(
          secCtx =>
            Option(secCtx._1.getSessionIndex) match {
              case Some(_) => Reject(SessionExists)
              case _ => Pass
            }
        )
    }
  }

  def withAuthenticationState = {
    filter1 {
      ctx =>
        val in = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage]
        try {
          Option(in.getMessage.getState.getLocalVariable("urn:org:atricore:idbus:samlr2:idp:authn-state").asInstanceOf[AuthenticationState]) match {
            case Some(state) => Pass(state)
            case _ =>
              val state = new AuthenticationState
              val in = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage]
              in.getMessage.getState.setLocalVariable("urn:org:atricore:idbus:samlr2:idp:authn-state", state)
              Pass(state)
          }
        } catch {
          case e: IllegalStateException => {
            Pass(new AuthenticationState)
          }
        }
    }
  }

  def selectClaimChannel = {
    filter2 {
      ctx =>
        withAuthenticationState.filter(ctx) match {
          case Pass(values, transform) =>
            val as = values._1
            Option(as.getAuthnRequest) match {
              case Some(authnRequest) =>
                val selectedEndpoint =
                  ctx.request.channel.asInstanceOf[SPChannel].getClaimProviders.flatMap(
                    cc =>
                      cc.getEndpoints.filter(
                        ep =>
                        // consider only unused artifact and local bindings
                          (ep.getBinding == SSOBinding.SSO_ARTIFACT.getValue ||
                            ep.getBinding == SSOBinding.SSO_LOCAL.getValue) &&
                            !as.getUsedClaimsEndpoints.contains(ep.getName)
                      ).filter(
                        ep2 =>
                          Option(authnRequest.getRequestedAuthnContext) match {
                            case Some(reqAuthnCtx) =>
                              // see if this is one of the requested endpoints
                              val authnCtxEp = reqAuthnCtx.getAuthnContextClassRef.filter(_ == ep2.getType)
                              val authnCtxClass = AuthnCtxClass.asEnum(ep2.getType)

                              // only consider passive endpoints for passive authentication requests
                              if (authnRequest.isIsPassive)
                                authnCtxClass.isPassive
                              else
                                authnCtxEp.headOption.isDefined
                            case _ =>
                              // there is no preferred authentication context, any will do
                              true
                          }
                      ).map((cc, _))).headOption

                selectedEndpoint match {
                  case Some((ch, ep)) =>
                    as.getUsedClaimsEndpoints.add(ep.getName)
                    Pass(ch, ep)
                  case _ => Reject(NoMoreClaimEndpoints)
                }
              case _ =>
                Reject(NoAuthenticationRequestAvailable)
            }
          case _ => Reject(NoAuthenticationStateAvailable)
        }
    }

  }

  def redirect(channel : Channel, endpoint : IdentityMediationEndpoint) : IdentityFlowRoute = {
    ctx =>
      ctx.respond(IdentityFlowResponse( Redirect(channel, endpoint) ))

  }

}
