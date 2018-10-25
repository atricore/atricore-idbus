package org.atricore.idbus.capabilities.preauthn;

import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;

public interface ClaimColletor {

    ClaimSet collect(String federatedProvider, CamelMediationMessage message);
}
