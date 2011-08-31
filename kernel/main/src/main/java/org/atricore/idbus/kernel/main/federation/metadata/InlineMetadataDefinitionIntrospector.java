package org.atricore.idbus.kernel.main.federation.metadata;

import org.springframework.core.io.Resource;

import java.util.Collection;

public abstract class InlineMetadataDefinitionIntrospector implements MetadataDefinitionIntrospector {
    public MetadataDefinition load(CircleOfTrustMemberDescriptor member) throws CircleOfTrustManagerException {
        return ((InlineCircleOfTrustMemberDescriptor)member).getMetadataDefinition();
    }

}
