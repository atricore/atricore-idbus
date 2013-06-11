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

package org.atricore.idbus.capabilities.idconfirmation.main.producers

import org.atricore.idbus.capabilities.sso.dsl.{IdentityFlowResponse, IdentityFlowRequest}

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, CamelMediationExchange}
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer
import org.apache.camel.Endpoint
import org.atricore.idbus.capabilities.idconfirmation.main.{IdentityConfirmationMediator}
import org.apache.commons.logging.LogFactory
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannel
import org.atricore.idbus.capabilities.sso.dsl.core.{IdentityBusConnector, Rejection, IdentityFlowRequestContext}
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.BasicIdentityConfirmationDirectives
import org.atricore.idbus.capabilities.sso.dsl.core.directives.{DebuggingDirectives, BasicIdentityFlowDirectives}
import org.atricore.idbus.capabilities.oauth2.component.builtin.BasicOAuth2Directives
import org.atricore.idbus.capabilities.sso.component.builtin.directives.{MediationDirectives, UserDirectives}

/**
 * Implementation of the identity confirmation capability.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[main] class IdentityConfirmationNegotiationProducer(camelEndpoint: Endpoint[CamelMediationExchange])
  extends AbstractCamelProducer[CamelMediationExchange](camelEndpoint)
  with BasicIdentityFlowDirectives
  with MediationDirectives
  with BasicIdentityConfirmationDirectives
  with BasicOAuth2Directives
  with UserDirectives
  with DebuggingDirectives
  with IdentityBusConnector {

  private final val logger = LogFactory.getLog(classOf[IdentityConfirmationNegotiationProducer])

  protected def doProcess(exchange: CamelMediationExchange) {
    val idcChannel = channel.asInstanceOf[IdentityConfirmationChannel]
    val idcMediator = idcChannel.getIdentityMediator.asInstanceOf[IdentityConfirmationMediator]
    val provider = idcChannel.getProvider
    var rejections: Option[Set[Rejection]] = None
    val ctx = IdentityFlowRequestContext(IdentityFlowRequest(exchange, provider, idcChannel, endpoint))

    logRequestResponse("") {
      onConfirmationRequest {
        _ =>
          issueSecret(10) {
            secret =>
              tokenAuthenticationMessage(secret, "/templates/token_authentication_message.ssp") {
                (tokenAuthenticationMessage) =>
                  logger.debug("Token shared message is = " + tokenAuthenticationMessage)
                  shareSecretByEmail(secret) {
                    notifyTokenShared(idcMediator.tokenSharingConfirmationUILocation, secret)
                  }
              }

          }
      } ~
        onConfirmationTokenAuthenticationRequest {
          _ =>
            forReceivedSecret {
              (receivedSecret) =>
                forAclEntry(receivedSecret) {
                  (aclEntry) =>
                    verifyToken(receivedSecret, aclEntry) {
                      whitelistSourceByAclEntry(aclEntry) {
                        aclEntry =>
                          requestOAuth2AccessToken(
                            idcMediator.oauth2ClientId,
                            idcMediator.oauth2ClientSecret,
                            idcMediator.oauth2AuthorizationServerEndpoint,
                            aclEntry.getPrincipalNameClaim,
                            aclEntry.getPasswordClaim
                          ) {
                            (oauth2Token) =>
                              preauthUrl(idcMediator.getIdpInitiatedEndpoint, aclEntry.getSpAlias, oauth2Token) {
                                (preauthUrl) =>
                                  logger.debug("Preauth Url = " + preauthUrl)
                                  notifyCompletion(preauthUrl)
                              }
                          }
                      }
                    }
                }
            }
        }
    }(ctx.withResponse(resp => respond(exchange, resp)).withReject(rejs => rejections = Some(rejs)))

  }
}
