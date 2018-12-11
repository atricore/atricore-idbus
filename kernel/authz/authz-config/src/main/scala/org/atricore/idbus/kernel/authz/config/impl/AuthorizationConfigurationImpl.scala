package org.atricore.idbus.kernel.authz.config.impl

import collection.mutable.ListBuffer
import scala.Array._
import java.io.File
import org.apache.commons.logging.{LogFactory, Log}
import org.atricore.idbus.kernel.authz.core.support.Precompiler
import org.atricore.idbus.kernel.authz.core.util.IOUtil
import org.atricore.idbus.kernel.authz.core.AuthorizationEngine
import org.atricore.idbus.kernel.authz.config.{AuthorizationConfiguration, PolicyConfig}

class AuthorizationConfigurationImpl extends AuthorizationConfiguration {
  private[this] val log: Log = LogFactory.getLog(this.getClass)
  private lazy val authzPolicyHome = {
    val ph = new File(System.getProperty("karaf.home") + "/data/work/authz");
    if (!ph.exists()) ph.mkdirs()
    ph
  }

  private val _policies = new ListBuffer[PolicyConfig]

  def policies: List[PolicyConfig] = _policies.toList

  def init() {
  }

  def register(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      _policies += policy
      val baseName = policy.getPolicyResource().getURI.getPath.split("/").last
      log.debug("Storing Authorization Policy [" + baseName + "] in repository")

      log.debug("Policy Descriptor name = " + baseName)

      IOUtil.copy(policy.getPolicyResource().getURL, new File(authzPolicyHome.getAbsolutePath + "/" + baseName));
      precompilePolicies
      log.debug("Registered Authorization Policy [" + baseName + "]")
    }
  }

  def unregister(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      _policies -= policy;
      log.debug("Unregistered Authorization Policy [" + policy.getPolicyResource().getFilename + "]")
    }
  }

  private def precompilePolicies {
    log.debug("Precompiling Authorization Policies in repository " + authzPolicyHome);

    val precompiler = new Precompiler()

    precompiler.sources = Array(authzPolicyHome)
    precompiler.workingDirectory = authzPolicyHome
    precompiler.targetDirectory = authzPolicyHome
    precompiler.classpath = System.getProperty("karaf.home") +
      "/system/org/scala-lang/scala-library/2.9.1/scala-library-2.9.1.jar:" +
      System.getProperty("karaf.home") + "/system/org/atricore/idbus/kernel/authz/org.atricore.idbus.kernel.authz.core/1.6.0-SNAPSHOT/org.atricore.idbus.kernel.authz.core-1.6.0-SNAPSHOT.jar"

    precompiler.execute
  }

  def engine = {
    val authzEngine = new AuthorizationEngine(Array(authzPolicyHome)) {
      // lets output generated bytecode to the classes directory.
      override def bytecodeDirectory = {
        authzPolicyHome
      }
    }

    authzEngine.classpath = System.getProperty("karaf.home") +
      "/system/org/scala-lang/scala-library/2.9.1/scala-library-2.9.1.jar:" +
      System.getProperty("karaf.home") + "/system/org/atricore/idbus/kernel/authz/org.atricore.idbus.kernel.authz.core/1.6.0-SNAPSHOT/org.atricore.idbus.kernel.authz.core-1.6.0-SNAPSHOT.jar"

    authzEngine.combinedClassPath = true
    authzEngine.classLoader = Thread.currentThread.getContextClassLoader

    authzEngine
  }


  def close() {}

}
