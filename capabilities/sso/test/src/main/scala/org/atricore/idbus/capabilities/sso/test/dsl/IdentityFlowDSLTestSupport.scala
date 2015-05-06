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

import org.atricore.idbus.kernel.main.mediation._
import camel.logging.MediationLogger
import org.atricore.idbus.kernel.main.mediation.state.LocalStateImpl
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType
import org.atricore.idbus.kernel.main.mediation.provider._
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl
import java.util
import org.atricore.idbus.kernel.main.mediation.claim.{ClaimChannelImpl, ClaimChannel}
import org.atricore.idbus.kernel.main.mediation.endpoint.{IdentityMediationEndpointImpl, IdentityMediationEndpoint}
import org.apache.camel.impl.{DefaultProducer, DefaultCamelContext, DefaultMessage, DefaultComponent}
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationEndpoint, MediationBindingComponent, CamelMediationExchange}
import org.apache.camel.{Message, CamelContext, Exchange, Endpoint}
import org.atricore.idbus.capabilities.sso.dsl.core._
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass
import org.atricore.idbus.kernel.main.mediation.confirmation.{IdentityConfirmationChannel, IdentityConfirmationChannelImpl}
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget
import org.atricore.idbus.kernel.main.provisioning.spi.request._
import org.atricore.idbus.kernel.main.provisioning.spi.response._
import org.atricore.idbus.kernel.main.provisioning.domain.User
import org.atricore.idbus.kernel.main.mail.MailService
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor
import scala.Some
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse
import org.atricore.idbus.capabilities.sso.dsl.core.IdentityFlowRequestContext

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


  protected def newIdentityProvider(name: String, identityConfirmationEnabled : Boolean = false,
                                    provisioningTarget : Option[ProvisioningTarget] = None) = {
    val provider = new IdentityProviderImpl
    provider.setName(name)
    provider.setIdentityConfirmationEnabled(identityConfirmationEnabled)
    provisioningTarget.foreach { pt => provider.setProvisioningTarget(pt) }
    provider
  }

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

  protected def newSpChannel(name: String, idp : FederatedLocalProvider) = {
    val spChannel = new SPChannelImpl
    spChannel.setName(name)
    spChannel.setClaimProviders(new util.ArrayList[ClaimChannel])
    spChannel.setIdentityConfirmationProviders(new util.ArrayList[IdentityConfirmationChannel])
    spChannel.setFederatedProvider(idp)
    spChannel
  }

  protected def newClaimChannel(name: String) = {
    val claimChannel = new ClaimChannelImpl
    claimChannel.setName("cc-1")
    claimChannel.setEndpoints(new util.ArrayList[IdentityMediationEndpoint]())
    claimChannel
  }

  protected def newIdentityConfirmationChannel(name: String, provider : FederatedLocalProvider, location : String) = {
    val idConfChannel = new IdentityConfirmationChannelImpl
    idConfChannel.setName("idconf-1")
    idConfChannel.setFederatedProvider(provider)
    idConfChannel.setLocation(location)
    idConfChannel.setEndpoints(new util.ArrayList[IdentityMediationEndpoint]())
    idConfChannel
  }

  protected def newIdentityMediationEndpoint(name: String, location : String, binding: String, epType : AuthnCtxClass ) = {
   val endpoint = new IdentityMediationEndpointImpl
    endpoint.setName(name)
    endpoint.setLocation(location)
    endpoint.setBinding(binding)
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

object MockProvisioningTarget extends ProvisioningTarget {
  def getName: String = ""

  def init() {}

  def shutDown() {}

  def purgeOldTransactions() {}

  def isSchemaManagementAvailable(): Boolean = false

  def isMediationPartitionAvailable(): Boolean = false

  def isTransactionValid(transactionId: String): Boolean = false

  def lookupTransactionRequest(transactionId: String): AbstractProvisioningRequest = null

  def getHashAlgorithm: String = "MD5";

  def getHashEncoding: String = "HEX";

  def removeGroup(groupRequest: RemoveGroupRequest): RemoveGroupResponse = null

  def addGroup(groupRequest: AddGroupRequest): AddGroupResponse = null

  def findGroupById(groupRequest: FindGroupByIdRequest): FindGroupByIdResponse = null

  def findGroupByName(groupRequest: FindGroupByNameRequest): FindGroupByNameResponse = null

  def listGroups(groupRequest: ListGroupsRequest): ListGroupsResponse = null

  def searchGroups(groupRequest: SearchGroupRequest): SearchGroupResponse = null

  def updateGroup(groupRequest: UpdateGroupRequest): UpdateGroupResponse = null

  def removeUser(userRequest: RemoveUserRequest): RemoveUserResponse = null

  def addUser(userRequest: AddUserRequest): AddUserResponse = null

  def prepareAddUser(userRequest: AddUserRequest): PrepareAddUserResponse = null

  def confirmAddUser(userRequest: ConfirmAddUserRequest): AddUserResponse = null

  def findUserById(userRequest: FindUserByIdRequest): FindUserByIdResponse = null

  def findUserByUsername(userRequest: FindUserByUsernameRequest): FindUserByUsernameResponse = {
    val user = new User
    user.setEmail(userRequest.getUsername + "@acme.com")
    val resp = new FindUserByUsernameResponse
    resp.setUser(user)
    resp
  }

  def listUsers(userRequest: ListUsersRequest): ListUsersResponse = null

  def searchUsers(userRequest: SearchUserRequest): SearchUserResponse = null

  def updateUser(userRequest: UpdateUserRequest): UpdateUserResponse = null

  def getUsersByGroup(usersByGroupRequest: GetUsersByGroupRequest): GetUsersByGroupResponse = null

  def setPassword(setPwdRequest: SetPasswordRequest): SetPasswordResponse = null

  def resetPassword(resetPwdRequest: ResetPasswordRequest): ResetPasswordResponse = null

  def prepareResetPassword(resetPwdRequest: ResetPasswordRequest): PrepareResetPasswordResponse = null

  def confirmResetPassword(resetPwdRequest: ConfirmResetPasswordRequest): ResetPasswordResponse = null

  def listSecurityQuestions(request: ListSecurityQuestionsRequest): ListSecurityQuestionsResponse = null

  def findAclEntryByApprovalToken(aclEntryRequest: FindAclEntryByApprovalTokenRequest): FindAclEntryByApprovalTokenResponse = null

  def updateAclEntry(aclEntryRequest: UpdateAclEntryRequest): UpdateAclEntryResponse = null

  def removeAclEntry(aclEntryRequest: RemoveAclEntryRequest): RemoveAclEntryResponse = null

  def addUserAttribute(userAttributeRequest: AddUserAttributeRequest): AddUserAttributeResponse = null

  def updateUserAttribute(userAttributeRequest: UpdateUserAttributeRequest): UpdateUserAttributeResponse = null

  def removeUserAttribute(userAttributeRequest: RemoveUserAttributeRequest): RemoveUserAttributeResponse = null

  def findUserAttributeById(userAttributeRequest: FindUserAttributeByIdRequest): FindUserAttributeByIdResponse = null

  def findUserAttributeByName(userAttributeRequest: FindUserAttributeByNameRequest): FindUserAttributeByNameResponse = null

  def listUserAttributes(userAttributeRequest: ListUserAttributesRequest): ListUserAttributesResponse = null

  def addGroupAttribute(groupAttributeRequest: AddGroupAttributeRequest): AddGroupAttributeResponse = null

  def updateGroupAttribute(groupAttributeRequest: UpdateGroupAttributeRequest): UpdateGroupAttributeResponse = null

  def removeGroupAttribute(groupAttributeRequest: RemoveGroupAttributeRequest): RemoveGroupAttributeResponse = null

  def findGroupAttributeById(groupAttributeRequest: FindGroupAttributeByIdRequest): FindGroupAttributeByIdResponse = null

  def findGroupAttributeByName(groupAttributeRequest: FindGroupAttributeByNameRequest): FindGroupAttributeByNameResponse = null

  def listGroupAttributes(groupAttributeRequest: ListGroupAttributesRequest): ListGroupAttributesResponse = null

  def addSecurityToken(addSecurityTokenRequest: AddSecurityTokenRequest): AddSecurityTokenResponse = null

  def updateSecurityToken(updateSecurityTokenRequest: UpdateSecurityTokenRequest): UpdateSecurityTokenResponse = null

  def removeSecurityToken(removeSecurityTokenRequest: RemoveSecurityTokenRequest): RemoveSecurityTokenResponse = null

  def findSecurityTokenByTokenId(findSecurityTokenByTokenIdRequest: FindSecurityTokenByTokenIdRequest): FindSecurityTokenByTokenIdResponse = null

  def findSecurityTokensByExpiresOnBefore(findSecurityTokensByExpiresOnBeforeRequest: FindSecurityTokensByExpiresOnBeforeRequest): FindSecurityTokensByExpiresOnBeforeResponse = null

  def findSecurityTokensByIssueInstantBefore(findSecurityTokensByIssueInstantBeforeRequest: FindSecurityTokensByIssueInstantBeforeRequest): FindSecurityTokensByIssueInstantBeforeResponse = null

  def listUserAccounts(request: ListUserAccountsRequest): ListUserAccountsResponse = null

  def listResources(requesst: ListResourcesRequest): ListResourcesResponse = null
}

object MockMailService extends MailService {
  def send(config: String, from: String, to: String, subject: String, message: String, contentType: String) {}

  def sendAsync(config: String, from: String, to: String, subject: String, message: String, contentType: String) {}

  def send(from: String, to: String, subject: String, message: String, contentType: String) {}

  def sendAsync(from: String, to: String, subject: String, message: String, contentType: String) {}
}

trait MailProvider {
  def getMailService : MailService
}

object MockIdentityMediator extends IdentityMediator {

  def init(unitContainer: IdentityMediationUnitContainer) {}

  def start() {}

  def stop() {}

  def setupEndpoints(channel: Channel) {}

  def resolveEndpoint(channel: Channel, endpoint: IdentityMediationEndpoint): EndpointDescriptor = null

  def getBindingFactory: MediationBindingFactory = null

  def getErrorUrl: String = ""

  def getWarningUrl: String = ""

  def getLogger: MediationLogger = null

  def isLogMessages: Boolean = false

  def sendMessage(message: MediationMessage[_], channel: Channel): AnyRef = null

  def sendMessage(content: Any, destination: EndpointDescriptor, channel: Channel): AnyRef = null

  def getMailService : MailService = MockMailService

}
