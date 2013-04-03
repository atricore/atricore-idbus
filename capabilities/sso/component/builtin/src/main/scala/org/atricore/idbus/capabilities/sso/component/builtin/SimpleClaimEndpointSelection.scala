package org.atricore.idbus.capabilities.sso.component.builtin

import org.atricore.idbus.capabilities.sso.component.container.impl.BaseIdentityFlowComponent

class SimpleClaimEndpointSelection(name : String, retries : Int)
  extends BaseIdentityFlowComponent(name)
  with MediationDirectives {

  val route =
    logRequestResponse("") {
      withNoSession {
        pendingRetries(retries) {
          retryToCollectClaimsOnSameClaimChannel
        } ~
        pickClaimChannel {
          collectClaims(_, _)
        }
      }
    }
}
