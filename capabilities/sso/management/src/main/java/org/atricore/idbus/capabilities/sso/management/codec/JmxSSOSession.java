package org.atricore.idbus.capabilities.sso.management.codec;

import org.atricore.idbus.capabilities.sso.management.ProviderMBean;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.management.openmbean.*;
import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JmxSSOSession {
    
    public final static TabularType SSO_SESSION_TABLE;
    
    public final static CompositeType SSO_SESSION;
    
    static {
        SSO_SESSION = createSsoSessionType();
        SSO_SESSION_TABLE = createSsoSessionTableType();
    }
    
    private final CompositeData data;

    private SSOSession session;

    public JmxSSOSession(SSOSession session) {
        this.session = session;
        
        try {
            String[] itemNames = ProviderMBean.SSO_SESSION;
            Object[] itemValues = new Object[itemNames.length];

            itemValues[0] = session.getId();
            itemValues[1] = session.isValid();
            itemValues[2] = session.getUsername();
            itemValues[3] = new java.util.Date(session.getCreationTime());
            itemValues[4] = session.getSecurityToken().getId();
            itemValues[5] = session.getSecurityToken().getNameIdentifier();

            // session.getLastAccessTime();
            // session.getAccessCount();
            // session.getMaxInactiveInterval();

            data = new CompositeDataSupport(SSO_SESSION, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Cannot form SSO Session open data", e);
        }
        
    }

    public CompositeData asCompositeData() {
        return data;
    }

    public static TabularData tableFrom(Collection<JmxSSOSession> ssoSessions) {
        TabularDataSupport table = new TabularDataSupport(SSO_SESSION_TABLE);
        for (JmxSSOSession ssoSession : ssoSessions) {
            table.put(ssoSession.asCompositeData());
        }
        return table;
    }
    
    private static CompositeType createSsoSessionType() {
        try {
            String description = "This type encapsulates Atricore IDBus SSO Sessions";
            String[] itemNames = ProviderMBean.SSO_SESSION;
            OpenType[] itemTypes = new OpenType[itemNames.length];
            String[] itemDescriptions = new String[itemNames.length];
            itemTypes[0] = SimpleType.STRING;
            itemTypes[1] = SimpleType.BOOLEAN;
            itemTypes[2] = SimpleType.STRING;
            itemTypes[3] = SimpleType.DATE;
            itemTypes[4] = SimpleType.STRING;
            itemTypes[5] = SimpleType.STRING;

            itemDescriptions[0] = "The ID of the SSO Session";
            itemDescriptions[1] = "Whether the SSO Session is valid";
            itemDescriptions[2] = "The login name of the user";
            itemDescriptions[3] = "The SSO Session creation time";
            itemDescriptions[4] = "The Security Token ID";
            itemDescriptions[5] = "The Security Token Name Identifier";

            return new CompositeType("SSOSession", description, itemNames,
                    itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build SSOSession type", e);
        }
    }

    private static TabularType createSsoSessionTableType() {
        try {
            return new TabularType("SSOSessions", "The table of all SSO Sessions",
                    SSO_SESSION, new String[] { ProviderMBean.SSO_SESSION_ID,
                            ProviderMBean.SSO_SESSION_VALID,
                            ProviderMBean.SSO_SESSION_USERNAME,
                            ProviderMBean.SSO_SESSION_CREATION_TIME,
                            ProviderMBean.SSO_SESSION_SEC_TKN_ID,
                            ProviderMBean.SSO_SESSION_SEC_TKN_NAME_ID});
        } catch (OpenDataException e) {
            throw new IllegalStateException("Unable to build SSOSession table type", e);
        }
    }    
    
}
