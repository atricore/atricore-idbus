package org.atricore.idbus.kernel.authz.config.impl

import collection.mutable.ListBuffer
import org.atricore.idbus.kernel.authz.config.PolicyConfig
import scala.Array._
import java.io.File
import org.apache.commons.logging.{LogFactory, Log}
import org.atricore.idbus.kernel.authz.core.support.Precompiler
import org.atricore.idbus.kernel.authz.core.util.IOUtil

class AuthorizationConfiguration {
  private[this] val log: Log = LogFactory.getLog( this.getClass )
  private lazy val authzPolicyHome = 
  {
    val ph = new File(System.getProperty("karaf.home") + "/data/work/authz");
    if (!ph.exists()) ph.mkdirs()
    ph
  }

  private val policies = new ListBuffer[PolicyConfig]

  def init() {
  }

  def register(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      policies += policy
      log.debug("Storing Authorization Policy [" + policy.getPolicyResource().getURI.getPath + "] in repository")

      val bundlePoliciesHome = new File(authzPolicyHome.getAbsolutePath + policy.getPolicyResource().getURI.getPath).getParentFile

      if (!bundlePoliciesHome.exists()) bundlePoliciesHome.mkdirs()

      IOUtil.copy(policy.getPolicyResource().getURL, new File(authzPolicyHome.getAbsolutePath + policy.getPolicyResource().getURI.getPath));
      precompilePolicies
      log.debug("Registered Authorization Policy [" + policy.getPolicyResource().getFilename + "]")
    }
  }

  def unregister(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      policies -= policy;
      log.debug("Unregistered Authorization Policy [" + policy.getPolicyResource().getFilename + "]")
    }
  }

  def precompilePolicies {
    log.debug("Precompiling Authorization Policies in repository " + authzPolicyHome);

    val precompiler = new Precompiler()

    precompiler.sources = Array( authzPolicyHome )
    precompiler.workingDirectory = authzPolicyHome
    precompiler.targetDirectory = authzPolicyHome
    precompiler.classpath = System.getProperty("karaf.home") +
      "/system/org/scala-lang/scala-library/2.9.1/scala-library-2.9.1.jar:" +
      System.getProperty("karaf.home") + "/system/org/atricore/idbus/kernel/authz/org.atricore.idbus.kernel.authz.core/1.4.0-SNAPSHOT/org.atricore.idbus.kernel.authz.core-1.4.0-SNAPSHOT.jar"

    precompiler.execute
  }

  def close() {}

  /**
  def componentizePath(path : String) =
  {
    val parts = new ListBuffer[String]();

    var index = 0;
    while(index < path.length())
    {
      if(path.charAt(index) == '/' || index == path.length()-1)
      {
        parts.add(path.substring(0, index+1));
      }
      index++;
    }

    parts.toArray;
  }
   */

}
