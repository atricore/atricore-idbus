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

import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory
import org.atricore.idbus.kernel.main.mediation.{MediationBinding, Channel}
import org.atricore.idbus.capabilities.idconfirmation.main.IdentityConfirmationBinding._

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class IdentityConfirmationBindingFactory extends SamlR2BindingFactory {
  override def createBinding(binding: String, channel: Channel): MediationBinding = {

    IdentityConfirmationBinding.withName(binding) match {
      case ID_CONFIRMATION_HTTP_INITIATION =>
        new IdentityConfirmationNegotiatorBinding(binding, channel)
      case ID_CONFIRMATION_HTTP_NEGOTIATION =>
        new IdentityConfirmationNegotiatorBinding(binding, channel)
      case _ =>
        super.createBinding(binding, channel)

    }



  }
}
