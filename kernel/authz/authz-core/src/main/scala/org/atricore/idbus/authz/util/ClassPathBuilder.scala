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

package org.atricore.idbus.authz.util

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

import java.io.File
import java.net.{URI, URLClassLoader}
import scala.collection.mutable.ArrayBuffer
import java.util.{jar => juj}
import java.util.jar.{Attributes, JarFile}

class ClassPathBuilder {

  import ClassPathBuilder._

  private val classpath = new ArrayBuffer[String]

  def classPath = {
    val cp = Sequences.removeDuplicates(classpath)
    // lets transform to the canonical path to remove duplicates
    val all = (cp ++ findManifestEntries(cp)).map { s => val f = new File(s); if (f.exists) f.getCanonicalPath else s }
    Sequences.removeDuplicates(all).mkString(File.pathSeparator)
  }

  def addClassesDir(dir: String): ClassPathBuilder = addEntry(dir)

  def addJar(jar: String): ClassPathBuilder = addEntry(jar)

  def addEntry(path: String): ClassPathBuilder = {
    if (path != null && path.length > 0)
      classpath += path
    this
  }

  def addLibDir(dir: String): ClassPathBuilder = {

    def listJars(root: File): Seq[String] = {
      def makeSeq(a: Array[File]): Seq[File] = if (a == null) Nil else a
      if (root.isFile) List(root.toString)
      else makeSeq(root.listFiles) flatMap {
        f => listJars(f)
      }
    }

    if (dir != null)
      classpath ++= listJars(new File(dir))
    this
  }

  def addPathFrom(clazz: Class[_]): ClassPathBuilder = {
    if (clazz != null)
      addPathFrom(clazz.getClassLoader)
    this
  }

  def addPathFrom(loader: ClassLoader): ClassPathBuilder = {
    classpath ++= getClassPathFrom(loader)
    this
  }

  def addPathFromContextClassLoader(): ClassPathBuilder = {
    addPathFrom(Thread.currentThread.getContextClassLoader)
    this
  }

  def addPathFromSystemClassLoader(): ClassPathBuilder = {
    addPathFrom(ClassLoader.getSystemClassLoader)
    this
  }

  def addJavaPath(): ClassPathBuilder = {
    classpath ++= javaClassPath
    this
  }

  protected def findManifestEntries(cp: Seq[String]): Seq[String] = cp.flatMap {
    p =>
      var answer: Seq[String] = Nil
      val f = new File(p)
      if (f.exists && f.isFile) {
        val parent = f.getParentFile
        try {
          val jar = new JarFile(f)
          val m = jar.getManifest
          if (m != null) {
            val attrs = m.getMainAttributes
            val v = attrs.get(Attributes.Name.CLASS_PATH)
            if (v != null) {
              answer = v.toString.trim.split("\\s+").map {
                n =>
                // classpath entries are usually relative to the jar
                  if (new File(n).exists) n else new File(parent, n).getPath
              }
              debug("Found manifest classpath values %s in ", answer, f)
            }
          }
        }
        catch {
          case e => // ignore any errors probably due to non-jar
            debug(e, "Ignoring exception trying to open jar file: %s", f)
        }
      }
      answer
  }
}

private object ClassPathBuilder extends Log {

  type AntLikeClassLoader = {
    def getClasspath: String
  }

  object AntLikeClassLoader {
    def unapply(ref: AnyRef): Option[AntLikeClassLoader] = {
      if (ref == null) return None
      try {
        val method = ref.getClass.getMethod("getClasspath")
        if (method.getReturnType == classOf[String])
          Some(ref.asInstanceOf[AntLikeClassLoader])
        else
          None
      } catch {
        case e: NoSuchMethodException => None
      }
    }
  }

  def getClassPathFrom(classLoader: ClassLoader): Seq[String] = classLoader match {

    case null => Nil

    case cl: URLClassLoader =>
      for (url <- cl.getURLs.toList; uri = new URI(url.toString); path = uri.getPath; if (path != null)) yield {

        // on windows the path can include %20
        // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
        // so lets use URI as a workaround
        new File(path).getCanonicalPath
        //val n = new File(uri.getPath).getCanonicalPath
        //if (n.contains(' ')) {"\"" + n + "\""} else {n}
      }

    case AntLikeClassLoader(acp) =>
      val cp = acp.getClasspath
      cp.split(File.pathSeparator)

    case _ =>
      warn("Cannot introspect on class loader: %s of type %s", classLoader, classLoader.getClass.getCanonicalName)
      val parent = classLoader.getParent
      if (parent != null && parent != classLoader) getClassPathFrom(parent)
      else Nil
  }

  def javaClassPath: Seq[String] = {
    val jcp = System.getProperty("java.class.path", "")
    if (jcp.length > 0) jcp.split(File.pathSeparator)
    else Nil
  }
}