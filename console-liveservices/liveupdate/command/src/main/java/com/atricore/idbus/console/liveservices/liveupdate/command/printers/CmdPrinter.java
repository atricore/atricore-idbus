package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

import com.atricore.idbus.console.liveservices.liveupdate.command.LiveUpdateCommandSupport;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CmdPrinter<T> {

    void print(LiveUpdateCommandSupport cmd, T o, Map<String, Object> options);

    void print(LiveUpdateCommandSupport cmd, T o);

    void printAll(LiveUpdateCommandSupport cmd, Collection<T> os);

    void printError(LiveUpdateCommandSupport cmd, Exception e);

}