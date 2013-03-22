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
package org.atricore.idbus.capabilities.sso.dsl

import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.Channel

/**
 * Encapsulate the outcome of executing an identity flow route.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
case class IdentityFlowResponse(statusCode : StatusCode)

sealed abstract class StatusCode {
  def value: Int
  def defaultMessage: String
  def isSuccess: Boolean
  def isWarning: Boolean
  def isFailure: Boolean
  override def toString = "StatusCode(" + value + ", " + defaultMessage + ')'

}

sealed abstract class IdentityFlowSuccess extends StatusCode {
  def isSuccess = true
  def isWarning = false
  def isFailure = false
}

sealed abstract class IdentityFlowWarning extends StatusCode {
  def isSuccess = false
  def isWarning = true
  def isFailure = false
}
sealed abstract class IdentityFlowFailure extends StatusCode {
  def isSuccess = false
  def isWarning = false
  def isFailure = true
}

case class Redirect(channel : Channel, endpoint : IdentityMediationEndpoint) extends IdentityFlowSuccess {
  def value: Int = 1

  def defaultMessage: String =
    "The user should be redirected to endpoint %s of channel %s".format(endpoint.getName, channel.getName)
}


