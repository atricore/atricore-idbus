package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UsernamePasscodeClaimsController extends SimpleFormController {

    private static final Log logger = LogFactory.getLog(UsernamePasscodeClaimsController.class);

    private UUIDGenerator idGenerator = new UUIDGenerator();

    private MessageQueueManager artifactQueueManager;

    private IdentityMediationUnitRegistry idauRegistry;

    @Override
    protected Object formBackingObject(HttpServletRequest hreq) throws Exception {

        String artifactId = hreq.getParameter(SsoHttpArtifactBinding.SSO_ARTIFACT_ID);

        CollectUsernamePasscodeClaims collectClaims = new CollectUsernamePasscodeClaims();
        if (logger.isDebugEnabled())
            logger.debug("Creating form backing object for artifact " + artifactId);

        // Lookup for ClaimsRequest!
        CredentialClaimsRequest credentialClaimsRequest = (CredentialClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));

        if (credentialClaimsRequest != null) {

            if (logger.isDebugEnabled())
                logger.debug("Received claims request " + credentialClaimsRequest.getId() +
                        " from " + credentialClaimsRequest.getIssuerChannel() +
                        " at " + credentialClaimsRequest.getIssuerEndpoint());

            if (credentialClaimsRequest.getLastErrorId() != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Received last error ID : " +
                        credentialClaimsRequest.getLastErrorId() +
                        " ("+ credentialClaimsRequest.getLastErrorMsg()+")");

                hreq.setAttribute("statusMessageKey", "claims.text.invalidCredentials");
            }

            collectClaims.setCredentialClaimsRequest(credentialClaimsRequest);

        } else {
            logger.debug("No claims request received!");
        }

        return collectClaims;
    }



    @Override
    protected ModelAndView onSubmit(HttpServletRequest hreq,
                                    HttpServletResponse hres,
                                    Object o,
                                    BindException error) throws Exception {

        CollectUsernamePasscodeClaims cmd = (CollectUsernamePasscodeClaims) o;

        if (logger.isDebugEnabled())
            logger.debug("Received CMD" + cmd);

        CredentialClaimsRequest requestCredential = cmd.getCredentialClaimsRequest();
        if (logger.isDebugEnabled())
            logger.debug("Collecting usenrame/passcode claims for request " +
                    (requestCredential != null ? requestCredential.getId() : "NULL"));

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new CredentialClaimImpl("username", cmd.getUsername()));
        claims.addClaim(new CredentialClaimImpl("passcode", cmd.getPasscode()));
        claims.addClaim(new CredentialClaimImpl("rememberMe", cmd.isRememberMe()));

        CredentialClaimsResponse responseCredential = new CredentialClaimsResponseImpl(idGenerator.generateId(),
                null,
                requestCredential.getId(),
                claims,
                requestCredential.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(requestCredential);
        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        // We want the binding factory to use a binding component to build this URL, if possible
        Channel claimsChannel = requestCredential.getClaimsChannel();
        claimsChannel = getNonSerializedChannel(claimsChannel);

        String claimsEndpointUrl = null;
        if (claimsChannel != null) {

            MediationBindingFactory f = claimsChannel.getIdentityMediator().getBindingFactory();
            MediationBinding b = f.createBinding(SSOBinding.SSO_ARTIFACT.getValue(), requestCredential.getClaimsChannel());

            claimsEndpointUrl = claimsEndpoint.getResponseLocation();
            if (claimsEndpointUrl == null)
                claimsEndpointUrl = claimsEndpoint.getLocation();

            if (b instanceof AbstractMediationHttpBinding) {
                AbstractMediationHttpBinding httpBinding = (AbstractMediationHttpBinding) b;
                claimsEndpointUrl = ((AbstractMediationHttpBinding) b).buildHttpTargetLocation(hreq, claimsEndpoint, true);

            } else {
                logger.warn("Cannot delegate URL construction to binding, non-http binding found " + b);
                claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                        claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
            }
        } else {

            logger.warn("Cannot delegate URL construction to binding, valid definition of channel " +
                    requestCredential.getClaimsChannel().getName() + " not foud ...");
            claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                    claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
        }

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL ["+claimsEndpointUrl+"]");

        Artifact a = getArtifactQueueManager().pushMessage(responseCredential);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returing claims to " + claimsEndpointUrl);

        return new ModelAndView(new RedirectView(claimsEndpointUrl));
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public IdentityMediationUnitRegistry getIdauRegistry() {
        return idauRegistry;
    }

    public void setIdauRegistry(IdentityMediationUnitRegistry idauRegistry) {
        this.idauRegistry = idauRegistry;
    }

    protected EndpointDescriptor resolveClaimsEndpoint(CredentialClaimsRequest requestCredential) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : requestCredential.getClaimsChannel().getEndpoints()) {
            // Look for PWD endpoint using Artifacct binding
            if (AuthnCtxClass.TIME_SYNC_TOKEN_AUTHN_CTX.getValue().equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        requestCredential.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                requestCredential.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;
    }

    protected Channel getNonSerializedChannel(Channel serChannel) {

        for (IdentityMediationUnit idu : idauRegistry.getIdentityMediationUnits()) {
            for (Channel c : idu.getChannels()) {
                if (c.getName().equals(serChannel.getName()))
                    return c;
            }
        }

        return null;
    }
}


