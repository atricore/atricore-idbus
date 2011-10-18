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
package org.atricore.idbus.capabilities.openid.ui.internal;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.atricore.idbus.capabilities.openid.ui.page.OpenIDLoginPage;

/**
 * Entry point for the Wicket-based OpenID front-end.
 */
public class OpenIDUIApplication extends WebApplication {

    public OpenIDUIApplication() {
        super();
    }

    @Override
    protected void init() {
        super.init();


        mountBookmarkablePage("/login", OpenIDLoginPage.class);
        mountBookmarkablePage("/error/401", AccessDeniedPage.class);
        mountBookmarkablePage("/error/404", PageExpiredErrorPage.class);

        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<OpenIDLoginPage> getHomePage() {
        return OpenIDLoginPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new OpenIDWebSession(request);
    }
}
