package com.atricore.idbus.console.services.dto.branding;

public class BuiltInBrandingDefinitionDTO extends BrandingDefinitionDTO {

    private static final long serialVersionUID = -608536473182572984L;

    public BuiltInBrandingDefinitionDTO() {
        super();
        this.type = BrandingTypeDTO.BUILT_IN;
    }
}
