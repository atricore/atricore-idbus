package org.atricore.idbus.kernel.main.federation.metadata;

public class CircleOfTrustMemberDescriptorImpl implements CircleOfTrustMemberDescriptor {

    private String id;
    private String alias;

    // The springmetadata entry that represents this COT member.
    private MetadataEntry metadata;

    private MetadataDefinitionIntrospector metadataIntrospector;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public MetadataEntry getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataEntry metadata) {
        this.metadata = metadata;
    }

    public MetadataDefinitionIntrospector getMetadataIntrospector() {
        return metadataIntrospector;
    }

    public void setMetadataIntrospector(MetadataDefinitionIntrospector metadataIntrospector) {
        this.metadataIntrospector = metadataIntrospector;
    }

    @Override
    public String toString() {
        return super.toString() + "[id="+id+"" +
                ",alias="+alias+"]";
    }
}
