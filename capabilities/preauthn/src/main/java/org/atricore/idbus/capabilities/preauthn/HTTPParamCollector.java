package org.atricore.idbus.capabilities.preauthn;

import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;
import java.util.Map;

public class HTTPParamCollector implements ClaimColletor {

    private String paramName;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public ClaimSet collect(String federatedProvider, CamelMediationMessage message) {
        ClaimSet claims = new ClaimSetImpl();

        MediationState state = message.getMessage().getState();

        Map<String, Object> headers = message.getHeaders();

        for (String name : state.getTransientVarNames()) {

            if (name.equals(paramName)) {
                BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
                binarySecurityToken.setValueType((String) headers.get(state.getTransientVariable(paramName)));
                binarySecurityToken.getOtherAttributes().put(new QName(Constants.TOKEN_NS, "HTTP-Param"), paramName);
                binarySecurityToken.getOtherAttributes().put(new QName(Constants.TOKEN_NS, "idp"), federatedProvider);

                CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.CUSTOM_PREAUTHN_CTX.getValue(), binarySecurityToken);

                claims.addClaim(credentialClaim);
            }
        }

        return claims;
    }
}
