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

package org.atricore.idbus.capabilities.sso.component.builtin.directives

import org.atricore.idbus.capabilities.sso.dsl.core._
import directives.BasicIdentityFlowDirectives
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import scala.Option
import scala.collection.JavaConversions._
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.Channel
import org.atricore.idbus.capabilities.sso.dsl.{RedirectToEndpoint, IdentityFlowResponse}
import org.atricore.idbus.capabilities.sso.dsl.util.Logging

/**
 * Identity Flow directives for handling
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait ClaimDirectives extends Logging {
  this: BasicIdentityFlowDirectives with MediationDirectives =>

  def pendingRetries(maxRetries : Int = 50) = {
    filter {
      ctx =>
        withAuthenticationState.filter(ctx) match {
          case Pass(values, transform) =>
            val as = values._1
            Option(as.getCurrentClaimsEndpoint) match {
              case Some(currentClaimsEndpoint) =>
                val authnCtxClass = AuthnCtxClass.asEnum(currentClaimsEndpoint.getType)
                if (!authnCtxClass.isPassive && as.getCurrentClaimsEndpointTryCount <= maxRetries) {
                  Pass
                } else {
                  as.getUsedClaimsEndpoints.add(currentClaimsEndpoint.getName)
                  as.setCurrentClaimsEndpoint(null)
                  as.setCurrentClaimsEndpointTryCount(0)
                  Reject()
                }
              case None =>
                Reject()
            }
        }
    }
  }

  def retryToCollectClaimsOnSameClaimChannel : IdentityFlowRoute = {
    ctx =>
      withAuthenticationState.filter(ctx) match {
        case Pass(values, transform) =>
          val as = values._1
          val spChannel = ctx.request.channel.asInstanceOf[SPChannel]
          val claimChannels = spChannel.getClaimProviders

          claimChannels.foreach { claimChannel =>
            claimChannel.getEndpoints.foreach { claimEndpoint =>
              if (claimEndpoint.getName == as.getCurrentClaimsEndpoint.getName) {
                log.debug(
                  "Retrying claim endpoint " + claimEndpoint.getName + ". Already tried " +
                    as.getCurrentClaimsEndpointTryCount + " times")
                as.setCurrentClaimsEndpointTryCount(as.getCurrentClaimsEndpointTryCount + 1)
                ctx.respond(IdentityFlowResponse(RedirectToEndpoint(claimChannel, claimEndpoint)))
              }
            }
          }
          Reject()
        case Reject(_) => Reject(NoAuthenticationStateAvailable)
      }
  }

  def pickClaimChannel = {
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
                      ).filter {
                        ep2 =>
                            val authnCtxClass = AuthnCtxClass.asEnum(ep2.getType)

                            // only consider passive endpoints for passive authentication requests
                            Option(authnRequest.isIsPassive) match {
                              case Some(isPassive) if (isPassive && !authnCtxClass.isPassive) =>
                                false
                              case _ =>
                                true
                            }

                      }.map((cc, _))).headOption

                selectedEndpoint match {
                  case Some((ch, ep)) =>
                    as.getUsedClaimsEndpoints.add(ep.getName)
                    log.debug("Picked claim channel " + ch.getName + ", endpoint " + ep.getName)
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

  def collectClaims(channel: Channel, endpoint: IdentityMediationEndpoint): IdentityFlowRoute = {
    ctx =>
      withAuthenticationState.filter(ctx) match {
        case Pass(values, transform) =>
          val as = values._1
          as.setCurrentClaimsEndpoint(endpoint)
          as.setCurrentClaimsEndpointTryCount(0);
          ctx.respond(IdentityFlowResponse(RedirectToEndpoint(channel, endpoint)))
        case Reject(_) => Reject(NoAuthenticationStateAvailable)
      }
  }

}
