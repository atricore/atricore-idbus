package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectOPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 HTTP/1.1 200 OK<br/>
Content-Type: application/json<br/>
 <br/>
{<br/>
"issuer":<br/>
 "https://server.example.com",<br/>
"authorization_endpoint":
 "https://server.example.com/connect/authorize",<br/>
"token_endpoint":
 "https://server.example.com/connect/token",<br/>
"token_endpoint_auth_methods_supported":
 ["client_secret_basic", "private_key_jwt"],<br/>
"token_endpoint_auth_signing_alg_values_supported":
 ["RS256", "ES256"],<br/>
"userinfo_endpoint":
 "https://server.example.com/connect/userinfo",<br/>
"check_session_iframe":
 "https://server.example.com/connect/check_session",<br/>
"end_session_endpoint":
 "https://server.example.com/connect/end_session",<br/>
"jwks_uri":
 "https://server.example.com/jwks.json",<br/>
"registration_endpoint":<br/>
 "https://server.example.com/connect/register",<br/>
"scopes_supported":
 ["openid", "profile", "email", "address",
  "phone", "offline_access"],<br/>
"response_types_supported":
 ["code", "code id_token", "id_token", "token id_token"],<br/>
"acr_values_supported":
 ["urn:mace:incommon:iap:silver",
  "urn:mace:incommon:iap:bronze"],<br/>
"subject_types_supported":
 ["public", "pairwise"],<br/>
"userinfo_signing_alg_values_supported":
 ["RS256", "ES256", "HS256"],<br/>
"userinfo_encryption_alg_values_supported":
 ["RSA1_5", "A128KW"],<br/>
"userinfo_encryption_enc_values_supported":
 ["A128CBC-HS256", "A128GCM"],<br/>
"id_token_signing_alg_values_supported":
 ["RS256", "ES256", "HS256"],<br/>
"id_token_encryption_alg_values_supported":
 ["RSA1_5", "A128KW"],<br/>
"id_token_encryption_enc_values_supported":
 ["A128CBC-HS256", "A128GCM"],<br/>
"request_object_signing_alg_values_supported":
 ["none", "RS256", "ES256"],<br/>
"display_values_supported":
 ["page", "popup"],<br/>
"claim_types_supported":
 ["normal", "distributed"],<br/>
"claims_supported":
 ["sub", "iss", "auth_time", "acr",
  "name", "given_name", "family_name", "nickname",
  "profile", "picture", "website",
  "email", "email_verified", "locale", "zoneinfo",
  "http://example.info/claims/groups"],<br/>
"claims_parameter_supported":
 true,<br/>
"service_documentation":
 "http://server.example.com/connect/service_documentation.html",<br/>
"ui_locales_supported":
 ["en-US", "en-GB", "en-CA", "fr-FR", "fr-CA"]<br/>
}<br/>
 */
public class MetadataProducer  extends AbstractOpenIDProducer {

    private static Log logger = LogFactory.getLog(MetadataProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();


    public MetadataProducer(Endpoint endpoint) {
        super(endpoint);
    }

    /**
     * Back channel stateless endpoint
     *
     * In JOSSO, Metadata is created on a per-client bases (Relaying Party) This runs on the OIDC Binding channel
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        OIDCProviderConfigurationRequest req = (OIDCProviderConfigurationRequest) in.getMessage().getContent();
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();

        // Metadata associated to the OP
        OIDCProviderMetadata metadata = mediator.getProvider();



        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                metadata,
                "OIDCProviderInformation",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);

    }
}
