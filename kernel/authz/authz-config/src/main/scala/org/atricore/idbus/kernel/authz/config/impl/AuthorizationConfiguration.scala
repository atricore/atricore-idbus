package org.atricore.idbus.kernel.authz.config.impl

import collection.mutable.ListBuffer
import org.atricore.idbus.kernel.authz.config.PolicyConfig
import scala.Array._
import java.io.File
import org.apache.commons.logging.{LogFactory, Log}
import org.atricore.idbus.kernel.authz.core.support.Precompiler

class AuthorizationConfiguration {
  private[this] val log: Log = LogFactory.getLog( this.getClass )

  private val policies = new ListBuffer[PolicyConfig]

  def init() {
    log.debug("Precompiling [" + policies.size + "] Authorization Policies");

    val workingDirectory = System.getProperty("karaf.home")
    val precompiler = new Precompiler()

    val policyFiles = policies.map( p => p.getPolicyResource().getFile).toArray
    
    precompiler.sources = policyFiles
    precompiler.workingDirectory = new File(workingDirectory + "/data/work/authz")
    precompiler.targetDirectory = new File(workingDirectory + "/data/work/authz")
    precompiler.execute
  }

  def register(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      policies += policy;
      log.debug("Registered Authorization Policy [" + policy.getPolicyResource().getFilename + "]")
    }
  }

  def unregister(policy: PolicyConfig, properties: java.util.Map[String, _]) {
    if (policy != null) {
      policies -= policy;
      log.debug("Unregistered Authorization Policy [" + policy.getPolicyResource().getFilename + "]")
    }
  }

  def close() {}

}
