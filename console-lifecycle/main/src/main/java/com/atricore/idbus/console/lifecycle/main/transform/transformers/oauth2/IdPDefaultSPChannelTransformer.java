package com.atricore.idbus.console.lifecycle.main.transform.transformers.oauth2;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.oauth2.AbstractOAuth2SPChannelTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Transformer for OAuth 2.0 IdP local services
 *
 * Creates OAuth 2.0 Default SP channel for local IdPs with OAuth 2.0 enabled
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdPDefaultSPChannelTransformer extends AbstractOAuth2SPChannelTransformer {

    private static final Log logger = LogFactory.getLog(IdPDefaultSPChannelTransformer.class);

    public IdPDefaultSPChannelTransformer() {
        super();
        setContextSpChannelBean("defaultOauth2SPChannelBean");
    }

    @Override
    public boolean accept(TransformEvent event) {
        // TODO : Make sure that OAuth 2.0 is enabled
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote() &&
                ((IdentityProvider)event.getData()).isOauth2Enabled();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        IdentityProvider idp = (IdentityProvider) event.getData();
        generateIdPComponents(idp, null, null, null, null, event.getContext());
    }
}
