package org.atricore.idbus.kernel.main.mediation.state;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalStateImpl extends AbstractLocalState {

    private static Log logger = LogFactory.getLog(LocalStateImpl.class);

    private boolean newState = false;

    private boolean dirty = true;

    private Map<String, Object> values;
    private List<String> removedKeys;
    private Map<String, Set<String>> alternativeKeys;

    public LocalStateImpl(String id) {
        super(id);
        values = new HashMap<String, Object>();
        removedKeys = new ArrayList<String>();
        alternativeKeys = new HashMap<String , Set<String>>();
        newState = true;
    }

    @Override
    public void setValue(String key, Object value) {
        if (logger.isTraceEnabled())
            logger.trace("setValue:" + key + "=" + value);
        values.put(key, value);
        dirty = true;
    }

    @Override
    public Object getValue(String key) {

        if (logger.isTraceEnabled()) {
            Object value = values.get(key);
            logger.trace("getValue:" + key + "=" + value);
            return value;
        }
        return values.get(key);
    }

    @Override
    public void removeValue(String key) {
        logger.trace("removeValue:" + key);
        values.remove(key);
        dirty = true;
    }

    @Override
    public Collection<String> getKeys() {
        return values.keySet();
    }

    @Override
    public boolean isNew() {
        return newState;
    }

    public void setNew(boolean b) {
        this.newState = b;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public synchronized void addAlternativeId(String idName, String id) {
        super.addAlternativeId(idName, id);
        addAlternativeKey(idName, idName + ":" + id);
        this.dirty = true;
    }

    protected void addAlternativeKey(String idName, String key) {
        Set<String> keys = alternativeKeys.get(idName);
        if (keys == null) {
            keys = new HashSet<String>();
            alternativeKeys.put(idName, keys);
        }
        keys.add(key);
    }

    @Override
    public synchronized void removeAlternativeIds(String idName) {
        this.alternativeKeys.remove(idName);
        super.removeAlternativeIds(idName);
        this.dirty = true;
    }


    @Override
    public synchronized void removeAlternativeId(String idName, String id) {
        // Remove Key, then
        Set<String> keys = this.getAlternativeKeys(idName);
        if (keys != null) {
            String key = idName + ":" + id;
            removedKeys.add(key);
            keys.remove(key);
        }
        super.removeAlternativeId(idName, id);
        this.dirty = true;
    }

    protected Set<String> getAlternativeKeys(String idName) {
        return alternativeKeys.get(idName);
    }

    public Collection<String> getRemovedKeys() {
        return removedKeys;
    }

    public synchronized void clearState() {
        dirty = false;
        removedKeys.clear();
    }

}
