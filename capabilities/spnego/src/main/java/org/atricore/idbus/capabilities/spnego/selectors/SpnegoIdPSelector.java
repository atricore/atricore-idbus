package org.atricore.idbus.capabilities.spnego.selectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spnego.SpnegoAuthenticationScheme;
import org.atricore.idbus.capabilities.spnego.SpnegoBinding;
import org.atricore.idbus.capabilities.spnego.authenticators.SpnegoSecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.internal.EntitySelectionState;
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
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

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

        CircleOfTrustManager cotMgr = channel.getProvider().getCotManager();

        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();

        // Get IdP claim channel and look for service: urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos
        for (FederatedProvider fp : cotMgr.getCot().getProviders()) {

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

        CircleOfTrustManager cotMgr = channel.getProvider().getCotManager();

        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();


        // This goes a little too deep into the dependencies tree
        // Get IdP claim channel and look for service: urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos
        for (FederatedProvider fp : cotMgr.getCot().getProviders()) {

            if (fp instanceof IdentityProvider) {
                IdentityProvider idp = (IdentityProvider) fp;

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

                                    Subject s = new Subject();

                                    String spnegoSecurityToken = binaryToken.getOtherAttributes().get( new QName( SpnegoSecurityTokenAuthenticator.SPNEGO_NS) );

                                    Credential spnegoCredential = legacyAuthenticator.newCredential(scheme.getName(), "spnegoSecurityToken", spnegoSecurityToken);
                                    SpnegoAuthenticationScheme spnegoAuthnScheme = (SpnegoAuthenticationScheme) scheme.clone();
                                    spnegoAuthnScheme.initialize(new Credential[]{spnegoCredential}, s);

                                    if (spnegoAuthnScheme.authenticate()) {
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
}
