package org.atricore.idbus.capabilities.idconfirmation.ui.internal

import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication
import org.apache.wicket.markup.html.pages.{PageExpiredErrorPage, AccessDeniedPage}
import org.apache.wicket.Session
import org.apache.wicket.request.{Response, Request}
import org.atricore.idbus.capabilities.idconfirmation.ui.page.IdentityConfirmationInitiationPage

/**
 * Entry point for the Wicket-based Identity Confirmation front-end.
 */
class IdentityConfirmationApplication extends BaseWebApplication {

  protected override def buildPageMounts {
    addPageMount("/INITIATE", classOf[IdentityConfirmationInitiationPage])
    addPageMount("/ERROR/401", classOf[AccessDeniedPage])
    addPageMount("/ERROR/404", classOf[PageExpiredErrorPage])

  }

  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  override def getHomePage: Class[IdentityConfirmationInitiationPage] = classOf[IdentityConfirmationInitiationPage]

  override def newSession(request: Request, response: Response): Session = new IdentityConfirmationWebSession(request)

}

