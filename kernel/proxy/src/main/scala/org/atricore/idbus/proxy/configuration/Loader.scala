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

import java.io.{FileInputStream, InputStream, InputStreamReader, BufferedReader, File}

/**
 * Trait for proxy configuration loaders.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait Loader {

  @throws(classOf[ParseException])
  def loadFile(filename: String, required: Boolean): String

  @throws(classOf[ParseException])
  def loadFile(filename: String): String = loadFile(filename, true)

  private val BUFFER_SIZE = 8192

  protected def streamToString(in: InputStream): String = {
    val reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))
    val buffer = new Array[Char](BUFFER_SIZE)
    val out = new StringBuilder
    var n = 0
    while (n >= 0) {
      n = reader.read(buffer, 0, buffer.length)
      if (n >= 0) {
        out.append(buffer, 0, n)
      }
    }
    try {
      in.close()
    } catch {
      case _ =>
    }
    out.toString
  }
}


/**
 * Concrete loader implementation for fetching configuration descriptor from the filesystem.
 */
class FilesystemLoader(val baseFolder: String) extends Loader {
  def loadFile(filename: String, required: Boolean): String = {
    var f = new File(filename)
    if (! f.isAbsolute) {
      f = new File(baseFolder, filename)
    }
    if (!required && !f.exists) {
      ""
    } else {
      try {
        streamToString(new FileInputStream(f))
      } catch {
        case x => throw new ParseException(x.toString)
      }
    }
  }
}


/**
 * Concrete loader implementation for fetching configuration descriptors from the classpath.
 */
class ResourceLoader(classLoader: ClassLoader) extends Loader {
  def loadFile(filename: String, required: Boolean): String = {
    try {
      val stream = classLoader.getResourceAsStream(filename)
      if (stream eq null) {
        if (required) {
          throw new ParseException("Can't find resource: " + filename)
        }
        ""
      } else {
        streamToString(stream)
      }
    } catch {
      case x => throw new ParseException(x.toString)
    }
  }
}