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

package org.atricore.idbus.proxy.rest

import cc.spray.http.HttpHeaders
import cc.spray.directives.{SimpleRegexMatcher, PathMatcher1, SprayRoute1}
import cc.spray.{Rejection, Directives, Pass}
import org.atricore.idbus.proxy.dsl.{ProxyRequestContext, ProxyRequestContextBuilder, Rejection => PRejection}

/**
 * Core Proxy routes for bringing together proxy and spray routes.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait ProxySprayDirectives {
  this: Directives =>

  /**
   * Bridges Spray and Proxy routes
   */
  def proxy(proxyRequestContextBuilder: ProxyRequestContextBuilder): SprayRoute1[ProxyRequestContext] =
    filter1[ProxyRequestContext] {
      ctx =>
        Pass(proxyRequestContextBuilder())
    }

  def host(): SprayRoute1[java.lang.String] = {
    val directive = headerValue {
      case HttpHeaders.Host(host, port) =>
        port.map(p => Some("%s:%d".format(host, p))).getOrElse(Some(host))
      case _ => None
    }
    filter1 {
      directive.filter(_)
    }
  }

}

case class ProxyRejection(proxyRejection: Set[PRejection]) extends Rejection

/**
 * A PathMatcher that matches and extracts Proxy path elements
 */
object ProxyPathElement extends PathMatcher1[String] {
  private val regexMatcher = new SimpleRegexMatcher(
    """[A-Za-z0-9]+""".r
  )

  def apply(path: String) = {
    // use 'flatMapValue' on the result of any PathMatcher1 to convert the extracted value to another type
    regexMatcher(path).flatMapValue {
      string =>
        try {
          Some(string)
        } catch {
          case _: IllegalArgumentException => None
        }
    }
  }
}




