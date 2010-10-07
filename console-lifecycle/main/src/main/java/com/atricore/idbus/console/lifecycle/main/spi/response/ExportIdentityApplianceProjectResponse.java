package com.atricore.idbus.console.lifecycle.main.spi.response;

/**
 * @version $Id$
 */
public class ExportIdentityApplianceProjectResponse extends AbstractManagementResponse {

    private byte[] zip;

    public ExportIdentityApplianceProjectResponse() {
        super();
    }

    public ExportIdentityApplianceProjectResponse(byte[] zip) {
        super();
        this.zip = zip;
    }

    public byte[] getZip() {
        return zip;
    }

    public void setZip(byte[] zip) {
        this.zip = zip;
    }
}
