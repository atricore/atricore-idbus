package org.atricore.idbus.capabilities.idconfirmation.main

import endpoints.IdentityConfirmationNegotiationEndpoint
import org.apache.camel.{Exchange, Endpoint}
import org.apache.camel.impl.DefaultComponent
import java.util
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange

class IdentityConfirmationComponent extends DefaultComponent[CamelMediationExchange] {

  def createEndpoint(uri: String, remaining: String, parameters: util.Map[_, _]): Endpoint[CamelMediationExchange] =
    new IdentityConfirmationNegotiationEndpoint(uri, this, parameters)

}

