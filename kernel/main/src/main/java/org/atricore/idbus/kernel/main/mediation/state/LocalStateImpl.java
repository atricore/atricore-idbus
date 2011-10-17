package org.atricore.idbus.kernel.main.mediation.state;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalStateImpl extends AbstractLocalState {

    private static Log logger = LogFactory.getLog(LocalStateImpl.class);

    private boolean newState = false;

    private Map<String, Object> values;


    public LocalStateImpl(String id) {
        super(id);
        values = new HashMap<String, Object>();
        newState = true;
    }

    public void setValue(String key, Object value) {
        if (logger.isTraceEnabled())
            logger.trace("setValue:" + key + "=" + value);
        values.put(key, value);
    }

    public Object getValue(String key) {

        if (logger.isTraceEnabled()) {
            Object value = values.get(key);
            logger.trace("getValue:" + key + "=" + value);
            return value;
        }

        return values.get(key);
    }

    public void removeValue(String key) {
        logger.trace("removeValue:" + key);
        values.remove(key);
    }

    public Collection<String> getKeys() {
        return values.keySet();
    }

    public boolean isNew() {
        return newState;
    }

    public void setNew(boolean b) {
        this.newState = b;
    }
}

