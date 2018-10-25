package org.atricore.idbus.capabilities.sso.component.container

import org.apache.camel.Exchange
import org.atricore.idbus.kernel.main.mediation.provider.Provider
import org.atricore.idbus.kernel.main.mediation.Channel
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet

trait IdentityFlowContainer {

  def components : java.util.List[IdentityFlowComponent]

  @throws (classOf[RouteRejectionException])
  def dispatch( componentId : String, exchange : Exchange, provider : Provider, channel : Channel,
                endpoint : IdentityMediationEndpoint, claims : ClaimSet) : IdentityFlowResponse

}
