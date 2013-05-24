package org.atricore.idbus.capabilities.sso.component.builtin

import directives.{MediationDirectives, IdentityConfirmationDirectives}
import org.atricore.idbus.capabilities.sso.component.container.impl.BaseIdentityFlowComponent

class SimpleIdentityConfirmationEndpointSelection(name: String, retries: Int)
  extends BaseIdentityFlowComponent(name)
  with IdentityConfirmationDirectives
  with MediationDirectives {

  val route =
    logRequestResponse("") {
      pendingIdentityConfirmationRetries(1) {
        retryToConfirmIdentity
      } ~
      pickIdentityConfirmationChannel {
          confirmIdentity(_, _)
      }
    }


}
