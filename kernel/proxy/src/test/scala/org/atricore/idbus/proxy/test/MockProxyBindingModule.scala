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

package org.atricore.idbus.proxy.test

import org.atricore.idbus.proxy._
import org.atricore.idbus.proxy.spi.SecurityContextEstablisher
import org.atricore.idbus.proxy.configuration.{ProxyBindingModule, ProxyConfiguration}
import org.josso.gateway.ws._1_2.protocol._
import org.josso.gateway.ws._1_2.wsdl.{SSOIdentityProviderWS, SSOIdentityProvider, SSOIdentityManager, SSOIdentityManagerWS}

/**
 * Mock stubs useful for testing.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class MockProxyBindingModule(config: ProxyConfiguration)
  extends ProxyBindingModule(config, {
    implicit module =>
      import module._

      bind[SecurityContextEstablisher] toProvider {
        MockSecurityContextEstablisher
      }

      bind[SSOIdentityProviderWS] toProvider {
        MockSSOIdentityProviderWS
      }

      bind[SSOIdentityManagerWS] toProvider {
        MockSSOIdentityManagerWS
      }
  })

object MockSecurityContextEstablisher extends SecurityContextEstablisher {
  final val mockHttpRequestEnvName = "org.atricore.idbus.proxy.environment.names.request"

  def apply(subject: SSOUserType, env: Environment) {
    val mockHttpRequest = env.getOrElse(
      mockHttpRequestEnvName,
      throw new IllegalArgumentException ("Cannot establish security context due to no http request is available")).
      asInstanceOf[MutableMockRequest]

    mockHttpRequest.outUserPrincipal = Some(subject.getName)
  }

}

case class MutableMockRequest(inUserPrincipal : Option[String] = None) {
  var outUserPrincipal : Option[String] = None
}

object MockSSOIdentityManagerSOAP extends SSOIdentityManager {
  def findUserInSession(p1: FindUserInSessionRequestType) = {
    val ssoUser = new SSOUserType
    val fuis = new FindUserInSessionResponseType

    ssoUser.setName("user1")
    ssoUser.setSecuritydomain("default")
    fuis.setSSOUser(ssoUser)
    fuis
  }

  def findUserInSecurityDomain(p1: FindUserInSecurityDomainRequestType) = throw new UnsupportedOperationException

  def userExists(p1: UserExistsRequestType) = throw new UnsupportedOperationException

  def findRolesBySSOSessionId(p1: FindRolesBySSOSessionIdRequestType) = throw new UnsupportedOperationException
}

object MockSSOIdentityManagerWS extends SSOIdentityManagerWS {
  override def getSSOIdentityManagerSoap = MockSSOIdentityManagerSOAP
}

object MockSSOIdentityProviderSOAP extends SSOIdentityProvider {
  def resolveAuthenticationAssertion(p1: ResolveAuthenticationAssertionRequestType) = {
    val rsp = new ResolveAuthenticationAssertionResponseType()
    rsp.setSecurityDomain("default")
    rsp.setSsoSessionId("6AF80DCA9B78E3DC8175BEB5756AFA77")
    rsp
  }

  def globalSignoff(p1: GlobalSignoffRequestType) = throw new UnsupportedOperationException

  def assertIdentityWithSimpleAuthentication(p1: AssertIdentityWithSimpleAuthenticationRequestType) = throw new UnsupportedOperationException
}

object MockSSOIdentityProviderWS extends SSOIdentityProviderWS {
  override def getSSOIdentityProviderSoap = MockSSOIdentityProviderSOAP
}