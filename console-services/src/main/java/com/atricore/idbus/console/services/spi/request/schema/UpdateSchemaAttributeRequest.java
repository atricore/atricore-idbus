package com.atricore.idbus.console.services.spi.request.schema;

import com.atricore.idbus.console.services.dto.schema.AttributeDTO;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateSchemaAttributeRequest extends AbstractSchemaManagementRequest {

    private String schemaName;

    private AttributeDTO attribute;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public AttributeDTO getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeDTO attribute) {
        this.attribute = attribute;
    }
}
