/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.capabilities.sso.ui.page.authn.twofactor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.ui.page.authn.LoginPage;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;

/**
 * Strong authentication page for collecting username and passcode credentials.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class TwoFactorLoginPage extends LoginPage {

    private static final Log logger = LogFactory.getLog(TwoFactorLoginPage.class);

    public TwoFactorLoginPage() throws Exception {
        super();
    }

    public TwoFactorLoginPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    protected Panel prepareSignInPanel(String id, SSOCredentialClaimsRequest credentialClaimsRequest, MessageQueueManager artifactQueueManager,
                                       IdentityMediationUnitRegistry idsuRegistry) {
        

        UsernamePasscodeSignInPanel p = new UsernamePasscodeSignInPanel(id, credentialClaimsRequest, artifactQueueManager, idsuRegistry);
        p.setOutputMarkupId(true);
        return p;

    }
}
