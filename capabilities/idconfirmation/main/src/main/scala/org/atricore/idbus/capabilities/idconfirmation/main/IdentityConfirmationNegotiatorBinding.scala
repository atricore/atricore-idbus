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

package org.atricore.idbus.capabilities.idconfirmation.main

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.{CamelMediationMessage, AbstractMediationHttpBinding}
import org.atricore.idbus.kernel.main.mediation.{Channel, MediationMessageImpl, MediationMessage}
import org.apache.camel.{Message, Exchange}
import org.apache.commons.logging.LogFactory
import org.atricore.idbus.capabilities.idconfirmation.component.builtin.{IdentityConfirmationNegotiationInitiation, IdentityConfirmationNegotiationRequest}

/**
 * Adapts http messages to identity confirmation exchanges.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[main] class IdentityConfirmationNegotiatorBinding(binding : String, channel : Channel)
  extends AbstractMediationHttpBinding(binding, channel) {

  private final val logger = LogFactory.getLog(classOf[IdentityConfirmationNegotiatorBinding])

  def createMessage(message: CamelMediationMessage): MediationMessage[IdentityConfirmationNegotiationRequest] = {
    val exchange = message.getExchange.getExchange
    val httpMsg: Message = exchange.getIn
    val idconfNegotiationReqMsg = IdentityConfirmationNegotiationRequest()

    logger.debug("Create Message Body from exchange " + exchange.getClass.getName)

    new MediationMessageImpl(
      message.getMessageId,
      idconfNegotiationReqMsg,
      null,
      null,
      null,
      createMediationState(exchange)
    )
  }

  def copyMessageToExchange(message: CamelMediationMessage, exchange: Exchange) {
    val out = message.getMessage
    val ed = out.getDestination
    val httpOut = exchange.getOut
    val idconfMsg = out.getContent

    copyBackState(out.getState, exchange)

    idconfMsg match {
      case iicn: IdentityConfirmationNegotiationInitiation =>
        logger.debug("Initiating Identity Confirmation Negotiation on " + ed.getLocation)

        httpOut.getHeaders.put("Cache-Control", "no-cache, no-store")
        httpOut.getHeaders.put("Pragma", "no-cache")
        httpOut.getHeaders.put("http.responseCode", new Integer(302))
        httpOut.getHeaders.put("Content-Type", "text/html")
        httpOut.getHeaders.put("Location", "localhost")
        httpOut.getHeaders.put("FollowRedirect", "FALSE")
      case _ =>
    }

  }

}
