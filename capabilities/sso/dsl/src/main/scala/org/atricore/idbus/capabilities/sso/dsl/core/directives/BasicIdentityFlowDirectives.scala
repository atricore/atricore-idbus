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
private[sso] trait BasicIdentityFlowDirectives {

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
     * If the first Route rejects the request the second identity and access management route is given a chance to
     * act upon the request.
     */
    def ~(other: IdentityFlowRoute): IdentityFlowRoute = {
      ctx =>
        route {
          ctx.withReject { rejections1 =>
            other {
              ctx.withReject(rejections2 => ctx.reject(rejections1 ++ rejections2))
            }
          }
        }
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

  protected def or(other: IdentityRoute[T]): IdentityFlowRouteFilter[T] = { ctx =>
    self.filter(ctx) match {
      case x: Pass[_] => x
      case Reject(rejections1) => other.filter(ctx) match {
        case x: Pass[_] => x
        case Reject(rejections2) => Reject(rejections1 ++ rejections2)
      }
    }
  }
  protected def and[S <: Product, R <: Product](other: IdentityRoute[S]): IdentityFlowRouteFilter[R] = { ctx =>
    self.filter(ctx) match {
      case Pass(values1, transform1) => other.filter(transform1(ctx)) match {
        case Pass(values2, transform2) => {
          new Pass((values1 productJoin values2).asInstanceOf[R], transform1 andThen transform2)
        }
        case x: Reject => x
      }
      case x: Reject => x
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
  def | (other: IdentityRoute0) = new IdentityRoute0(or(other))
  def & (other: IdentityRoute0) = new IdentityRoute0(and(other))
  def & [A](other: IdentityRoute1[A]) = new IdentityRoute1[A](and(other))
  def & [A, B](other: IdentityRoute2[A, B]) = new IdentityRoute2[A, B](and(other))
  def & [A, B, C](other: IdentityRoute3[A, B, C]) = new IdentityRoute3[A, B, C](and(other))
  def & [A, B, C, D](other: IdentityRoute4[A, B, C, D]) = new IdentityRoute4[A, B, C, D](and(other))
  def & [A, B, C, D, E](other: IdentityRoute5[A, B, C, D, E]) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [A, B, C, D, E, F](other: IdentityRoute6[A, B, C, D, E, F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [A, B, C, D, E, F, G](other: IdentityRoute7[A, B, C, D, E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [A, B, C, D, E, F, G, H](other: IdentityRoute8[A, B, C, D, E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [A, B, C, D, E, F, G, H, I](other: IdentityRoute9[A, B, C, D, E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 1 value) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute1[A](filter: IdentityFlowRouteFilter[Tuple1[A]]) extends IdentityRoute(filter) with ((A => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: A => IdentityFlowRoute) = fromRouting {
    t => routing(t._1)
  }
  def | (other: IdentityRoute1[A]) = new IdentityRoute1[A](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute1[A](and(other))
  def & [B](other: IdentityRoute1[B]) = new IdentityRoute2[A, B](and(other))
  def & [B, C](other: IdentityRoute2[B, C]) = new IdentityRoute3[A, B, C](and(other))
  def & [B, C, D](other: IdentityRoute3[B, C, D]) = new IdentityRoute4[A, B, C, D](and(other))
  def & [B, C, D, E](other: IdentityRoute4[B, C, D, E]) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [B, C, D, E, F](other: IdentityRoute5[B, C, D, E, F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [B, C, D, E, F, G](other: IdentityRoute6[B, C, D, E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [B, C, D, E, F, G, H](other: IdentityRoute7[B, C, D, E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [B, C, D, E, F, G, H, I](other: IdentityRoute8[B, C, D, E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
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
  def | (other: IdentityRoute2[A, B]) = new IdentityRoute2[A, B](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute2[A, B](and(other))
  def & [C](other: IdentityRoute1[C]) = new IdentityRoute3[A, B, C](and(other))
  def & [C, D](other: IdentityRoute2[C, D]) = new IdentityRoute4[A, B, C, D](and(other))
  def & [C, D, E](other: IdentityRoute3[C, D, E]) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [C, D, E, F](other: IdentityRoute4[C, D, E, F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [C, D, E, F, G](other: IdentityRoute5[C, D, E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [C, D, E, F, G, H](other: IdentityRoute6[C, D, E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [C, D, E, F, G, H, I](other: IdentityRoute7[C, D, E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 3 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute3[A, B, C](filter: IdentityFlowRouteFilter[(A, B, C)]) extends IdentityRoute(filter) with (((A, B, C) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3)
  }
  def | (other: IdentityRoute3[A, B, C]) = new IdentityRoute3[A, B, C](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute3[A, B, C](and(other))
  def & [D](other: IdentityRoute1[D]) = new IdentityRoute4[A, B, C, D](and(other))
  def & [D, E](other: IdentityRoute2[D, E]) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [D, E, F](other: IdentityRoute3[D, E, F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [D, E, F, G](other: IdentityRoute4[D, E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [D, E, F, G, H](other: IdentityRoute5[D, E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [D, E, F, G, H, I](other: IdentityRoute6[D, E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 4 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute4[A, B, C, D](filter: IdentityFlowRouteFilter[(A, B, C, D)]) extends IdentityRoute(filter) with (((A, B, C, D) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4)
  }
  def | (other: IdentityRoute4[A, B, C, D]) = new IdentityRoute4[A, B, C, D](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute4[A, B, C, D](and(other))
  def & [E](other: IdentityRoute1[E]) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [E, F](other: IdentityRoute2[E, F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [E, F, G](other: IdentityRoute3[E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [E, F, G, H](other: IdentityRoute4[E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [E, F, G, H, I](other: IdentityRoute5[E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 5 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute5[A, B, C, D, E](filter: IdentityFlowRouteFilter[(A, B, C, D, E)]) extends IdentityRoute(filter) with (((A, B, C, D, E) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5)
  }
  def | (other: IdentityRoute5[A, B, C, D, E]) = new IdentityRoute5[A, B, C, D, E](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute5[A, B, C, D, E](and(other))
  def & [F](other: IdentityRoute1[F]) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [F, G](other: IdentityRoute2[F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [F, G, H](other: IdentityRoute3[F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [F, G, H, I](other: IdentityRoute4[F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 6 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute6[A, B, C, D, E, F](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6)
  }
  def | (other: IdentityRoute6[A, B, C, D, E, F]) = new IdentityRoute6[A, B, C, D, E, F](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute6[A, B, C, D, E, F](and(other))
  def & [G](other: IdentityRoute1[G]) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [G, H](other: IdentityRoute2[G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [G, H, I](other: IdentityRoute3[G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))

}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 7 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute7[A, B, C, D, E, F, G](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  }
  def | (other: IdentityRoute7[A, B, C, D, E, F, G]) = new IdentityRoute7[A, B, C, D, E, F, G](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute7[A, B, C, D, E, F, G](and(other))
  def & [H](other: IdentityRoute1[H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [H, I](other: IdentityRoute2[H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 8 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute8[A, B, C, D, E, F, G, H](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G, H) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
  }
  def | (other: IdentityRoute8[A, B, C, D, E, F, G, H]) = new IdentityRoute8[A, B, C, D, E, F, G, H](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute8[A, B, C, D, E, F, G, H](and(other))
  def & [I](other: IdentityRoute1[I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

/**
 * An Identity Flow Route using the given IdentityFlowRouteFilter function (which extracts 9 values) on all inner
 * Identity Flow routes it is applied to.
 */
class IdentityRoute9[A, B, C, D, E, F, G, H, I](filter: IdentityFlowRouteFilter[(A, B, C, D, E, F, G, H, I)]) extends IdentityRoute(filter) with (((A, B, C, D, E, F, G, H, I) => IdentityFlowRoute) => IdentityFlowRoute) {
  def apply(routing: (A, B, C, D, E, F, G, H, I) => IdentityFlowRoute) = fromRouting {
    t => routing(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
  }
  def | (other: IdentityRoute9[A, B, C, D, E, F, G, H, I]) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](or(other))
  def & (other: IdentityRoute0) = new IdentityRoute9[A, B, C, D, E, F, G, H, I](and(other))
}

