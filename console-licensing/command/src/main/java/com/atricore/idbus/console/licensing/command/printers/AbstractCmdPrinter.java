package com.atricore.idbus.console.licensing.command.printers;

import com.atricore.josso2.licensing._1_0.license.*;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    protected String getNameString(LicenseType license) {
        return getNameString(license.getLicenseName(), 20);
    }

    protected String getNameString(FeatureType feature) {
        return getNameString(feature.getName());
    }


    protected String getNameString(FeaturePropertyType featureProperty) {
        return getNameString(featureProperty.getName());
    }

    protected String getNameString(OrganizationType organization) {
        return getNameString(organization.getOrganizationName());
    }

    protected String getNameString(String n) {
        return getNameString(n, 12);
    }

    protected String getNameString(String n, int length) {
        String name = (n == null ? "" : n );        
        while (name.length() < length) {
            name = name + " ";
        }
        return name;
    }

    protected String getIdString(LicenseType license) {
        String id = license.getID();
        while (id.length() < 4) {
            id = " " + id;
        }
        return id;
    }

    protected String getDateString(LicenseType license){
        String dateString = "";
        if(license.getIssueInstant() != null){
            dateString = getDateString(license.getIssueInstant().getTime());
        }
        return dateString;
    }

    protected String getExpirationDateString(FeatureType licFeature){
        String dateString = "";
        if(licFeature.getExpiresOn() != null){
            dateString = getDateString(licFeature.getExpiresOn().getTime());
        }
        return dateString;
    }

    protected String getIssueInstantString(FeatureType licFeature){
        String dateString = "";
        if(licFeature.getIssueInstant() != null){
            dateString = getDateString(licFeature.getIssueInstant().getTime());
        }
        return dateString;
    }


    protected String getDateString(Date date){
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yy", Locale.US);
        return formatter.format(date);        
    }

    protected String getLicenseOwnerString(LicenseType license){
        String owner = "";
        if(license.getOrganization() != null){
            owner = license.getOrganization().getOwner();
        }
        return getNameString(owner, 30);
    }

    protected String getOrganizationNameString(LicenseType license){
        String orgName = "";
        if(license.getOrganization() != null){
            orgName = license.getOrganization().getOrganizationName();
        }
        return getNameString(orgName, 30);
    }

    protected String getEulaString(LicenseType license){
        return license.getEULA();
    }

    protected String getEulaString(FeatureType feature){
        return feature.getEULA();
    }

    protected String getLicenseTextString(FeatureType feature){
        return feature.getLicenseText();
    }

    protected String getDescriptionString(FeatureType feature){
        return getNameString(feature.getDescription(), 50);
    }

    protected String getVersionRangeString(FeatureType feature){
        return getNameString(feature.getVersion(), 15);
    }
}
