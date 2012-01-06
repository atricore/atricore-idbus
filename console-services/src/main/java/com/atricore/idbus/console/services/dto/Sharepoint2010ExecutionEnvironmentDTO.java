package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class Sharepoint2010ExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {
    private static final long serialVersionUID = 475740871227635432L;

    private LocationDTO stsLocation;

    private String stsSigningCertSubject;

    private String stsEncryptingCertSubject;

    private ResourceDTO stsMetadata;

    public LocationDTO getStsLocation() {
        return stsLocation;
    }

    public void setStsLocation(LocationDTO stsLocation) {
        this.stsLocation = stsLocation;
    }

    public String getStsSigningCertSubject() {
        return stsSigningCertSubject;
    }

    public void setStsSigningCertSubject(String stsSigningCertSubject) {
        this.stsSigningCertSubject = stsSigningCertSubject;
    }

    public String getStsEncryptingCertSubject() {
        return stsEncryptingCertSubject;
    }

    public void setStsEncryptingCertSubject(String stsEncryptingCertSubject) {
        this.stsEncryptingCertSubject = stsEncryptingCertSubject;
    }

    public ResourceDTO getStsMetadata() {
        return stsMetadata;
    }

    public void setStsMetadata(ResourceDTO stsMetadata) {
        this.stsMetadata = stsMetadata;
    }
}
