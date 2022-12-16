package org.atricore.idbus.examples.sso.dbsource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.idojos.dbidentitystore.DynamicJDBCIdentityStore;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

/**
 * This is an example for extending a DB identity source.  You can add your custom logic here.
 */
public class CustomIdSourceDB extends DynamicJDBCIdentityStore {

    private Log logger = LogFactory.getLog(CustomIdSourceDB.class);
    
    private String label;


    /**
     * @param key the user name, this is a unique identifier.
     */
    @Override
    public BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException {
        logger.info("Executing custom 'loadUser': " + label);
        return super.loadUser(key);
    }

    /**
     * @param key the user name, this is a unique identifier.
     */
    @Override
    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {
        logger.info("Executing custom 'findRolesByUserKey': " + label);
        return super.findRolesByUserKey(key);
    }

    /**
     *
     * @param key the key used to retrieve credentials from store.
     * @param cp credential provider. Used to build credential objects based on data retrieved from the DB.  CP is related to authentication scheme
     */
    @Override
    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {
        logger.info("Executing custom 'loadCredentials': " + label);
        return super.loadCredentials(key, cp);
    }

    /**
     *
     * @param key
     *
     */
    @Override
    public boolean userExists(UserKey key) throws SSOIdentityException {
        logger.info("Executing custom 'userExists': " + label);
        return super.userExists(key);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
