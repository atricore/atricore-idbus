package org.atricore.idbus.kernel.authz.config

import org.springframework.core.io.Resource

trait PolicyConfig {

  def getPolicyResource() : Resource

  def setPolicyResource(r : Resource)

}
