package org.atricore.idbus.capabilities.spnego.selectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spnego.SpnegoAuthenticationScheme;
import org.atricore.idbus.capabilities.spnego.SpnegoBinding;
import org.atricore.idbus.capabilities.spnego.authenticators.SpnegoSecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.WSTSecurityTokenService;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederationService;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * This selector works ONLY for locally defined IDPs
 */
public class SpnegoIdPSelector extends AbstractEntitySelector implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpnegoIdPSelector.class);

    private ApplicationContext applicationContext;

    @Override
    public List<EndpointDescriptor> getUserClaimsEndpoints(EntitySelectionContext ctx, SelectorChannel channel) {

        Set<FederatedProvider> idps = findTrustedIdPs(ctx, channel);
        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();

        // Get IdP claim channel and look for service: urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos
        for (FederatedProvider fp : idps) {

            if (fp instanceof IdentityProvider) {
                IdentityProvider idp = (IdentityProvider) fp;

                // Claim channels are the same no matter what SP is requesting them, use the default federation channel.
                FederationChannel c = idp.getChannel();

                for (ClaimChannel cc : c.getClaimProviders()) {
                    // Does this CC support a Kerberos endpoint ?!
                    for (IdentityMediationEndpoint e  : cc.getEndpoints()) {
                        // TODO : Validate binding !?
                        if (e.getType().equals(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue()) &&
                            e.getBinding().equals(SpnegoBinding.SSO_ARTIFACT.getValue())) {

                            String location = cc.getLocation() + e.getLocation();

                            EndpointDescriptor ed = new EndpointDescriptorImpl(
                                    "SelectUserClaimsEndpoint",
                                    "UserClaimsRequest",
                                    SpnegoBinding.SSO_ARTIFACT.toString(),
                                    location,
                                    null);

                            endpoints.add(ed);
                        }
                    }
                }
            }
        }

        if (endpoints.size() > 0)
            return endpoints;

        return null;

    }

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx, SelectorChannel channel) throws SSOException {

        UserClaim uc = ctx.getUserClaim(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue());
        BinarySecurityTokenType binaryToken = (BinarySecurityTokenType) uc.getValue();
        if (binaryToken == null)
            return null;

        String spnegoSecurityToken = binaryToken.getOtherAttributes().get( new QName( SpnegoSecurityTokenAuthenticator.SPNEGO_NS) );
        String idpName = binaryToken.getOtherAttributes().get( new QName( SpnegoSecurityTokenAuthenticator.SPNEGO_NS, "idp") );

        if (logger.isDebugEnabled())
            logger.debug("Validating SPNEGO token issued by IdP " + idpName);

        Set<FederatedProvider> idps = findTrustedIdPs(ctx, channel);

        // This goes a little too deep into the dependencies tree
        // Get IdP claim channel and look for service: urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos
        for (FederatedProvider fp : idps) {

            if (fp instanceof IdentityProvider) {

                IdentityProvider idp = (IdentityProvider) fp;

                if (!idp.getName().equals(idpName))
                    continue;

                if (logger.isTraceEnabled())
                    logger.trace("Trying WIA on IdP " + idp.getName());

                // We assume we have a WST STS .
                WSTSecurityTokenService sts = (WSTSecurityTokenService) ((SPChannel) idp.getChannel()).getSecurityTokenService();
                // Maybe we can navigate the beans instead.
                Collection<SecurityTokenAuthenticator> authenticators = sts.getAuthenticators();

                for (SecurityTokenAuthenticator authenticator : authenticators) {

                    if (authenticator instanceof SpnegoSecurityTokenAuthenticator) {

                        SpnegoSecurityTokenAuthenticator spnegoAuthn = (SpnegoSecurityTokenAuthenticator) authenticator;
                        Authenticator legacyAuthenticator = spnegoAuthn.getAuthenticator();

                        for (AuthenticationScheme scheme : legacyAuthenticator.getAuthenticationSchemes()) {

                            if (scheme instanceof SpnegoAuthenticationScheme) {

                                try {

                                    // This will create a copy of the scheme !

                                    if (logger.isTraceEnabled())
                                        logger.trace("Trying WIA on authentication scheme " + scheme.getName());

                                    Subject s = new Subject();


                                    Credential spnegoCredential = legacyAuthenticator.newCredential(scheme.getName(), "spnegoSecurityToken", spnegoSecurityToken);

                                    // This will CLONE the scheme, very important
                                    SpnegoAuthenticationScheme spnegoAuthnScheme = (SpnegoAuthenticationScheme) legacyAuthenticator.getAuthenticationScheme(scheme.getName());

                                    spnegoAuthnScheme.initialize(new Credential[]{spnegoCredential}, s);

                                    if (spnegoAuthnScheme.authenticate()) {

                                        if (logger.isDebugEnabled())
                                            logger.debug("Spnego authentication success");

                                        spnegoAuthnScheme.confirm();

                                        UserClaim sp = ctx.getUserClaim(EntitySelectorConstants.ISSUER_SP_ATTR);
                                        String spId = sp != null ? (String) sp.getValue() : null;
                                        if (spId != null) {

                                            if (logger.isDebugEnabled())
                                                logger.debug("Selecting SP Channel for sp " + spId);

                                            for (FederationChannel spChannel : idp.getChannels()) {
                                                if (spChannel.getTargetProvider() != null && spChannel.getTargetProvider().getName().equals(spId)) {
                                                    if (logger.isDebugEnabled())
                                                        logger.debug("Found specific SP Channel for sp " + spId);
                                                    return spChannel.getMember();
                                                }
                                            }
                                        }

                                        // Default SP Channel
                                        return idp.getChannel().getMember();
                                    } else {
                                        spnegoAuthnScheme.cancel();
                                    }


                                } catch (SSOAuthenticationException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }

            }
        }
        return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected Set<FederatedProvider> findTrustedIdPs(EntitySelectionContext ctx, SelectorChannel channel) {

        CircleOfTrustManager cotMgr = channel.getProvider().getCotManager();

        // If we have a request issuer, look for trusted providers for it
        String spId = ctx.getRequest().getIssuer();
        Set<FederatedProvider> idps = null;

        if (spId != null) {

            FederatedProvider sp = null;
            for (FederatedProvider fp : cotMgr.getCot().getProviders()) {
                if (fp.getName().equals(spId)) {
                    sp = fp;
                    break;
                }
            }

            // Look for providers associated to this SP
            if (sp != null) {

                idps = new HashSet<FederatedProvider>();

                Set<FederatedProvider> trustedProviders = sp.getDefaultFederationService().getChannel().getTrustedProviders();

                // Adding default providers
                if (trustedProviders != null) {
                    for (FederatedProvider fp : trustedProviders) {
                        if (fp instanceof IdentityProvider)
                            idps.add(fp);
                    }
                }

                // Adding service specific providers
                for (FederationService fs  : sp.getFederationServices()) {
                    trustedProviders = fs.getChannel().getTrustedProviders();
                    if (trustedProviders != null) {
                        for (FederatedProvider fp : trustedProviders) {
                            if (fp instanceof IdentityProvider)
                                idps.add(fp);
                        }
                    }
                }
            }
        }

        if (idps == null) {
            if (logger.isDebugEnabled())
                logger.debug("Cannot find specific set of IdPs for " + spId);
            idps = cotMgr.getCot().getProviders();
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Using specific set of IdPs for " + spId + ", found: " + idps.size());
        }

        return idps;
    }
}
