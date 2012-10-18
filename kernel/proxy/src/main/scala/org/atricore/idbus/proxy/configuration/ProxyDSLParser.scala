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

import scala.util.parsing.combinator.syntactical._

// -------------------------------------------------------------------------------------------------------------------
// AST node types
// -------------------------------------------------------------------------------------------------------------------

case class Proxy(service : String, platform : String, tenants : Tenants)

case class Tenants(content: Seq[Tenant])

case class Tenant(name : String, hostName : String, path : String, connections : Connections)

case class Connections(content : Seq[Connection])

case class Connection(name : String, serviceProvider : ServiceProvider, attributes : Attributes)

case class ServiceProvider(spName : String, spType : String,  identityAppliance : IdentityAppliance)

case class IdentityAppliance(name : String)

case class Attributes(attrs: Seq[Attribute])

case class Attribute(name: String, value: String)

class ParseException(reason: String, cause: Throwable) extends Exception(reason, cause) {
  def this(reason: String) = this(reason, null)
  def this(cause: Throwable) = this(null, cause)
}

/**
 * Configuration parser which turns an atricore proxy configuration descriptors (i.e. atricore.conf) to
 * an abstract syntax tree (AST) with nodes of the type above.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
object ProxyDSLParser extends StandardTokenParsers {

  lexical.reserved +=
    ("deliver", "service", "on", "platform", "tenant", "bind", "to", "host", "path", "connection", "with", "service",
      "provider", "of", "type", "at", "identity", "appliance", "having")
  lexical.delimiters +=("(", ")", ",")

  lazy val proxy_configuration: Parser[Proxy] =
    "deliver" ~ "service" ~ ident ~ "on" ~ "platform" ~ stringLit ~ "to" ~ tenants ^^ {
      case d ~ s ~ service_name ~ o ~ p ~ platform_fqcn ~ t ~ ts  => Proxy(service_name, platform_fqcn, ts)
    }

  lazy val tenants: Parser[Tenants] =
    "(" ~> rep1sep(tenant, ",") <~ ")" ^^ Tenants

  lazy val tenant: Parser[Tenant] =
    "tenant" ~ ident ~ "bind" ~ "to" ~ "host" ~ stringLit ~ "path" ~ stringLit ~ connections ^^ {
      case tn ~ t_name ~ b ~ to ~ h ~ host_name ~ p ~ path ~ cns => Tenant(t_name, host_name, path, cns)
    }

  lazy val connections: Parser[Connections] = 
    "(" ~> rep1sep(connection, ",") <~ ")" ^^ Connections

  lazy val connection : Parser[Connection] =
    "connection" ~ ident ~ "with" ~ service_provider ~ "having" ~ attributes ^^ {
      case c ~ cn ~ w ~ sp ~ h ~ attrs => Connection(cn, sp, attrs)      
    } 

  lazy val service_provider: Parser[ServiceProvider] =
    "service" ~ "provider" ~ ident ~ "of" ~ "type" ~ ident ~ "at" ~ identity_appliance ^^ {
      case s ~ p ~ spName ~ o ~ t ~ spType ~ a ~ ia  => ServiceProvider(spName, spType, ia)
    }

  lazy val identity_appliance: Parser[IdentityAppliance] = 
    "identity" ~ "appliance" ~ ident ^^ {
      case ia ~ iaName => IdentityAppliance(iaName)
    }

  lazy val attributes: Parser[Attributes] =
    "(" ~> rep1sep(attribute, ",") <~ ")" ^^ Attributes

  lazy val attribute: Parser[Attribute] =
    ident ~ stringLit ^^ {
      case aname ~ aval => Attribute(aname, aval)
    }
}
