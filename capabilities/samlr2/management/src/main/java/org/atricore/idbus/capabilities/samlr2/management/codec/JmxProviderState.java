package org.atricore.idbus.capabilities.samlr2.management.codec;

import org.atricore.idbus.capabilities.samlr2.management.IdentityProviderMBean;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;

import javax.management.openmbean.*;
import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JmxProviderState {
    
    public final static TabularType PROVIDER_STATE_TABLE;
    
    public final static CompositeType PROVIDER_STATE;
    
    static {
        PROVIDER_STATE = createProviderStateType();
        PROVIDER_STATE_TABLE = createProviderStateEntryTableType();
    }

    private final CompositeData data;

    private LocalState state;

    public JmxProviderState(LocalState state) {
        this.state = state;
        try {
            String[] itemNames = IdentityProviderMBean.PROVIDER_STATE;
            Object[] itemValues = new Object[itemNames.length];

            itemValues[0] = state.getId();

            String altKeys = "";

            for (String altKey : state.getAlternativeIdNames()) {
                altKeys += altKey + "=" + state.getAlternativeId(altKey) + ",";
            }
            itemValues[1] = altKeys;

            data = new CompositeDataSupport(PROVIDER_STATE, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Cannot form Provider State open data", e);
        }

    }

    public CompositeData asCompositeData() {
        return data;
    }

    public static TabularData tableFrom(Collection<JmxProviderState> jmxProviderStates) {
        TabularDataSupport table = new TabularDataSupport(PROVIDER_STATE_TABLE);
        for (JmxProviderState entry : jmxProviderStates) {
            table.put(entry.asCompositeData());
        }
        return table;
    }

    private static CompositeType createProviderStateType() {
        try {
            String description = "This type encapsulates Atricore IDBus Provider State";
            String[] itemNames = IdentityProviderMBean.PROVIDER_STATE;
            OpenType[] itemTypes = new OpenType[itemNames.length];
            String[] itemDescriptions = new String[itemNames.length];
            itemTypes[0] = SimpleType.STRING;
            itemTypes[1] = SimpleType.STRING;

            itemDescriptions[0] = "The state entry key";
            itemDescriptions[1] = "The state alternative keys";

            return new CompositeType("ProviderState", description, itemNames,
                    itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build ProviderState type", e);
        }
    }

    private static TabularType createProviderStateEntryTableType() {
        try {
            return new TabularType("ProviderState", "The table of all Provider States",
                    PROVIDER_STATE , new String[] { IdentityProviderMBean.PROVIDER_STATE_ID,
                            IdentityProviderMBean.PROVIDER_STATE_ALT_KEYS});

        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build SSOSession table type", e);
        }
    }
    
}
