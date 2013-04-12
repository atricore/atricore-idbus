package org.atricore.idbus.capabilities.idconfirmation.main

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait IdentityConfirmationMessage extends Serializable

case class IdentityConfirmationNegotiationRequest extends IdentityConfirmationMessage

case class IdentityConfirmationNegotiationInitiation extends IdentityConfirmationMessage