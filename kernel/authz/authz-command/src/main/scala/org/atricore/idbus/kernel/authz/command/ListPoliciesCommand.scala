package org.atricore.idbus.kernel.authz.command

import org.apache.felix.gogo.commands.Command

@Command( scope = "authz", name = "policy-list", description = "List Authorization Policies")
class ListPoliciesCommand extends AuthzCommandSupport {

  override protected def doExecute = {
    val authzConfig = getAuthorizationConfiguration()

    authzConfig.policies.foreach( p => println (p.getPolicyResource))
    null
  }
}
