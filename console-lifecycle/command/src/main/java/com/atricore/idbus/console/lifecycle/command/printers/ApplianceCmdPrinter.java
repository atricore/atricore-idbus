package com.atricore.idbus.console.lifecycle.command.printers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.impl.ValidationError;

import java.util.Collection;
import java.util.Map;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceCmdPrinter extends AbstractCmdPrinter<IdentityAppliance> {

    public void print(IdentityAppliance appliance) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[1m  ID      Name           State       Revision        Display Name\u001B[0m\n");
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
            sb.append("\u001B[1m ");
            sb.append(" Appliance Validation Errors found : \u001B[0m\n");
            sb.append(((ApplianceValidationException) e).getErrors().size());
            sb.append("  -------------------------------------\n");

            for (ValidationError err : ((ApplianceValidationException)e).getErrors()) {

                sb.append("\u001B[1m");
                sb.append(getIntegerString(i+1, 2));
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
            printApplianceDefinition(sb, applianceDef, appliance.getNamespace());

            if (applianceDep != null) {
                sb.append("\n");
                printApplianceDeployment(sb, applianceDep);
            }
        }

        sb.append("\n");
    }

    protected void printApplianceDefinition(StringBuilder sb, IdentityApplianceDefinition applianceDef, String namespace) {

        sb.append("\u001B[1m  Defined   \u001B[0m");
        sb.append("\n");
        sb.append("\u001B[1m    Revision   : \u001B[0m");
        sb.append(getRevisionString(applianceDef.getRevision()));
        sb.append("\n");

        sb.append("\u001B[1m    Namespace  : \u001B[0m");
        sb.append(namespace);
        sb.append("\n");

        sb.append("\u001B[1m    Location   : \u001B[0m");
        sb.append(getLocationString(applianceDef.getLocation()));
        sb.append("\n");


        if (applianceDef.getProviders() != null) {
            sb.append("\u001B[1m    Providers  : \u001B[0m");
            sb.append(applianceDef.getProviders().size());
            sb.append("\n");

            for (Provider p : applianceDef.getProviders()) {
                sb.append("          ");
                sb.append(getNameString(p));
                sb.append(getLocationString(p));

                if (p instanceof ServiceProvider) {
                    ServiceProvider sp = (ServiceProvider) p;
                    if (sp.getActivation() != null ) {

                        ExecutionEnvironment execEnv = sp.getActivation().getExecutionEnv();
                        sb.append("\n");
                        sb.append("            ");
                        sb.append(getNameString(execEnv.getName(), 10));

                        if (sp.getActivation() instanceof JOSSOActivation) {

                            JOSSOActivation jossoActivation = (JOSSOActivation) sp.getActivation();
                            sb.append(getNameString(jossoActivation.getPartnerAppId()));
                            sb.append(" JOSSO ");
                            sb.append(execEnv.getPlatformId());
                            sb.append(" ");
                            sb.append(getLocationString(jossoActivation.getPartnerAppLocation()));
                            sb.append(" [");
                            sb.append(execEnv.isActive() ? "\u001B[32mACTIVATED\u001B[0m" : "\u001B[31mNOT ACTIVATED\u001B[0m");
                            sb.append("] ");

                        } else {
                            sb.append(execEnv.getPlatformId());
                            sb.append(" [");
                            sb.append(execEnv.isActive() ? "\u001B[32mACTIVATED\u001B[0m" : "\u001B[31mNOT ACTIVATED\u001B[0m");
                            sb.append("]");
                        }
                    }

                } else if (p instanceof IdentityProvider) {
                    IdentityProvider idp = (IdentityProvider) p;
                    if (idp.getIdentityLookup() != null) {
                        IdentityLookup idl = idp.getIdentityLookup();
                        sb.append("\n");
                        sb.append("            ");
                        sb.append(getNameString(idl.getIdentitySource().getName(), 10));
                    }
                }
                sb.append("\n");
            }
        }

        if (applianceDef.getExecutionEnvironments() != null) {
            sb.append("\u001B[1m    Exec. Envs : \u001B[0m");
            sb.append(applianceDef.getExecutionEnvironments().size());
            sb.append("\n");

            for (ExecutionEnvironment ex : applianceDef.getExecutionEnvironments()) {
                // TODO : Create printExecEnv method
                sb.append("          ");
                sb.append(getNameString(ex));
                sb.append("[");
                sb.append(ex.getPlatformId());
                sb.append("]");
                sb.append(ex.getInstallUri());
                if (ex.isActive()) {
                    sb.append(" [");
                    sb.append("\u001B[32mACTIVATED\u001B[0m");
                    sb.append("]");
                }
                sb.append("\n");

            }
        }

        if (applianceDef.getIdentitySources() != null) {
            sb.append("\u001B[1m    ID Sources : \u001B[0m");
            sb.append(applianceDef.getIdentitySources().size());
            sb.append("\n");
            for (IdentitySource is : applianceDef.getIdentitySources()) {
                sb.append("          ");
                sb.append(getNameString(is));
                sb.append(is.getClass().getSimpleName());
                sb.append("\n");

            }

        }
    }

    protected void printApplianceDeployment(StringBuilder sb, IdentityApplianceDeployment applianceDep) {

        sb.append("\u001B[1m  Deployed  \u001B[0m");
        sb.append("\n");

        sb.append("\u001B[1m    Revision   : \u001B[0m");
        sb.append(getRevisionString(applianceDep.getDeployedRevision()));
        sb.append("\n");

        sb.append("\u001B[1m    Time       : \u001B[0m");
        if (applianceDep.getDeploymentTime() != null) {
            sb.append(applianceDep.getDeploymentTime().toString());
        }
        sb.append("\n");

        sb.append("\u001B[1m    Units      : \u001B[0m");
        sb.append(applianceDep.getIdaus().size());
        sb.append("\n");

        for (IdentityApplianceUnit idau : applianceDep.getIdaus()) {
            printIdauString(sb, idau);
        }
    }

    protected void printIdauString(StringBuilder sb, IdentityApplianceUnit idau) {

        sb.append("\u001B[1m    Unit       : \u001B[0m");
        sb.append(getNameString(idau));
        sb.append(" ");
        sb.append(getVersionString(idau));
        sb.append(" (");
        sb.append(idau.getType());
        sb.append(")");
        sb.append("\n");

        sb.append("\u001B[1m      Providers: \u001B[0m");
        sb.append(idau.getProviders().size());
        sb.append("\n");

        for (Provider p : idau.getProviders()) {
            sb.append("          ");
            sb.append(getNameString(p));
            sb.append(getLocationString(p));
            sb.append("\n");
        }

    }


    protected String getStateString(IdentityAppliance appliance) {
        return getNameString(appliance.getState(), 10);
    }


    protected String getVersionString(IdentityApplianceUnit idau) {
        String v = idau.getVersion();
        while (v.length() < 7) {
            v = " " + v;
        }
        return v;
    }



}
