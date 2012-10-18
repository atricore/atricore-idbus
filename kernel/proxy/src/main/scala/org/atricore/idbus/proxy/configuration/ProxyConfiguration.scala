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

package org.atricore.idbus.proxy.configuration

import java.io.File

/**
 * Helper class for introspecting the proxy configuration.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class ProxyConfiguration(data : String) {

  val config : Proxy = {
    import ProxyDSLParser.{ log => _, _ }

    (proxy_configuration(new lexical.Scanner(data)) match {
      case Success(proxyConfig, _) =>
        proxyConfig
      case Failure(msg, _) => throw new ParseException("Failure parsing configuration: " + msg)
      case Error(msg, _) => throw new ParseException("Error parsing configuration: " + msg)
      case _ =>  throw new ParseException("Unknown error parsing configuration")
    })
  }

  def toPlatformBindingClass = Class.forName(config.platform).asInstanceOf[Class[Any]]

  def tenant(tname : String) : Option[Tenant] =
    config.tenants.content.foldLeft(None : Option[Tenant]){(a, b) =>
      if (b.name == tname) Option(b) else a }

  def tenant(hostName : String, path : String) =
    config.tenants.content.foldLeft(None : Option[Tenant]){(a, b) =>
      if (b.hostName == hostName && b.path == path) Option(b) else a }

  def connection(tname : String, cname : String) : Option[Connection] =
    tenant(tname) match {
      case Some(t) =>
        t.connections.content.foldLeft(None : Option[Connection]) { (a, b) => if (b.name == cname) Option(b) else a }
      case _ => None
    }

  def connectionBySCER(tname : String, scer : String) : Option[Connection] =
    tenant(tname).flatMap(
      _.connections.content.foldLeft(None : Option[Connection]) { (a, b) =>
        attribute(b, "securityContextEstablishmentResource").map( _ => b ).orElse(a)
      })

  def attribute(connection : Connection, attrName : String)  =
    connection.attributes.attrs.foldLeft(None : Option[Attribute]) { (a, b) => if (b.name == attrName) Option(b) else a }

  def attribute(tname : String, cname : String, attrName : String) : Option[Attribute] =
    connection(tname, cname) match {
      case Some(c) => 
        attribute(c, attrName)
      case _ => None
    }

}

object ProxyConfiguration { 

  def fromFile(path: String, filename: String): ProxyConfiguration = {
    try {
      val data = new FilesystemLoader(path).loadFile(filename)
      new ProxyConfiguration(data)
    } catch {
      case e: Throwable =>
        throw e
    }
  }

  def fromFile(filename: String): ProxyConfiguration = {
    val n = filename.lastIndexOf('/')
    if (n < 0) {
      fromFile(new File(".").getCanonicalPath, filename)
    } else {
      fromFile(filename.substring(0, n), filename.substring(n + 1))
    }
  }

  def fromResource(name: String): ProxyConfiguration = {
    fromResource(name, ClassLoader.getSystemClassLoader)
  }

  def fromString(data: String): ProxyConfiguration = {
    new ProxyConfiguration(data)
  }

  def fromResource(name: String, classLoader: ClassLoader): ProxyConfiguration = {
    try {
      val data = new ResourceLoader(classLoader).loadFile(name)
      new ProxyConfiguration(data)
    } catch {
      case e: Throwable =>
        throw e
    }
  }

}


