package org.atricore.idbus.capabilities.idconfirmation.main.producers

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationExchange}
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer
import org.apache.camel.Endpoint

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[main] class IdentityConfirmationNegotiationProducer(endpoint : Endpoint[CamelMediationExchange])
  extends AbstractCamelProducer[CamelMediationExchange](endpoint) {

  protected def doProcess(exchange: CamelMediationExchange) {
    val out = exchange.getOut.asInstanceOf[CamelMediationMessage]
    val in = exchange.getIn.asInstanceOf[CamelMediationMessage]
    val content = in.getMessage.getContent
    val state = in.getMessage.getState

    content match {
      case _ =>
    }

  }
}
