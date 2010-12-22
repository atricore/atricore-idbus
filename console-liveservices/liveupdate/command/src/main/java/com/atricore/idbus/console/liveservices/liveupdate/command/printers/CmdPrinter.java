package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

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