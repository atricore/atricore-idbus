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

package org.atricore.idbus.capabilities.idconfirmation.component.builtin

import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.capabilities.sso.dsl.core._
import org.atricore.idbus.capabilities.sso.dsl.core.directives.BasicIdentityFlowDirectives
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage
import org.atricore.idbus.capabilities.sso.dsl.core.{Reject, Pass}
import org.atricore.idbus.kernel.main.mediation.confirmation.{IdentityConfirmationChannel, IdentityConfirmationRequest}
import scala.collection.JavaConversions._
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.Rejections._
import java.security.SecureRandom
import java.net.URL
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider
import org.atricore.idbus.kernel.main.provisioning.spi.request._
import org.atricore.idbus.kernel.main.provisioning.domain.{AclEntryStateType, AclDecisionType, AclEntry, Acl}
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim
import org.atricore.idbus.kernel.main.authn.Constants
import javax.xml.namespace.QName
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget
import org.atricore.idbus.capabilities.sso.component.builtin.directives.UserDirectives
import org.fusesource.scalate.TemplateEngine
import org.atricore.idbus.kernel.main.mail.MailService
import org.atricore.idbus.capabilities.sso.dsl.RedirectToLocation
import scala.Some
import org.atricore.idbus.capabilities.sso.dsl.RedirectToLocationWithArtifact
import org.atricore.idbus.capabilities.sso.dsl.IdentityFlowResponse

