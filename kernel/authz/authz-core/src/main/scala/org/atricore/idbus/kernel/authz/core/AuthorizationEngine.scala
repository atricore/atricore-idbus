package org.atricore.idbus.kernel.authz.core

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

import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import collection.generic.TraversableForwarder
import collection.mutable.HashMap
import compat.Platform
import support._
import tools.nsc.util.ScalaClassLoader.URLClassLoader
import java.util.concurrent.ConcurrentHashMap
import scala.util.parsing.input.{Position, OffsetPosition}
import util._
import xacml2.Xacml2CodeGenerator
import collection.immutable.TreeMap

/**
 * Authorization engine realizing the behavior of a PDP (policy decision point).
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
object AuthorizationEngine {
  val log = Log(getClass);

  def apply(sourceDirectories: Traversable[File], mode: String): AuthorizationEngine = {
    new AuthorizationEngine(sourceDirectories, mode)
  }

  /**
   * The default policy types available
   */
  val policyTypes: List[String] = List("xacml2")
}

class AuthorizationEngine(var sourceDirectories: Traversable[File] = None, var mode: String = System.getProperty("atricore.mode", "production")) {

  import AuthorizationEngine.log._

  private case class CacheEntry(policy: Policy, dependencies: Set[String], timestamp: Long) {
    def isStale() = timestamp != 0 && dependencies.exists {
      resourceLoader.lastModified(_) > timestamp
    }
  }

  private var _workingDirectory: File = null

  private var booted = new AtomicBoolean()


  /**
   * Set to false if you don't want the authorization engine to ever cache any of the compiled policies.
   *
   * If not explicitly configured this property can be configured using the ''atricore.allowCaching'' system property
   */
  var allowCaching = "true" == System.getProperty("atricore.allowCaching", "true")

  /**
   * If true, then the authorization engine will check to see if the authorization engine has been updated since last
   * compiled so that it can be reloaded.  Defaults to true.  YOu should set to false in production environments since
   * the policies should not be changing.
   *
   * If not explicitly configured this property can be configured using the ''atricore.allowReload'' system property
   */
  var allowReload = "true" == System.getProperty("atricore.allowReload", "true")

  private var compilerInstalled = true
  var compilerInitialized = false

  lazy val compiler = createCompiler

  /**
   * Factory method to create a compiler for this AuthorizationEngine.
   * Override if you wish to contorl the compilation in a different way
   * such as in side SBT or something.
   */
  protected def createCompiler: Compiler = {
    compilerInitialized = true
    ScalaCompiler.create(this)
  }


  var bootClassName = "org.atricore.idbus.kernel.authz.Boot"

  var bootInjections: List[AnyRef] = List(this)

  var classLoader = this.getClass.getClassLoader

  var packagePrefix = ""

  /**
   * A forwarder so we can refer to whatever the current latest value of sourceDirectories is even if the value
   * is mutated after the AuthorizationEngine is constructed
   */
  protected def sourceDirectoriesForwarder = {
    val engine = this
    new TraversableForwarder[File] {
      protected def underlying = engine.sourceDirectories
    }
  }

  /**
   * Loads resources such as the policies based on URIs
   */
  var resourceLoader: ResourceLoader = new FileResourceLoader(sourceDirectoriesForwarder)

  def boot: Unit = {
    if (booted.compareAndSet(false, true)) {

      if (allowReload) {
        // Is the Scala compiler on the class path?
        try {
          getClass.getClassLoader.loadClass("scala.tools.nsc.settings.ScalaSettings")
        } catch {
          case e: Throwable =>
            // if it's not, then disable class reloading..
            debug("Scala compiler not found on the class path. Policy reloading disabled.")
            allowReload = false
            compilerInstalled = false
        }
      }

      ClassLoaders.findClass(bootClassName, List(classLoader, Thread.currentThread.getContextClassLoader)) match {
        case Some(clazz) =>
          Boots.invokeBoot(clazz, bootInjections)

        case _ =>
          info("No bootstrap class " + bootClassName + " found on classloader: " + classLoader)
      }
    }
  }

  /**
   * The supported xacml languages and their default extensions
   */
  var codeGenerators: Map[String, CodeGenerator] = Map("xml" -> new Xacml2CodeGenerator)

