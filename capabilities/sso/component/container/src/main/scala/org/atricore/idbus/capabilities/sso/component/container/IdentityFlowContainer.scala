package org.atricore.idbus.capabilities.sso.component.container

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange
import org.atricore.idbus.kernel.main.mediation.provider.Provider
import org.atricore.idbus.kernel.main.mediation.Channel
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse

trait IdentityFlowContainer {

  def components : List[IdentityFlowComponent]

  def dispatch( componentId : String, exchange : CamelMediationExchange, provider : Provider, channel : Channel) : IdentityFlowResponse

}
