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
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.SelfServicesPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.DashboardPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdchange.PwdChangePage;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.IPageHeaderContributor;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.atricore.idbus.kernel.main.mail.MailService;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.osgi.framework.BundleContext;
import org.springframework.util.StringUtils;

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

    @PaxWicketBean(name = "kernelConfig", injectionSource = "spring")
    protected ConfigurationContext kernelConfig;

    private WebMarkupContainer pageBody;

    private WebMarkupContainer mainPanel;

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
                    kernelConfig,
                    mailService);

            // Set default locale if configured.
            String defaultLocale = app.getBranding().getDefaultLocale();
            if (defaultLocale != null)
                getSession().setLocale(StringUtils.parseLocaleString(defaultLocale));
        }

        // Handle internationalization
        if (parameters != null) {
            String lang = parameters.get("lang").toString();
            if (lang != null) {
                getSession().setLocale(StringUtils.parseLocaleString(lang));
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

        String variation = resolveVariation(branding);
        setVariation(variation);

        // These were added after the initial branding was created
        final SSOWebSession session = (SSOWebSession)getSession();
        pageBody = new WebMarkupContainer("body") {
        };
        mainPanel = new WebMarkupContainer("main-panel") {
        };

        if (this instanceof SelfServicesPage) {
            /*
            <body class="has-clouds light-bg">
            <div class="wrapper p-0">
             */
            pageBody.add(new AttributeAppender("class", "has-clouds light-bg"));
            mainPanel.add(new AttributeAppender("class", "wrapper p-0"));
        } else {
            pageBody.add(new AttributeAppender("class", "has-clouds -light-bg"));
            mainPanel.add(new AttributeAppender("class", "wrapper"));
        }





        super.add(pageBody);
        pageBody.add(mainPanel);

        // ---------------------------------------------------------------------
        // Utility box (current user, logout)
        // ---------------------------------------------------------------------
        WebMarkupContainer utilityBox = new WebMarkupContainer("utilityBox") {
            @Override
            public boolean isVisible() {
                return (session).isAuthenticated();
            };
        };


        if (session.isAuthenticated()) {
            utilityBox.add(new Label("username", session.getPrincipal()));
            utilityBox.add(new BookmarkablePageLink<Void>("logout", resolvePage("AGENT/LOGOUT")));
        }

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
            navBar.add(new BookmarkablePageLink<Void>("dashboard", resolvePage("SS/HOME")));

            // Profile
            navBar.add(new BookmarkablePageLink<Void>("profile", resolvePage("SS/PROFILE")));

            // Change Password
            navBar.add(new BookmarkablePageLink<Void>("pwdChange", resolvePage("SS/PWDCHANGE")));

            // Logout
            navBar.add(new BookmarkablePageLink<Void>("logout", resolvePage("AGENT/LOGOUT")));

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

    protected String resolveVariation(WebBranding branding) {

        BaseWebApplication app = (BaseWebApplication) getApplication();
        if (branding != null) {
            if (branding.getSkin() != null) {
                if (logger.isTraceEnabled())
                    logger.trace("Using 'variation/skin' ["+branding.getSkin()+"] based on " + branding.getId());
                return branding.getSkin();
            } else {
                logger.error("Branding does not define a skin : " + branding.getId());
            }

        } else {
            logger.error("No Branding found for application : " + app.getName());
        }
        return null;

    }


    public WebAppConfig getAppConfig() {
        WebAppConfig cfg = appConfigRegistry.lookupConfig(getApplication().getApplicationKey());
        if (cfg == null)
            logger.error("No configuration found for Wicket application " + getApplication().getApplicationKey());

        return cfg;
    }

    public Class resolvePage(String path) {
        BaseWebApplication app = (BaseWebApplication) getApplication();
        return app.resolvePage(path);
    }

    /**
     * We added new components in the heararchy, so we add childrent to the new tree.
     * @param behaviors
     * @return
     */
    @Override
    public Component add(Behavior... behaviors) {
        return mainPanel.add(behaviors);
    }

    /**
     * We added new components in the heararchy, so we add childrent to the new tree.
     * @param childs
     * @return
     */
    @Override
    public MarkupContainer add(Component... childs) {
        return mainPanel.add(childs);
    }

}
