package org.atricore.idbus.kernel.authz.command

import org.apache.karaf.shell.console.OsgiCommandSupport
import reflect.BeanProperty
import org.atricore.idbus.kernel.authz.config.AuthorizationConfiguration


class AuthzCommandSupport extends OsgiCommandSupport {
  protected val AUTHZ_CMDS = "AuthzCommand.COMMANDS";

  @BeanProperty
  var authorizationConfiguration : AuthorizationConfiguration = _

  /**
   * Add the command to the command queue.
   *
   * @return
   * @throws Exception
   */
  protected def doExecute: AnyRef = {
    val commandQueue = session.get(AUTHZ_CMDS)
//    if (commandQueue != null) {
//      commandQueue.add(this)
//    }

    null
  }




}
