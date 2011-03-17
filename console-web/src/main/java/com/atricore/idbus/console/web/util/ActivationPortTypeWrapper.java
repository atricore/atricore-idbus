package com.atricore.idbus.console.web.util;

import com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType;

/**
 * Work-around for an OSGi/CXF/Spring limitation!
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationPortTypeWrapper implements ActivationPortType {

    private ActivationPortType wrapped;

    public ActivationPortType getWrapped() {
        return wrapped;
    }

    public void setWrapped(ActivationPortType wrapped) {
        this.wrapped = wrapped;
    }

    /* (non-Javadoc)
     * @see com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType#configureAgent(com.atricore.idbus.console.activation._1_0.protocol.ConfigureAgentRequestType  body )*
     */
    public com.atricore.idbus.console.activation._1_0.protocol.ConfigureAgentResponseType configureAgent(com.atricore.idbus.console.activation._1_0.protocol.ConfigureAgentRequestType body) {
        return wrapped.configureAgent(body);
    }

    /* (non-Javadoc)
     * @see com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType#activateAgent(com.atricore.idbus.console.activation._1_0.protocol.ActivateAgentRequestType  body )*
     */
    public com.atricore.idbus.console.activation._1_0.protocol.ActivateAgentResponseType activateAgent(com.atricore.idbus.console.activation._1_0.protocol.ActivateAgentRequestType body) {
        return wrapped.activateAgent(body);
    }

    /* (non-Javadoc)
     * @see com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType#activateSamples(com.atricore.idbus.console.activation._1_0.protocol.ActivateSamplesRequestType  body )*
     */
    public com.atricore.idbus.console.activation._1_0.protocol.ActivateSamplesResponseType activateSamples(com.atricore.idbus.console.activation._1_0.protocol.ActivateSamplesRequestType body) {
        return wrapped.activateSamples(body);
    }

    /* (non-Javadoc)
     * @see com.atricore.idbus.console.activation._1_0.wsdl.ActivationPortType#platformSupported(com.atricore.idbus.console.activation._1_0.protocol.PlatformSupportedRequestType  body )*
     */
    public com.atricore.idbus.console.activation._1_0.protocol.PlatformSupportedResponseType platformSupported(com.atricore.idbus.console.activation._1_0.protocol.PlatformSupportedRequestType body) {
        return wrapped.platformSupported(body);
    }

}
