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

package org.atricore.idbus.capabilities.sso.component.builtin.directives

import org.atricore.idbus.capabilities.sso.dsl.core._
import directives.BasicIdentityFlowDirectives
import scala.Option
import org.atricore.idbus.capabilities.sso.dsl.util.Logging
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget
import org.atricore.idbus.kernel.main.provisioning.spi.request.{UpdateAclEntryRequest, FindUserByUsernameRequest}
import org.atricore.idbus.kernel.main.provisioning.domain._
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType
import scala.collection.JavaConversions._
import scala.Some
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannel
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider

/**
 * User directives
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait UserDirectives extends Logging {
  this: BasicIdentityFlowDirectives with MediationDirectives =>

  def withPrincipal = {
    filter1 {
      ctx =>
        (for {claims <- ctx.request.userClaims
              usernameClaim <- claims.getClaims.find(_.getValue.isInstanceOf[UsernameTokenType])
        } yield usernameClaim.getValue.asInstanceOf[UsernameTokenType].getUsername.getValue) match {
          case Some(username) => Pass(username)
          case None => Reject(NoPrincipalFoundInRequest)
        }
    }
  }

  def subject(nameId: String, pt: ProvisioningTarget) = {
    filter1 {
      ctx =>
        val fubureq = new FindUserByUsernameRequest
        fubureq.setUsername(nameId)

        val fuidrsp = pt.findUserByUsername(fubureq)
        Pass(fuidrsp.getUser)
    }
  }

  def aclEntryByFrom(from: String, nameId: String, pt: ProvisioningTarget) = {
    filter1 {
      ctx =>
        val idConfAcl = subject(nameId, pt).filter(ctx) match {
          case Pass(values, _) =>
            val user = values._1
            lazy val newAcl = {
              val acl = new Acl
              acl.setName("Identity Confirmation Acl")
              acl.setDescription("An Identity Confirmation Access Control List")
              acl.setAclEntries(Array.empty)
              acl
            }

            Option(user.getAcls) match {
              case Some(acls) =>
                acls.find(_.getName == "Identity Confirmation Acl") match {
                  case Some(acl) =>
                    Some(acl)
                  case None =>
                    user.setAcls((user.getAcls) :+ newAcl)
                    Some(newAcl)
                }
              case None =>
                log.debug("Empty Identity Confirmation Acl")
                user.setAcls(Array(newAcl))
                Option(newAcl)
            }

          case Reject(_) =>
            None

        }

        idConfAcl match {
          case Some(acl) =>
            Option(acl.getEntries) match {
              case Some(aclEntries) =>
                aclEntries.filter(aclEntry =>
                  aclEntry.getFrom == from).headOption match {
                  case Some(aclEntry) => Pass(aclEntry)
                  case None => Reject(AclEntryNotFound)
              }
              case None => Reject(AclEntryNotFound)
            }
          case None =>
            Reject(UserNotFound)
        }
    }
  }

  def notWhitelistedSource(from: String, nameId: String, pt: ProvisioningTarget) = {
    filter {
      ctx =>
        aclEntryByFrom(from, nameId, pt).filter(ctx) match {
          case Pass(values, _) if (values._1.getState == AclEntryStateType.PENDING) =>
            log.debug( "Principal %s connecting from %s whitelisting is pending".format(nameId, from))
            Pass
          case Pass(values, _) if (values._1.getState == AclEntryStateType.APPROVED) =>
            log.debug( "Principal %s connecting from %s is whitelisted".format(nameId, from))
            Reject(IdentityConfirmationNotRequired)
          case Reject(rejs) if (rejs.contains(AclEntryNotFound)) =>
            log.debug( "Principal %s connecting from %s is not whitelisted".format(nameId, from))
            Pass
          case Reject(r) => Reject(r)
        }
    }
  }

}
