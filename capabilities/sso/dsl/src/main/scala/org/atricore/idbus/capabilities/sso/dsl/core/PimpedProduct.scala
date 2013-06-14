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

class PimpedProduct(product: Product) {

  def productJoin(that: Product): Product = product match {
    case Product0 => that
    case p: Product1[_] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, q._1)
      case q: Product2[_, _] => (p._1, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, q._1, q._2, q._3)
      case q: Product4[_, _, _, _] => (p._1, q._1, q._2, q._3, q._4)
      case q: Product5[_, _, _, _, _] => (p._1, q._1, q._2, q._3, q._4, q._5)
      case q: Product6[_, _, _, _, _, _] => (p._1, q._1, q._2, q._3, q._4, q._5, q._6)
      case q: Product7[_, _, _, _, _, _, _] => (p._1, q._1, q._2, q._3, q._4, q._5, q._6, q._7)
      case q: Product8[_, _, _, _, _, _, _, _] => (p._1, q._1, q._2, q._3, q._4, q._5, q._6, q._7, q._8)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product2[_, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, q._1)
      case q: Product2[_, _] => (p._1, p._2, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, p._2, q._1, q._2, q._3)
      case q: Product4[_, _, _, _] => (p._1, p._2, q._1, q._2, q._3, q._4)
      case q: Product5[_, _, _, _, _] => (p._1, p._2, q._1, q._2, q._3, q._4, q._5)
      case q: Product6[_, _, _, _, _, _] => (p._1, p._2, q._1, q._2, q._3, q._4, q._5, q._6)
      case q: Product7[_, _, _, _, _, _, _] => (p._1, p._2, q._1, q._2, q._3, q._4, q._5, q._6, q._7)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product3[_, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, q._1)
      case q: Product2[_, _] => (p._1, p._2, p._3, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, p._2, p._3, q._1, q._2, q._3)
      case q: Product4[_, _, _, _] => (p._1, p._2, p._3, q._1, q._2, q._3, q._4)
      case q: Product5[_, _, _, _, _] => (p._1, p._2, p._3, q._1, q._2, q._3, q._4, q._5)
      case q: Product6[_, _, _, _, _, _] => (p._1, p._2, p._3, q._1, q._2, q._3, q._4, q._5, q._6)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product4[_, _, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, p._4, q._1)
      case q: Product2[_, _] => (p._1, p._2, p._3, p._4, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, p._2, p._3, p._4, q._1, q._2, q._3)
      case q: Product4[_, _, _, _] => (p._1, p._2, p._3, p._4, q._1, q._2, q._3, q._4)
      case q: Product5[_, _, _, _, _] => (p._1, p._2, p._3, p._4, q._1, q._2, q._3, q._4, q._5)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product5[_, _, _, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, p._4, p._5, q._1)
      case q: Product2[_, _] => (p._1, p._2, p._3, p._4, p._5, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, p._2, p._3, p._4, p._5, q._1, q._2, q._3)
      case q: Product4[_, _, _, _] => (p._1, p._2, p._3, p._4, p._5, q._1, q._2, q._3, q._4)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product6[_, _, _, _, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, p._4, p._5, p._6, q._1)
      case q: Product2[_, _] => (p._1, p._2, p._3, p._4, p._5, p._6, q._1, q._2)
      case q: Product3[_, _, _] => (p._1, p._2, p._3, p._4, p._5, p._6, q._1, q._2, q._3)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product7[_, _, _, _, _, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, p._4, p._5, p._6, p._7, q._1)
      case q: Product2[_, _] => (p._1, p._2, p._3, p._4, p._5, p._6, p._7, q._1, q._2)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product8[_, _, _, _, _, _, _, _] => that match {
      case Product0 => p
      case q: Product1[_] => (p._1, p._2, p._3, p._4, p._5, p._6, p._7, p._8, q._1)
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case p: Product9[_, _, _, _, _, _, _, _, _] => that match {
      case Product0 => p
      case _ => throw new UnsupportedOperationException("productJoin with " + that)
    }
    case _ => throw new UnsupportedOperationException("productJoin on " + product)
  }

}
sealed class Product0 extends Product {
  def canEqual(that: Any) = that.isInstanceOf[Product0]

  def productArity = 0

  def productElement(n: Int) = throw new IllegalStateException
}

object Product0 extends Product0
