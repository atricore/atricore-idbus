package org.atricore.idbus.connectors.jdoidentityvault.domain;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOSecurityToken {

    private static final long serialVersionUID = 4595183658527120945L;

    private Long id;

    private String tokenId;
    private String nameIdentifier;
    private Object content;
    private String contentBin;
    private String serializedContent;
    private long issueInstant;
    private long expiresOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public long getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOSecurityToken)) return false;

        JDOSecurityToken that = (JDOSecurityToken) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public String getContentBin() {
        return contentBin;
    }

    public void setContentBin(String contentBin) {
        this.contentBin = contentBin;
    }


}
