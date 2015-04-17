package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 * jQuery binding support
 */
public class SsoAjaxPostBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SsoHttpPostBinding.class);

    public SsoAjaxPostBinding(Channel channel) {
        super(SSOBinding.SSO_POST.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // TODO : Get an ajax post and read parameters to build the proper message
        // messageType should always be present, and used to create the message, maybe as part of the URL !? (check ajax standars/conventions)

        String messageType = "";

        if (messageType.equals("PreAuthenticatedIDPInitiantedSSO")) {
            // Create PreAuthenticatedIDPInitiantedSSO instance
        }
        return null;
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {
        // TODO !
        // this writes the response to a POST, sending JSON as content
    }

}
