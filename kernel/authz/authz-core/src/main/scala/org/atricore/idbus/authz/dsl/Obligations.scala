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

package org.atricore.idbus.authz.dsl

import collection.mutable.{Stack, ListBuffer}


/**
 * Second-level/Auxiliary DSL for specifying authorization obligations.
 *
 * An obligation is a directive from the Policy Decision Point (PDP) to the Policy Enforcement Point (PEP) on what
 * must be carried out before or after an access is granted.
 * If the PEP is unable to comply with the directive, the granted access may or must not be realized.
 * The augmentation of obligations eliminates a gap between formal requirements and policy enforcement.
 * Obligations can be an effective way to meet formal requirements (non-repudiation for example) that can be hard to
 * implement as access control rules. Furthermore, any formal requirements will be part of the access control policy as
 * obligations and not as separate functions, which makes policies consistent and centralization of the IT environment
 * easier to achieve.                  *
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class Obligations {
  var parent: Option[Obligations] = None
  var obligationDeclarations = ListBuffer.empty[ObligationDeclaration]
  var attributeDeclarations = ListBuffer.empty[AttributeDeclaration]
  var current = Stack(this)

  private def newObligationDeclaration(element: ObligationElement): ObligationDeclaration = {
    val obligationDeclaration = new ObligationDeclaration(element)
    obligationDeclaration.parent = Some(this)
    current.top.obligationDeclarations += obligationDeclaration
    current push obligationDeclaration

    obligationDeclaration
  }

  private def newAttributeDeclaration(obligation: ObligationProperty): AttributeDeclaration = {
    val atcl = new AttributeDeclaration(obligation)
    current.top.attributeDeclarations += atcl
    atcl
  }

  def toAST: ObligationsNode = {
    ObligationsNode(obligationDeclarations flatMap {
      _.toObligationNode
    } toList)
  }

  implicit def withObligationDeclaration(value: ObligationElement): ObligationDeclaration = newObligationDeclaration(value)

  implicit def withAttributeDeclaration(value: ObligationProperty): AttributeDeclaration = newAttributeDeclaration(value)
}

class ObligationDeclaration(val element: ObligationElement) extends Obligations {

  def is[T](x: => T) = {
    x;
    parent match {
      case Some(p) => p.current.pop()
      case None =>
    }

  }

  def toObligationNode: List[ObligationNode] = {
    val adecls = attributeDeclarations flatMap {
      _.toObligationPropertyNode
    } toList

    val aasgn =
      obligationDeclarations flatMap {
        o =>
          o.element match {
            case `attributeAssignment` =>
              o.toAttributeAssignmentNode
            case `obligation` => Nil
          }
      } toList

    List(ObligationNode(adecls, aasgn))
  }

  def toAttributeAssignmentNode: List[AttributeAssignmentNode] = {
    List(AttributeAssignmentNode(attributeDeclarations flatMap {
      _.toObligationPropertyNode
    } toList))
  }

}

final class AttributeDeclaration(val property: ObligationProperty) {
  var value: Option[String] = None

  def :=(value: String) = this.value = Some(value)

  def toObligationPropertyNode = value match {
    case Some(v) => ObligationPropertyNode(property.name, v) :: Nil
    case None => Nil
  }
}


/**
 * Base type for obligation DSL constructs.
 */
trait ObligationElement

case object obligation extends ObligationElement

case object attributeAssignment extends ObligationElement


/**
 * Base type for obligation's properties DSL constructs.
 */
sealed trait ObligationProperty {

  def name: String

  override def toString = name

}

class StringBasedProperty(val name: String) extends ObligationProperty

case object id extends StringBasedProperty("id")

case object category extends StringBasedProperty("category")

case object issuer extends StringBasedProperty("issuer")

case object dataType extends StringBasedProperty("dataType")

case object avalue extends StringBasedProperty("avalue")

case object attributeValue extends StringBasedProperty("attributeValue")

case object fulfillOn extends StringBasedProperty("fulfillOn")


/**
 * Abstract Syntax tree domain abstractions to which DSL statements are transformed to for evaluation.
 */
case class ObligationsNode(obligations: List[ObligationNode])

case class ObligationNode(obligationProperties: List[ObligationPropertyNode], attributeAssignments: List[AttributeAssignmentNode])

case class ObligationPropertyNode(name: String, value: String)

case class AttributeAssignmentNode(properties: List[ObligationPropertyNode])