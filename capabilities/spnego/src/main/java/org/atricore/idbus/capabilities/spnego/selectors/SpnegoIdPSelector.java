package org.atricore.idbus.capabilities.spnego.selectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spnego.SpnegoAuthenticationScheme;
import org.atricore.idbus.capabilities.spnego.authenticators.SpnegoSecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 12/3/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpnegoIdPSelector extends AbstractEntitySelector implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpnegoIdPSelector.class);

    private ApplicationContext applicationContext;

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx) throws SSOException {

        BinarySecurityTokenType binaryToken = (BinarySecurityTokenType) ctx.getAttribute(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue());
        if (binaryToken == null)
            return null;

        Map<String ,Authenticator> authenticators = applicationContext.getBeansOfType(Authenticator.class);

        for (FederatedProvider fp : ctx.getCotManager().getCot().getProviders()) {
            if (fp instanceof IdentityProvider) {
                IdentityProvider idp = (IdentityProvider) fp;

                // Maybe we can navigate the beans instead.
                Authenticator idpAuthn = authenticators.get(idp.getName() + "-legacy-authenticator");

                for (AuthenticationScheme scheme : idpAuthn.getAuthenticationSchemes()) {

                    if (scheme instanceof SpnegoAuthenticationScheme) {

                        try {

                            Subject s = new Subject();

                            String spnegoSecurityToken = binaryToken.getOtherAttributes().get( new QName( SpnegoSecurityTokenAuthenticator.SPNEGO_NS) );

                            Credential spnegoCredential = idpAuthn.newCredential(scheme.getName(), "spnegoSecurityToken", spnegoSecurityToken);
                            SpnegoAuthenticationScheme spnegoAuthn = (SpnegoAuthenticationScheme) scheme.clone();
                            spnegoAuthn.initialize(new Credential[] {spnegoCredential}, s);

                            if (spnegoAuthn.authenticate()) {
                                spnegoAuthn.confirm();
                                // TODO !!!! We have the SP and the IDP, Now, resolve the IDP channel that must be used
                                return idp.getAllMembers().get(0);
                            } else {
                                spnegoAuthn.cancel();
                            }


                        } catch (SSOAuthenticationException e) {
                            logger.error(e.getMessage(), e);
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
