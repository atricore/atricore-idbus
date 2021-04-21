package org.atricore.idbus.kernel.main.federation.metadata;

import org.springframework.core.io.Resource;

import java.util.Collection;

public interface MetadataDefinitionIntrospector {

    MetadataDefinition load(CircleOfTrustMemberDescriptor member)
            throws CircleOfTrustManagerException;

    MetadataDefinition load(CircleOfTrustMemberDescriptor member, Resource resource)
            throws CircleOfTrustManagerException;

    MetadataEntry searchEntityDefinition(MetadataDefinition metadataDefinition,
                                                            String memberAlias, boolean strict)
            throws CircleOfTrustManagerException;

    MetadataEntry searchEntityDefinition(MetadataDefinition metadataDefinition,
                                         String memberAlias)
            throws CircleOfTrustManagerException;


    MetadataEntry searchEntityRoleDefinition(MetadataDefinition metadataDefinition,
                                                                String memberAlias,
                                                                String roleType)
            throws CircleOfTrustManagerException;

    MetadataEntry searchEndpointDescriptor(MetadataDefinition metadataDefinition,
                                                                  String memberAlias,
                                                                  String roleType,
                                                                  EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException;

    Collection<MetadataEntry> searchEndpointDescriptors(MetadataDefinition metadataDefinition,
                                                                  String memberAlias,
                                                                  String roleType,
                                                                  EndpointDescriptor endpoint)
            throws CircleOfTrustManagerException;

}
