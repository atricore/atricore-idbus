package com.atricore.idbus.console.lifecycle.command;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ListIdentityAppliancesRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ListIdentityAppliancesResponse;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@Command(scope = "appliance", name = "list", description = "List defined identity appliances")
public class ListAppliancesCommand extends ManagementCommandSupport {

    @Option(name = "-s", aliases = "--state", description = "List appliances for the specified states", required = false, multiValued = true)
    List<String> states;

    @Override
    protected Object doExecute(IdentityApplianceManagementService svc) throws Exception {

        // TODO : Implement list by state

        ListIdentityAppliancesRequest req = new ListIdentityAppliancesRequest ();
        ListIdentityAppliancesResponse res  = svc.listIdentityAppliances(req);

        StringBuilder sb = new StringBuilder();

        // Build headers line

        sb.append("  ID      Name           State       Revision        Display Name\n");
        sb.append("                                   Last/Deployed\n");

        for (IdentityAppliance appliance : res.getIdentityAppliances()) {
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

        System.out.println(sb);

        return null;
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
                sb.append(":");
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
                sb.append(":");
                sb.append(ex.getInstallUri());
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
                sb.append(":");
                sb.append(is.getClass().getSimpleName());

            }

        }
    }

    protected void printApplianceDeployment(StringBuilder sb, IdentityApplianceDeployment applianceDep) {
        sb.append("\n");
        sb.append("     Deployed : (Rev. ");
        sb.append(getRevisionString(applianceDep.getDeployedRevision()));
        sb.append(")");

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

            if (p instanceof ServiceProvider ) {
                ServiceProvider sp = (ServiceProvider) p;
                if (sp.getActivation() != null) {

                    if (sp.getActivation().isActivated()) {
                        sb.append(" (Active:");
                        sb.append(sp.getActivation().getExecutionEnv().getInstallUri());
                        sb.append(")");
                    }

                }
            }
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
        if (revision > 99)
            return "" + revision;

        if (revision > 9)
            return "0" + revision;

        return "00" + revision;
    }

    protected String getVersionString(IdentityApplianceUnit idau) {
        String v = idau.getVersion();
        while (v.length() < 7) {
            v = " " + v;
        }
        return v;
    }

}
