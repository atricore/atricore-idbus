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

import org.atricore.idbus.kernel.authz._
import core._
import core.dsl._
import collection.mutable.ListBuffer
import org.atricore.idbus.kernel.authz.core.Decisions.{Permit => PermitDecision}
import org.atricore.idbus.kernel.authz.core.Decisions.{Deny => DenyDecision}


/**
 * Primitive access control directives consumed by business-specific directives.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait BasicAccessControlDirectives {

  def filter(filter: AccessControlRouteFilter[Product0]) = new PolicyDecisionPointRoute0(filter)

  def filter1[A](filter: AccessControlRouteFilter[Tuple1[A]]) = new PolicyDecisionPointRoute1(filter)

  def filter2[A, B](filter: AccessControlRouteFilter[(A, B)]) = new PolicyDecisionPointRoute2(filter)

  def filter3[A, B, C](filter: AccessControlRouteFilter[(A, B, C)]) = new PolicyDecisionPointRoute3(filter)

  def filter4[A, B, C, D](filter: AccessControlRouteFilter[(A, B, C, D)]) = new PolicyDecisionPointRoute4(filter)

  def filter5[A, B, C, D, E](filter: AccessControlRouteFilter[(A, B, C, D, E)]) = new PolicyDecisionPointRoute5(filter)

  def filter6[A, B, C, D, E, F](filter: AccessControlRouteFilter[(A, B, C, D, E, F)]) = new PolicyDecisionPointRoute6(filter)

  def filter7[A, B, C, D, E, F, G](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G)]) = new PolicyDecisionPointRoute7(filter)

  def filter8[A, B, C, D, E, F, G, H](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G, H)]) = new PolicyDecisionPointRoute8(filter)

  def filter9[A, B, C, D, E, F, G, H, I](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G, H, I)]) = new PolicyDecisionPointRoute9(filter)

  implicit def pimpRouteWithConcatenation(route: AccessControlRoute) = new AccessControlRouteConcatenation(route: AccessControlRoute)

  class AccessControlRouteConcatenation(route: AccessControlRoute) {
    /**
     * Returns an access control route that chains two access control routes. If the first Route rejects the request
     * the second access control route is given a chance to act upon the request.
     */
    def ~(other: AccessControlRoute): AccessControlRoute = {
      ctx =>
        route(ctx)

        other(ctx)
    }

  }

  /**
   * Dispatches an access control route and evalutes the result using the supplied result evaluation strategy.
   */
  protected def dispatchAndCollect(route: AccessControlRoute, obligations: Option[Obligations])
                                  (f: List[AccessControlAction] => AccessControlAction): AccessControlRoute = {
    ctx =>
      val results = new ListBuffer[AccessControlAction]()

      route(ctx.withResponse {
        result => results += result
      })

      val decision = f(results.toList) match {
        case DoPermit => PermitDecision
        case DoDeny => DenyDecision
      }

      ctx.response = Response(decision, None, Some(responseObligations(obligations)))
  }

  /**
   * Transforms the obligations DSL-specific objects to domain model objects.
   */
  def responseObligations(obligations: Option[Obligations]): List[Obligation] = {
    obligations match {
      case Some(obs) =>
        obs.toAST.obligations.map(
          obls => {
            val obligationProperties = Map(obls.obligationProperties.map(op => (op.name, op.value)): _*)

            val mappedAttributeAssignments = obls.attributeAssignments.map(
              attrasg => {
                val props =
                  Map(attrasg.properties.map(oblprop => (oblprop.name, oblprop.value)): _*)

                (for {
                  id <- props.get("id")
                  category <- props.get("category")
                  issuer <- props.get("issuer")
                  dataType <- props.get("dataType")
                  attributeValue <- props.get("attributeValue")
                } yield AttributeAssignment(id, category, issuer, dataType, attributeValue))
              }
            ).filter(_.isDefined).map (_.get)

            obligationProperties.get("id") match {
              case Some(oid) =>
                obligationProperties.get("fulfillOn") match {
                  case Some(oeffect) =>
                    Some(Obligation(oid, mappedAttributeAssignments,
                      oeffect match {
                        case "Permit" => Effects.Permit
                        case "Deny" => Effects.Deny
                        case unknownEffect => throw new IllegalArgumentException("Invalid effect : " + unknownEffect)
                      }))
                  case _ => None
                }
              case _ => None
            }
          }).filter(_.isDefined).map(_.get)
      case _ => Nil
    }
  }

}

/**
 * Base policy decision point route.
 */
sealed abstract class PolicyDecisionPointRoute[T <: Product](val filter: AccessControlRouteFilter[T]) {
  self =>
  // bind function (bind)
  // --------------------
  // returns a route which applies the filter for the route, and if it passes, it builds the inner route,
  // dispatching it with the result of applying the filter's transformation to the security context.
  // If if the filter doesn't pass, it does not invoke the inner route, marking the transaction as failed.
  protected def fromRouting(f: T => AccessControlRoute): AccessControlRoute = {
    ctx =>
      filter(ctx) match {
        case Pass(values, transform) => f(values)(transform(ctx))
        case Reject(rejections) => ctx.reject(rejections)
      }
  }
}

/**
 * A Policy Decision Point Route using the given AccessControlRouteFilter function on all inner Policy Decision Point
 * routes it is applied to.
 */
class PolicyDecisionPointRoute0(filter: AccessControlRouteFilter[Product0]) extends PolicyDecisionPointRoute(filter) with (AccessControlRoute => AccessControlRoute) {
  // returns a dispatcher function for the supplied route.
  // The dispatcher function adds conditional execution to the inner route based on the supplied filter
  def apply(identityRoute: AccessControlRoute) = fromRouting {
    _ => identityRoute
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 1 value) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute1[A](filter: AccessControlRouteFilter[Tuple1[A]]) extends PolicyDecisionPointRoute(filter) with ((A => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: A => AccessControlRoute) = fromRouting {
    t => routing(t._1)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 2 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute2[A, B](filter: AccessControlRouteFilter[(A, B)]) extends PolicyDecisionPointRoute(filter) with (((A, B) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 3 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute3[A, B, C](filter: AccessControlRouteFilter[(A, B, C)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 4 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute4[A, B, C, D](filter: AccessControlRouteFilter[(A, B, C, D)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 5 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute5[A, B, C, D, E](filter: AccessControlRouteFilter[(A, B, C, D, E)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D, E) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D, E) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 6 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute6[A, B, C, D, E, F](filter: AccessControlRouteFilter[(A, B, C, D, E, F)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D, E, F) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D, E, F) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 7 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute7[A, B, C, D, E, F, G](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D, E, F, G) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D, E, F, G) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 8 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute8[A, B, C, D, E, F, G, H](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G, H)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D, E, F, G, H) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
  }
}

/**
 * A Policy Decision Route using the given AccessControlRouteFilter function (which extracts 9 values) on all inner
 * Policy Decision point routes it is applied to.
 */
class PolicyDecisionPointRoute9[A, B, C, D, E, F, G, H, I](filter: AccessControlRouteFilter[(A, B, C, D, E, F, G, H, I)]) extends PolicyDecisionPointRoute(filter) with (((A, B, C, D, E, F, G, H, I) => AccessControlRoute) => AccessControlRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H, I) => AccessControlRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
  }
}

