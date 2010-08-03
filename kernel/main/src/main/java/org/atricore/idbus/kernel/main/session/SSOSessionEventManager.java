package org.atricore.idbus.kernel.main.session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SSOSessionEventManager {

    private static SSOSessionEventManager instance;

    private List<SSOSessionEventListener> listeners = new ArrayList<SSOSessionEventListener>();

    protected SSOSessionEventManager() {

    }

    public static SSOSessionEventManager getInstance() {
        if (instance == null) {
            instance = new SSOSessionEventManager();
        }
        return instance;
    }

    public List<SSOSessionEventListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SSOSessionEventListener> listeners) {
        this.listeners = listeners;
    }

    public void fireSessionEvent(String type, SSOSession session, Object data) {
        for (SSOSessionEventListener listener : listeners) {
            listener.handleEvent(type, session, data);
        }
    }
}
