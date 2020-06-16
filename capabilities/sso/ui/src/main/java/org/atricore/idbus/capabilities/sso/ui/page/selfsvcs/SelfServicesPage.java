package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.FederationUtils;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.sidebar.SideBarPanel;
import org.atricore.idbus.kernel.main.mediation.provider.*;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;

import java.util.*;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public abstract class SelfServicesPage extends BasePage {

    private static Log logger = LogFactory.getLog(SelfServicesPage.class);

    public SelfServicesPage() throws Exception {
        this(null);
    }

    public SelfServicesPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SideBarPanel("sideBar", lookupUser(), lookupSps()));

    }

    protected User lookupUser() {

        SSOWebSession ssoSession = (SSOWebSession) getSession();

        ProvisioningTarget pt = ((SSOIdPApplication)getApplication()).getProvisioningTarget();
        if (pt == null)
            return null;

        try {


            if (logger.isTraceEnabled())
                logger.trace("Looking for user " + ssoSession.getPrincipal() + " in Provisioning Target" + pt.getName());

            FindUserByUsernameRequest req = new FindUserByUsernameRequest();
            req.setUsername(ssoSession.getPrincipal());
            FindUserByUsernameResponse resp = pt.findUserByUsername(req);

            User user = resp.getUser();

            if (logger.isTraceEnabled())
                logger.trace("Found user " + user.getId() + " for principal " + ssoSession.getPrincipal());

            return user;

        } catch (ProvisioningException e) {
            logger.error(e.getMessage(),  e);
            // TODO : Provide error information
            throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/APP"));
        }
    }

    protected List<PartnerAppModel> lookupSps() {

        SSOIdPApplication app = ((SSOIdPApplication)getApplication());
        IdentityProvider idp = app.getIdentityProvider();

        List<PartnerAppModel> apps = new ArrayList<PartnerAppModel>();
        Set<FederationUtils.TrustedProviders> providers = new HashSet<FederationUtils.TrustedProviders>();

        // Resolve trusted providers for our IDP
        for (FederatedProvider p : app.getIdentityProvider().getCircleOfTrust().getProviders()) {
            providers.addAll(FederationUtils.resolveTargetProviders(idp, p));
        }

        // Create a partner app model
        for (FederationUtils.TrustedProviders trustedProviders : providers) {

            for (FederationUtils.ProviderResource providerResource : trustedProviders.getTrusted()) {

                FederatedProvider p = providerResource.getProvider();
                String idpInitiatedSsoEndpoint = providerResource.getSsoUrl();

                if (logger.isDebugEnabled())
                    logger.debug("Found IDP initiated SSO Endpoint [" + idpInitiatedSsoEndpoint + "] for SP  : " + p.getName());

                apps.add(new PartnerAppModel(p.getName(),
                        p.getName(),
                        p.getDescription() != null ? p.getDescription() : p.getName(),
                        p.getDescription(),
                        idpInitiatedSsoEndpoint,
                        providerResource.getResourceType()));

            }

        }

        Collections.sort(apps, new Comparator<PartnerAppModel>() {
            @Override
            public int compare(PartnerAppModel o1, PartnerAppModel o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });

        if (logger.isDebugEnabled())
            logger.debug("Found "  + apps.size() + " partner applications");

        return apps;

    }

}
