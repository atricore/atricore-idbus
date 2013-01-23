package org.atricore.idbus.kernel.authz.core

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

/**
 * Abstractions representing the authorization domain
 */
case class DecisionRequest(subjects: Option[SubjectAttributes] = None, resources: Option[ResourceAttributes] = None,
                           actions: Option[ActionAttributes] = None, environment: Option[EnvironmentAttributes] = None)

case class SubjectAttributes(category: String, attributes: List[Attribute])

case class ResourceAttributes(attributes: List[Attribute])

case class ActionAttributes(attributes: List[Attribute])

case class EnvironmentAttributes(attributes: List[Attribute])

case class Attribute(id: String, issuer: String, values: List[AttributeValue])

case class AttributeValue(dataType: String, value: String)

case class Response(decision: Decision, status: Option[Status], obligations: Option[List[Obligation]])

case class Status(code: String, message: String, detail: String)

case class Obligation(id: String, attributeAssignments: List[AttributeAssignment], fullFillOn: Effect)

case class AttributeAssignment(id: String, category: String, issuer: String, dataType: String, value: String)

trait Decision

trait Effect

object Decisions {

  case object Permit extends Decision

  case object Deny extends Decision

  case object Indeterminate extends Decision

  case object NotApplicable extends Decision

}

object Effects {

  case object Permit extends Effect

  case object Deny extends Effect

}


