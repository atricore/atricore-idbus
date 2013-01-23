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

package org.atricore.idbus.kernel.authz.core.util

/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.URL

object ClassLoaders {
  val log = Log(getClass);


  /**
   * Returns the default class loaders to use for loading which is the current threads context class loader
   * and the class loader which loaded the authorization engine core by default
   */
  def defaultClassLoaders: List[ClassLoader] = {
    List(Thread.currentThread.getContextClassLoader, classOf[Logging].getClassLoader)
  }

  /**
   * Tries to load the named class on the given class loaders
   */
  def findClass(className: String, classLoaders: Traversable[ClassLoader] = defaultClassLoaders): Option[Class[_]] = {
    def tryLoadClass(classLoader: ClassLoader) = {
      try {
        Some(classLoader.loadClass(className))
      }
      catch {
        case e => None
      }
    }
    classLoaders.map(tryLoadClass).find(_.isDefined) match {
      case Some(a) => a
      case _ => None
    }
  }

  /**
   * Tries to find the named resource on the given class loaders
   */
  def findResource(name: String, classLoaders: Traversable[ClassLoader] = defaultClassLoaders): Option[URL] = {
    def tryLoadClass(classLoader: ClassLoader) = {
      try {
        classLoader.getResource(name)
      }
      catch {
        case e => null
      }
    }
    classLoaders.map(tryLoadClass).find(_ != null)
  }


  /**
   * Loads the given named class on the given class loaders or fails with a ClassNotFoundException
   */
  def loadClass(className: String, classLoaders: Traversable[ClassLoader]) = findClass(className, classLoaders) match {
    case Some(c) => c
    case _ => throw new ClassNotFoundException(className + " not found in class loaders: " + classLoaders)
  }

  /**
   * Evaluates the given block using the context class loader; then restores the context class loader to its
   * previous value
   */
  def withContextClassLoader[T](classLoader: ClassLoader)(block: => T): T = {
    def thread = Thread.currentThread

    val old = thread.getContextClassLoader
    try {
      thread.setContextClassLoader(classLoader)
      block
    } finally {
      thread.setContextClassLoader(old)
    }

  }
}