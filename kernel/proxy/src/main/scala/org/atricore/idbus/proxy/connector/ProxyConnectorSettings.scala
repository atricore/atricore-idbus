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

package org.atricore.idbus.proxy.connector

import com.typesafe.config.{ConfigFactory, Config}
import collection.JavaConverters._

/**
 * Helper object to pick up proxy-specific Spray settings.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
object ProxyConnectorSettings {
  private[this] val c: Config = {
    val c = ConfigFactory.load()
    c.checkValid(ConfigFactory.defaultReference(), "spray.proxy")
    c.getConfig("spray.proxy")
  }

  val BootClasses = c.getStringList("boot").asScala.toList
  val RootActorPath = c getString "root-actor-path"
  val TimeoutActorPath = c getString "timeout-actor-path"
  val RequestTimeout = c getMilliseconds "request-timeout"

}