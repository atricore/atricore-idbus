package com.atricore.idbus.console.services.spi.response.schema;

import com.atricore.idbus.console.services.dto.schema.AttributeDTO;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AddSchemaAttributeResponse extends AbstractSchemaManagementResponse {

    private AttributeDTO attribute;

    public AttributeDTO getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeDTO attribute) {
        this.attribute = attribute;
    }
}
