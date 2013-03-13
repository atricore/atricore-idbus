package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.AppErrorPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.sidebar.SideBarPanel;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedRemoteProvider;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;

import java.util.ArrayList;
import java.util.List;

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
            throw new RestartResponseAtInterceptPageException(AppErrorPage.class);
        }
    }

    protected List<PartnerAppModel> lookupSps() {

        SSOWebSession ssoSession = (SSOWebSession) getSession();
        SSOIdPApplication app = ((SSOIdPApplication)getApplication());

        IdentityProvider idp = app.getIdentityProvider();

        String defaultIdpInitiatedSsoLoation = "";
        for (IdentityMediationEndpoint e : idp.getChannel().getEndpoints()) {
            if (e.getType().equals(SSOService.SingleSignOnService.toString()) &&
                    e.getBinding().equals(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue())) {
                defaultIdpInitiatedSsoLoation = idp.getChannel().getLocation() + e.getLocation();
                break;
            }
        }

        List<PartnerAppModel> apps = new ArrayList<PartnerAppModel>();

        for (FederatedProvider p : app.getIdentityProvider().getCircleOfTrust().getProviders()) {

            if (p instanceof ServiceProvider) {
                // Here is an SP, get the SP initiated SSO url along with other details
                ServiceProvider sp = (ServiceProvider) p;

                // Use default endpoint, but look for overwritten values
                String idpInitiatedSsoEndpoint = defaultIdpInitiatedSsoLoation;

                for (FederationChannel c : idp.getChannels()) {

                    // Do we have a specific channel for this provider ?
                    if (c.getTargetProvider() != null && c.getTargetProvider().getName().equals(sp.getName())) {
                        for (IdentityMediationEndpoint e : idp.getChannel().getEndpoints()) {
                            if (e.getType().equals(SSOService.SingleSignOnService.toString()) &&
                                    e.getBinding().equals(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue())) {
                                idpInitiatedSsoEndpoint = c.getLocation() + e.getLocation();
                                c.getLocation();
                                break;
                            }
                        }
                        break;
                    }
                }

                IdPChannel idpChannel = (IdPChannel) sp.getChannel();
                for (FederationChannel c : idp.getChannels()) {
                    if (c.getTargetProvider().getName().equals(idp.getName()))
                        idpChannel = (IdPChannel) c;
                    break;
                }

                String spAlias = idpChannel.getMember().getAlias();
                spAlias = new String(Base64.encodeBase64(spAlias.getBytes()));
                idpInitiatedSsoEndpoint += "?atricore_sp_alias=" + spAlias;

                if (logger.isDebugEnabled())
                    logger.debug("Found IDP initiated SSO Endpoint ["+idpInitiatedSsoEndpoint+"] for SP  : " + sp.getName());

                apps.add(new PartnerAppModel(sp.getName(),
                        sp.getName(),
                        sp.getDisplayName() != null ? sp.getDisplayName() : sp.getDescription(),
                        sp.getDescription(),
                        idpInitiatedSsoEndpoint,
                        sp.getResourceType()));

            } else if (p instanceof FederatedRemoteProvider) {

                String idpInitiatedSsoEndpoint = defaultIdpInitiatedSsoLoation;

                FederatedRemoteProvider rp = (FederatedRemoteProvider) p;

                if (rp.getRole() != null &&
                    (rp.getRole().equals(SSOMetadataConstants.SPSSODescriptor_QNAME.getNamespaceURI() +":"+
                            SSOMetadataConstants.SPSSODescriptor_QNAME.getLocalPart()) ||
                    rp.getRole().equals("{" + SSOMetadataConstants.SPSSODescriptor_QNAME.getNamespaceURI() +"}"+
                            SSOMetadataConstants.SPSSODescriptor_QNAME.getLocalPart()))) {

                    // For remote providers, there's only one member !
                    CircleOfTrustMemberDescriptor descr = rp.getMembers().iterator().next();
                    String spAlias = descr.getAlias();
                    idpInitiatedSsoEndpoint += "?atricore_sp_alias=" + spAlias;

                    apps.add(new PartnerAppModel(rp.getName(),
                            rp.getName(),
                            rp.getDisplayName() != null ? rp.getDisplayName() : rp.getDescription(),
                            rp.getDescription(),
                            idpInitiatedSsoEndpoint,
                            rp.getResourceType()));
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Found "  + apps.size() + " partner applications");

        return apps;

    }


}
