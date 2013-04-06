package org.atricore.idbus.capabilities.idconfirmation.main.producers

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationExchange}
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer
import org.apache.camel.{Exchange, Endpoint}
import org.atricore.idbus.kernel.main.mediation.MediationState
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsRequest

class IdentityConfirmationNegotiationProducer(endpoint : Endpoint[CamelMediationExchange])
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
