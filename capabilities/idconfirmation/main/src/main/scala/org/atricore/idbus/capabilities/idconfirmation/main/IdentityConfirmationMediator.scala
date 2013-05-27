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

package org.atricore.idbus.capabilities.idconfirmation.main

import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator
import org.apache.commons.logging.LogFactory
import org.apache.camel.builder.RouteBuilder
import org.atricore.idbus.kernel.main.mediation.{Channel, IdentityMediationException}
import org.atricore.idbus.kernel.main.federation.metadata.{EndpointDescriptorImpl, EndpointDescriptor}
import scala.collection.JavaConversions._
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.IdentityConfirmationBindings
import IdentityConfirmationBindings._
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannel
import reflect.BeanProperty

/**
 * Exposes identity confirmation services.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class IdentityConfirmationMediator extends AbstractCamelMediator {
  private final val logger = LogFactory.getLog(classOf[IdentityConfirmationMediator])

  @BeanProperty
  var tokenSharingConfirmationUILocation : String = ""

  protected override def createIdentityConfirmationRoutes(identityConfirmationChannel: IdentityConfirmationChannel): RouteBuilder = {
    logger.info("Creating Identity Confirmation Routes")

    new RouteBuilder {
      def configure {
        Option(identityConfirmationChannel.getEndpoints) match {
          case Some(endpoints) =>
            endpoints.foreach {
              endpoint =>
                val binding = IdentityConfirmationBindings.withName(endpoint.getBinding)
                val ed: EndpointDescriptor = resolveEndpoint(identityConfirmationChannel, endpoint)
                binding match {
                  case SSO_ARTIFACT =>
                    // FROM idbus-http TO idbus-bind
                    from("idbus-http:" + ed.getLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName)
                    // FROM idbus-bind TO idconf initiation
                    from("idbus-bind:camel://direct:" + ed.getName + "?binding=" + ed.getBinding + "&channelRef=" + identityConfirmationChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + identityConfirmationChannel.getName + "&endpointRef=" + endpoint.getName)
                    if (ed.getResponseLocation != null) {
                      from("idbus-http:" + ed.getResponseLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName + "-response")
                      from("idbus-bind:camel://direct:" + ed.getName + "-response" + "?binding=" + ed.getBinding + "&channelRef=" + identityConfirmationChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + identityConfirmationChannel.getName + "&endpointRef=" + endpoint.getName + "&response=true")
                    }
                  case ID_CONFIRMATION_HTTP_AUTHENTICATION =>
                    // FROM idbus-http TO idbus-bind
                    from("idbus-http:" + ed.getLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName)
                    // FROM idbus-bind TO idconf authentication
                    from("idbus-bind:camel://direct:" + ed.getName + "?binding=" + ed.getBinding + "&channelRef=" + identityConfirmationChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + identityConfirmationChannel.getName + "&endpointRef=" + endpoint.getName)
                  case _ =>
                    throw new IdentityConfirmationException("Unsupported Identity Confirmation Binding " + binding)
                }
            }
          case None =>
            throw new IdentityMediationException("No endpoints defined for identity confirmation channel : " + identityConfirmationChannel.getName)

        }
      }
    }
  }

  def resolveEndpoint(channel: Channel, endpoint: IdentityMediationEndpoint): EndpointDescriptor = {

    (channel match {
      case cc : IdentityConfirmationChannel =>
        for (ch <- Option(channel); ep <- Option(endpoint)) yield {

          // ---------------------------------------------
          // Resolve Binding
          // ---------------------------------------------
          val binding = IdentityConfirmationBindings.values.find(_.toString == ep.getBinding).getOrElse(
            logger.warn("No Identity Confirmation Binding found in endpoint " + endpoint.getName)
          ).toString

          // ---------------------------------------------
          // Resolve Endpoint location
          // ---------------------------------------------
          val location = if (ep.getLocation.startsWith("/")) ch.getLocation + ep.getLocation else ep.getLocation

          // ---------------------------------------------
          // Resolve Endpoint response location
          // ---------------------------------------------
          val responseLocation = {
            Option(ep.getResponseLocation) match {
              case Some(rl) if (rl.startsWith(("/"))) => ch.getLocation + rl
              case Some(rl) => rl
              case _ => null
            }
          }

          // ---------------------------------------------
          // Resolve Endpoint type
          // ---------------------------------------------
          // If no ':' is present, lastIndexOf should return -1 and the entire type is used.
          val endpointType = {
            Option(ep.getType) match {
              case Some(ept) => ep.getType.substring(ep.getType.lastIndexOf("}") + 1)
              case _ => null
            }
          }

          new EndpointDescriptorImpl(ep.getName, endpointType, binding, location, responseLocation)
        }
      case _ =>
          None
    }).getOrElse(
      throw new IdentityMediationException("Unsupported channel type " + channel.getName + " " +channel.getClass.getName)
    )

  }

  class ScalaLoggerProcessor(mediationLogger: MediationLogger) extends LoggerProcessor(mediationLogger)

}





