package org.atricore.idbus.capabilities.sso.main.sp.plans;

import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlR2AuthnResponseToSPAuthnResponse extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "spsso-samlr2authnresp-to-ssospauthnresp";
    }

}

