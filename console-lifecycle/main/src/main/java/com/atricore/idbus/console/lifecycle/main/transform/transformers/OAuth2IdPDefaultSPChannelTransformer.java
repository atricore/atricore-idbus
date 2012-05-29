package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates OAuth 2.0 Default SP channel for local IdPs with OAuth 2.0 enabled
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2IdPDefaultSPChannelTransformer extends AbstractOAuth2SPChannelTransformer {

    private static final Log logger = LogFactory.getLog(OAuth2IdPDefaultSPChannelTransformer.class);

    public OAuth2IdPDefaultSPChannelTransformer() {
        super();
        setContextSpChannelBean("defaultOauth2SPChannelBean");
    }

    @Override
    public boolean accept(TransformEvent event) {
        // TODO : Make sure that OAuth 2.0 is enabled
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        IdentityProvider idp = (IdentityProvider) event.getData();
        generateIdPComponents(idp, null, null, null, null, event.getContext());
    }
}
