package org.atricore.idbus.capabilities.sts.management;

import javax.management.openmbean.TabularData;

public interface SecurityTokenServiceMBean {

    String PROVIDER_STATE_ID = "Id";

    String PROVIDER_STATE_ALT_KEYS = "AltKeys";

    String PROVIDER_STATE_ENTRY_KEY = "Key";

    String PROVIDER_STATE_ENTRY_TYPE = "Type";

    String PROVIDER_STATE_ENTRY_VALUE = "Value";

    String[] PROVIDER_STATE_ENTRY = {PROVIDER_STATE_ENTRY_KEY, PROVIDER_STATE_ENTRY_TYPE, PROVIDER_STATE_ENTRY_VALUE};

    String[] PROVIDER_STATE = {  PROVIDER_STATE_ID, PROVIDER_STATE_ALT_KEYS};

    String SECURITY_TOKEN_ID = "Id";

    String SECURITY_TOKEN_NAME_IDENTIFIER = "NameIdentifier";

    String SECURITY_TOKEN_ISSUE_INSTANT = "IssueInstant";

    String SECURITY_TOKEN_EXPIRES_ON = "ExpiresOn";

    String SECURITY_TOKEN_SERIALIZED_CONTENT = "SerializedContent";

    String SECURITY_TOKEN_TYPE = "Type";

    // WARNING : Keep this in SYNC ..
    String[] SECURITY_TOKEN = { SECURITY_TOKEN_ID, SECURITY_TOKEN_NAME_IDENTIFIER, SECURITY_TOKEN_ISSUE_INSTANT, SECURITY_TOKEN_EXPIRES_ON,
            SECURITY_TOKEN_SERIALIZED_CONTENT, SECURITY_TOKEN_TYPE };

    TabularData listTokensAsTable();

}