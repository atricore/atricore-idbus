package org.atricore.idbus.capabilities.openid.main.metadata;

import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.springframework.core.io.Resource;

import java.util.Collection;

/**
 * Exposes the OpenID metadata of the provider inline, from the definition itself.
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OpenIDMetadataDefinitionIntrospector extends InlineMetadataDefinitionIntrospector {
    public MetadataDefinition load(CircleOfTrustMemberDescriptor member, Resource resource) throws CircleOfTrustManagerException {
        throw new UnsupportedOperationException("OpenID metadata is not resource-backed");
    }

    public MetadataEntry searchEntityDefinition(MetadataDefinition metadataDefinition, String memberAlias) throws CircleOfTrustManagerException {
        return new MetadataEntryImpl(memberAlias, null);
    }

    public MetadataEntry searchEntityRoleDefinition(MetadataDefinition metadataDefinition, String memberAlias, String roleType) throws CircleOfTrustManagerException {
        return null;
    }

    public MetadataEntry searchEndpointDescriptor(MetadataDefinition metadataDefinition, String memberAlias, String roleType, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        return null;
    }

    public Collection<MetadataEntry> searchEndpointDescriptors(MetadataDefinition metadataDefinition, String memberAlias, String roleType, EndpointDescriptor endpoint) throws CircleOfTrustManagerException {
        return null;
    }
}
