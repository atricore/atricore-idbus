package org.atricore.idbus.capabilities.idconfirmation.main

import endpoints.IdentityConfirmationNegotiationEndpoint
import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultComponent
import java.util
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class IdentityConfirmationComponent extends DefaultComponent[CamelMediationExchange] {

  def createEndpoint(uri: String, remaining: String, parameters: util.Map[_, _]): Endpoint[CamelMediationExchange] =
    new IdentityConfirmationNegotiationEndpoint(uri, this, parameters)

}

