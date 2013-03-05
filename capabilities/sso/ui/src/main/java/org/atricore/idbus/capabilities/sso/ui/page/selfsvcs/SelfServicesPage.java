package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.AppErrorPage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.store.identity.IdentityPartitionStore;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/5/13
 */
public class SelfServicesPage extends BasePage {

    private static Log logger = LogFactory.getLog(SelfServicesPage.class);

    public SelfServicesPage() throws Exception {
    }

    public SelfServicesPage(PageParameters parameters) throws Exception {
        super(parameters);
    }

    protected IdentityPartition getIdentityPartition() {

        // Get the identity partition from default idp's sp channel:

        SSOIdPApplication app = (SSOIdPApplication) getApplication();
        SPChannel spChannel = (SPChannel) app.getIdentityProvider().getChannel();

        // Identity Manager
        SSOIdentityManager identityManager = spChannel.getIdentityManager();
        if (identityManager == null) {
            logger.error("IdP " + app.getIdentityProvider().getName() + " has no identity manager for channel " + spChannel.getName());
            return null;
        }

        // Identity Store
        IdentityStore store = identityManager.getIdentityStore();
        if (store instanceof IdentityPartitionStore) {
            IdentityPartitionStore s = (IdentityPartitionStore) store;
            IdentityPartition p = s.getPartition();
            return p;
        } else {
            logger.error("Identity Store is not of type IdentityPartitionStore, found " + store.getClass().getName());
        }

        // No Identity partition found
        return null;

    }

    protected User lookupUser() {

        SSOWebSession ssoSession = (SSOWebSession) getSession();

        IdentityPartition p = getIdentityPartition();
        if (p == null)
            return null;

        try {

            if (logger.isTraceEnabled())
                logger.trace("Looking for user " + ssoSession.getPrincipal() + " in Identity Partition " + p.getName());

            User user = p.findUserByUserName(ssoSession.getPrincipal());

            if (logger.isTraceEnabled())
                logger.trace("Found user " + user.getId() + " for principal " + ssoSession.getPrincipal());

            return user;

        } catch (ProvisioningException e) {
            logger.error(e.getMessage(),  e);
            // TODO : Provide error information
            throw new RestartResponseAtInterceptPageException(AppErrorPage.class);
        }
    }


}
