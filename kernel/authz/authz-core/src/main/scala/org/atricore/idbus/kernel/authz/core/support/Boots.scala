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

package org.atricore.idbus.kernel.authz.core.support

import org.atricore.idbus.kernel.authz.core.AuthorizationException
import org.atricore.idbus.kernel.authz.core.AuthorizationException
import org.atricore.idbus.kernel.authz.core.util.Objects

/**
 * A helper class for working with Boot style classes which
 * take an AuthorizationEngine as a constructor argument and have a zero argument
 * run method.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
object Boots {

  def invokeBoot(clazz: Class[_], injectionParameters: List[AnyRef]): Unit = {
    // Structural Typing to make Reflection easier.
    type Boot = {
      def run: Unit
    }

    lazy val bootClassName = clazz.getName
    val o = try {
      Objects.instantiate(clazz, injectionParameters).asInstanceOf[Boot]
    } catch {
      case e: Throwable => throw new AuthorizationException("Failed to create the instance of class " + bootClassName, e)
    }

    try {
      o.run
    } catch {
      case e: Throwable => throw new AuthorizationException("Failed to invoke " + bootClassName + ".run() : " + e, e)
    }
  }
}