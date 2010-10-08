package com.atricore.idbus.console.lifecycle.command.printers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ExecutionEnvironment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Location;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Provider;

import java.io.PrintStream;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
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

    public void printError(Exception e) {
        System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
    }

    protected String getNameString(IdentityAppliance appliance) {
        return getNameString(appliance.getIdApplianceDefinition().getName());
    }

    protected String getNameString(IdentitySource is) {
        return getNameString(is.getName());
    }


    protected String getNameString(Provider p) {
        return getNameString(p.getName());
    }

    protected String getNameString(String n) {
        return getNameString(n, 12);
    }

    protected String getNameString(String n, int length) {
        String name = n;
        while (name.length() < length) {
            name = name + " ";
        }
        return name;
    }

    protected String getNameString(ExecutionEnvironment ex) {
        return getNameString(ex.getName());
    }

    protected String getDisplayNameString(ExecutionEnvironment ex) {

        String displayName = ex.getDisplayName();
        if (displayName == null) {
            displayName = "";
        }

        while (displayName.length() < 12) {
            displayName = displayName + " ";
        }
        return displayName;
    }

    protected String getDisplayNameString(Provider provider) {

        String displayName = provider.getDisplayName();
        if (displayName == null) {
            displayName = "";
        }

        while (displayName.length() < 12) {
            displayName = displayName + " ";
        }
        return displayName;
    }


    protected String getLocationString(Provider provider) {

        Location pl = provider.getLocation();
        Location al = provider.getIdentityAppliance().getLocation();

        String location = "";

        if (pl != null)
            location = getLocationString(pl);

        if (!"".equals(location) && !location.startsWith("/"))
            return location;

        if (al != null)
            location = getLocationString(al) + location;

        return location;
    }

    protected String getLocationString(Location location) {

        if (location == null) {
            return "";
        }

        String contextString = "";
        if (location.getContext() != null) {
            contextString = (location.getContext().startsWith("/") ? location.getContext().substring(1) : location.getContext());
            contextString = (contextString.endsWith("/") ? contextString.substring(0, contextString.length() - 1) : contextString);
            contextString = "/" + contextString;
        }


        String uriString = "";
        if (location.getUri() != null) {
            uriString = "/" +
            (location.getUri() != null ? location.getUri() : "");

            if (uriString.startsWith("//"))
                uriString = uriString.substring(1);
        }

        return  getBaseLocationString(location) + contextString + uriString;
    }

    protected String getBaseLocationString(Location location) {

        if (location == null) {
            return "";
        }

        String portString = "";
        if (location.getPort() > 0)
            portString = location.getPort() + "";

        String protocolString = "";
        if (location.getProtocol() != null) {
            protocolString = location.getProtocol() + "://";
            // For HTTP, remove default ports
            if (location.getProtocol().equalsIgnoreCase("http"))
                portString = (location.getPort() == 80 ? "" :  ":" + location.getPort());
            if (location.getProtocol().equalsIgnoreCase("https"))
                portString = (location.getPort() == 443 ? "" :  ":" + location.getPort());
        }

        String hostString = "";
        if (location.getHost() != null)
            hostString = location.getHost();

        return protocolString  + hostString + portString;
    }



    protected String getNameString(IdentityApplianceUnit idau) {
        String name = idau.getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }

    protected String getIdString(IdentityAppliance appliance) {
        String id = appliance.getId() + "";
        while (id.length() < 4) {
            id = " " + id;
        }
        return id;
    }

    protected String getIdString(Provider p) {
        String id = p.getId() + "";
        while (id.length() < 4) {
            id = " " + id;
        }
        return id;
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
    

}
