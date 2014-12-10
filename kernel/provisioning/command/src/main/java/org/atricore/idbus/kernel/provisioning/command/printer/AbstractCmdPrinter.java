package org.atricore.idbus.kernel.provisioning.command.printer;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.io.PrintStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractCmdPrinter implements CmdPrinter {

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

    public void printMsg(String msg) {
        out.println(msg);
    }

    @Override
    public void printUser(User user) {

    }

    public void printErrMsg(String errMsg) {
        err.println("\u001B[31m" + errMsg + "\u001B[0m");
    }

    protected void printMsg(StringBuilder sb) {
        printMsg(sb.toString());
    }

    protected void printErrMsg(StringBuilder sb) {
        printErrMsg(sb.toString());
    }

    protected String getLabelString(String label) {
        return getLabelString(label, 16);
    }


    protected String getLabelString(String label, int size) {
        label = getLeftString(label, size);
        label += ": ";
        return "\u001B[1m" + label + "\u001B[0m";
    }

    protected String getLeftString(String str, int padding) {
        String text = str;
        if (text == null)
            text = "--";

        while (text.length() < padding - 1) {
            text += " ";
        }

        return text;

    }

    protected String getRightString(String str, int padding) {
        String text = str;
        if (text == null)
            text = "--";

        while (text.length() < padding - 1) {
            text = " " + text;
        }

        return text;

    }




}


