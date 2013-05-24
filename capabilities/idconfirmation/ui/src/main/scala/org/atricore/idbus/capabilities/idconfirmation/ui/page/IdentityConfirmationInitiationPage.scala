package org.atricore.idbus.capabilities.idconfirmation.ui.page

import org.atricore.idbus.capabilities.sso.ui.page.BasePage
import org.atricore.idbus.capabilities.idconfirmation.ui.IdentityConfirmationModel
import org.apache.wicket.markup.html.form.{SubmitLink, PasswordTextField}
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.atricore.idbus.capabilities.idconfirmation.ui.panel.EMailBasedIdentityConfirmationInitiationPanel
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.atricore.idbus.kernel.main.mediation.claim.CredentialClaimsRequest
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl
import org.atricore.idbus.capabilities.sso.ui.internal.{BaseWebApplication, SSOWebSession}
import org.atricore.idbus.capabilities.sso.ui.WebBranding
import org.apache.wicket.RestartResponseAtInterceptPageException
import org.atricore.idbus.capabilities.sso.ui.page.error.SessionExpiredPage
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.TokenSharedConfirmation
import org.apache.commons.logging.LogFactory

class IdentityConfirmationInitiationPage extends BasePage with IdentityConfirmationModel {
  private final val logger = LogFactory.getLog(classOf[IdentityConfirmationInitiationPage])

  private var artifactId : Option[String] = _

  def this(parameters: PageParameters) {
    this()
    if (parameters != null) artifactId = Option(parameters.get(SsoHttpArtifactBinding.SSO_ARTIFACT_ID).toString)
  }

  protected override def onInitialize() {
    super.onInitialize()

    artifactId match {
      case Some(aid) =>
        try {
          val tsc = artifactQueueManager.pullMessage(new ArtifactImpl(aid)).asInstanceOf[TokenSharedConfirmation]

           Option(tsc) match {
             case Some(msg) =>
               if (logger.isDebugEnabled)
                 logger.info("Received token shared confirmation : " + msg);

                 add(new EMailBasedIdentityConfirmationInitiationPanel("identityConfirmationInitiation", tsc))

             case None =>
                  logger.error("Empty payload in token shared confirmation message with artifact id " + aid)
           }

        } catch {
          case e : Exception => {
            logger.error("Cannot resolve artifact id [" + aid + "] : " + e.getMessage, e)
          }
        }

      case None =>
        logger.error("Cannot continue due to that artifact identifier was not received")

    }


  }
}
