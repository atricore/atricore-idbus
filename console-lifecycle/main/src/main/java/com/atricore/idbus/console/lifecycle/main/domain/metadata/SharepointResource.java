package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 *
 * Sharepoint Server 2010 execution environment, very specifc.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SharepointResource extends ServiceResource {

    private static final long serialVersionUID = 873640871223858432L;

    private Location stsLocation;

    private String stsSigningCertSubject;

    private String stsEncryptingCertSubject;

    private Resource stsMetadata;

    public Location getStsLocation() {
        return stsLocation;
    }

    public void setStsLocation(Location stsLocation) {
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

    public Resource getStsMetadata() {
        return stsMetadata;
    }

    public void setStsMetadata(Resource stsMetadata) {
        this.stsMetadata = stsMetadata;
    }
}
