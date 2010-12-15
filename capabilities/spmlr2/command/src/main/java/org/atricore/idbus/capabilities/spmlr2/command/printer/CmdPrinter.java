package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CmdPrinter {

    void printMsg(String msg);

    void printResponse(ResponseType response);

    void printRequest(RequestType request);

    void printOutcome(Object outcome);

    void printErrMsg(String errMsg);
}
