package org.atricore.idbus.capabilities.sts.management.codec;

import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.capabilities.sts.management.SecurityTokenServiceMBean;

import javax.management.openmbean.*;
import java.util.Collection;

public class JmxSecurityToken {

    public final static TabularType SECURITY_TOKEN_TABLE;

    public final static CompositeType SECURITY_TOKEN;

    static {
        SECURITY_TOKEN = createSecurityTokenType();
        SECURITY_TOKEN_TABLE = createSecurityTokensTableType();
    }

    private final CompositeData data;

    private SecurityToken token;

    public JmxSecurityToken(SecurityToken token) {
        this.token = token;

        try {

            String[] itemNames = SecurityTokenServiceMBean.SECURITY_TOKEN;
            Object[] itemValues = new Object[itemNames.length];

            itemValues[0] = token.getId();
            itemValues[1] = token.getNameIdentifier();

            itemValues[2] = new java.util.Date(token.getIssueInstant());
            itemValues[3] = new java.util.Date(token.getExpiresOn() * 1000L);

            itemValues[4] = token.getSerializedContent();
            itemValues[5] = token.getClass().getSimpleName();

            data = new CompositeDataSupport(SECURITY_TOKEN, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Cannot form SSO Session open data", e);
        }

    }

    public CompositeData asCompositeData() {
        return data;
    }

    public static TabularData tableFrom(Collection<JmxSecurityToken> tokens) {
        TabularDataSupport table = new TabularDataSupport(SECURITY_TOKEN_TABLE);
        for (JmxSecurityToken token : tokens) {
            table.put(token.asCompositeData());
        }
        return table;
    }

    private static CompositeType createSecurityTokenType() {
        try {

            // WARNING : Keep ProviderMBean.SECURITY_TOKEN in SYNC with this ...

            String description = "This type encapsulates Atricore IDBus SSO Sessions";
            String[] itemNames = SecurityTokenServiceMBean.SECURITY_TOKEN;
            OpenType[] itemTypes = new OpenType[itemNames.length];
            String[] itemDescriptions = new String[itemNames.length];
            itemTypes[0] = SimpleType.STRING;
            itemTypes[1] = SimpleType.STRING;

            itemTypes[2] = SimpleType.DATE;
            itemTypes[3] = SimpleType.DATE;

            itemTypes[4] = SimpleType.STRING;
            itemTypes[5] = SimpleType.STRING;

            itemDescriptions[0] = "The ID of the security token";
            itemDescriptions[1] = "The Name Identifier of the token";
            itemDescriptions[2] = "The issue instant";
            itemDescriptions[3] = "The expiration date";
            itemDescriptions[4] = "The serialized content";
            itemDescriptions[5] = "The type (class name)";

            return new CompositeType("SecurityToken", description, itemNames,
                    itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build Security Token type", e);
        }
    }

    private static TabularType createSecurityTokensTableType() {
        try {
            return new TabularType("SecurityTokens", "The table of all SSO Sessions",
                    SECURITY_TOKEN,
                    SecurityTokenServiceMBean.SECURITY_TOKEN);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build SecurityToken table type", e);
        }
    }

}
