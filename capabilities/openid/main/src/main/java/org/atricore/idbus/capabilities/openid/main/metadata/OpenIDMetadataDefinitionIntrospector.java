package org.atricore.idbus.capabilities.openid.main.metadata;

import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.springframework.core.io.Resource;

import java.util.Collection;

public class OpenIDMetadataDefinitionIntrospector extends InlineMetadataDefinitionIntrospector {
    public MetadataDefinition load(CircleOfTrustMemberDescriptor member, Resource resource) throws CircleOfTrustManagerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MetadataEntry searchEntityDefinition(MetadataDefinition metadataDefinition, String memberAlias) throws CircleOfTrustManagerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MetadataEntry searchEntityRoleDefinition(MetadataDefinition metadataDefinition, String memberAlias, String roleType) throws CircleOfTrustManagerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MetadataEntry searchEndpointDescriptor(MetadataDefinition metadataDefinition, String memberAlias, String roleType, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<MetadataEntry> searchEndpointDescriptors(MetadataDefinition metadataDefinition, String memberAlias, String roleType, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
