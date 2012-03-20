package com.atricore.idbus.console.brandservice.command.printers;

import com.atricore.idbus.console.brandservice.main.BrandingDefinition;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingDefinitionCmdPrinter extends AbstractCmdPrinter<BrandingDefinition> {

    public void print(BrandingDefinition def) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[1m  ID      Name           Type        Description\u001B[0m\n");
        printDetails(def, sb, true);
        getOut().println(sb);
    }

    public void printAll(Collection<BrandingDefinition> os) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[1m  ID      Name           Type        Description\u001B[0m\n");
        for (BrandingDefinition def : os) {
            printDetails(def, sb, false);
        }
        getOut().println(sb);
    }

    protected void printDetails(BrandingDefinition def, StringBuilder sb, boolean verbose) {
        // System out ?
        sb.append("[");
        sb.append(getIdString(def));
        sb.append("]  [");
        sb.append(getNameString(def));
        sb.append("]  [");
        sb.append(getTypeString(def));
        sb.append("]");
        sb.append("\n");
    }


}
