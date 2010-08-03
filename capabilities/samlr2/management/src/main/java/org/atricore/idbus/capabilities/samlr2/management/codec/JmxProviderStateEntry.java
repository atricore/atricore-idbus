package org.atricore.idbus.capabilities.samlr2.management.codec;

import org.atricore.idbus.capabilities.samlr2.management.IdentityProviderMBean;

import javax.management.openmbean.*;
import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JmxProviderStateEntry {
    
    public final static TabularType PROVIDER_STATE_ENTRY_TABLE;
    
    public final static CompositeType PROVIDER_STATE_ENTRY;
    
    static {
        PROVIDER_STATE_ENTRY = createProviderStateEntryType();
        PROVIDER_STATE_ENTRY_TABLE = createProviderStateEntryTableType();
    }

    private final CompositeData data;

    private String key;

    private Object value;

    public JmxProviderStateEntry(String key, Object value) {
        this.key = key;
        this.value = value;
        try {
            String[] itemNames = IdentityProviderMBean.PROVIDER_STATE_ENTRY;
            Object[] itemValues = new Object[itemNames.length];

            itemValues[0] = key;
            itemValues[1] = value != null ? value.getClass().getSimpleName() : "<NULL>";
            itemValues[2] = value != null ? value.toString() : "<NULL>";

            data = new CompositeDataSupport(PROVIDER_STATE_ENTRY, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Cannot form Provider State Entry open data", e);
        }

    }

    public CompositeData asCompositeData() {
        return data;
    }

    public static TabularData tableFrom(Collection<JmxProviderStateEntry> jmxProviderStateEntries) {
        TabularDataSupport table = new TabularDataSupport(PROVIDER_STATE_ENTRY_TABLE);
        for (JmxProviderStateEntry entry : jmxProviderStateEntries) {
            table.put(entry.asCompositeData());
        }
        return table;
    }

    private static CompositeType createProviderStateEntryType() {
        try {
            String description = "This type encapsulates Atricore IDBus Provider State entries";
            String[] itemNames = IdentityProviderMBean.PROVIDER_STATE_ENTRY;
            OpenType[] itemTypes = new OpenType[itemNames.length];
            String[] itemDescriptions = new String[itemNames.length];
            itemTypes[0] = SimpleType.STRING;
            itemTypes[1] = SimpleType.STRING;
            itemTypes[2] = SimpleType.STRING;

            itemDescriptions[0] = "The state entry key";
            itemDescriptions[1] = "The state entry type";
            itemDescriptions[2] = "The state entry value";

            return new CompositeType("ProviderStateEntry", description, itemNames,
                    itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build ProviderStateEntry type", e);
        }
    }

    private static TabularType createProviderStateEntryTableType() {
        try {
            return new TabularType("ProviderStateEntries", "The table of all Provider State entries",
                    PROVIDER_STATE_ENTRY , new String[] { IdentityProviderMBean.PROVIDER_STATE_ENTRY_KEY,
                            IdentityProviderMBean.PROVIDER_STATE_ENTRY_TYPE,
                            IdentityProviderMBean.PROVIDER_STATE_ENTRY_VALUE});

        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build SSOSession table type", e);
        }
    }
    
}
