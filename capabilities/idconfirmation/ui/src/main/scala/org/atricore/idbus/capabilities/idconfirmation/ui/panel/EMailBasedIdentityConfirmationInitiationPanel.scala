package org.atricore.idbus.capabilities.idconfirmation.ui.panel

import org.apache.wicket.markup.html.panel.{FeedbackPanel, Panel}
import org.apache.wicket.markup.html.form.{TextField, Form}
import org.apache.wicket.markup.html.WebMarkupContainer
import org.atricore.idbus.capabilities.idconfirmation.ui.IdentityConfirmationModel
import org.apache.wicket.model.CompoundPropertyModel
import org.atricore.idbus.capabilities.idconfirmation.ui.page.IdentityConfirmationInitiationPage

import org.atricore.idbus.capabilities.idconfirmation.ui.WicketImplicits._

class EMailBasedIdentityConfirmationInitiationPanel(id : String) extends Panel(id) with IdentityConfirmationModel {

  val form : Form[EMailBasedIdentityConfirmationModel] = new Form(
    "idConfirmationForm",
    new CompoundPropertyModel[EMailBasedIdentityConfirmationModel](EMailBasedIdentityConfirmationModel())
  )

  val email = new TextField("email")
  email.setOutputMarkupId(true)
  form.add(email)

  form.withSubmitLink( { (modelObject : Any)  =>
    println("Selection is = " + modelObject)
    Right(classOf[IdentityConfirmationInitiationPage])
  }
  )

  add(form)

  // Create feedback panel and add it to page
  val feedbackBox = new WebMarkupContainer("feedbackBox")
  add(feedbackBox)
  val feedback = new FeedbackPanel("feedback")
  feedback.setOutputMarkupId(true)

  feedbackBox.add(feedback)

  private def sendEMailWithIdentityConfirmationToken {
    val submission = form.getModelObject

    Option(submission) match {
      case Some(email) => println("Submitted email is = " + email)
      case None => println("No valid submission")
    }

  }

  private def onSendEMailWithIdentityConfirmationToken {
    // Go to confirmation page
    form.setResponsePage(classOf[IdentityConfirmationInitiationPage])
    error(getLocalizer.getString("app.error", this, "Operation failed"))
  }

  private def onSendEMailWithIdentityConfirmationTokenFailed {
    error(getLocalizer.getString("app.error", this, "Operation failed"))
  }


}