  /**
   * If not explicitly configured this will default to using the ''atricore.workdir'' system property to specify the
   * directory used for generating the scala source code and compiled bytecode - otherwise a temporary directory is used
   */
  def workingDirectory: File = {
    // Use a temp working directory if none is configured.
    if (_workingDirectory == null) {
      val value = System.getProperty("atricore.workdir", "")
      if (value != null && value.length > 0) {
        _workingDirectory = new File(value)
      }
      else {
        val f = File.createTempFile("atricore-", "-workdir")
        // now lets delete the file so we can make a new directory there instead
        f.delete
        if (f.mkdirs) {
          _workingDirectory = f
          f.deleteOnExit
        }
        else {
          warn("Could not delete file %s so we could create a temp directory", f)
          _workingDirectory = new File(new File(System.getProperty("java.io.tmpdir")), "_atricore");
        }
      }
    }
    _workingDirectory
  }

  def workingDirectory_=(value: File) = {
    this._workingDirectory = value
  }


  def sourceDirectory = new File(workingDirectory, "src")

  def bytecodeDirectory = new File(workingDirectory, "classes")

  def libraryDirectory = new File(workingDirectory, "lib")

  def tmpDirectory = new File(workingDirectory, "tmp")

  var classpath: String = null

  /**
   * Whether a custom classpath should be combined with the deduced classpath
   */
  var combinedClassPath = false

  val finderCache = new ConcurrentHashMap[String, String]

  private val policyCache = new HashMap[String, CacheEntry]
  private var _cacheHits = 0
  private var _cacheMisses = 0

  /**
   * Compiles and then caches the specified policy.  If the policy
   * was previously cached, the previously compiled policy instance
   * is returned.  The cache entry in invalidated and then policy
   * is re-compiled if the policy file has been updated since
   * it was last compiled.
   */
  def load(uri: String): Policy = {
    load(uriToSource(uri))
  }

  /**
   * Compiles and then caches the specified policy.  If the policy
   * was previously cached, the previously compiled policy instance
   * is returned.  The cache entry in invalidated and the policy
   * is re-compiled if the policy file has been updated since
   * it was last compiled.
   */
  def load(source: PolicySource): Policy = {
    source.engine = this
    policyCache.synchronized {

      // on the first load request, check to see if the INVALIDATE_CACHE JVM option is enabled
      if (_cacheHits == 0 && _cacheMisses == 0 && java.lang.Boolean.getBoolean("org.atricore.authz.INVALIDATE_CACHE")) {
        // this deletes generated scala and class files.
        invalidateCachedPolicies
      }

      // Determine whether to build/rebuild the policy, load existing .class files from the file system,
      // or reuse an existing policy that we've already loaded
      policyCache.get(source.uri) match {

        // Not in the cache..
        case None =>
          _cacheMisses += 1
          try {
            // Try to load a pre-compiled policy from the classpath
            cache(source, loadPrecompiledEntry(source))
          } catch {
            case _: Throwable =>
              // It was not pre-compiled... compile and load it.
              cache(source, compileAndLoadEntry(source))
          }

        // It was in the cache..
        case Some(entry) =>
          // check for staleness
          if (allowReload && entry.isStale) {
            // Cache entry is stale, re-compile it
            _cacheMisses += 1
            cache(source, compileAndLoadEntry(source))
          } else {
            // Cache entry is valid
            _cacheHits += 1
            entry.policy
          }
      }
    }
  }

  private def loadPrecompiledEntry(source: PolicySource) = {
    source.engine = this
    val uri = source.uri
    val className = source.className
    val policy = loadCompiledPolicy(className, allowCaching);
    policy.source = source
    if (allowCaching && allowReload && resourceLoader.exists(source.uri)) {
      // Even though the policy was pre-compiled, it may go or is stale
      // We still need to parse the policy to figure out it's dependencies..
      val code = generateScala(source);
      val entry = CacheEntry(policy, code.dependencies, lastModified(policy.getClass))
      if (entry.isStale) {
        // Throw an exception since we should not load stale pre-compiled classes.
        throw new StaleCacheEntryException(source)
      }
      // Yay the policy is not stale.  Lets use it.
      entry
    } else {
      // If we are not going to be cache reloading.. then we
      // don't need to do the extra work.
      CacheEntry(policy, Set(), 0)
    }
  }

  private def compileAndLoadEntry(source: PolicySource) = {
    val (policy, dependencies) = compileAndLoad(source, 0)
    CacheEntry(policy, dependencies, Platform.currentTime)
  }

  protected val sourceMapLog = Log(getClass, "SourceMap")

