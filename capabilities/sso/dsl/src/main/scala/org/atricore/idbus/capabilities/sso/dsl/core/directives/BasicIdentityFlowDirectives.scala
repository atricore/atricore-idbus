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

package org.atricore.idbus.capabilities.sso.dsl.core.directives

import org.atricore.idbus.capabilities.sso.dsl.core._


/**
 * Primitive identity and access management directives consumed by business-specific directives.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait BasicIdentityFlowDirectives {

  def filter(filter: IdentityFlowRouteFilter[Product0]) = new IdentityRoute0(filter)

  def filter1[A](filter: IdentityFlowRouteFilter[Tuple1[A]]) = new IdentityRoute1(filter)

  def filter2[A, B](filter: IdentityFlowRouteFilter[(A, B)]) = new IdentityRoute2(filter)

  def filter3[A, B, C](filter: IdentityFlowRouteFilter[(A, B, C)]) = new IdentityRoute3(filter)

  def filter4[A, B, C, D](filter: IdentityFlowRouteFilter[(A, B, C, D)]) = new IdentityRoute4(filter)

  def filter5[A, B, C, D, E](filter: IdentityFlowRouteFilter[(A, B, C, D, E)]) = new IdentityRoute5(filter)

  def filter6[A, B, C, D, E, F](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F)]) = new IdentityRoute6(filter)

  def filter7[A, B, C, D, E, F, G](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G)]) = new IdentityRoute7(filter)

  def filter8[A, B, C, D, E, F, G, H](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H)]) = new IdentityRoute8(filter)

  def filter9[A, B, C, D, E, F, G, H, I](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H, I)]) = new IdentityRoute9(filter)

  /**
   * Creates an identity flow route that accepts all requests but applies the given transformation function to
   * the IdentityFlowRequestContext.
   */
  def transformRequestContext(f: IdentityFlowRequestContext => IdentityFlowRequestContext) =
    new IdentityRoute0(_ => new Pass(Product0, transform = f))

  implicit def pimpRouteWithConcatenation(route: IdentityFlowRoute) = new IdentityFlowRouteConcatenation(route: IdentityFlowRoute)

  class IdentityFlowRouteConcatenation(route: IdentityFlowRoute) {
    /**
     * Returns an identity and access management route that chains two identity and access management routes.
     * If the first Route rejects the requestthe second identity and access management route is given a chance to
     * act upon the request.
     */
    def ~(other: IdentityFlowRoute): IdentityFlowRoute = {
      ctx =>
        route(ctx)

        other(ctx)
    }

  }

}

/**
 * Base identity and access management route.
 */
sealed abstract class IdentityRoute[T <: Product](val filter: IdentityFlowRouteFilter[T]) {
  self =>
  // bind function (bind)
  // --------------------
  // returns a route which applies the filter for the route, and if it passes, it builds the inner route,
  // dispatching it with the result of applying the filter's transformation to the security context.
  // If if the filter doesn't pass, it does not invoke the inner route, marking the transaction as failed.
  protected def fromRouting(f: T => IdentityFlowRoute): IdentityFlowRoute = {
    ctx =>
      filter(ctx) match {
        case Pass(values, transform) => f(values)(transform(ctx))
        case Reject(rejections) => ctx.reject(rejections)
      }
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function on all inner Identity Flow
 * routes it is applied to.
 */
class IdentityRoute0(filter: IdentityFlowRouteFilter[Product0]) extends IdentityRoute(filter) with (IdentityFlowRoute => IdentityFlowRoute) {
  // returns a dispatcher function for the supplied route.
  // The dispatcher function adds conditional execution to the inner route based on the supplied filter
  def apply(identityFlowRoute: IdentityFlowRoute) = fromRouting {
    _ => identityFlowRoute
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 1 value) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute1[A](filter: IdentityFlowRouteFilter[Tuple1[A]]) extends IdentityRoute(filter) with ((A => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: A => IdentityFlowRoute) = fromRouting {
    t => routing(t._1)
  }

  def map[B](f: A => B): IdentityRoute1[B] = new IdentityRoute1[B](filter(_).map(t => Tuple1(f(t._1))))


}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 2 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute2[A, B](filter: IdentityFlowRouteFilter[(A, B)]) extends IdentityRoute(filter) with (((A, B) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 3 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute3[A, B, C](filter: IdentityFlowRouteFilter[(A, B, C)]) extends IdentityRoute(filter) with (((A, B, C) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 4 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute4[A, B, C, D](filter: IdentityFlowRouteFilter[(A, B, C, D)]) extends IdentityRoute(filter) with (((A, B, C, D) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 5 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute5[A, B, C, D, E](filter: IdentityFlowRouteFilter[(A, B, C, D, E)]) extends IdentityRoute(filter) with (((A, B, C, D, E) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 6 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute6[A, B, C, D, E, F](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 7 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute7[A, B, C, D, E, F, G](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 8 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute8[A, B, C, D, E, F, G, H](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G, H) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
  }
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 9 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute9[A, B, C, D, E, F, G, H, I](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H, I)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G, H, I) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H, I) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
  }
}

