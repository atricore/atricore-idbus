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

package org.atricore.idbus.kernel.authz.dsl

import org.atricore.idbus.kernel.authz.util._

/**
 * The AccessControlFilterResult represents the two different filtering outcomes of AccessControlRouteFilters:
 * Pass and Reject.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
sealed trait AccessControlFilterResult[+T <: Product] {
  def map[B <: Product](f: T => B): AccessControlFilterResult[B]

  def flatMap[B <: Product](f: T => AccessControlFilterResult[B]): AccessControlFilterResult[B]

  def mapRejections(f: Rejection => Rejection): AccessControlFilterResult[T]
}

class Pass[+T <: Product](val values: T, val transform: AccessControlRequestContext => AccessControlRequestContext = identityFunc)
  extends AccessControlFilterResult[T] {
  def map[B <: Product](f: T => B) = new Pass(f(values), transform)

  def flatMap[B <: Product](f: T => AccessControlFilterResult[B]) = f(values) match {
    case pass: Pass[_] => new Pass(pass.values, transform andThen pass.transform)
    case reject => reject
  }

  def mapRejections(f: Rejection => Rejection) = this
}

case class Reject(rejections: Set[Rejection] = Set.empty) extends AccessControlFilterResult[Nothing] {
  def map[B <: Product](f: Nothing => B) = this

  def flatMap[B <: Product](f: Nothing => AccessControlFilterResult[B]) = this

  def mapRejections(f: Rejection => Rejection) = Reject(rejections.map(f))
}

object Reject {
  def apply(rejection: Rejection): Reject = apply(Set(rejection))
}

object Pass extends Pass[Product0](Product0, identityFunc) {
  lazy val Always: AccessControlRouteFilter[Product0] = _ => Pass

  def apply[A](a: A): Pass[Tuple1[A]] = new Pass(Tuple1(a))

  def apply[A, B](a: A, b: B): Pass[(A, B)] = new Pass((a, b))

  def apply[A, B, C](a: A, b: B, c: C): Pass[(A, B, C)] = new Pass((a, b, c))

  def apply[A, B, C, D](a: A, b: B, c: C, d: D): Pass[(A, B, C, D)] = new Pass((a, b, c, d))

  def apply[A, B, C, D, E](a: A, b: B, c: C, d: D, e: E): Pass[(A, B, C, D, E)] = new Pass((a, b, c, d, e))

  def apply[A, B, C, D, E, F](a: A, b: B, c: C, d: D, e: E, f: F): Pass[(A, B, C, D, E, F)] = new Pass((a, b, c, d, e, f))

  def apply[A, B, C, D, E, F, G](a: A, b: B, c: C, d: D, e: E, f: F, g: G): Pass[(A, B, C, D, E, F, G)] = new Pass((a, b, c, d, e, f, g))

  def withTransform(transform: AccessControlRequestContext => AccessControlRequestContext) = new Pass(Product0, transform)

  def withTransform[A](a: A)(transform: AccessControlRequestContext => AccessControlRequestContext) = new Pass(Tuple1(a), transform)

  def unapply[T <: Product](pass: Pass[T]): Option[(T, AccessControlRequestContext => AccessControlRequestContext)] = Some(pass.values, pass.transform)
}

