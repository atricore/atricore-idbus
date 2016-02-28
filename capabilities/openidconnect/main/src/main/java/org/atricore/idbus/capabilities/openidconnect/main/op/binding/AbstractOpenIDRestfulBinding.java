package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 */
public abstract class AbstractOpenIDRestfulBinding extends AbstractMediationHttpBinding {


    private static final Log logger = LogFactory.getLog(AbstractOpenIDRestfulBinding.class);

    public AbstractOpenIDRestfulBinding(String binding, Channel channel) {
        super(binding, channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        return null;
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {

    }

    protected int getRetryCount() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryCountStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryCount");
        if (retryCountStr == null)
            return -1;

        int retryCount = Integer.parseInt(retryCountStr);
        if (retryCount < 1) {
            logger.warn("Configuratio property 'binding.restful.loadStateRetryCount' cannot be " + retryCount);
            retryCount = 3;
        }

        return retryCount;
    }

    protected long getRetryDelay() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryDelayStr = getConfigurationContext().getProperty("binding.soap.loadStateRetryDelay");
        if (retryDelayStr == null)
            return -1;

        long retryDelay = Long.parseLong(retryDelayStr);
        if (retryDelay < 0) {
            logger.warn("Configuratio property 'binding.restful.loadStateRetryDelay' cannot be " + retryDelay);
            retryDelay = 100;
        }

        return retryDelay;

    }

    protected java.util.Map<String, String> getParameters(String httpBody) throws IOException {

        java.util.Map<String, String> params = new HashMap<String, String>();
        if (httpBody == null)
            return params;

        StringTokenizer st = new StringTokenizer(httpBody, "&");
        while (st.hasMoreTokens()) {
            String param = st.nextToken();
            int pos = param.indexOf('=');
            String key = URLDecoder.decode(param.substring(0, pos), "UTF-8"); // TODO : Can encoding be modified?
            String value = URLDecoder.decode(param.substring(pos + 1), "UTF-8");

            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Parameter " + key + "=[" + value + "]");
            }
            params.put(key, value);
        }

        return params;
    }
}
