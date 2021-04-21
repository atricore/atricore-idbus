package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AddSecurityTokenRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -1339068098156498718L;

    private String tokenId;
    private String nameIdentifier;
    private Object content;
    private String serializedContent;
    private long issueInstant;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getSerializedContent() {
        return serializedContent;
    }

    public void setSerializedContent(String serializedContent) {
        this.serializedContent = serializedContent;
    }

    public long getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(long issueInstant) {
        this.issueInstant = issueInstant;
    }
}
