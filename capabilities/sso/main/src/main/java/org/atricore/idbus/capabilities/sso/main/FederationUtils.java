package org.atricore.idbus.capabilities.sso.main;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.sp.SSOSPMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediator;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FederationUtils {

    private static final Log logger = LogFactory.getLog(FederationUtils.class);

    /**
     * This will create the SSO link required to trigger authentication between the two providers
     *
     * @param defaultIdpInitiatedSsoLoation
     * @param idp
     * @param sp
     * @return
     */
    public static String getSSOEndpoint(String defaultIdpInitiatedSsoLoation, IdentityProvider idp, FederatedProvider sp) {

        // Use default endpoint, but look for overwritten values
        String idpInitiatedSsoEndpoint = defaultIdpInitiatedSsoLoation;

        // Look if the IdP has a specific target channel
        for (FederationChannel c : idp.getChannels()) {
            if (c.getTargetProvider() != null && c.getTargetProvider().getName().equals(sp.getName())) {

                // We have a specific channel targeting the SP, look for the IDP initated endpoint
                for (IdentityMediationEndpoint e : idp.getChannel().getEndpoints()) {
                    if (e.getType().equals(SSOService.SingleSignOnService.toString()) &&
                            e.getBinding().equals(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue())) {
                        idpInitiatedSsoEndpoint = c.getLocation() + e.getLocation();
                        break;
                    }
                }
                break;
            }
        }

        String spAlias = null;
        if (sp instanceof FederatedRemoteProvider) {
            FederatedRemoteProvider rsp = (FederatedRemoteProvider) sp;
            spAlias = ((CircleOfTrustMemberDescriptor) rsp.getMembers().iterator().next()).getAlias();
        } else if (sp instanceof ServiceProvider) {

            ServiceProvider lsp = (ServiceProvider) sp;
            // Look for the corresponding IdP channel
            IdPChannel idpChannel = (IdPChannel) lsp.getChannel();
            for (FederationChannel c : lsp.getChannels()) {
                if (c.getTargetProvider().getName().equals(idp.getName())) {
                    idpChannel = (IdPChannel) c;
                    break;
                }
            }
            spAlias = idpChannel.getMember().getAlias();
        } else if (sp instanceof BindingProvider) {
            BindingChannel bc = (BindingChannel) ((BindingProvider) sp).getChannel();
            IdentityMediator mediator = bc.getIdentityMediator();

            // Some reflexion ...
            try {
                Method m = mediator.getClass().getMethod("getSpAlias", null);
                spAlias = (String) m.invoke(mediator, null);
            } catch (NoSuchMethodException e) {
                // Ignore ...
                logger.debug("No getSpAlias method in mediator " + mediator.getClass() +  ".  " + e.getMessage());
            } catch (IllegalAccessException e) {
                // Ignore ...
                logger.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                // Ignore ...
                logger.error(e.getMessage(), e);
            }

        }

        spAlias = new String(Base64.encodeBase64(spAlias.getBytes()));
        idpInitiatedSsoEndpoint += "?atricore_sp_alias=" + spAlias;

        return idpInitiatedSsoEndpoint;
    }

    public static FederatedLocalProvider lookupProviderForACS(CircleOfTrust cot , String acsLocation) {

        for (FederatedProvider p : cot.getProviders()) {

            if (!(p instanceof FederatedLocalProvider))
                continue;

            FederatedLocalProvider provider = (FederatedLocalProvider) p;

            if (provider.getBindingChannel() != null) {
                if (lookupEndpoint(provider.getBindingChannel(), acsLocation) != null)
                    return provider;
            }

            FederationService defaultFs = provider.getDefaultFederationService();
            if (defaultFs != null) {
                FederationChannel fc = defaultFs.getChannel();
                if (lookupEndpoint(fc, acsLocation) != null)
                    return provider;
            }

            for (FederationService fs : provider.getFederationServices()) {
                FederationChannel fc = fs.getChannel();
                if (lookupEndpoint(fc, acsLocation) != null)
                    return provider;
            }

        }
        return null;
    }

    public static IdentityMediationEndpoint lookupEndpoint(Channel fc, String location) {
        for (IdentityMediationEndpoint endpoint : fc.getEndpoints()) {
            if ((fc.getLocation() + endpoint.getLocation()).equals(location))
                return endpoint;
        }

        return null;
    }

    /**
     * This will provide a list of all trust relationships derived from this IDP, including VP (proxies)
     *
     * If the FederatedProvider (fp) is a service provider(remote or local), the result will be single TrustedProviders
     * instance with the idp, and an single trusted provider (fp).
     *
     * If the FederatedProvider is a virtual provider, the result will be all providders trusting the VP.
     *
     * @param idp Identity Provider
     * @param fp a fedeated provider acting as Service Provider
     *
     * @return
     */
    public static Set<TrustedProviders> resolveTargetProviders(IdentityProvider idp, FederatedProvider fp) {

        String defaultIdpInitiatedSsoLoation = "";
        for (IdentityMediationEndpoint e : idp.getChannel().getEndpoints()) {
            if (e.getType().equals(SSOService.SingleSignOnService.toString()) &&
                    e.getBinding().equals(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue())) {
                defaultIdpInitiatedSsoLoation = idp.getChannel().getLocation() + e.getLocation();
                break;
            }
        }

        boolean isTrusted = false;
        for (FederatedProvider trusted : idp.getChannel().getTrustedProviders()) {
            if (trusted.getName().equals(fp.getName()))
                isTrusted = true;
        }

        for(FederationChannel fc : idp.getChannels()) {
            for (FederatedProvider trusted : fc.getTrustedProviders())
                if (trusted.getName().equals(fp.getName()))
                    isTrusted = true;
        }

        if (!isTrusted)
            return Collections.emptySet();

        if (fp instanceof ServiceProvider) {
            ServiceProvider sp = (ServiceProvider) fp;
            // Do not reference ourselves
            if (sp.getResourceType().equals(AppResource.SELFSERVICES.getResourceType()))
                return Collections.emptySet();

            // This is a PROXY! We need to look for proxied resources!
            // Proxy providers have a special treatment
            if (sp.getResourceType().equals(AppResource.SAML2_SP_PROXY.getResourceType())) {

                FederationChannel c = sp.getDefaultFederationService().getChannel();
                SSOSPMediator spMediator = (SSOSPMediator) c.getIdentityMediator();

                // This is where to forward Assertions/Tokens to proxied entities.
                String spBindingACS = spMediator.getSpBindingACS();

                if (spBindingACS != null) {
                    // We need to look for providers that host this ACS
                    // http://localhost:8081/IDBUS/IAOI/RP-1-OP/SSO/ACS/ARTIFACT
                    // http://localhost:8081/IDBUS/IAOI/VP-A-IDP-PROXY/SSO/ACSPROXY/ARTIFACT
                    FederatedLocalProvider proxied = lookupProviderForACS(idp.getCircleOfTrust(), spBindingACS);

                    if (proxied instanceof BindingProvider) {
                        Set<TrustedProviders> providers = new HashSet<TrustedProviders>(1);
                        BindingProvider bp = (BindingProvider) proxied;
                        if (bp.getRole() != null &&
                                bp.getRole().equals("{urn:openidconnect:1.0}ProviderDescriptor")) {

                            // For OIDC, we use the BC default service URL
                            String ssoUrl = bp.getBindingChannel().getDefaultServiceURL();
                            if (ssoUrl == null)
                                ssoUrl = getSSOEndpoint(defaultIdpInitiatedSsoLoation, idp, proxied);

                            providers.add(new TrustedProviders(idp,
                                    new ProviderResource(proxied, ssoUrl,
                                            AppResource.OIDC_RP.getResourceType())));
                        } else {
                            // TODO : Handle other proxied protocols
                        }

                        return providers;

                    } else if (proxied instanceof IdentityProvider) {

                        // We are proxying something else! Recursively resolve providers
                        IdentityProvider proxiedIdp = (IdentityProvider) proxied;
                        Set<TrustedProviders> proxiedProviders = new HashSet<TrustedProviders>();
                        for (FederatedProvider p : proxiedIdp.getChannel().getTrustedProviders()) {
                            proxiedProviders.addAll(resolveTargetProviders(proxiedIdp, p));
                        }

                        // Update URL with IDP alias for proxy
                        for (TrustedProviders tp : proxiedProviders) {
                            for (ProviderResource providerResource : tp.trusted) {
                                if (providerResource.ssoUrl.contains("atricore_sp_alias="))
                                    providerResource.ssoUrl += "&atricore_idp_alias=" + idp.getChannel().getMember().getAlias();
                            }
                        }

                        return proxiedProviders;
                    } else {
                        logger.warn("Proxied provider type unsupported " + proxied);
                    }


                }
                return Collections.emptySet();

            } else {
                String idpInitiatedSsoEndpoint = getSSOEndpoint(defaultIdpInitiatedSsoLoation, idp, sp);
                Set<TrustedProviders> providers = new HashSet<TrustedProviders>(1);
                providers.add(new TrustedProviders(idp, new ProviderResource(sp, idpInitiatedSsoEndpoint)));
                return providers;
            }

        } else if (fp instanceof FederatedRemoteProvider) {
            FederatedRemoteProvider rp = (FederatedRemoteProvider) fp;

            if (rp.getRole() != null &&
                    (rp.getRole().equals(SSOMetadataConstants.SPSSODescriptor_QNAME.getNamespaceURI() + ":" +
                            SSOMetadataConstants.SPSSODescriptor_QNAME.getLocalPart()) ||
                            rp.getRole().equals("{" + SSOMetadataConstants.SPSSODescriptor_QNAME.getNamespaceURI() + "}" +
                                    SSOMetadataConstants.SPSSODescriptor_QNAME.getLocalPart()))) {

                String idpInitiatedSsoEndpoint = getSSOEndpoint(defaultIdpInitiatedSsoLoation, idp, rp);
                Set<TrustedProviders> providers = new HashSet<TrustedProviders>(1);
                providers.add(new TrustedProviders(idp, new ProviderResource(rp, idpInitiatedSsoEndpoint)));
                return providers;
            }
        }

        // Ignore this type of provider ...
        return Collections.emptySet();

    }

    /**
     * Set of providers that trust the associated IDP
     */
    public static class TrustedProviders {

        private IdentityProvider idp;

        private Set<ProviderResource> trusted;

        public TrustedProviders(IdentityProvider idp, ProviderResource p) {
            this.idp = idp;
            this.trusted = new HashSet<ProviderResource>();
            this.trusted.add(p);
        }

        public TrustedProviders(IdentityProvider idp, Set<ProviderResource> p) {
            this.idp = idp;
            this.trusted = p;
        }

        public IdentityProvider getIdp() {
            return idp;
        }

        public Set<ProviderResource> getTrusted() {
            return trusted;
        }
    }

    /**
     * A provider as a resource, includint type and SSO endpoint.
     */
    public static class ProviderResource {

        private FederatedProvider provider;

        private String resourceType;

        private String ssoUrl;

        public ProviderResource(FederatedProvider provider, String ssoUrl, String resourceType) {
            this.provider = provider;
            this.ssoUrl = ssoUrl;
            this.resourceType = resourceType;
        }

        public ProviderResource(FederatedProvider provider, String ssoUrl) {
            this.provider = provider;
            this.ssoUrl = ssoUrl;
            if (provider instanceof ServiceProvider)
                resourceType = ((ServiceProvider) provider).getResourceType();

            if (provider instanceof FederatedRemoteProvider)
                resourceType = ((FederatedRemoteProvider) provider).getResourceType();
        }

        public FederatedProvider getProvider() {
            return provider;
        }

        public String getSsoUrl() {
            return ssoUrl;
        }

        public String getResourceType() {
            return resourceType;
        }
    }


}
