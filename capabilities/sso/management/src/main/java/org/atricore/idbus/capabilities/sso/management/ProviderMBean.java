package org.atricore.idbus.capabilities.sso.management;

import javax.management.openmbean.TabularData;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface ProviderMBean {

    String PROVIDER_STATE_ID = "Id";

    String PROVIDER_STATE_ALT_KEYS = "AltKeys";

    String PROVIDER_STATE_ENTRY_KEY = "Key";

    String PROVIDER_STATE_ENTRY_TYPE = "Type";

    String PROVIDER_STATE_ENTRY_VALUE = "Value";

    String[] PROVIDER_STATE_ENTRY = {PROVIDER_STATE_ENTRY_KEY, PROVIDER_STATE_ENTRY_TYPE, PROVIDER_STATE_ENTRY_VALUE};

    String[] PROVIDER_STATE = {  PROVIDER_STATE_ID, PROVIDER_STATE_ALT_KEYS};

    String SSO_SESSION_ID = "Id";

    String SSO_SESSION_VALID = "IsValid";

    String SSO_SESSION_USERNAME = "Username";

    String SSO_SESSION_CREATION_TIME = "CreationTime";

    String SSO_SESSION_SEC_TKN_ID = "SecurityTokenID";

    String SSO_SESSION_SEC_TKN_NAME_ID = "SecurityTokenNameID";

    String[] SSO_SESSION = { SSO_SESSION_ID, SSO_SESSION_VALID, SSO_SESSION_USERNAME, SSO_SESSION_CREATION_TIME,
            SSO_SESSION_SEC_TKN_ID, SSO_SESSION_SEC_TKN_NAME_ID };    

    TabularData listStatesAsTable();

    TabularData listStateEntriesAsTable(String stateId);

}
