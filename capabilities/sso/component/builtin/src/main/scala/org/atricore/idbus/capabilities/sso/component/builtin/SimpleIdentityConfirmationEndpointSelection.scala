package org.atricore.idbus.capabilities.sso.component.builtin

import directives.{UserDirectives, MediationDirectives, IdentityConfirmationDirectives}
import org.atricore.idbus.capabilities.sso.component.container.impl.BaseIdentityFlowComponent
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget

class SimpleIdentityConfirmationEndpointSelection(name : String, retries: Int)
  extends BaseIdentityFlowComponent(name)
  with IdentityConfirmationDirectives
  with UserDirectives
  with MediationDirectives {

  val route =
    logRequestResponse("") {
      withPrincipal {
        (principal) =>
          remoteAddress {
            (from) =>
              provisioningTarget {
                (pt) =>
                  notWhitelistedSource(from, principal, pt) {
                    pendingIdentityConfirmationRetries(1) {
                      retryToConfirmIdentity
                    } ~
                    pickIdentityConfirmationChannel {
                      confirmIdentity(_, _)
                    }
                  } ~
                  noIdentityConfirmationRequired
              }
          }
      }
    }
}
