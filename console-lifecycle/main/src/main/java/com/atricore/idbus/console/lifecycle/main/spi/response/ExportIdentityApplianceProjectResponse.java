package com.atricore.idbus.console.lifecycle.main.spi.response;

/**
 * @version $Id$
 */
public class ExportIdentityApplianceProjectResponse extends AbstractManagementResponse {

    private String name;

    private int revision;

    private byte[] zip;

    public ExportIdentityApplianceProjectResponse() {
        super();
    }

    public ExportIdentityApplianceProjectResponse(String name, int revision, byte[] zip) {
        super();
        this.zip = zip;
    }

    public byte[] getZip() {
        return zip;
    }

    public void setZip(byte[] zip) {
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
}
