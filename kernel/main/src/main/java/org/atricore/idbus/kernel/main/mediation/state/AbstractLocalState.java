package org.atricore.idbus.kernel.main.mediation.state;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractLocalState implements LocalState {

    private String id;

    // Map containing a set of IDs for a name space.
    private Map<String, Set<String>> alternativeIds;

    public AbstractLocalState(String id) {
        this.id = id;
        this.alternativeIds = Collections.synchronizedMap(new HashMap<String, Set<String>>());
    }

    public String getId() {
        return id;
    }

    /**
     * Add an alternative way of recovering this state element.
     *
     * @param idName works as a name space, may have more than on ID for the same name.
     */
    public void addAlternativeId(String idName, String id) {
        Set<String> ids = this.alternativeIds.get(idName);
        if (ids == null) {
            ids = new HashSet<String>();
            this.alternativeIds.put(idName, ids);
        }
        ids.add(id);

    }

    public synchronized void removeAlternativeId(String idName, String id) {
        Set<String> ids = this.alternativeIds.get(idName);
        if (ids != null) {
            ids.remove(id);
            if (ids.isEmpty())
                removeAlternativeIds(idName);
        }
    }

    public void removeAlternativeIds(String idName) {
        alternativeIds.remove(idName);
    }

    public Set<String> getAlternativeIds(String idName) {
        return this.alternativeIds.get(idName);
    }

    public Collection<String> getAlternativeIdNames() {
        return alternativeIds.keySet();
    }
}
