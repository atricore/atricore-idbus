package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FindSecurityTokenByTokenIdRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -2439068098156498718L;

    private String tokenId;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
