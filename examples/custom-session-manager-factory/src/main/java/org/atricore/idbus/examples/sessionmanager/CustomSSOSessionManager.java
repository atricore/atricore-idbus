package org.atricore.idbus.examples.sessionmanager;

import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.session.SSOSessionContext;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl;

public class CustomSSOSessionManager extends SSOSessionManagerImpl {

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        // TODO: override properties
    }

    @Override
    protected String initiate(String username, SecurityToken securityToken, SSOSessionContext ctx, int sessionTimeout) throws SSOSessionException {
        // TODO : do something custom

        // We can get the remote user IP address
        String remoteIpAddress = ctx.getProperty("org.atricore.idbus.http.RemoteAddress");

        return super.initiate(username, securityToken, ctx, sessionTimeout);
    }
}
