package org.atricore.idbus.capabilities.sts.management.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.TokenStore;
import org.atricore.idbus.capabilities.sts.main.WSTSecurityTokenService;
import org.atricore.idbus.capabilities.sts.management.SecurityTokenServiceMBean;
import org.atricore.idbus.capabilities.sts.management.codec.JmxSecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityToken;

import javax.management.openmbean.TabularData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityTokenServiceMBeanImpl implements SecurityTokenServiceMBean {

    private static final Log logger = LogFactory.getLog(SecurityTokenServiceMBeanImpl.class);

    private TokenStore store;

    public TokenStore getStore() {
        return store;
    }

    public void setStore(TokenStore store) {
        this.store = store;
    }

    @Override
    public TabularData listTokensAsTable() {

        try {

            Collection<String> tokens = store.getTokens();
            List<JmxSecurityToken> jmxTokens = new ArrayList<>(tokens.size());
            for (String tokenId : tokens) {
                SecurityToken t = store.retrieve(tokenId);
                JmxSecurityToken jmxToken = new JmxSecurityToken(t);
                jmxTokens.add(jmxToken);
            }
            TabularData table = JmxSecurityToken.tableFrom(jmxTokens);
            return table;
        } catch (Exception e) {
            logger.error("Cannot find tokens: " + e.getMessage(), e);
        }

        return null;
    }
}
