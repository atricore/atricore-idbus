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

import akka.config.ConfigurationException
import akka.actor.ActorSystem
import akka.util.Switch

/**
 * Extended lifecycle manager for Spray which relies on proxy configuration instead of the spray one.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait SprayLifecycle {
  self : ProxyConnector =>

  import LogLevels._
  
  private val booted = new Switch(false)

  def initializeSpray() {

    booted switchOn {
      log(Info,"Starting spray application ...")
      val loader = getClass.getClassLoader

      Thread.currentThread.setContextClassLoader(loader)

      ProxyConnectorSettings.BootClasses match {
        case Nil =>
          val e = new ConfigurationException("No boot classes configured. Please specify at least one boot class " +
            "in the spray.servlet.boot-classes config setting.")
          log(Error, e.getMessage, e)

        case classes => {
          for (className <- classes) {
            try {
              loader
                .loadClass(className)
                .getConstructor(classOf[ActorSystem])
                .newInstance(system)
            } catch {
              case e: ClassNotFoundException =>
                log(Error, "Configured boot class " + className + " cannot be found", e)
              case e: NoSuchMethodException =>
                log(Error, "Configured boot class " + className + " does not define required constructor " +
                  "with one parameter of type `akka.actor.ActorSystem`", e)
              case e: Exception =>
                log(Error, "Could not create instance of boot class " + className, e)
            }
          }
        }
      }
    }
  }

  def tearDownSpray() {
    booted switchOff {
      log(Info, "Shutting down spray application ...")
      system.shutdown()
    }

  }


}
