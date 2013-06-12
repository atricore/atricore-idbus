package org.atricore.idbus.capabilities.idconfirmation.ui.panel

import org.apache.wicket.markup.html.panel.{FeedbackPanel, Panel}
import org.apache.wicket.markup.html.form.{TextField, Form}
import org.apache.wicket.markup.html.WebMarkupContainer
import org.atricore.idbus.capabilities.idconfirmation.ui.IdentityConfirmationModel
import org.apache.wicket.model._
import org.atricore.idbus.capabilities.idconfirmation.ui.page.IdentityConfirmationInitiationPage

import org.atricore.idbus.capabilities.idconfirmation.ui.WicketImplicits._
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.TokenSharedConfirmation
import java.net.URL
import org.apache.wicket.util.convert.IConverter
import org.atricore.idbus.capabilities.idconfirmation.ui.util.URLConverter
import org.apache.wicket.markup.html.basic.Label
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.TokenSharedConfirmation

class EMailBasedIdentityConfirmationInitiationPanel(id : String, tsc : TokenSharedConfirmation) extends Panel(id) with IdentityConfirmationModel {
  add(new Label("confirmationMessageLabel", new StringResourceModel("label.confirmationMessage", this, new Model(tsc))))
}
