package org.atricore.idbus.kernel.authz.config.impl

import collection.mutable.ListBuffer
import org.atricore.idbus.kernel.authz.config.PolicyConfig
import org.atricore.idbus.kernel.authz.support.Precompiler
import scala.Array._
import java.io.File

class AuthorizationConfiguration {
  private val policies = new ListBuffer[PolicyConfig]

  def init() {
    val workingDirectory : String = System.getProperty("karaf.home")
    val precompiler = new Precompiler()

    val policyFiles = policies.map( p => p.getPolicyResource().getFile).toArray
    
    precompiler.sources = policyFiles
    precompiler.workingDirectory = new File(workingDirectory + "/data/work/authz")
    precompiler.targetDirectory = new File(workingDirectory + "/data/work/authz")
    precompiler.execute
  }

  def register(policy: PolicyConfig, properties: java.util.Map[String, _]) = {
    if (policy != null) {
      policies += policy;
    }
  }

  def unregister(policy: PolicyConfig, properties: java.util.Map[String, _]) = {
    if (policy != null) {
      policies -= policy;
    }
  }

  def close() {}

}
