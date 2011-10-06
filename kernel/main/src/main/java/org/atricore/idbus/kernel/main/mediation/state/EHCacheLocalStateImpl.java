package org.atricore.idbus.kernel.main.mediation.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EHCacheLocalStateImpl extends LocalStateImpl {

    private List<String> removedKeys = new ArrayList<String>();

    public EHCacheLocalStateImpl(String id) {
        super(id);
    }

    private boolean dirty = true;

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public synchronized void addAlternativeId(String idName, String id) {
        super.addAlternativeId(idName, id);
        this.dirty = true;
    }

    @Override
    public synchronized void removeAlternativeId(String idName) {
        String key = this.getAlternativeKey(idName);
        super.removeAlternativeId(idName);
        if (key != null)
            removedKeys.add(key);
        this.dirty = true;
    }

    @Override
    public synchronized void setValue(String key, Object value) {
        super.setValue(key, value);
        dirty = true;
    }

    @Override
    public synchronized void removeValue(String key) {
        super.removeValue(key);
        dirty = true;
    }

    public String getAlternativeKey(String idName) {
        String id = super.getAlternativeId(idName);
        if (id != null)
            return idName + ":" + id;

        return null;
    }

    public Collection<String> getRemovedKeys() {
        return removedKeys;
    }

    public synchronized void clearState() {
        dirty = false;
        removedKeys.clear();
    }

}
