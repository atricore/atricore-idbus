package com.atricore.idbus.console.liveservices.liveupdate.command.printers;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */

import com.atricore.idbus.console.liveservices.liveupdate.command.LiveUpdateCommandSupport;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractCmdPrinter<T> implements CmdPrinter<T> {

    private PrintStream out  = System.out;

    private PrintStream err = System.err;

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public PrintStream getErr() {
        return err;
    }

    public void setErr(PrintStream err) {
        this.err = err;
    }

    public void print(LiveUpdateCommandSupport cmd, T o, Map<String, Object> options) {
        print(cmd, o);
    }

    public void printAll(LiveUpdateCommandSupport cmd, Collection<T> os) {
        for (T o : os) {
            print(cmd, o);
        }

    }

    public void printError(LiveUpdateCommandSupport cmd, Exception e) {
        System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
    }

    protected String getNameString(String n) {
        return getNameString(n, 12);
    }

    protected String getEnabledString(boolean e) {
        return e ? "Enabled " : "Disabled";
    }


    protected String getIdString(int i) {
        return getIdString(i + "");
    }

    protected String getIdString(String  id) {
        while (id.length() < 4) {
            id = " " + id;
        }
        return id;
    }

    protected String getNameString(String n, int length) {
        String name = n;
        while (name.length() < length) {
            name = name + " ";
        }
        return name;
    }

    protected String getRevisionString(int revision) {
        return getIntegerString(revision, 2);
    }

    protected String getIntegerString(int value, int length) {

        String strRevision = value + "";

        while (strRevision.length() < length) {
            strRevision = "0" + strRevision;
        }

        return strRevision;
    }

    protected String getNameValue(String name, String value) {
        return "\u001B[1m" + getNameString("  " + name, 24) + "\u001B[0m" + value + "\n";
    }


}
