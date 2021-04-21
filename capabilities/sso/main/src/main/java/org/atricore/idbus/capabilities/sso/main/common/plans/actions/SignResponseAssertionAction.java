package org.atricore.idbus.capabilities.sso.main.common.plans.actions;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.metadata.SPSSODescriptorType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.sp.IDPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Signs SAML R2 Assertions part of a response
 */
public class SignResponseAssertionAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(SignResponseAssertionAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        StatusResponseType response = (StatusResponseType) out.getContent();

        FederationChannel channel = (FederationChannel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        SamlR2Signer signer = mediator.getSigner();

        ChannelConfiguration cfg = mediator.getChannelConfig(channel.getName());

        String digest = null;
        if (cfg instanceof SPChannelConfiguration) {
            digest = ((SPChannelConfiguration) cfg).getSignatureHash();
        } else if (cfg instanceof IDPChannelConfiguration) {
            digest = ((IDPChannelConfiguration) cfg).getSignatureHash();
        } else {
            digest = "SHA256";
        }


        String signatureHash = cfg instanceof IDPChannelConfiguration ?
                ((IDPChannelConfiguration) cfg).getSignatureHash() : ((SPChannelConfiguration) cfg).getSignatureHash();

        CircleOfTrustMemberDescriptor dest =
                (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable(VAR_DESTINATION_COT_MEMBER);

        // If the Response can contains assertions
        if (response instanceof ResponseType) {

            // Mediator configuration as default for assertions signature
            boolean signAssertion = true;
            if (dest != null) {

                EntityDescriptorType entity = (EntityDescriptorType) dest.getMetadata().getEntry();

                // This is the destination entity, we need to figure out the role, only SPs can request signed assertions:
                for (RoleDescriptorType role : entity.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
                    if (role instanceof SPSSODescriptorType) {
                        SPSSODescriptorType spRole = (SPSSODescriptorType) role;

                        if (spRole.getWantAssertionsSigned() != null)
                            signAssertion = spRole.getWantAssertionsSigned();
                        break;
                    }
                }

            }

            if (((ResponseType)response).getAssertionOrEncryptedAssertion().size() > 0) {

                List assertions = new ArrayList();
                for (Object o : ((ResponseType)response).getAssertionOrEncryptedAssertion()) {

                    if (o instanceof AssertionType) {

                        AssertionType assertion = (AssertionType) o;

                        if (logger.isDebugEnabled())
                            logger.debug("Signing SAMLR2 Assertion: " + assertion.getID() + " in channel " + channel.getName());

                        // The Assertion is now enveloped within a SAML Response so we need to sign a second time within this context.
                        assertion.setSignature(null);

                        // If the response has an assertion, remove the signature and re-sign it ... (we're discarding STS signature!)
                        if (signAssertion) {
                            AssertionType signedAssertion =  signer.sign(assertion, digest);
                            assertions.add(signedAssertion);
                        } else {
                            assertions.add(assertion);
                        }
                    } else {
                        // Keep encrypted assertions as they are
                        assertions.add(o);
                    }

                }
                // Replace the assertions (should be only one!)
                ((ResponseType)response).getAssertionOrEncryptedAssertion().clear();
                ((ResponseType)response).getAssertionOrEncryptedAssertion().addAll(assertions);


            }
        }

        out.replaceContent(response);

    }
}
