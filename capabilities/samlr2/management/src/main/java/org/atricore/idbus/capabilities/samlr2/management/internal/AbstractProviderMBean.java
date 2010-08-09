package org.atricore.idbus.capabilities.samlr2.management.internal;

import org.atricore.idbus.capabilities.samlr2.management.ProviderMBean;
import org.atricore.idbus.capabilities.samlr2.management.codec.JmxProviderState;
import org.atricore.idbus.capabilities.samlr2.management.codec.JmxProviderStateEntry;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.management.openmbean.TabularData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractProviderMBean implements ProviderMBean, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public TabularData listStatesAsTable() {
        ProviderStateContext ctx = new ProviderStateContext(getProvider(), applicationContext.getClassLoader());
        Collection<LocalState> states = ctx.retrievAll();
        List<JmxProviderState> jmxStates = new ArrayList<JmxProviderState>(states.size());
        for (LocalState localState : states) {
            jmxStates.add(new JmxProviderState(localState));
        }

        return JmxProviderState.tableFrom(jmxStates);

    }

    public TabularData listStateEntriesAsTable(String stateId) {

        ProviderStateContext ctx = new ProviderStateContext(getProvider(), applicationContext.getClassLoader());
        LocalState state = ctx.retrieve(stateId);
        List<JmxProviderStateEntry> entries = new ArrayList<JmxProviderStateEntry >();
        for (String key : state.getKeys()) {
            Object value = state.getValue(key);
            JmxProviderStateEntry entry = new JmxProviderStateEntry (key, value);
            entries.add(entry);
        }

        return JmxProviderStateEntry.tableFrom(entries);
    }

    protected abstract FederatedLocalProvider getProvider() ;

}
