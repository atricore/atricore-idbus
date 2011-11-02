package org.atricore.idbus.capabilities.oauth2.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuthMediator  extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OAuthMediator.class);

    public OAuthMediator() {
        logger.info("OAuthMediator Instantiated");
    }

    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
