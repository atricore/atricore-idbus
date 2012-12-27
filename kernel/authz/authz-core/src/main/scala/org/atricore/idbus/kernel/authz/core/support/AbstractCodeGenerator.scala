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

/**
 * Provides source code generation tools to concrete code generators.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
abstract class AbstractCodeGenerator[T] {

  abstract class AbstractSourceBuilder[T] {
    var indentLevel = 0
    var code = ""

    def <<(): this.type = <<("")

    def <<(line: String): this.type = {
      for (i <- 0 until indentLevel) {
        code += "  ";
      }
      code += line + "\n";
      this
    }

    def current_position = {
      code.length + (indentLevel*2)
    }

    def indent[T](op: => T): T = {indentLevel += 1; val rc = op; indentLevel -= 1; rc}

    def generateInitialImports: Unit = {}

    def generate(statements: List[T]): Unit

    def asString(text: String): String = {
      val buffer = new StringBuffer
      buffer.append("\"")
      text.foreach(c => {
        if (c == '"')
          buffer.append("\\\"")
        else if (c == '\\')
          buffer.append("\\\\")
        else if (c == '\n')
          buffer.append("\\n")
        else if (c == '\r')
          buffer.append("\\r")
        else if (c == '\b')
          buffer.append("\\b")
        else if (c == '\t')
          buffer.append("\\t")
        else if ((c >= '#' && c <= '~') || c == ' ' || c == '!')
          buffer.append(c)
        else {
          buffer.append("\\u")
          buffer.append("%04x".format(c.asInstanceOf[Int]))
        }
      })
      buffer.append("\"")
      buffer.toString
    }
  }
}