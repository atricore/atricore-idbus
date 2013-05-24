package org.atricore.idbus.capabilities.sso.dsl.core

import org.atricore.idbus.capabilities.sso.dsl.{RedirectToLocation, RedirectToEndpoint, IdentityFlowResponse}
import org.atricore.idbus.kernel.main.federation.metadata.{EndpointDescriptorImpl, EndpointDescriptor}
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationExchange}
import org.atricore.idbus.kernel.main.util.UUIDGenerator
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding

trait IdentityBusConnector {
  this: org.apache.camel.Producer[_] =>

  def respond(ex: CamelMediationExchange, response: IdentityFlowResponse) {
    val in = ex.getIn.asInstanceOf[CamelMediationMessage]
    val out = ex.getOut.asInstanceOf[CamelMediationMessage]
    val ids = new UUIDGenerator

    response.statusCode match {
      case RedirectToEndpoint(ch, ep) =>
        val redirectTo = {
            val targetLocation = if (ep.getLocation.startsWith("/")) ch.getLocation + ep.getLocation else ep.getLocation

            new EndpointDescriptorImpl(
              ep.getName,
              ep.getType,
              ep.getBinding,
              targetLocation,
              ep.getResponseLocation
            )
          }

        out.setMessage(
          new MediationMessageImpl(
            ids.generateId(), response.content.getOrElse(null), response.contentType.getOrElse(null), null, redirectTo, in.getMessage.getState
          )
        )
      case RedirectToLocation(location) =>
        val redirectTo =
          new EndpointDescriptorImpl(
            "WebRedirect",
            "WebRedirect",
            SSOBinding.SSO_ARTIFACT.getValue,
            location,
            null
          )

        out.setMessage(
          new MediationMessageImpl(
            ids.generateId(), response.content.getOrElse(null), response.contentType.getOrElse(null), null, redirectTo, in.getMessage.getState
          )
        )

    }
  }

}
