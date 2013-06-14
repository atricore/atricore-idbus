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
package org.atricore.idbus.capabilities.sso.test.dsl

import org.atricore.idbus.kernel.main.mediation.{MediationMessageImpl, MediationStateImpl}
import org.atricore.idbus.kernel.main.mediation.state.LocalStateImpl
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType
import org.atricore.idbus.kernel.main.mediation.provider.{IdentityConfirmationProviderImpl, ServiceProviderImpl}
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl
import java.util
import org.atricore.idbus.kernel.main.mediation.claim.{ClaimChannelImpl, ClaimChannel}
import org.atricore.idbus.kernel.main.mediation.endpoint.{IdentityMediationEndpointImpl, IdentityMediationEndpoint}
import org.apache.camel.impl.{DefaultProducer, DefaultCamelContext, DefaultMessage, DefaultComponent}
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationEndpoint, MediationBindingComponent, CamelMediationExchange}
import org.apache.camel.{Message, CamelContext, Exchange, Endpoint}
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse
import org.atricore.idbus.capabilities.sso.dsl.core._
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding
import org.atricore.idbus.kernel.main.mediation.confirmation.{IdentityConfirmationChannel, IdentityConfirmationChannelImpl}

/**
 * Base class for identity flow testers
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait IdentityFlowDSLTestSupport {

  type ContextBuilder = Exchange => IdentityFlowRequestContext

  protected def test(context: Exchange => IdentityFlowRequestContext, route: => IdentityFlowRoute, ex: Exchange):
    MockCamelIdentityFlowResponse = {

    new DefaultProducer(newMediationEndpoint) {
      def process(exchange: Exchange) {
        var rejections : Option[Set[Rejection]] = None
        var response : Option[IdentityFlowResponse] = None
        val ctx = context(exchange)
        route(ctx.withResponse( resp => response = Some(resp)).withReject( rejs => rejections = Some(rejs)))
        exchange.setOut(new MockCamelIdentityFlowResponse(response, rejections))
      }
    }.process(ex)

    ex.getOut.asInstanceOf[MockCamelIdentityFlowResponse]
  }

  protected def newExchange(userLocalVars: Map[String, _], authnState: AuthenticationState = new AuthenticationState) = {
    val state = new MediationStateImpl(new LocalStateImpl("mocked-up-mediation-state"))

    authnState.setAuthnRequest(new AuthnRequestType)

    val targetLocalVars = Map[String, Any]("urn:org:atricore:idbus:samlr2:idp:authn-state" -> authnState)

    (targetLocalVars ++ userLocalVars).foreach({
      case (k, v) => state.setLocalVariable(k, v)
    })

    val exchange = new MockCamelMediationExchange
    val in = new MediationMessageImpl("123", null, null, null, null, state)
    val msg = new MockCamelMediationMessage

    msg.setMessage(in)
    exchange.setIn(msg)
    exchange
  }

  protected def newMediationEndpoint = new MockCamelMediationEndpoint("foo-uri", "foo-addr", new MediationBindingComponent)


  protected def newServiceProvider(name: String) = {
    val provider = new ServiceProviderImpl
    provider.setName(name)
    provider
  }

  protected def newIdentityConfirmationProvider(name: String) = {
    val provider = new IdentityConfirmationProviderImpl
    provider.setName(name)
    provider
  }

  protected def newSpChannel(name: String) = {
    val spChannel = new SPChannelImpl
    spChannel.setName(name)
    spChannel.setClaimProviders(new util.ArrayList[ClaimChannel])
    spChannel.setIdentityConfirmationProviders(new util.ArrayList[IdentityConfirmationChannel])
    spChannel
  }

  protected def newClaimChannel(name: String) = {
    val claimChannel = new ClaimChannelImpl
    claimChannel.setName("cc-1")
    claimChannel.setEndpoints(new util.ArrayList[IdentityMediationEndpoint]())
    claimChannel
  }

  protected def newIdentityConfirmationChannel(name: String) = {
    val idConfChannel = new IdentityConfirmationChannelImpl
    idConfChannel.setName("idconf-1")
    idConfChannel.setEndpoints(new util.ArrayList[IdentityMediationEndpoint]())
    idConfChannel
  }

  protected def newIdentityMediationEndpoint(name: String, binding: SSOBinding, epType : AuthnCtxClass ) = {
   val endpoint = new IdentityMediationEndpointImpl
    endpoint.setName(name)
    endpoint.setBinding(binding.getValue)
    endpoint.setType(epType.getValue)
    endpoint
  }

  protected def newSecurityContext(spChannelName : String) =
    Map("%s_SECURITY_CTX".format(spChannelName.toUpperCase) -> new IdPSecurityContext(null, null, null))


}

class MockComponent extends DefaultComponent[CamelMediationExchange] {
  def createEndpoint(p1: String, p2: String, p3: util.Map[_, _]): Endpoint[CamelMediationExchange] = null

}

class MockCamelMediationEndpoint(uri: String, consumingAddress: String, component: MediationBindingComponent)
  extends CamelMediationEndpoint(uri, consumingAddress, component) {
  override def createExchange(exchange: Exchange): CamelMediationExchange = {
    new MockCamelMediationExchange()
  }
}

class MockCamelMediationMessage(wrappedMsg: CamelMediationMessage = new CamelMediationMessage)
  extends CamelMediationMessage {
  protected override def createBody(): AnyRef = {
    wrappedMsg
  }

  override def newInstance(): DefaultMessage = new MockCamelMediationMessage(wrappedMsg)
}

class MockCamelMediationExchange(camelContext: CamelContext = new DefaultCamelContext)
  extends CamelMediationExchange(camelContext) {

  def this(ex: CamelMediationExchange) {
    this()
  }

  override def newInstance(): Exchange = new MockCamelMediationExchange(this)

  protected override def createInMessage(): Message = new MockCamelMediationMessage

  protected override def createFaultMessage(): Message = new MockCamelMediationMessage

  protected override def createOutMessage(): Message = new MockCamelMediationMessage

}

case class MockCamelIdentityFlowResponse(response : Option[IdentityFlowResponse], rejections : Option[Set[Rejection]]) extends DefaultMessage
