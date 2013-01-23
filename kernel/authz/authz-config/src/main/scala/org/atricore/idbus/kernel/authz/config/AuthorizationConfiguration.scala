package org.atricore.idbus.kernel.authz.config

import org.atricore.idbus.kernel.authz.core.AuthorizationEngine


trait AuthorizationConfiguration {

  def policies : List[PolicyConfig]

  def engine : AuthorizationEngine
}
