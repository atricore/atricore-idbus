package com.atricore.idbus.console.lifecycle.command.printers;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CmdPrinter<T> {

    void print(T o);

    void printAll(Collection<T> os);
}
