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

package org.atricore.idbus.capabilities.sso.dsl.core

import org.atricore.idbus.capabilities.sso.dsl.util._

/**
 * The IdentityFlowFilterResult represents the two different filtering outcomes of IdentityRouteFilters:
 * Pass and Reject.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
sealed trait IdentityFlowFilterResult[+T <: Product] {
  def map[B <: Product](f: T => B): IdentityFlowFilterResult[B]

  def flatMap[B <: Product](f: T => IdentityFlowFilterResult[B]): IdentityFlowFilterResult[B]

  def mapRejections(f: Rejection => Rejection): IdentityFlowFilterResult[T]
}

class Pass[+T <: Product](val values: T, val transform: IdentityFlowRequestContext => IdentityFlowRequestContext = identityFunc)
  extends IdentityFlowFilterResult[T] {
  def map[B <: Product](f: T => B) = new Pass(f(values), transform)

  def flatMap[B <: Product](f: T => IdentityFlowFilterResult[B]) = f(values) match {
    case pass: Pass[_] => new Pass(pass.values, transform andThen pass.transform)
    case reject => reject
  }

  def mapRejections(f: Rejection => Rejection) = this
}

case class Reject(rejections: Set[Rejection] = Set.empty) extends IdentityFlowFilterResult[Nothing] {
  def map[B <: Product](f: Nothing => B) = this

  def flatMap[B <: Product](f: Nothing => IdentityFlowFilterResult[B]) = this

  def mapRejections(f: Rejection => Rejection) = Reject(rejections.map(f))
}

object Reject {
  def apply(rejection: Rejection): Reject = apply(Set(rejection))
}

object Pass extends Pass[Product0](Product0, identityFunc) {
  lazy val Always: IdentityFlowRouteFilter[Product0] = _ => Pass

  def apply[A](a: A): Pass[Tuple1[A]] = new Pass(Tuple1(a))

  def apply[A, B](a: A, b: B): Pass[(A, B)] = new Pass((a, b))

  def apply[A, B, C](a: A, b: B, c: C): Pass[(A, B, C)] = new Pass((a, b, c))

  def apply[A, B, C, D](a: A, b: B, c: C, d: D): Pass[(A, B, C, D)] = new Pass((a, b, c, d))

  def apply[A, B, C, D, E](a: A, b: B, c: C, d: D, e: E): Pass[(A, B, C, D, E)] = new Pass((a, b, c, d, e))

  def apply[A, B, C, D, E, F](a: A, b: B, c: C, d: D, e: E, f: F): Pass[(A, B, C, D, E, F)] = new Pass((a, b, c, d, e, f))

  def apply[A, B, C, D, E, F, G](a: A, b: B, c: C, d: D, e: E, f: F, g: G): Pass[(A, B, C, D, E, F, G)] = new Pass((a, b, c, d, e, f, g))

  def withTransform(transform: IdentityFlowRequestContext => IdentityFlowRequestContext) = new Pass(Product0, transform)

  def withTransform[A](a: A)(transform: IdentityFlowRequestContext => IdentityFlowRequestContext) = new Pass(Tuple1(a), transform)

  def unapply[T <: Product](pass: Pass[T]): Option[(T, IdentityFlowRequestContext => IdentityFlowRequestContext)] = Some(pass.values, pass.transform)
}