/**
 * Identity confirmation directives of the identity combinator library.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait BasicIdentityConfirmationDirectives extends Logging {
  this: BasicIdentityFlowDirectives with UserDirectives =>

  def onConfirmationRequest =
    filter1[IdentityConfirmationRequest] {
      ctx =>
        log.debug("Identity Confirmation Message = " + ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getContent)
        Option(ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getContent) match {
          case Some(idConfReq: IdentityConfirmationRequest) =>
            Pass(idConfReq)
          case _ =>
            Reject(NoIdentityConfirmationRequestAvailable)
        }
    }

  def onConfirmationTokenAuthenticationRequest =
    filter1[TokenAuthenticationRequest] {
      ctx =>
        Option(ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getContent) match {
          case Some(idConfTokenReq: TokenAuthenticationRequest) =>
            Pass(idConfTokenReq)
          case _ =>
            Reject(NoIdentityConfirmationTokenAuthenticationRequestAvailable)
        }
    }

  def userClaim(claimId: String) =
    filter1 {
      ctx =>
        onConfirmationRequest.filter(ctx) match {
          case Pass(values, transform) =>
            values._1.getClaims.find {
              case uc: UserClaim => uc.getName == claimId
              case _ => false
            } match {
              case Some(claimValue) =>
                Pass(claimValue)
              case None =>
                Reject(NoClaimFound)
            }
          case reject: Reject =>
            reject
        }

    }

  def claim[T](claimType: Class[T]) =
    filter1 {
      ctx =>
        onConfirmationRequest.filter(ctx) match {
          case Pass(values, transform) =>
            values._1.getClaims.find {
              _.getValue.isInstanceOf[T]
            } match {
              case Some(claimValue) =>
                Pass(claimValue)
              case None =>
                Reject(NoClaimFound)
            }
          case reject: Reject =>
            reject
        }

    }

  def identityToConfirm =
    filter1 {
      ctx =>
        val icr = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getContent.asInstanceOf[IdentityConfirmationRequest]

        val username = icr.getClaims.find(_.getValue.isInstanceOf[UsernameTokenType]).
          getOrElse(throw new IllegalArgumentException("No credentials found")).getValue.asInstanceOf[UsernameTokenType]

        val nid = Option(username.getUsername).
          getOrElse(throw new IllegalArgumentException("No username found")).getValue

        Pass(nid)
    }

  def sourceIpAddress =
    filter1 {
      ctx =>
        userClaim("sourceIpAddress").filter(ctx) match {
          case Pass(values, transform) => Pass(values._1)
          case reject => reject
        }
    }

  def emailAddress(nameId: String) =
    filter1 {
      ctx =>
        val idp = ctx.request.channel.asInstanceOf[IdentityConfirmationChannel].getFederatedProvider.asInstanceOf[IdentityProvider]
        val pt = idp.getProvisioningTarget

        subject(nameId, pt).filter(ctx).flatMap( v =>
          Option(v._1.getEmail) match {
            case Some(email) => Pass(email)
            case None => Reject(EMailNotDefinedForUser)
          }
        )
    }

  def issueSecret(size: Int = 10) = {
    /**
     * Create a random string of a given size
     * @param size size of the string to create. Must be a positive or nul integer
     * @return the generated string
     */
    def randomString(size: Int): String = {
      val random = new SecureRandom()

      def addChar(pos: Int, lastRand: Int, sb: StringBuilder): StringBuilder = {
        if (pos >= size) sb
        else {
          val randNum = if ((pos % 6) == 0) random.nextInt else lastRand
          sb.append((randNum & 0x1f) match {
            case n if n < 26 => ('A' + n).toChar
            case n => ('0' + (n - 26)).toChar
          })
          addChar(pos + 1, randNum >> 5, sb)
        }
      }
      addChar(0, 0, new StringBuilder(size)).toString
    }

    filter1 {
      ctx =>
        Pass(randomString(size))
    }
  }

  def shareSecretByEmail(tam: String) = {
    filter {
      ctx =>
        identityToConfirm.filter(ctx).flatMap {
          nameId =>
            emailAddress(nameId._1).filter(ctx).flatMap {
              email =>
                val recipient = email._1
                val mediator = ctx.request.channel.getIdentityMediator.asInstanceOf[ {def getMailService: MailService}]
                log.debug("Sending message [" + tam + "] to email address [" + recipient + "]")
                // TODO : Cange from
                mediator.getMailService.send("josso@atricore.com", recipient, "Identity Confirmation", tam, "text/html")
                Pass
            }
        }
    }
  }

  def notifyTokenShared(tokenSharedConfirmationUILocation: String, secret: String): IdentityFlowRoute = {
    ctx =>
      userClaim("sourceIpAddress").filter(ctx) match {
        case Pass(values, transform) =>
          val sourceIpAddress = values._1.getValue.asInstanceOf[String]
          val icr = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage].getMessage.getContent.asInstanceOf[IdentityConfirmationRequest]
          val pt = icr.getIssuerChannel.getProvider.asInstanceOf[IdentityProvider].getProvisioningTarget

          val username = icr.getClaims.find(_.getValue.isInstanceOf[UsernameTokenType]).
            getOrElse(throw new IllegalArgumentException("No credentials found")).getValue.asInstanceOf[UsernameTokenType]

          val nid = Option(username.getUsername).
            getOrElse(throw new IllegalArgumentException("No username found")).getValue

          val pwd = Option(username.getOtherAttributes.get(new QName(Constants.PASSWORD_NS))).
            getOrElse(throw new IllegalArgumentException("No password found"))

          val fureq = new FindUserByUsernameRequest
          fureq.setUsername(nid)

          val user = pt.findUserByUsername(fureq).getUser

          val idConfAcl = {
            lazy val newAcl = {
              val acl = new Acl
              acl.setName("Identity Confirmation Acl")
              acl.setDescription("An Identity Confirmation Access Control List")
              acl
            }

            Option(user.getAcls) match {
              case Some(acls) =>
                acls.find(_.getName == "Identity Confirmation Acl") match {
                  case Some(acl) =>
                    acl
                  case None =>
                    user.setAcls((user.getAcls) :+ newAcl)
                    newAcl
                }
              case None =>
                user.setAcls(Array(newAcl))
                newAcl
            }
          }


          val pendingAclEntries = Option(idConfAcl.getEntries).map { aclEntries =>
            aclEntries.filter( _.getFrom == sourceIpAddress ).filter( _.getState == AclEntryStateType.PENDING)
          }

          val updatedAclEntries = {
            val aclEntry = new AclEntry
            aclEntry.setPrincipalNameClaim(nid)
            aclEntry.setPasswordClaim(pwd)
            aclEntry.setDecision(AclDecisionType.ALLOW)
            aclEntry.setFrom(sourceIpAddress)
            aclEntry.setState(AclEntryStateType.PENDING)
            aclEntry.setApprovalToken(secret)
            aclEntry.setSpAlias(icr.getSpAlias)

            Option(idConfAcl.getEntries) match {
              case Some(aclEntries) =>

                aclEntries.filterNot{ e =>
                  pendingAclEntries.get.contains(e)
                } :+ aclEntry

              case None =>
                Array(aclEntry)
            }
          }

          idConfAcl.setAclEntries(updatedAclEntries)

          val uureq = new UpdateUserRequest
          uureq.setUser(user)
          pt.updateUser(uureq)

          pendingAclEntries.foreach { pentries =>
            pentries.foreach { pentry =>
              val rereq = new RemoveAclEntryRequest
              rereq.setId(pentry.getId)
              pt.removeAclEntry(rereq)
            }

          }


        case reject => reject
      }

      val taloc =
        ctx.request.channel.asInstanceOf[IdentityConfirmationChannel].getEndpoints.filter(ep =>
          IdentityConfirmationBindings.withName(ep.getBinding) == IdentityConfirmationBindings.ID_CONFIRMATION_HTTP_AUTHENTICATION
        ).headOption.map(ep => new URL("%s%s?t=%s".format(ctx.request.channel.getLocation, ep.getLocation, secret)))

      taloc match {
        case Some(tal) =>
          val tsc = TokenSharedConfirmation(secret, tal)
          ctx.respond(
            IdentityFlowResponse(
              RedirectToLocationWithArtifact(tokenSharedConfirmationUILocation),
              Option(tsc),
              Option("TokenSharedConfirmation"))
          )
        case None =>
          log.error("No endpoint specified in channel " + ctx.request.channel.getName + " Identity Confirmation Token Authentication")
      }
  }

  def withIdentityConfirmationState = {
    filter1 {
      ctx =>
        val in = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage]
        try {
          Option(in.getMessage.getState.getLocalVariable("urn:org:atricore:idbus:idconf-state").asInstanceOf[IdentityConfirmationState]) match {
            case Some(state) => Pass(state)
            case _ =>
              val state = IdentityConfirmationState(None)
              val in = ctx.request.exchange.getIn.asInstanceOf[CamelMediationMessage]
              in.getMessage.getState.setLocalVariable("urn:org:atricore:idbus:idconf-state", state)
              Pass(state)
          }
        } catch {
          case e: IllegalStateException => {
            Pass(IdentityConfirmationState(None))
          }
        }
    }
  }

  def forReceivedSecret = {
    filter1[String] {
      ctx =>
        onConfirmationTokenAuthenticationRequest.filter(ctx) match {
          case Pass(values, transform) =>
            Option(values._1.secret) match {
              case Some(token) =>
                Pass(token)
              case None =>
                Reject(NoTokenInAuthenticationRequest)
            }
          case reject =>
            Reject(NoTokenInAuthenticationRequest)
        }
    }
  }

  def forAclEntry(secret: String) = {
    filter1[AclEntry] {
      ctx =>
        val idp = ctx.request.channel.asInstanceOf[IdentityConfirmationChannel].getFederatedProvider.asInstanceOf[IdentityProvider]
        val pt = idp.getProvisioningTarget

        val faereq = new FindAclEntryByApprovalTokenRequest
        faereq.setApprovalToken(secret)

        val faersp = pt.findAclEntryByApprovalToken(faereq)

        Pass(faersp.getAclEntry)
    }
  }

  def verifyToken(receivedToken: String, aclEntry: AclEntry) = {
    filter {
      ctx =>
        if (receivedToken == aclEntry.getApprovalToken) Pass else Reject(AuthenticationFailed)
    }
  }

  def whitelistSourceByFrom(from: String, nameId: String, pt: ProvisioningTarget) = {
    filter1 {
      ctx =>
        aclEntryByFrom(from, nameId, pt).filter(ctx) match {
          case Pass(values, _) =>
            val aclEntry = values._1
            aclEntry.setDecision(AclDecisionType.ALLOW)

            val uaereq = new UpdateAclEntryRequest
            uaereq.setAclEntry(aclEntry)

            val uaersp = pt.updateAclEntry(uaereq)
            Pass(uaersp.getAclEntry)
          case Reject(r) => Reject(r)
        }

    }

  }

  def whitelistSourceByAclEntry(aclEntry: AclEntry) = {
    filter1 {
      ctx =>
        val idp = ctx.request.channel.asInstanceOf[IdentityConfirmationChannel].getFederatedProvider.asInstanceOf[IdentityProvider]
        val pt = idp.getProvisioningTarget

        aclEntry.setState(AclEntryStateType.APPROVED)

        val uaereq = new UpdateAclEntryRequest
        uaereq.setAclEntry(aclEntry)

        val uaersp = pt.updateAclEntry(uaereq)
        Pass(uaersp.getAclEntry)

    }

  }

  def tokenAuthenticationMessage(secret: String, templateUri: String) = {
    filter1 {
      ctx =>
        val taloc =
          ctx.request.channel.asInstanceOf[IdentityConfirmationChannel].getEndpoints.filter(ep =>
            IdentityConfirmationBindings.withName(ep.getBinding) == IdentityConfirmationBindings.ID_CONFIRMATION_HTTP_AUTHENTICATION
          ).headOption.map(ep => new URL("%s%s?t=%s".format(ctx.request.channel.getLocation, ep.getLocation, secret)))

        taloc match {
          case Some(tal) =>
            // NOTE: This is the classloader of the main identity confirmation module. Hence pre-compiled templates need
            // to live there
            val oldCl = Thread.currentThread().getContextClassLoader
            Thread.currentThread().setContextClassLoader(this.getClass.getClassLoader)
            val engine = new TemplateEngine
            val output = engine.layout(templateUri, Map("tokenAuthenticationUrl" -> tal))
            Thread.currentThread().setContextClassLoader(oldCl)
            Pass(output)
          case None =>
            Reject(TokenAuthenticationMessageGenerationFailed)
        }
    }
  }

  def notifyCompletion(preauthUrl: URL): IdentityFlowRoute = {
    ctx =>
      ctx.respond(
        IdentityFlowResponse(
          RedirectToLocation(preauthUrl.toString),
          Option(null),
          Option("TokenSharedConfirmation"))
      )
  }


}