  private def compileAndLoad(source: PolicySource, attempt: Int): (Policy, Set[String]) = {
    source.engine = this
    var code: Code = null
    try {
      val uri = source.uri
      val text = source.text

      if (!compilerInstalled) {
        throw new AuthorizationException("Scala compiler not on the classpath.  You must either add it to the classpath or precompile all the policies")
      }

      val g = generator(source);
      // Generate the scala source code from the policy
      code = g.generate(this, source)

      val sourceFile = sourceFileName(uri)
      sourceFile.getParentFile.mkdirs
      IOUtil.writeBinaryFile(sourceFile, code.source.getBytes("UTF-8"))

      // Compile the generated scala code
      compiler.compile(sourceFile)

      // Write the source map information to the class file
      val sourceMap = buildSourceMap(g.stratumName, uri, sourceFile, code.positions)

      sourceMapLog.debug("installing:" + sourceMap)

      storeSourceMap(new File(bytecodeDirectory, code.className.replace('.', '/') + ".class"), sourceMap)
      //storeSourceMap(new File(bytecodeDirectory, code.className.replace('.', '/')+"$.class"), sourceMap)

      // Load the compiled class and instantiate the policy object
      val policy = loadCompiledPolicy(code.className)
      policy.source = source

      (policy, code.dependencies)

    } catch {
      // TODO: figure out why we sometimes get these InstantiationException errors that
      // go away if you redo
      case e: InstantiationException =>
        if (attempt == 0) {
          compileAndLoad(source, 1)
        } else {
          throw new AuthorizationException(e.getMessage, e)
        }

      case e: CompilerException =>
        // TODO: figure out why scala.tools.nsc.Global sometimes returns
        // false compile errors that go away if you redo
        if (attempt == 0) {
          compileAndLoad(source, 1)
        } else {
          // Translate the scala error location info
          // to the policy locations..
          def policy_pos(pos: Position) = {
            pos match {
              case p: OffsetPosition => {
                val filtered = code.positions.filterKeys(code.positions.ordering.compare(_, p) <= 0)
                if (filtered.isEmpty) {
                  null
                } else {
                  val (key, value) = filtered.last
                  // TODO: handle the case where the line is different too.
                  val colChange = pos.column - key.column
                  if (colChange >= 0) {
                    OffsetPosition(value.source, value.offset + colChange)
                  } else {
                    pos
                  }
                }
              }
              case _ => null
            }
          }

          var newmessage = "Compilation failed:\n"
          val errors = e.errors.map {
            (olderror) =>
              val uri = source.uri
              val pos = policy_pos(olderror.pos)
              if (pos == null) {
                newmessage += ":" + olderror.pos + " " + olderror.message + "\n"
                newmessage += olderror.pos.longString + "\n"
                olderror
              } else {
                newmessage += uri + ":" + pos + " " + olderror.message + "\n"
                newmessage += pos.longString + "\n"
                // TODO should we pass the source?
                CompilerError(uri, olderror.message, pos, olderror)
              }
          }
          error(e)
          if (e.errors.isEmpty) {
            throw e
          }
          else {
            throw new CompilerException(newmessage, errors)
          }
        }
      case e: InvalidSyntaxException =>
        e.source = source
        throw e
      case e: AuthorizationException => throw e
      case e: ResourceNotFoundException => throw e
      case e: Throwable => throw new AuthorizationException(e.getMessage, e)
    }
  }


  private def cache(source: PolicySource, ce: CacheEntry): Policy = {
    if (allowCaching) {
      policyCache += (source.uri -> ce)
    }
    val answer = ce.policy
    debug("Loaded uri: " + source.uri + " policy: " + answer)
    answer
  }

  /**
   * Returns the source file of the policy URI
   */
  protected def sourceFileName(uri: String) = {
    // Write the source code to file..
    // to avoid paths like foo/bar/C:/whatnot on windows lets mangle the ':' character
    new File(sourceDirectory, uri.replace(':', '_') + ".scala")
  }


  /**
   *  Invalidates all cached Policies.
   */
  def invalidateCachedPolicies() = {
    policyCache.synchronized {
      policyCache.clear
      finderCache.clear
      IOUtil.recursiveDelete(sourceDirectory)
      IOUtil.recursiveDelete(bytecodeDirectory)
      sourceDirectory.mkdirs
      bytecodeDirectory.mkdirs
    }
  }

