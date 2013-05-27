package org.atricore.idbus.capabilities.idconfirmation.ui.panel

import org.apache.wicket.markup.html.panel.{FeedbackPanel, Panel}
import org.apache.wicket.markup.html.form.{TextField, Form}
import org.apache.wicket.markup.html.WebMarkupContainer
import org.atricore.idbus.capabilities.idconfirmation.ui.IdentityConfirmationModel
import org.apache.wicket.model.{PropertyModel, CompoundPropertyModel}
import org.atricore.idbus.capabilities.idconfirmation.ui.page.IdentityConfirmationInitiationPage

import org.atricore.idbus.capabilities.idconfirmation.ui.WicketImplicits._
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.TokenSharedConfirmation
import java.net.URL
import org.apache.wicket.util.convert.IConverter
import org.atricore.idbus.capabilities.idconfirmation.ui.util.URLConverter

class EMailBasedIdentityConfirmationInitiationPanel(id : String, tsc : TokenSharedConfirmation) extends Panel(id) with IdentityConfirmationModel {

  val form : Form[EMailBasedIdentityConfirmationModel] = new Form(
    "idConfirmationForm",
    new CompoundPropertyModel[EMailBasedIdentityConfirmationModel](EMailBasedIdentityConfirmationModel())
  )

  val email = new TextField[URL]("email", new PropertyModel[URL](tsc, "tokenAuthenticationLocation")) {
    override def getConverter[C](t: Class[C]): IConverter[C] = if (t == classOf[URL]) URLConverter.asInstanceOf[IConverter[C]] else super.getConverter(t)
  }

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
