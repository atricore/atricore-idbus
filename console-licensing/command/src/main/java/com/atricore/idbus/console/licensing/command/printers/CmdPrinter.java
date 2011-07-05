package com.atricore.idbus.console.licensing.command.printers;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CmdPrinter<T> {

    void print(T o, Map<String, Object> options);

    void print(T o, boolean verbose);

    void print(T o);

    void printError(Exception e);

}
