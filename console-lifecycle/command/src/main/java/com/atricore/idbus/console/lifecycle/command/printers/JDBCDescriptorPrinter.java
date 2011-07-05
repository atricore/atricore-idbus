package com.atricore.idbus.console.lifecycle.command.printers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.JDBCDriverDescriptor;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDBCDescriptorPrinter extends AbstractCmdPrinter<JDBCDriverDescriptor> {

    public void print(JDBCDriverDescriptor d) {
        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m  Driver Class                          Description                       Website\u001B[0m\n");
        sb.append(rightPad(d.getClassName(), 64));
        sb.append(rightPad(d.getName(), 40));
        if (d.getWebSiteUrl() != null)
            sb.append(d.getWebSiteUrl());

        sb.append("\n");
        
        getOut().println(sb);

    }

    public void printAll(Collection<JDBCDriverDescriptor> ds) {
        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m  Driver Class                          Description                       Website\u001B[0m\n");

        for (JDBCDriverDescriptor d : ds) {
            sb.append(rightPad(d.getClassName(), 40));
            sb.append(rightPad(d.getName(), 32));
            
            if (d.getWebSiteUrl() != null)
                sb.append(d.getWebSiteUrl());

            sb.append("\n");
        }

        getOut().println(sb);

    }

    protected String leftPad(String str, int padding) {
        String v = str;
        while (v.length() < padding) {
            v = " " + v;
        }
        return v;
    }

    protected String rightPad(String str, int padding) {
        String v = str;
        while (v.length() < padding) {
            v = v + " " ;
        }
        return v;
    }

}
