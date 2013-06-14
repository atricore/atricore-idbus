/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.capabilities.oauth2.component.builtin.test

import org.specs2.mutable._
import org.atricore.idbus.capabilities.sso.dsl.core.directives.IdentityFlowDirectives
import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange
import org.atricore.idbus.capabilities.sso.dsl.core.{NoMoreClaimEndpoints, IdentityFlowRequestContext}
import org.atricore.idbus.capabilities.sso.dsl.{IdentityFlowRequest}
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.capabilities.sso.test.dsl.{MockCamelMediationMessage, MockCamelMediationExchange, IdentityFlowDSLTestSupport}
import org.atricore.idbus.kernel.main.mediation.confirmation.{IdentityConfirmationRequest, IdentityConfirmationRequestImpl}
import org.atricore.idbus.kernel.main.mediation.{MediationStateImpl, MediationMessageImpl}
import org.atricore.idbus.kernel.main.mediation.claim.{UserClaimImpl, UserClaim}
import org.atricore.idbus.kernel.main.mediation.state.LocalStateImpl
import org.atricore.idbus.capabilities.sso.component.builtin.directives.MediationDirectives

/**
 * OAuth2 route tester.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class BuiltOAuth2Spec
  extends Specification
  with IdentityFlowDSLTestSupport
  with IdentityFlowDirectives
  with MediationDirectives
  with Logging {

}
