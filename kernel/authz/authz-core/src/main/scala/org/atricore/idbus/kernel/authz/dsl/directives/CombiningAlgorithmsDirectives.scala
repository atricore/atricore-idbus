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

package org.atricore.idbus.kernel.authz.dsl.directives

import org.atricore.idbus.kernel.authz.dsl._
import org.atricore.idbus.kernel.authz.util.Logging

/**
 * Rule-combining algorithm directives which define a procedure for arriving at an
 * authorization decision given the individual results of evaluation of a set of rules.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait CombiningAlgorithmsDirectives extends Logging {
  this: BasicAccessControlDirectives =>

  def denyOverrides(obligations : Option[Obligations] = None)(route : AccessControlRoute) : AccessControlRoute = {
    dispatchAndCollect(route, obligations) {
      results =>
        results.foldLeft(AccessControlAction(Permit))( (a, b) => if (b == DoDeny || b == DoIndeterminate ) DoDeny else a)
    }

  }

  def permitOverrides(route : AccessControlRoute, obligations : Option[Obligations]) : AccessControlRoute = {
    dispatchAndCollect(route, obligations) {
      results =>
        results.foldLeft(AccessControlAction(Deny))( (a, b) => if (b == DoPermit) DoPermit else a)
     }
  }

  def firstApplicable(route : AccessControlRoute, obligations : Option[Obligations]) : AccessControlRoute = {
    dispatchAndCollect(route, obligations) {
      results =>

        results.foldLeft(AccessControlAction(NotApplicable))( (a, b) => if (b == DoPermit || b == DoDeny) b else a)

    }
  }

  def onlyOneApplicable(route : AccessControlRoute, obligations : Option[Obligations]) : AccessControlRoute = {
    dispatchAndCollect(route, obligations) {
      results =>
      // TODO
        throw new UnsupportedOperationException("Only one applicable combining Algorithm not implemented")
    }
  }

}
