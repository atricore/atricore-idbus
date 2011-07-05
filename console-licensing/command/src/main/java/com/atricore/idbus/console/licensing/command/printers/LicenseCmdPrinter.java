package com.atricore.idbus.console.licensing.command.printers;

import com.atricore.josso2.licensing._1_0.license.FeaturePropertyType;
import com.atricore.josso2.licensing._1_0.license.FeatureType;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.atricore.josso2.licensing._1_0.license.LicensedFeatureType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseCmdPrinter extends AbstractCmdPrinter<LicenseType> {

    private String featureStr;

    public void print(LicenseType license){
        print(license, false);
    }

    public void print(LicenseType licenseType, boolean verbose) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001B[1m  ID                   Name               Version   Issue Instant    Expires On        License Owner                  Organization Name\u001B[0m\n");
//        sb.append("\n");

        printDetails(licenseType, sb, verbose);
        getOut().println(sb);
    }

    public void printError(Exception e) {
        getErr().println(e.getMessage());
    }

    protected void printDetails(LicenseType license, StringBuilder sb, boolean verbose) {
        // System out ?
//        IdentityApplianceDefinition applianceDef = license.getIdApplianceDefinition();
//        IdentityApplianceDeployment applianceDep = license.getIdApplianceDeployment();

        sb.append("[");
        sb.append(getIdString(license));
        sb.append("]  [");
        sb.append(getNameString(license));
        sb.append("]  [");
        sb.append(getVersionString(license));
        sb.append("]   [");
        sb.append(getIssueInstantDateString(license));
        sb.append("]    [");
        sb.append(getExpiresOnDateString(license));
        sb.append("]  [");
        sb.append(getLicenseOwnerString(license));
        sb.append("]  [");
        sb.append(getOrganizationNameString(license));
        sb.append("] ");

        if (verbose) {
            //show eula
            sb.append("\n");
            sb.append(getEulaString(license));
        }

        //show features
        sb.append("\n\n");
        sb.append("\u001B[1m  Name           Description                                          Version Range   Issue Instant   Expires On\u001B[0m");
        if(license.getLicensedFeature() != null){
            for(LicensedFeatureType licFeature : license.getLicensedFeature()){
                for(FeatureType feature : licFeature.getFeature()){
                    if(featureStr == null || (featureStr.equals(feature.getName()))){
                        printFeature(feature, sb, verbose);
                    }
                }
            }

        }

        sb.append("\n");
    }

    protected void printFeature(FeatureType feature, StringBuilder sb, boolean verbose){
        sb.append("\n");
        sb.append("[");
        sb.append(getNameString(feature));
        sb.append("]  [");
        sb.append(getDescriptionString(feature));
        sb.append("]  ");
        sb.append(getVersionRangeString(feature));

        if(verbose && featureStr != null && featureStr.equals(feature.getName())){ //print license Text
            sb.append("\n");
            sb.append(getLicenseTextString(feature));
        }

        sb.append("   ");
        sb.append(getIssueInstantString(feature));
        sb.append("       ");

        sb.append(getExpiresOnDateString(feature));

        // Print out properties

        if (feature.getFeatureProperty() != null && feature.getFeatureProperty().size() > 0) {

            sb.append(" Properties : [");
            for (FeaturePropertyType prop : feature.getFeatureProperty()) {
                sb.append(prop.getName());
                sb.append("=");
                sb.append(prop.getValue());
                sb.append(",");
            }
            sb.append("]");
        }



    }


    protected String getVersionString(LicenseType license) {
        String v = license.getVersion();
        while (v.length() < 7) {
            v = " " + v;
        }
        return v;
    }

    public String getFeatureStr() {
        return featureStr;
    }

    public void setFeatureStr(String featureStr) {
        this.featureStr = featureStr;
    }
}
