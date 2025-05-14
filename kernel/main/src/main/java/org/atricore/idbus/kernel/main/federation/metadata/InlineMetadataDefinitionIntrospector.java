package org.atricore.idbus.kernel.main.federation.metadata;

public abstract class InlineMetadataDefinitionIntrospector implements MetadataDefinitionIntrospector {
    public MetadataDefinition load(CircleOfTrustMemberDescriptor member) throws CircleOfTrustManagerException {
        return ((InlineCircleOfTrustMemberDescriptor)member).getMetadataDefinition();
    }

}
