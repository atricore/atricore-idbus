/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.atricore.idbus.capabilities.sso.ui.page.SimpleLoginPage;
import org.atricore.idbus.capabilities.sso.ui.page.StrongLoginPage;

/**
 * Entry point for the Wicket-based SSO front-end.
 */
public class SSOUIApplication extends WebApplication {

    public SSOUIApplication() {
        super();
    }

    @Override
    protected void init() {
        super.init();


        mountBookmarkablePage("/login/simple", SimpleLoginPage.class);
        mountBookmarkablePage("/login/strong", StrongLoginPage.class);

        mountBookmarkablePage("/error/401", AccessDeniedPage.class);
        mountBookmarkablePage("/error/404", PageExpiredErrorPage.class);

        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<SimpleLoginPage> getHomePage() {
        return SimpleLoginPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SSOWebSession(request);
    }
}
