package org.atricore.idbus.capabilities.sso.ui.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolver;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.OAuth2RServerException;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverFactory;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.AppErrorPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.DashboardPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.profile.ProfilePage;

import java.util.Properties;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class JossoSecurityCheckPage extends BasePage {

    private static final Log logger = LogFactory.getLog(JossoSecurityCheckPage.class);

    private static final String ACCESS_TOKEN_PARAM = "access_token";


    public JossoSecurityCheckPage() throws Exception {
    }

    public JossoSecurityCheckPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize()  {
        super.onInitialize();

        try {
            SecurityContext ctx = createOAuth2SecurityContext();
            if (ctx == null) {
                SSOWebSession session = (SSOWebSession) getSession();
                if (session != null) session.setSecurityContext(null);
                throw new RestartResponseAtInterceptPageException(ProfilePage.class);
            }
        } catch (OAuth2RServerException e) {
            logger.error("Cannot create security context " + e.getMessage(), e);
            throw new RestartResponseAtInterceptPageException(AppErrorPage.class);
        }

        SSOWebSession session = (SSOWebSession) getSession();
        if (!session.isAuthenticated()) {
            logger.warn("Session is NOT authenticated, but we have a security context!!! Session ID / Principal : "
                    + session.getId() + " / " + session.getPrincipal());
            throw new RestartResponseAtInterceptPageException(DashboardPage.class);
        }

    }

    @Override
    protected void onBeforeRender() {
        // logon successful. Continue to the original destination
        continueToOriginalDestination();
        // Ups, no original destination. Go to the home page
        throw new RestartResponseException(getSession().getPageFactory().newPage(
                getApplication().getHomePage()));
    }

    protected SecurityContext createOAuth2SecurityContext() throws OAuth2RServerException {

        WebAppConfig cfg = getAppConfig();

        Properties oauth2Config = new Properties();

        PageParameters parameters = getPageParameters();
        if (parameters == null) {
            logger.debug("No page parameters available, required parameter " + ACCESS_TOKEN_PARAM);
            return null;
        }

        String oauth2Token = parameters.get(ACCESS_TOKEN_PARAM).toString();
        if (oauth2Token == null) {
            logger.debug("No token found for parameter " + ACCESS_TOKEN_PARAM);
            return null;
        }

        oauth2Config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_PROPERTY, cfg.getSelfServicesSharedSecret());
        oauth2Config.setProperty(SecureAccessTokenResolverFactory.TOKEN_VALIDITY_INTERVAL_PROPERTY, "30");

        AccessTokenResolver tokenResolver = AccessTokenResolverFactory.newInstance(oauth2Config).newResolver();

        if (logger.isDebugEnabled())
            logger.debug("Using Access Token resolver : " + tokenResolver);

        OAuth2AccessToken at = tokenResolver.resolve(oauth2Token);
        SecurityContext ctx = new OAuth2SecurityContext(at);

        SSOWebSession session = (SSOWebSession) getSession();
        session.setSecurityContext(ctx);

        return ctx;

    }


}