  private def loadCompiledPolicy(className: String, from_cache: Boolean = true): Policy = {
    val cl = if (from_cache) {
      //new URLClassLoader(Array(bytecodeDirectory.toURI.toURL), classLoader)
      new URLClassLoader(
        Array(
          bytecodeDirectory.toURI.toURL
          //new File(System.getProperty("karaf.home") + "/system/org/scala-lang/scala-library/2.9.1/scala-library-2.9.1.jar").toURI.toURL,
          //new File(System.getProperty("karaf.home") + "/system/org/atricore/idbus/kernel/authz/org.atricore.idbus.kernel.authz.core/1.4.3-SNAPSHOT/org.atricore.idbus.kernel.authz.core-1.4.3-SNAPSHOT.jar").toURI.toURL
        ), Thread.currentThread.getContextClassLoader)
    } else {
      classLoader
    }
    val clazz = try {
      //debug("Policy class = " + Thread.currentThread.getContextClassLoader.loadClass("org.atricore.idbus.kernel.authz.core.Policy"))
      debug ("From cache = " + from_cache)
      debug("Using classloader to load policy : " + cl)
      debug("Context classloader is  : " + Thread.currentThread.getContextClassLoader)
      cl.loadClass(className)
    } catch {
      case e: ClassNotFoundException =>
        if (packagePrefix == "") {
          throw e
        } else {
          // Try without the package prefix.
          try {
            cl.loadClass(className.stripPrefix(packagePrefix).stripPrefix("."))
          } catch {
            case _ => throw e
          }
        }
    }
    return clazz.asInstanceOf[Class[Policy]].newInstance
  }

  /**
   * Figures out the modification time of the class.
   */
  private def lastModified(clazz: Class[_]): Long = {
    val codeSource = clazz.getProtectionDomain.getCodeSource;
    if (codeSource != null && codeSource.getLocation.getProtocol == "file") {
      val location = new File(codeSource.getLocation.getPath)
      if (location.isDirectory) {
        val classFile = new File(location, clazz.getName.replace('.', '/') + ".class")
        if (classFile.exists) {
          return classFile.lastModified
        }
      } else {
        // class is inside an archive.. just use the modification time of the jar
        return location.lastModified
      }
    }
    // Bail out
    return 0
  }

  /**
   * Generates the Scala code for a policy.  Useful for generating scala code that
   * will then be compiled into the application as part of a build process.
   */
  def generateScala(source: PolicySource) = {
    source.engine = this
    generator(source).generate(this, source)
  }

  /**
   * Gets the code generator to use for the give uri string by looking up the uri's extension
   * in the the codeGenerators map.
   */
  protected def generator(source: PolicySource): CodeGenerator = {
    extension(source) match {
      case Some(ext) =>
        generatorForExtension(ext)
      case None =>
        throw new AuthorizationException("Policy file extension missing. Cannot determine which code generator to use.")
    }
  }

  /**
   * Extracts the extension from the source's uri though derived engines could override this behaviour to
   * auto default missing extensions or performing custom mappings etc.
   */
  protected def extension(source: PolicySource): Option[String] = source.policyType

  /**
   * Returns the code generator for the given file extension
   */
  protected def generatorForExtension(extension: String) = codeGenerators.get(extension) match {
    case None =>
      val extensions = codeGenerators.keySet.toList
      throw new AuthorizationException("Not a policy file extension (" + extensions.mkString(" | ") + "), you requested: " + extension);
    case Some(generator) => generator
  }


  protected def buildSourceMap(stratumName: String, uri: String, scalaFile: File, positions: TreeMap[OffsetPosition, OffsetPosition]) = {
    val shortName = uri.split("/").last
    val longName = uri.stripPrefix("/")

    val stratum: SourceMapStratum = new SourceMapStratum(stratumName)
    val fileId = stratum.addFile(shortName, longName)

    // build a map of input-line -> List( output-line )
    var smap = new TreeMap[Int, List[Int]]()
    positions.foreach {
      case (out, in) =>
        var outs = out.line :: smap.getOrElse(in.line, Nil)
        smap += in.line -> outs
    }
    // sort the output lines..
    smap = smap.transform {
      (x, y) => y.sortWith(_ < _)
    }

    smap.foreach {
      case (in, outs) =>
        outs.foreach {
          out =>
            stratum.addLine(in, fileId, 1, out, 1)
        }
    }
    stratum.optimize

    var sourceMap: SourceMap = new SourceMap
    sourceMap.setOutputFileName(scalaFile.getName)
    sourceMap.addStratum(stratum, true)
    sourceMap.toString
  }

  protected def storeSourceMap(classFile: File, sourceMap: String) = {
    SourceMapInstaller.store(classFile, sourceMap)
  }


  protected def uriToSource(uri: String) = PolicySource.fromUri(uri, resourceLoader)

  // Policy evaluation methods
  //-------------------------------------------------------------------------

  def evaluate(uri: String, request: DecisionRequest): Response = {
    val policy = load(uri)

    policy.evaluate(request)
  }


}
