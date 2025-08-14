package org.atricore.idbus.kernel.main.mediation.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Set;

public class EHCacheLocalStateImpl extends LocalStateImpl {

    private transient Object lock = new Object();

    public EHCacheLocalStateImpl(String id) {
        super(id);
        lock = new Object();
    }

    @Override
    public Object getValue(String key) {
        synchronized (lock) {
            return super.getValue(key);
        }
    }

    @Override
    public Collection<String> getKeys() {
        synchronized (lock) {
            return super.getKeys();
        }
    }

    @Override
    public Collection<String> getRemovedKeys() {
        synchronized (lock) {
            return super.getRemovedKeys();
        }
    }

    @Override
    public synchronized void clearState() {
        synchronized (lock) {
            super.clearState();
        }
    }

    @Override
    public String getId() {
        synchronized (lock) {
            return super.getId();
        }
    }

    @Override
    public Set<String> getAlternativeIds(String idName) {
        synchronized (lock) {
            return super.getAlternativeIds(idName);
        }
    }

    @Override
    public Collection<String> getAlternativeIdNames() {
        synchronized (lock) {
            return super.getAlternativeIdNames();
        }
    }

    public Object getMutex() {
        synchronized (lock) {
            return lock;
        }
    }

    @Override
    public void removeValue(String key) {
        synchronized (lock) {
            super.removeValue(key);
        }
    }

    @Override
    public void setValue(String key, Object value) {
        synchronized (lock) {
            super.setValue(key, value);
        }
    }

    @Override
    public synchronized void addAlternativeId(String idName, String id) {
        synchronized (lock) {
            super.addAlternativeId(idName, id);
        }
    }

    @Override
    public synchronized void removeAlternativeIds(String idName) {
        synchronized (lock) {
            super.removeAlternativeIds(idName);
        }
    }

    @Override
    public synchronized void removeAlternativeId(String idName, String id) {
        synchronized (lock) {
            super.removeAlternativeId(idName, id);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.lock = new Object();
        synchronized (lock) {
            ois.defaultReadObject();
        }

    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        synchronized (lock) {
            oos.defaultWriteObject();
        }
    }



}
