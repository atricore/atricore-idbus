package org.atricore.idbus.idojos.virtualidentitystore;


import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;

/**
 * Exception triggered upon error conditions with virtual identity store
 * loadUser operation.
 */
public class VirtualNoSuchUserException extends NoSuchUserException {
	
	public VirtualNoSuchUserException(UserKey key) {
        super(key);
    }

    public VirtualNoSuchUserException(String msg) {
        super(msg);
    }
}
