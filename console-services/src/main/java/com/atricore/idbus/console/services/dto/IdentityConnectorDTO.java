package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityConnectorDTO {

    private String name;

    private String description;

    private String pstName;

    public IdentityConnectorDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPstName() {
        return pstName;
    }

    public void setPstName(String pstName) {
        this.pstName = pstName;
    }
}
