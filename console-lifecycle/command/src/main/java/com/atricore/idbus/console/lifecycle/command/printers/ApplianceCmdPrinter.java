package com.atricore.idbus.console.lifecycle.command.printers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.impl.ValidationError;

import java.util.Collection;


/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceCmdPrinter extends AbstractCmdPrinter<IdentityAppliance> {

    public void print(IdentityAppliance appliance) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ID      Name           State       Revision        Display Name\n");
        sb.append("                                   Last/Deployed\n");

        printDetails(appliance, sb, true);
        getOut().println(sb);
    }

    public void printAll(Collection<IdentityAppliance> os) {

        StringBuilder sb = new StringBuilder();
        // Build headers line
        sb.append("\u001B[1m  ID      Name           State       Revision        Display Name\u001B[0m\n");
        sb.append("                                   Last/Deployed\n");

        for (IdentityAppliance appliance : os) {
            printDetails(appliance, sb, false);
        }

        getOut().println(sb);
    }

    public void printError(Exception e) {

        StringBuilder sb = new StringBuilder();


        if (e instanceof ApplianceValidationException) {

            int i = 0;
            sb.append("\u001B[1m ").append(((ApplianceValidationException) e).getErrors().size()).append(" Appliance Validation Errors \u001B[0m\n");
            sb.append("  -------------------------------------\n");

            for (ValidationError err : ((ApplianceValidationException)e).getErrors()) {

                sb.append("\u001B[1m");
                sb.append(getIntegerString(i, 2));
                sb.append(") \u001B[0m");
                sb.append("\u001B[31m").append(err.getMsg()).append("\u001B[0m");
                if (err.getError() != null) {
                    Throwable cause = err.getError();
                    while (cause.getCause() != null)
                        cause = cause.getCause();

                    sb.append(" Cause:").
                            append(cause.getClass().getName()).
                            append(cause.getMessage() != null ? ":" + cause.getMessage() : "");
                    
                }
                sb.append("\n");
                i++;
            }

            getErr().println(sb);
            return;
        }
        getErr().println(e.getMessage());
    }

    protected void printDetails(IdentityAppliance appliance, StringBuilder sb, boolean verbose) {
        // System out ?
        IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
        IdentityApplianceDeployment applianceDep = appliance.getIdApplianceDeployment();

        // TODO : Build a line, using proper format and information (id, description, state, version, ... ?).
        // TODO : padd ids and states!
        sb.append("[");
        sb.append(getIdString(appliance));
        sb.append("]  [");
        sb.append(getNameString(appliance));
        sb.append("]  [");
        sb.append(getStateString(appliance));
        sb.append("]    [");
        sb.append(getRevisionString(applianceDef.getRevision()));
        sb.append("/");
        if (applianceDep != null) {
            sb.append(getRevisionString(applianceDep.getDeployedRevision()));
        } else {
            sb.append("  -");
        }
        sb.append("]    ");
        sb.append(applianceDef.getDisplayName());

        if (verbose) {

            sb.append("\n");
            printApplianceDefinition(sb, applianceDef);

            if (applianceDep != null) {
                sb.append("\n");
                printApplianceDeployment(sb, applianceDep);
            }
        }

        sb.append("\n");
    }

    protected void printApplianceDefinition(StringBuilder sb, IdentityApplianceDefinition applianceDef) {
        sb.append("\n");
        sb.append("     Defined  : (Rev. ");
        sb.append(getRevisionString(applianceDef.getRevision()));
        sb.append(")");

        if (applianceDef.getProviders() != null) {
            sb.append("\n");
            sb.append("       Providers  : ");
            for (Provider p : applianceDef.getProviders()) {
                sb.append("\n");
                sb.append("          ");
                sb.append(getNameString(p));
                sb.append(getLocationString(p));
            }
        }

        if (applianceDef.getExecutionEnvironments() != null) {
            sb.append("\n");
            sb.append("       Exec. Envs : ");
            for (ExecutionEnvironment ex : applianceDef.getExecutionEnvironments()) {
                // TODO : Create printExecEnv method
                sb.append("\n");
                sb.append("          ");
                sb.append(getNameString(ex));
                sb.append(ex.getInstallUri());
                if (ex.isActive()) {
                    sb.append(" (");
                    sb.append("activated");
                    sb.append(")");
                }
            }
        }

        if (applianceDef.getIdentitySources() != null) {
            sb.append("\n");
            sb.append("       ID Sources : ");
            for (IdentitySource is : applianceDef.getIdentitySources()) {
                // TODO : Create printIdsource method
                sb.append("\n");
                sb.append("          ");
                sb.append(getNameString(is));
                sb.append(is.getClass().getSimpleName());

            }

        }
    }

    protected void printApplianceDeployment(StringBuilder sb, IdentityApplianceDeployment applianceDep) {
        sb.append("\n");
        sb.append("     Deployed : (Rev. ");
        sb.append(getRevisionString(applianceDep.getDeployedRevision()));
        sb.append(") at ");
        sb.append(applianceDep.getDeploymentTime().toString());

        for (IdentityApplianceUnit idau : applianceDep.getIdaus()) {
            printIdauString(sb, idau);
        }
    }

    protected void printIdauString(StringBuilder sb, IdentityApplianceUnit idau) {
        sb.append("\n");
        sb.append("        ");
        sb.append(getNameString(idau));
        sb.append(" ");
        sb.append(getVersionString(idau));
        sb.append(" (");
        sb.append(idau.getType());
        sb.append(")");

        for (Provider p : idau.getProviders()) {
            sb.append("\n");
            sb.append("          ");
            sb.append(getNameString(p));
            sb.append(":");
            sb.append(getLocationString(p));
        }

    }


    protected String getStateString(IdentityAppliance appliance) {

        String state = appliance.getState();
        while (state.length() < 10) {
            state = state + " ";
        }
        return state;

    }

    protected String getNameString(IdentityAppliance appliance) {
        String name = appliance.getIdApplianceDefinition().getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }

    protected String getNameString(IdentitySource is) {
        String name = is.getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }


    protected String getNameString(Provider p) {
        String name = p.getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
    }

    protected String getNameString(ExecutionEnvironment ex) {
        String name = ex.getName();
        while (name.length() < 12) {
            name = name + " ";
        }
        return name;
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

    protected String getVersionString(IdentityApplianceUnit idau) {
        String v = idau.getVersion();
        while (v.length() < 7) {
            v = " " + v;
        }
        return v;
    }



}
