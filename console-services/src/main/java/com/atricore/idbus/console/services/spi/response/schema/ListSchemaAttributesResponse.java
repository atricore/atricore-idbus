package com.atricore.idbus.console.services.spi.response.schema;

import com.atricore.idbus.console.services.dto.schema.AttributeDTO;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListSchemaAttributesResponse extends AbstractSchemaManagementResponse {

    private Collection<AttributeDTO> attributesCollection;

    public Collection<AttributeDTO> getAttributesCollection() {
        return attributesCollection;
    }

    public void setAttributesCollection(Collection<AttributeDTO> attributesCollection) {
        this.attributesCollection = attributesCollection;
    }
}
