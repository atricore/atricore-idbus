package org.atricore.idbus.capabilities.spmlr2.command.printer;

import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;
import oasis.names.tc.spml._2._0.StatusCodeType;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
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

    public void printResponse(ResponseType response) {
        if (response.getStatus().equals(StatusCodeType.SUCCESS)) {
            printMsg("SPML " + response.getClass().getSimpleName() + " ("+response.getRequestID()+")" + "=" +
                    response.getStatus().toString());
        } else {
            printErrMsg("SPML " + response.getClass().getSimpleName() + " ("+response.getRequestID()+")" + "=" +
                    response.getStatus().toString());
        }
    }

    public void printRequest(RequestType request) {
        printMsg("SPML " + request.getClass().getSimpleName() + " ("+ request.getRequestID()+")");
    }

    public void printOutcome(Object outcome) {

    }

    public void printMsg(String msg) {
        out.println(msg);
    }

    public void printErrMsg(String errMsg) {
        err.println(errMsg);
    }

    protected void printMsg(StringBuilder sb) {
        printMsg(sb.toString());
    }

    protected void printErrMsg(StringBuilder sb) {
        printErrMsg(sb.toString());
    }

    protected String getPsoIDString(PSOIdentifierType psoId) {
        return getRightString(psoId.getID(), 12);
    }

    protected String getLabelString(String label) {
        return getLabelString(label, 16);
    }


    protected String getLabelString(String label, int size) {
        label = getLeftString(label, size);
        label += ": ";
        return label;
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


