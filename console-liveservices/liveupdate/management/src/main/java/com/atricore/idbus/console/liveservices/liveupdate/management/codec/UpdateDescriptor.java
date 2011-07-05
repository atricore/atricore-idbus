package com.atricore.idbus.console.liveservices.liveupdate.management.codec;

import com.atricore.idbus.console.liveservices.liveupdate.management.LiveUpdateMBean;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import javax.management.openmbean.*;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateDescriptor {

    public final static TabularType UPDATE_DESCRIPTOR_TABLE;

    public final static CompositeType UPDATE_DESCRIPTOR;

    static {
        UPDATE_DESCRIPTOR = createUpdateDescriptorType();
        UPDATE_DESCRIPTOR_TABLE = createUpdateDescriptorTableType();
    }

    private final CompositeData data;

    private UpdateDescriptorType update;

    public UpdateDescriptor(UpdateDescriptorType update) {
        this.update = update;

        try {
            String[] itemNames = LiveUpdateMBean.UPDATE_DESCRIPTOR;
            Object[] itemValues = new Object[itemNames.length];

            itemValues[0] = update.getInstallableUnit().getUpdateNature().toString();
            itemValues[1] = update.getInstallableUnit().getGroup();
            itemValues[2] = update.getInstallableUnit().getName();
            itemValues[3] = update.getInstallableUnit().getVersion();
            itemValues[4] = update.getDescription();
            itemValues[5] = update.getIssueInstant().toGregorianCalendar().getTime();

            data = new CompositeDataSupport(UPDATE_DESCRIPTOR, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Cannot form Update Descriptor open data", e);
        }

    }

    public CompositeData asCompositeData() {
        return data;
    }

    public static TabularData tableFrom(Collection<UpdateDescriptor> updates) {
        TabularDataSupport table = new TabularDataSupport(UPDATE_DESCRIPTOR_TABLE);
        for (UpdateDescriptor update : updates) {
            table.put(update.asCompositeData());
        }
        return table;
    }

    private static CompositeType createUpdateDescriptorType() {
        try {
            String description = "This type encapsulates Atricore updates";

            String[] itemNames = LiveUpdateMBean.UPDATE_DESCRIPTOR;
            OpenType[] itemTypes = new OpenType[itemNames.length];
            String[] itemDescriptions = new String[itemNames.length];

            itemTypes[0] = SimpleType.STRING;
            itemTypes[1] = SimpleType.STRING;
            itemTypes[2] = SimpleType.STRING;
            itemTypes[3] = SimpleType.STRING;
            itemTypes[4] = SimpleType.STRING;
            itemTypes[5] = SimpleType.DATE;

            itemDescriptions[0] = "Update nature";
            itemDescriptions[1] = "Update group";
            itemDescriptions[2] = "Update name";
            itemDescriptions[3] = "Update version";
            itemDescriptions[4] = "Update description";
            itemDescriptions[5] = "Update release date";

            return new CompositeType("UpdateDescriptor", description, itemNames,
                    itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build UpdateDescriptor type", e);
        }
    }

    private static TabularType createUpdateDescriptorTableType() {
        try {
            return new TabularType("Updates", "The table of all updates",
                    UPDATE_DESCRIPTOR, new String[] { LiveUpdateMBean.UPDATE_DESCRIPTOR_NATURE,
                            LiveUpdateMBean.UPDATE_DESCRIPTOR_GROUP,
                            LiveUpdateMBean.UPDATE_DESCRIPTOR_NAME,
                            LiveUpdateMBean.UPDATE_DESCRIPTOR_VERSION,
                            LiveUpdateMBean.UPDATE_DESCRIPTOR_DESCRIPTION,
                            LiveUpdateMBean.UPDATE_DESCRIPTOR_RELEASE_DATE});
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build UpdateDescriptor table type", e);
        }
    }
}
