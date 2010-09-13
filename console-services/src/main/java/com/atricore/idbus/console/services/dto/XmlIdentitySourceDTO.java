package com.atricore.idbus.console.services.dto;

public class XmlIdentitySourceDTO extends IdentitySourceDTO {

    private static final long serialVersionUID = -8915783648390299026L;

    private String xmlUrl;

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }
}
