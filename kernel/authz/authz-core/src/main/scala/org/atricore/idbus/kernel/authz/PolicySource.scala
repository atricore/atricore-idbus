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

package org.atricore.idbus.kernel.authz

import support.UriPolicySource
import util.{Log, Resource, ResourceLoader}
import java.net.URI
import java.io.File
import java.util.regex.Pattern
import util.Strings.isEmpty

/**
 * Represents the source of a policy
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait PolicySource extends Resource {
  import PolicySource.log._

  var engine: AuthorizationEngine = _
  private var _packageName: String = ""
  private var _simpleClassName: String = _


  /**
   * Returns the type of the policy (xacml2, xacml3, etc).
   *
   * By default the extension is extracted from the uri but custom implementations
   * can override this so that a uri could be "foo.html" but the extension overriden to be "mustache"
   * for example
   */
  def policyType: Option[String] = {
    val t = uri.split("\\.")
    if (t.length < 2) {
      None
    } else {
      Some(t.last)
    }
  }

  /**
   * Returns the package name the generated policy class will be in for code generated policies
   */
  def packageName: String = {
    checkInitialised()
    if( engine.packagePrefix.length==0 || _packageName.length==0 ) {
      engine.packagePrefix + _packageName
    } else {
      engine.packagePrefix + "." + _packageName
    }
  }

  /**
   * Returns the generated fully qualified class name for code generated policies
   */
  def className: String = {
    val pn = packageName
    if (pn.length==0) {
      _simpleClassName
    } else {
      pn + "." + _simpleClassName
    }
  }

  /**
   * Checks that we have lazily created the package and class names
   */
  protected def checkInitialised(): Unit = {
    if (_simpleClassName == null) {
      // TODO is there a nice way to assign to fields from tuple matching???
      val (pn, sn) = extractPackageAndClassNames(uri)
      _simpleClassName = sn
      _packageName = Option(pn).getOrElse("")
    }
  }

  protected def extractPackageAndClassNames(uri: String): (String, String) = {

    def processClassName(cn: String) = cn.replace('.', '_').replace("-", "$dash")

    def invalidPackageName(name: String): Boolean = isEmpty(name) || reservedWords.contains(name) || name(0).isDigit || name(0) == '_'

    val normalizedURI: String = try {
      new URI(uri).normalize.toString
    } catch {
      // on windows we can't create a URI from files named things like C:/Foo/bar.ssp
      case e: Exception => val name = new File(uri).getCanonicalPath
      val sep = File.pathSeparator
      if (sep != "/") {
        // on windows lets replace the \ in a directory name with /
        val newName = name.replace('\\', '/')
        debug("converted windows path into: " + newName)
        newName
      }
      else {
        name
      }
    }
    val SPLIT_ON_LAST_SLASH_REGEX = Pattern.compile("^(.*)/([^/]*)$")
    val matcher = SPLIT_ON_LAST_SLASH_REGEX.matcher(normalizedURI.toString)
    if (matcher.matches == false) {
      // lets assume we have no package then
      val cn = processClassName(normalizedURI)
      ("", cn)
    }
    else {
      val unsafePackageNameWithWebInf = matcher.group(1).replaceAll("[^A-Za-z0-9_/]", "_").replaceAll("/", ".").replaceFirst("^\\.", "")

      // lets remove WEB-INF from the first name, since we should consider stuff in WEB-INF/org/foo as being in package org.foo
      val unsafePackageName = unsafePackageNameWithWebInf.stripPrefix("WEB_INF.")

      var packages = unsafePackageName.split("\\.")

      // lets find the tail of matching package names to use
      val lastIndex = packages.lastIndexWhere(invalidPackageName(_))
      if (lastIndex > 0) {
        packages = packages.drop(lastIndex + 1)
      }

      //val packageName = packages.map(safePackageName(_)).mkString(".")
      val packageName = packages.mkString(".")

      val cn = processClassName(matcher.group(2))

      (packageName, cn)
    }
  }

  protected val reservedWords = Set[String]("package", "class", "trait", "if", "else", "while", "def", "extends", "val", "var")



}

object PolicySource {
  val log = Log(getClass);

  def fromUri(uri: String, resourceLoader: ResourceLoader) = new UriPolicySource(uri, resourceLoader)

}
