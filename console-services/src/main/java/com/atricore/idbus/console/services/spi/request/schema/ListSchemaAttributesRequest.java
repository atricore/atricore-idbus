package com.atricore.idbus.console.services.spi.request.schema;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListSchemaAttributesRequest extends AbstractSchemaManagementRequest {

    private String schemaName;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
