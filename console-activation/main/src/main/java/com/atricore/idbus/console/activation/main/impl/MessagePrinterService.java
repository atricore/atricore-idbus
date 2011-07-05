package com.atricore.idbus.console.activation.main.impl;

import org.apache.geronimo.gshell.command.IO;
import org.josso.tooling.gshell.core.support.MessagePrinter;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MessagePrinterService extends MessagePrinter {

    public MessagePrinterService() {
        super(new IO(new java.io.ByteArrayInputStream(new byte[0]), System.out, System.err, true));
    }

}