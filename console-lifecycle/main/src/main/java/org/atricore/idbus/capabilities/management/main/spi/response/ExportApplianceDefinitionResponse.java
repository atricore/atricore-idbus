package org.atricore.idbus.capabilities.management.main.spi.response;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ExportApplianceDefinitionResponse extends AbstractManagementResponse {

    private String descriptor;

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
}
