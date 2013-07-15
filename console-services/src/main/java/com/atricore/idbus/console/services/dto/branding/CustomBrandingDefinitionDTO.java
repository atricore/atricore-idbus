package com.atricore.idbus.console.services.dto.branding;

public class CustomBrandingDefinitionDTO extends BrandingDefinitionDTO {

    private static final long serialVersionUID = 422458937698481308L;

    private String customSsoAppClazz;

    private String customSsoIdPAppClazz;

    private String customOpenIdAppClazz;

    private String bundleSymbolicName;

    private String bundleUri;

    private byte[] resource;

    public CustomBrandingDefinitionDTO() {
        super();
        this.type = BrandingTypeDTO.CUSTOM;
    }

    public String getBundleUri() {
        return bundleUri;
    }

    public void setBundleUri(String bundleUri) {
        this.bundleUri = bundleUri;
    }

    public String getCustomSsoAppClazz() {
        return customSsoAppClazz;
    }

    public void setCustomSsoAppClazz(String customSsoAppClazz) {
        this.customSsoAppClazz = customSsoAppClazz;
    }

    public String getCustomSsoIdPAppClazz() {
        return customSsoIdPAppClazz;
    }

    public void setCustomSsoIdPAppClazz(String customSsoIdPAppClazz) {
        this.customSsoIdPAppClazz = customSsoIdPAppClazz;
    }

    public String getCustomOpenIdAppClazz() {
        return customOpenIdAppClazz;
    }

    public void setCustomOpenIdAppClazz(String customOpenIdAppClazz) {
        this.customOpenIdAppClazz = customOpenIdAppClazz;
    }

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public byte[] getResource() {
        return resource;
    }

    public void setResource(byte[] resource) {
        this.resource = resource;
    }
}
