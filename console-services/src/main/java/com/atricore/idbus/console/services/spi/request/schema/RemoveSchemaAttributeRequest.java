package com.atricore.idbus.console.services.spi.request.schema;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RemoveSchemaAttributeRequest extends AbstractSchemaManagementRequest {

    private String schemaName;

    private int attributeId;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
}
