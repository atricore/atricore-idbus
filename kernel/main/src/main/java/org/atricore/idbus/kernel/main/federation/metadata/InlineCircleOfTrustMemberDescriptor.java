package org.atricore.idbus.kernel.main.federation.metadata;

public class InlineCircleOfTrustMemberDescriptor extends CircleOfTrustMemberDescriptorImpl {

    private MetadataDefinition metadataDefinition;

    public MetadataDefinition getMetadataDefinition() {
        return metadataDefinition;
    }

    public void setMetadataDefinition(MetadataDefinition metadataDefinition) {
        this.metadataDefinition = metadataDefinition;
    }
}
