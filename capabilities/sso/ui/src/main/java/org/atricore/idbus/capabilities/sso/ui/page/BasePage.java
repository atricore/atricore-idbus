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
package org.atricore.idbus.capabilities.sso.ui.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.agent.JossoLogoutPage;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.DashboardPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange.PwdChangePage;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.IPageHeaderContributor;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.atricore.idbus.kernel.main.mail.MailService;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.osgi.framework.BundleContext;

import java.util.Locale;

/**
 * Convenience base page for concrete SSO pages requiring a common layout and theme.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class BasePage extends WebPage implements IHeaderContributor {

    private static final Log logger = LogFactory.getLog(BasePage.class);

    @PaxWicketBean(name = "bundleContext", injectionSource = "spring")
    protected BundleContext bundleContext;

    @PaxWicketBean(name = "idsuRegistry", injectionSource = "spring")
    protected IdentityMediationUnitRegistry idsuRegistry;

    @PaxWicketBean(name = "artifactQueueManager", injectionSource = "spring")
    protected MessageQueueManager artifactQueueManager;

    @PaxWicketBean(name = "webAppConfigRegistry", injectionSource = "spring")
    protected ApplicationRegistry appConfigRegistry;

    @PaxWicketBean(name = "webBrandingService", injectionSource = "spring")
    protected WebBrandingService brandingService;

    @PaxWicketBean(name = "mailService", injectionSource = "spring")
    protected MailService mailService;

    private IPageHeaderContributor headerContributors;

    private String variant;

    @SuppressWarnings("serial")
    public BasePage() throws Exception {
        this(null);
    }

    @SuppressWarnings("serial")
    public BasePage(PageParameters parameters) throws Exception {
        
        // -------------------------------------------------------------------
        // The very first thing to do is set the application ready if it's not
        //                                WebMarkupContainer
        // Pax-wicket does not support dependency injection in the app. object.
        // -------------------------------------------------------------------
        BaseWebApplication app = (BaseWebApplication) getApplication();
        if (!app.isReady()) {
            app.config(bundleContext,
                      appConfigRegistry,
                      brandingService,
                      idsuRegistry,
                    mailService);

            // Set default locale if configured.
            String defaultLocale = app.getBranding().getDefaultLocale();
            if (defaultLocale != null)
                getSession().setLocale(new Locale(defaultLocale));
        }
        
        // Handle internationalization
        if (parameters != null) {
            String lang = parameters.get("lang").toString();
            if (lang != null) {
                getSession().setLocale(new Locale(lang));
            }
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getSession().bind();

        // ---------------------------------------------------------------------
        // Resolve variation (branding)
        // ---------------------------------------------------------------------
        BaseWebApplication app = (BaseWebApplication) getApplication();
        WebBranding branding = app.getBranding();
        if (branding != null) {

            if (logger.isTraceEnabled())
                logger.trace("Using 'variant' ["+branding.getSkin()+"] based on " + branding.getId());

            if (branding.getSkin() != null)
                setVariation(branding.getSkin());

        } else {
            logger.error("No Branding found for application : " + app.getName());
        }

        final SSOWebSession session = (SSOWebSession)getSession();

        // ---------------------------------------------------------------------
        // Utility box (current user, logout)
        // ---------------------------------------------------------------------
        WebMarkupContainer utilityBox = new WebMarkupContainer("utilityBox") {
            @Override
            public boolean isVisible() {
                return (session).isAuthenticated();
            };
        };

        if (session.isAuthenticated())
            utilityBox.add(new Label("username", session.getPrincipal()));

        add(utilityBox);

        // ---------------------------------------------------------------------
        // Navigation Bar
        // ---------------------------------------------------------------------
        // Do not display the menu for the IdBus ERROR PAGE
        WebMarkupContainer navBar = new WebMarkupContainer("navbar") {
            @Override
            public boolean isVisible() {
                return session.isAuthenticated();
            };
        };


        if (navBar.isVisible()) {
            // Select the proper section on the navbar (alter css class)

            // Dashboard
            if (this instanceof DashboardPage)
                navBar.add(new BookmarkablePageLink<Void>("dashboard", DashboardPage.class).add(new AttributeAppender("class", "gt-active")));
            else
                navBar.add(new BookmarkablePageLink<Void>("dashboard", DashboardPage.class));

            // Profile
            if (this instanceof  ProfilePage)
                navBar.add(new BookmarkablePageLink<Void>("profile", ProfilePage.class).add(new AttributeAppender("class", "gt-active")));
            else
                navBar.add(new BookmarkablePageLink<Void>("profile", ProfilePage.class));


            // Change Password
            if (this  instanceof PwdChangePage)
                navBar.add(new BookmarkablePageLink<Void>("pwdChange", PwdChangePage.class).add(new AttributeAppender("class", "gt-active")));
            else
                navBar.add(new BookmarkablePageLink<Void>("pwdChange", PwdChangePage.class));

            // Logout 1
            navBar.add(new BookmarkablePageLink<Void>("logout", JossoLogoutPage.class));

            // Logout 2
        }

        add(navBar);

    }

    public void render(HeaderItem item) {
        if ( ((BaseWebApplication)getApplication()).getBranding() == null)
            return;

        BaseWebApplication app = (BaseWebApplication) getApplication();
        
        WebBranding branding = app.getBranding();
        
        if (branding == null)
            return;

        for (IPageHeaderContributor c : branding.getPageHeaderContributors()) {
            c.render(item, this);
        }


    }

    public void setVariation(String variation) {
        this.variant = variation;
    }

    @Override
    public String getVariation() {
        return variant;
    }


    public WebAppConfig getAppConfig() {
        WebAppConfig cfg = appConfigRegistry.lookupConfig(getApplication().getApplicationKey());
        if (cfg == null)
            logger.error("No configuration found for Wicket application " + getApplication().getApplicationKey());

        return cfg;
    }

}
