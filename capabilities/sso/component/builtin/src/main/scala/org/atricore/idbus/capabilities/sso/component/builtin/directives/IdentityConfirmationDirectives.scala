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
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel
import scala.Option
import scala.collection.JavaConversions._
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.Channel
import org.atricore.idbus.capabilities.sso.dsl.{NoFurtherActionRequired, RedirectToEndpoint, IdentityFlowResponse}
import org.atricore.idbus.capabilities.sso.dsl.util.Logging

/**
 * Identity Confirmation directives
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait IdentityConfirmationDirectives extends Logging {
  this: BasicIdentityFlowDirectives with MediationDirectives =>

  def pendingIdentityConfirmationRetries(maxRetries: Int = 50) = {
    filter {
      ctx =>
        withAuthenticationState.filter(ctx) match {
          case Pass(values, transform) =>
            val as = values._1
            Option(as.getCurrentIdConfirmationEndpoint) match {
              case Some(currentIdConfirmationEndpoint) =>
                if (as.getCurrentIdConfirmationEndpointTryCount < maxRetries) {
                  Pass
                } else {
                  as.getUsedIdConfirmationEndpoints.add(currentIdConfirmationEndpoint.getName)
                  as.setCurrentIdConfirmationEndpoint(null)
                  as.setCurrentIdConfirmationEndpointTryCount(0)
                  Reject()
                }
              case None =>
                Reject()
            }
        }
    }
  }

  def retryToConfirmIdentity: IdentityFlowRoute = {
    ctx =>
      withAuthenticationState.filter(ctx) match {
        case Pass(values, transform) =>
          val as = values._1
          val spChannel = ctx.request.channel.asInstanceOf[SPChannel]
          val idConfChannels = spChannel.getIdentityConfirmationProviders

          idConfChannels.foreach {
            idConfChannel =>
              idConfChannel.getEndpoints.foreach {
                idConfEndpoint =>
                  if (idConfEndpoint.getName == as.getCurrentIdConfirmationEndpoint.getName) {
                    log.debug(
                      "Retrying identity confirmation endpoint " + idConfEndpoint.getName + ". Already tried " +
                        as.getCurrentIdConfirmationEndpointTryCount + " times")
                    as.setCurrentIdConfirmationEndpointTryCount(as.getCurrentIdConfirmationEndpointTryCount + 1)
                    ctx.respond(IdentityFlowResponse(RedirectToEndpoint(idConfChannel, idConfEndpoint)))
                  }
              }
          }
          Reject()
        case Reject(_) => Reject(NoAuthenticationStateAvailable)
      }
  }

  def pickIdentityConfirmationChannel = {
    filter2 {
      ctx =>
        withAuthenticationState.filter(ctx) match {
          case Pass(values, transform) =>
            val as = values._1
            val spChannel = ctx.request.channel.asInstanceOf[SPChannel]
            val idConfChannels = spChannel.getIdentityConfirmationProviders

            val selectedIdConfEp =
              for {
                   idConfChannel <- idConfChannels
                   idConfEndpoint <- idConfChannel.getEndpoints
                   if !as.getUsedIdConfirmationEndpoints.contains(idConfEndpoint.getName)
              } yield (idConfChannel, idConfEndpoint)

            selectedIdConfEp.headOption match {
              case Some((ch, ep)) =>
                as.getUsedIdConfirmationEndpoints.add(ep.getName)
                log.debug("Picked identity confirmation channel " + ch.getName + ", endpoint " + ep.getName)
                Pass(ch, ep)
              case _ => Reject(NoMoreIdentityConfirmationEndpoints)

            }

        }
    }

  }

  def confirmIdentity(channel: Channel, endpoint: IdentityMediationEndpoint): IdentityFlowRoute = {
    ctx =>
      withAuthenticationState.filter(ctx) match {
        case Pass(values, transform) =>
          val as = values._1
          as.setCurrentIdConfirmationEndpoint(endpoint)
          as.setCurrentIdConfirmationEndpointTryCount(0)
          log.debug("Confirming identity using channel " + channel + ", endpoint " +  endpoint)
          ctx.respond(IdentityFlowResponse(RedirectToEndpoint(channel, endpoint)))
        case Reject(_) => Reject(NoAuthenticationStateAvailable)
      }
  }

  def noIdentityConfirmationRequired: IdentityFlowRoute = {
    ctx =>
      log.debug("Identity has been confirmed. Skipping identity confirmation.")
      ctx.respond(IdentityFlowResponse(NoFurtherActionRequired("Identity Confirmation is not required")))
  }


}
