package org.atricore.idbus.kernel.authz.config

import org.springframework.core.io.Resource

import scala.beans.BeanProperty

class PolicyConfigImpl extends PolicyConfig {

  @BeanProperty var policyResource: Resource = _

}
