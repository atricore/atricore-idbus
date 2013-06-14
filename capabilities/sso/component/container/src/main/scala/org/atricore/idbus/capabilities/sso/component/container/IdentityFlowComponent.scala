package org.atricore.idbus.capabilities.sso.component.container

import org.atricore.idbus.capabilities.sso.dsl.core._
import directives.IdentityFlowDirectives

trait IdentityFlowComponent extends IdentityFlowDirectives {

  val name : String

  def route : IdentityFlowRoute


}
