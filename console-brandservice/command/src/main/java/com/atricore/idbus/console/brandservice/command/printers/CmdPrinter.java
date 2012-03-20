package com.atricore.idbus.console.brandservice.command.printers;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CmdPrinter<T> {

    void print(T o, Map<String, Object> options);

    void print(T o);

    void printAll(Collection<T> os);

    void printError(Exception e);

}
