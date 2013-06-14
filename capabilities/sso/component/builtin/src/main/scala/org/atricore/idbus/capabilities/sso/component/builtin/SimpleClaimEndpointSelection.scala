package org.atricore.idbus.capabilities.sso.component.builtin

import directives.{MediationDirectives, ClaimDirectives}
import org.atricore.idbus.capabilities.sso.component.container.impl.BaseIdentityFlowComponent

class SimpleClaimEndpointSelection(name : String, retries : Int)
  extends BaseIdentityFlowComponent(name)
  with ClaimDirectives
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
