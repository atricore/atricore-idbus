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
package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.atricore.idbus.capabilities.sso.ui.page.*;

/**
 * Entry point for the Wicket-based SSO front-end.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class SSOUIApplication extends BaseWebApplication {

    public SSOUIApplication() {
        super();
    }

    @Override
    protected void preInit() {
        super.preInit();
    }

    @Override
    protected void postInit() {
        super.postInit();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void mountPages() {
        // SSO Authentication pages: SIMPLE (usr/pwd), STRONG (x509 cert, SSL), 2FA (2 factor pass code)
        mountPage("LOGIN/SIMPLE", SimpleLoginPage.class);
        mountPage("LOGIN/STRONG", StrongLoginPage.class);
        mountPage("LOGIN/2FA", TwoFactorLoginPage.class);

        mountPage("ERROR", ErrorPage.class);
        mountPage("ERROR/401", AccessDeniedPage.class);
        mountPage("ERROR/404", PageExpiredErrorPage.class);
        mountPage("ERROR/SESSION", SessionExpiredPage.class);

        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage() {
        return SimpleLoginPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SSOWebSession(request);
    }
}
