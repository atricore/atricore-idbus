package org.atricore.idbus.capabilities.idconfirmation.main

import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator
import org.apache.commons.logging.LogFactory
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel
import org.apache.camel.builder.RouteBuilder
import org.atricore.idbus.kernel.main.mediation.{Channel, IdentityMediationException}
import org.atricore.idbus.kernel.main.federation.metadata.{EndpointDescriptorImpl, EndpointDescriptor}
import scala.collection.JavaConversions._
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import IdentityConfirmationBinding._
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class IdentityConfirmationMediator extends AbstractCamelMediator {
  private final val logger = LogFactory.getLog(classOf[IdentityConfirmationMediator])

  protected override def createClaimRoutes(claimChannel: ClaimChannel): RouteBuilder = {
    logger.info("Creating Identity Confirmation Routes")

    new RouteBuilder {
      def configure {
        Option(claimChannel.getEndpoints) match {
          case Some(endpoints) =>
            endpoints.foreach({
              endpoint =>
                val binding = IdentityConfirmationBinding.withName(endpoint.getBinding)
                val ed: EndpointDescriptor = resolveEndpoint(claimChannel, endpoint)
                binding match {
                  case SSO_ARTIFACT =>
                    // FROM idbus-http TO idbus-bind
                    from("idbus-http:" + ed.getLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName)
                    // FROM idbus-bind TO idconf (claim processing)
                    from("idbus-bind:camel://direct:" + ed.getName + "?binding=" + ed.getBinding + "&channelRef=" + claimChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + claimChannel.getName + "&endpointRef=" + endpoint.getName)
                    if (ed.getResponseLocation != null) {
                      from("idbus-http:" + ed.getResponseLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName + "-response")
                      from("idbus-bind:camel://direct:" + ed.getName + "-response" + "?binding=" + ed.getBinding + "&channelRef=" + claimChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + claimChannel.getName + "&endpointRef=" + endpoint.getName + "&response=true")
                    }
                  case ID_CONFIRMATION_HTTP_INITIATION =>
                    // FROM idbus-http TO idbus-bind
                    from("idbus-http:" + ed.getLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName)
                    // FROM idbus-bind TO idconf (claim processing)
                    from("idbus-bind:camel://direct:" + ed.getName + "?binding=" + ed.getBinding + "&channelRef=" + claimChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + claimChannel.getName + "&endpointRef=" + endpoint.getName)
                  case ID_CONFIRMATION_HTTP_NEGOTIATION =>
                    // FROM idbus-http TO idbus-bind
                    from("idbus-http:" + ed.getLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName)
                    // FROM idbus-bind TO idconf (claim processing)
                    from("idbus-bind:camel://direct:" + ed.getName + "?binding=" + ed.getBinding + "&channelRef=" + claimChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + claimChannel.getName + "&endpointRef=" + endpoint.getName)
                    if (ed.getResponseLocation != null) {
                      from("idbus-http:" + ed.getResponseLocation).process(new ScalaLoggerProcessor(getLogger)).to("direct:" + ed.getName + "-response")
                      from("idbus-bind:camel://direct:" + ed.getName + "-response" + "?binding=" + ed.getBinding + "&channelRef=" + claimChannel.getName).process(new ScalaLoggerProcessor(getLogger)).to("idconf:" + ed.getType + "?channelRef=" + claimChannel.getName + "&endpointRef=" + endpoint.getName + "&response=true")
                    }
                  case _ =>
                    throw new IdentityConfirmationException("Unsupported Identity Confirmation Binding " + binding)
                }
            })
          case None =>
            throw new IdentityMediationException("No endpoints defined for claims channel : " + claimChannel.getName)

        }
      }
    }
  }

  def resolveEndpoint(channel: Channel, endpoint: IdentityMediationEndpoint): EndpointDescriptor = {

    (channel match {
      case cc : ClaimChannel =>
        for (ch <- Option(channel); ep <- Option(endpoint)) yield {

          // ---------------------------------------------
          // Resolve Binding
          // ---------------------------------------------
          val binding = IdentityConfirmationBinding.values.find(_.toString == ep.getBinding).getOrElse(
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





