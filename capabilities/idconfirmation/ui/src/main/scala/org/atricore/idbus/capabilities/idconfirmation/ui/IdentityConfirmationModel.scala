package org.atricore.idbus.capabilities.idconfirmation.ui

import scala.beans.BeanProperty

trait IdentityConfirmationModel {

  case class EMailBasedIdentityConfirmationModel(@BeanProperty var email : String = "")

}
