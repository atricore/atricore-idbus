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

package org.atricore.idbus.kernel.authz.core.dsl.directives

import org.atricore.idbus.kernel.authz.core.AttributeValue
import org.atricore.idbus.kernel.authz.core.util.Logging
import org.atricore.idbus.kernel.authz.core.dsl._

/**
 * Directives for matching request attribute values with targets, namely subjects, actions
 * and environments.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait MatchingDirectives extends Logging {
  this: BasicAccessControlDirectives =>

  /**
   * Directive for matching a request attribute value of string type with the supplied policy target attribute.
   */
  def matchString(requestAttributeValues: List[AttributeValue], policyAttributeValue: String) =
    filter1 {
      acCtx =>
        val matchedAttribute = requestAttributeValues.find(_.value == policyAttributeValue)

        matchedAttribute match {
          case Some(attr) => Pass(true)
          case None => Reject(NoMatch)
        }

    }

  /**
   * Directive for matching a request attribute value of URI type with the supplied policy target attribute.
   */
  def matchURI(requestAttributeValues: List[AttributeValue], policyAttributeValue: String) =
    filter1 {
      acCtx =>
        val matchedAttribute = requestAttributeValues.find(_.value == policyAttributeValue)

        matchedAttribute match {
          case Some(attr) => Pass(true)
          case None => Reject(NoMatch)
        }

    }

  /**
   * Directive for matching a request attribute value of IP Address type with the supplied policy target attribute.
   */
  def matchIPAddress(requestAttributeValues: List[AttributeValue], policyAttributeValue: String) =
    filter1 {
      acCtx =>
        val matchedAttribute = requestAttributeValues.find(_.value == policyAttributeValue)

        matchedAttribute match {
          case Some(attr) => Pass(true)
          case None => Reject(NoMatch)
        }

    }

  /**
   * Directive for notifying the supplied match value to subscribers.
   */
  def respond =
    (matched: Boolean) =>
      (accCtx: AccessControlRequestContext) =>
        matched match {
          case true => accCtx.respond(DoPermit)
          case _ => accCtx.respond(DoDeny)
        }

}
