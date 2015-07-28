package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Identity Mediator for OpenIDConnect Binding channels. These channels are adapters for OpenIDConnecto to SSO requests
 * (SSO/SLO)
 *
 */
public class OpenIDConnectBPMediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(OpenIDConnectBPMediator.class);

    // The SAML sp alias used as adapter
    private String spAlias;

    private Map<String, OIDCClientInformation> clients = new HashMap<String, OIDCClientInformation>();

    public OpenIDConnectBPMediator() {
        logger.info("OpenIDConnectBPMediator Instantiated");
    }

    @Override
    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {
        return null;
    }

    public String getSpAlias() {
        return spAlias;
    }

    public void setSpAlias(String spAlias) {
        this.spAlias = spAlias;
    }

    public Map<String, OIDCClientInformation> getClients() {
        return clients;
    }

    public void setClients(Map<String, OIDCClientInformation> clients) {
        this.clients = clients;
    }
}
