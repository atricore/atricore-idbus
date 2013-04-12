package org.atricore.idbus.capabilities.idconfirmation.main

import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory
import org.atricore.idbus.kernel.main.mediation.{MediationBinding, Channel}
import org.atricore.idbus.capabilities.idconfirmation.main.IdentityConfirmationBinding._

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class IdentityConfirmationBindingFactory extends SamlR2BindingFactory {
  override def createBinding(binding: String, channel: Channel): MediationBinding = {

    IdentityConfirmationBinding.withName(binding) match {
      case ID_CONFIRMATION_HTTP_INITIATION =>
        new IdentityConfirmationNegotiatorBinding(binding, channel)
      case ID_CONFIRMATION_HTTP_NEGOTIATION =>
        new IdentityConfirmationNegotiatorBinding(binding, channel)
      case _ =>
        super.createBinding(binding, channel)

    }



  }
}
