package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2IdpSPChannelTransformer extends AbstractOAuth2SPChannelTransformer {

    private static final Log logger = LogFactory.getLog(OAuth2IdpSPChannelTransformer.class);

    public OAuth2IdpSPChannelTransformer() {
        super();
        setContextSpChannelBean("defaultSPChannelBean");
    }

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        IdentityProvider idp = (IdentityProvider) event.getData();
        generateIdPComponents(idp, null, null, null, null, event.getContext());
    }
}
