package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FindSecurityTokensByIssueInstantBeforeRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -2639068098156498718L;

    private long issueInstant;

    public long getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(long issueInstant) {
        this.issueInstant = issueInstant;
    }
}
