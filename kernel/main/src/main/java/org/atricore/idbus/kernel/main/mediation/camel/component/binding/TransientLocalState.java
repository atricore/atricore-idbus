package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.atricore.idbus.kernel.main.mediation.state.AbstractLocalState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransientLocalState extends AbstractLocalState {

    private Map<String, Object> vars = new HashMap<String, Object>();

    public TransientLocalState(String id) {
        super(id);
    }

    public void setValue(String key, Object value) {
        vars.put(key, value);
    }

    public Object getValue(String key) {
        return vars.get(key);
    }

    public void removeValue(String key) {
        vars.remove(key);
    }

    public Collection<String> getKeys() {
        return vars.keySet();
    }

    public boolean isNew() {
        return true;
    }
}
