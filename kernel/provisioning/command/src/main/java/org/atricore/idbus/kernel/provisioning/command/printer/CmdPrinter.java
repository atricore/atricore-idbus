package org.atricore.idbus.kernel.provisioning.command.printer;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 *
 */
public interface CmdPrinter {

    void printMsg(String msg);

    void printErrMsg(String errMsg);

    void printUser(User user);
}
