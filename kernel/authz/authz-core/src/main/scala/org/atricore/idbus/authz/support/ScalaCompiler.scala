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

package org.atricore.idbus.authz.support

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

import scala.tools.nsc.interactive.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util._
import scala.util.parsing.input.OffsetPosition
import java.io.{PrintWriter, StringWriter, File}

import org.atricore.idbus.authz.{CompilerException, AuthorizationEngine, AuthorizationException}
import org.atricore.idbus.authz.util.{Log, ClassPathBuilder}

object ScalaCompiler extends Log {

  def create(engine: AuthorizationEngine): ScalaCompiler = {
    new ScalaCompiler(engine.bytecodeDirectory, engine.classpath, engine.combinedClassPath)
  }

}

import ScalaCompiler._

trait Compiler {
  def compile(file: File): Unit

  def shutdown() {
    // noop
  }
}

class ScalaCompiler(bytecodeDirectory: File, classpath: String, combineClasspath: Boolean = false) extends Compiler {

  val settings = generateSettings(bytecodeDirectory, classpath, combineClasspath)

  val compiler = new Global(settings, null)

  def compile(file: File): Unit = {
    synchronized {
      val messageCollector = new StringWriter
      val messageCollectorWrapper = new PrintWriter(messageCollector)

      var messages = List[CompilerError]()
      val reporter = new ConsoleReporter(settings, Console.in, messageCollectorWrapper) {

        override def printMessage(posIn: Position, msg: String) {
          val pos = if (posIn eq null) NoPosition
          else if (posIn.isDefined) posIn.inUltimateSource(posIn.source)
          else posIn
          pos match {
            case FakePos(fmsg) =>
              super.printMessage(posIn, msg);
            case NoPosition =>
              super.printMessage(posIn, msg);
            case _ =>
              messages = CompilerError(posIn.source.file.file.getPath, msg, OffsetPosition(posIn.source.content, posIn.point)) :: messages
              super.printMessage(posIn, msg);
          }

        }
      }
      compiler.reporter = reporter

      // Attempt compilation
      (new compiler.Run).compile(List(file.getCanonicalPath))

      // Bail out if compilation failed
      if (reporter.hasErrors) {
        reporter.printSummary
        messageCollectorWrapper.close
        throw new CompilerException("Compilation failed:\n" + messageCollector, messages)
      }
    }
  }

  override def shutdown() = compiler.askShutdown()

  private def errorHandler(message: String): Unit = throw new AuthorizationException("Compilation failed:\n" + message)

  protected def generateSettings(bytecodeDirectory: File, classpath: String, combineClasspath: Boolean): Settings = {
    bytecodeDirectory.mkdirs

    val pathSeparator = File.pathSeparator

    val classPathFromClassLoader = (new ClassPathBuilder)
      .addEntry(classpath)
      .addPathFromContextClassLoader()
      .addPathFrom(classOf[Product])
      .addPathFrom(classOf[Global])
      .addPathFrom(getClass)
      .addPathFromSystemClassLoader()
      .addJavaPath()
      .classPath

    var useCP = if (classpath != null && combineClasspath) {
      classpath + pathSeparator + classPathFromClassLoader
    } else {
      classPathFromClassLoader
    }

    debug("using classpath: " + useCP)
    debug("system class loader: " + ClassLoader.getSystemClassLoader)
    debug("context class loader: " + Thread.currentThread.getContextClassLoader)
    debug("atricore authorization class loader: " + getClass.getClassLoader)

    val settings = new Settings(errorHandler)
    settings.classpath.value = useCP
    settings.outdir.value = bytecodeDirectory.toString
    settings.deprecation.value = true
    //settings.unchecked.value = true

    // from play-scalate
    settings.debuginfo.value = "vars"
    settings.dependenciesFile.value = "none"
    settings.debug.value = false

    // TODO not sure if these changes make much difference?
    //settings.make.value = "transitivenocp"
    settings
  }
}

