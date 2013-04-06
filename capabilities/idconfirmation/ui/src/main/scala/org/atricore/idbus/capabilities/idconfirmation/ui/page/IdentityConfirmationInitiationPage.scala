package org.atricore.idbus.capabilities.idconfirmation.ui.page

import org.atricore.idbus.capabilities.sso.ui.page.BasePage
import org.atricore.idbus.capabilities.idconfirmation.ui.IdentityConfirmationModel
import org.apache.wicket.markup.html.form.{SubmitLink, PasswordTextField}
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.atricore.idbus.capabilities.idconfirmation.ui.panel.EMailBasedIdentityConfirmationInitiationPanel

class IdentityConfirmationInitiationPage extends BasePage with IdentityConfirmationModel {

  add(new EMailBasedIdentityConfirmationInitiationPanel("identityConfirmationInitiation"))

}
