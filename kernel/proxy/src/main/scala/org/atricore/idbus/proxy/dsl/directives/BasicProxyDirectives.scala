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

package org.atricore.idbus.proxy.dsl.directives

import org.atricore.idbus.proxy._
import akka.actor.ActorSystem
import dsl._

/**
 * Primitive proxy directives consumed by business-specific directives.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
private[dsl] trait BasicProxyDirectives {

  implicit def actorSystem: ActorSystem

  def filter(filter: IdentityRouteFilter[Product0]) = new ProxyRoute0(filter)

  def filter1[A](filter: IdentityRouteFilter[Tuple1[A]]) = new ProxyRoute1(filter)

  def filter2[A, B](filter: IdentityRouteFilter[(A, B)]) = new ProxyRoute2(filter)

  def filter3[A, B, C](filter: IdentityRouteFilter[(A, B, C)]) = new ProxyRoute3(filter)

  def filter4[A, B, C, D](filter: IdentityRouteFilter[(A, B, C, D)]) = new ProxyRoute4(filter)

  def filter5[A, B, C, D, E](filter: IdentityRouteFilter[(A, B, C, D, E)]) = new ProxyRoute5(filter)

  def filter6[A, B, C, D, E, F](filter: IdentityRouteFilter[(A, B, C, D, E, F)]) = new ProxyRoute6(filter)

  def filter7[A, B, C, D, E, F, G](filter: IdentityRouteFilter[(A, B, C, D, E, F, G)]) = new ProxyRoute7(filter)

  def filter8[A, B, C, D, E, F, G, H](filter: IdentityRouteFilter[(A, B, C, D, E, F, G, H)]) = new ProxyRoute8(filter)

  def filter9[A, B, C, D, E, F, G, H, I](filter: IdentityRouteFilter[(A, B, C, D, E, F, G, H, I)]) = new ProxyRoute9(filter)

  implicit def pimpRouteWithConcatenation(route: IdentityRoute) = new IdentityRouteConcatenation(route: IdentityRoute)

  class IdentityRouteConcatenation(route: IdentityRoute) {
    /**
     * Returns a Route that chains two Routes. If the first Route rejects the request the second route is given a
     * chance to act upon the request.
     */
    def ~(other: IdentityRoute): IdentityRoute = {
      ctx =>
        route {
          ctx.withReject {
            rejections1 =>
              other {
                ctx.withReject {
                  rejections2 =>
                    ctx.reject(rejections1 ++ rejections2)
                }
              }
          }
        }
    }
  }

}

sealed abstract class ProxyRoute[T <: Product](val filter: IdentityRouteFilter[T]) {
  self =>
  // bind function (bind)
  // --------------------
  // returns a route which applies the filter for the identity route, and if it passes, it builds the inner identity route,
  // dispatching it with the result of applying the filter's transformation to the security context.
  // If if the filter doesn't pass, it does not invoke the inner identity route, marking the transaction as failed.
  protected def fromRouting(f: T => IdentityRoute): IdentityRoute = {
    ctx =>
      filter(ctx) match {
        case Pass(values, transform) => f(values)(transform(ctx))
        case Reject(rejections) => ctx.reject(rejections)
      }
  }
}

class ProxyRoute0(filter: IdentityRouteFilter[Product0]) extends ProxyRoute(filter) with (IdentityRoute => IdentityRoute) {
  // returns a dispatcher function for the supplied route.
  // The dispatcher function adds conditional execution to the inner route based on the supplied filter
  def apply(identityRoute: IdentityRoute) = fromRouting {
    _ => identityRoute
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 1 value) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute1[A](filter: IdentityRouteFilter[Tuple1[A]]) extends ProxyRoute(filter) with ((A => IdentityRoute) => IdentityRoute) {
  def apply(routing: A => IdentityRoute) = fromRouting {
    t => routing(t._1)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 2 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute2[A, B](filter: IdentityRouteFilter[(A, B)]) extends ProxyRoute(filter) with (((A, B) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 3 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute3[A, B, C](filter: IdentityRouteFilter[(A, B, C)]) extends ProxyRoute(filter) with (((A, B, C) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3)
  }
}
  
/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 4 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute4[A, B, C, D](filter: IdentityRouteFilter[(A, B, C, D)]) extends ProxyRoute(filter) with (((A, B, C, D) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 5 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute5[A, B, C, D, E](filter: IdentityRouteFilter[(A, B, C, D, E)]) extends ProxyRoute(filter) with (((A, B, C, D, E) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D, E) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 6 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute6[A, B, C, D, E, F](filter: IdentityRouteFilter[(A, B, C, D, E, F)]) extends ProxyRoute(filter) with (((A, B, C, D, E, F) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D, E, F) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 7 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute7[A, B, C, D, E, F, G](filter: IdentityRouteFilter[(A, B, C, D, E, F, G)]) extends ProxyRoute(filter) with (((A, B, C, D, E, F, G) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D, E, F, G) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 8 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute8[A, B, C, D, E, F, G, H](filter: IdentityRouteFilter[(A, B, C, D, E, F, G, H)]) extends ProxyRoute(filter) with (((A, B, C, D, E, F, G, H) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
  }
}

/**
 * A ProxyRoute using the given IdentityRouteFilter function (which extracts 9 values) on all inner ProxyRoutes it is applied to.
 */
class ProxyRoute9[A, B, C, D, E, F, G, H, I](filter: IdentityRouteFilter[(A, B, C, D, E, F, G, H, I)]) extends ProxyRoute(filter) with (((A, B, C, D, E, F, G, H, I) => IdentityRoute) => IdentityRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H, I) => IdentityRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
  }
}
