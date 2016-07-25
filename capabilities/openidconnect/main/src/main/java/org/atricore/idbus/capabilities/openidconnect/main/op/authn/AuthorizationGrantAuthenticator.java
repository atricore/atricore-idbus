package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.Util;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Created by sgonzalez.
 */
public class AuthorizationGrantAuthenticator extends AbstractSecurityTokenAuthenticator {

    private static final Log logger = LogFactory.getLog(AuthorizationGrantAuthenticator.class);

    public static final String SCHEME_NAME = "oidc-authzgrant-authentication";

    public AuthorizationGrantAuthenticator() {
        super();
        setScheme(SCHEME_NAME);
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        setScheme(SCHEME_NAME);

        try {
            BinarySecurityTokenType authzGrantToken = (BinarySecurityTokenType) requestToken;

            String params = authzGrantToken.getValue();
            AuthorizationGrant authzGrant = AuthorizationGrant.parse(Util.unmarshall(params));

            Credential c = new AuthorizationCodeGrantCredential(authzGrant);

            return new Credential[]{c};
        } catch (ClassNotFoundException e) {
            throw new SSOAuthenticationException(e);
        } catch (ParseException e) {
            throw new SSOAuthenticationException(e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new SSOAuthenticationException(e);
        }
    }

    @Override
    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof BinarySecurityTokenType) {
            BinarySecurityTokenType authzGrantToken = (BinarySecurityTokenType) requestToken;
            return authzGrantToken.getOtherAttributes().containsKey( new QName( AUTHZ_CODE_NS) );
        }

        return false;
    }

}
