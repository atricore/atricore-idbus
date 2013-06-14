package org.atricore.idbus.capabilities.idconfirmation.ui

import reflect.BeanProperty

trait IdentityConfirmationModel {

  case class EMailBasedIdentityConfirmationModel(@BeanProperty var email : String = "")

}
