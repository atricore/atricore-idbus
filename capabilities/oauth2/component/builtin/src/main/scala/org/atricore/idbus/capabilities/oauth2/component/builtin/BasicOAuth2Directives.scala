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

package org.atricore.idbus.capabilities.oauth2.component.builtin

import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.capabilities.sso.dsl.core.directives.BasicIdentityFlowDirectives
import org.atricore.idbus.capabilities.sso.dsl.core.Pass
import org.atricore.idbus.capabilities.oauth2.client.AccessTokenRequestor
import java.net.{URL, URLEncoder}

/**
 * OAuth2 directives of the identity combinator library.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait BasicOAuth2Directives extends Logging {
  this: BasicIdentityFlowDirectives =>

  def requestOAuth2AccessToken(clientId : String, clientSecret : String, endpoint : String, username : String, password : String) =
    filter1 {
      ctx =>
        val requestor = new AccessTokenRequestor(clientId, clientSecret, endpoint)
        Pass(requestor.requestTokenForUsernamePassword(username, password))
    }

  def preauthUrl(idpInitiatedEndpoint : String, spAlias : String, accessToken : String) =
    filter1 {
      ctx =>
        Pass(
          new URL("%s?atricore_sp_alias=%s&atricore_security_token=%s".
            format(idpInitiatedEndpoint, spAlias, URLEncoder.encode(accessToken, "UTF-8")))
        )
    }

}
