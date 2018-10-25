package org.atricore.idbus.capabilities.preauthn;

import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;
import java.util.Map;

public class HTTPHeaderCollector implements ClaimColletor {

    private String headerName;

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public ClaimSet collect(String federatedProvider, CamelMediationMessage message) {

        ClaimSet claims = new ClaimSetImpl();

        Map<String, Object> headers = message.getHeaders();

        for (String name : headers.keySet()) {

            String camelHeaderName = "org.atricore.idbus.http.Header." + headerName;

            if (name.equals(camelHeaderName)) {
                BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
                binarySecurityToken.setValueType((String) headers.get(camelHeaderName));
                binarySecurityToken.getOtherAttributes().put(new QName(Constants.TOKEN_NS, "HTTP-Header"), headerName);
                binarySecurityToken.getOtherAttributes().put(new QName(Constants.TOKEN_NS, "idp"), federatedProvider);

                CredentialClaim credentialClaim = new CredentialClaimImpl(AuthnCtxClass.CUSTOM_PREAUTHN_CTX.getValue(), binarySecurityToken);

                claims.addClaim(credentialClaim);
            }
        }

        return claims;
    }
}
