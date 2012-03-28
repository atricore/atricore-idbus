package com.atricore.idbus.console.brandservice.command.printers;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;

import java.io.PrintStream;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
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

    public void print(T o, Map<String, Object> options) {
        print(o);
    }

    public void printError(Exception e) {
        System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
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

    protected String getNameString(BrandingDefinition def) {
        String name = def.getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }

    protected String getTypeString(BrandingDefinition def) {
        String name = def.getClass().getSimpleName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }


    protected String getIdString(BrandingDefinition def) {
        String id = def.getId() + "";
        while (id.length() < 4) {
            id = " " + id;
        }
        return id;
    }



}
