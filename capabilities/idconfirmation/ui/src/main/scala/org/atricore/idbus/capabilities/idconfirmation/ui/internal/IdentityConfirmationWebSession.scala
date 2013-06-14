package org.atricore.idbus.capabilities.idconfirmation.ui.internal

import org.apache.wicket.protocol.http.WebSession
import org.apache.wicket.request.Request
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession

class IdentityConfirmationWebSession(request : Request) extends SSOWebSession(request)
