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

import org.atricore.idbus.kernel.authz.core.dsl.{Reject, Pass}
import org.atricore.idbus.kernel.authz.core.util.Logging
import org.atricore.idbus.kernel.authz.core.AttributeValue

/**
 * Directives for extracting attributes from decision requests.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait TargetDirectives extends Logging {
  this: BasicAccessControlDirectives =>

  def rule(name: String, description: String) =
    filter {
      acCtx =>
        Pass
    }

  def subjectAttributeDesignator(mustBePresent: Boolean, attributeId: String, dataType: String) =
    filter2 {
      acCtx =>
        val category = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"

        val requestAttrValues : Option[List[AttributeValue]] =
          acCtx.request.subjects.filter( _.category == category ).
            map(_.attributes.filter ( _.id == attributeId ).flatMap( _.values ) )

        requestAttrValues match {
          case Some(vals) =>
            if (!vals.isEmpty) {
              Pass(attributeId, vals)
            } else {
              Reject() // attribute category matched, whereas the attribute id didn't
            }
          case _ =>
            Reject() // No subjects present in request

        }
    }

  def resourceAttributeDesignator(mustBePresent: Boolean, attributeId: String, dataType: String) =
    filter2 {
      acCtx =>

        val requestAttrValues : Option[List[AttributeValue]] =
          acCtx.request.resources.
            map(_.attributes.filter ( _.id == attributeId ).flatMap( _.values ) )

        requestAttrValues match {
          case Some(vals) =>
            if (!vals.isEmpty) {
              Pass(attributeId, vals)
            } else {
              Reject() // attribute category matched, whereas the attribute id didn't
            }
          case _ =>
            Reject() // No subjects present in request

        }
    }

  def actionAttributeDesignator(mustBePresent: Boolean, attributeId: String, dataType: String) =
    filter2 {
      acCtx =>
        val requestAttrValues : Option[List[AttributeValue]] =
          acCtx.request.actions.
            map(_.attributes.filter ( _.id == attributeId ).flatMap( _.values ) )

        requestAttrValues match {
          case Some(vals) =>
            if (!vals.isEmpty) {
              Pass(attributeId, vals)
            } else {
              Reject() // attribute category matched, whereas the attribute id didn't
            }
          case _ =>
            Reject() // No subjects present in request

        }
    }

  def environmentAttributeDesignator(mustBePresent: Boolean, attributeId: String, dataType: String) =
    filter2 {
      acCtx =>
        throw new UnsupportedOperationException("To be implemented")
    }


  def attributeSelector(mustBePresent: Boolean, category: String, path: String, dataType: String) =
    filter {
      acCtx =>
        throw new UnsupportedOperationException("To be implemented")
    }

}


