package org.atricore.idbus.capabilities.idconfirmation.main.endpoints

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint
import org.apache.camel.{Producer, Exchange, Component}
import java.util
import org.atricore.idbus.capabilities.idconfirmation.main.producers.IdentityConfirmationNegotiationProducer

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[main] class IdentityConfirmationNegotiationEndpoint(uri: String, component: Component[_ <: Exchange], parameters: util.Map[_, _])
  extends AbstractCamelEndpoint[CamelMediationExchange](uri, component, parameters) {

  override def createProducer: Producer[CamelMediationExchange] = new IdentityConfirmationNegotiationProducer(this)
}