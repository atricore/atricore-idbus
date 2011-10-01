package org.atricore.idbus.capabilities.openid.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.validator.UrlValidator;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;


public class OpenIDSignInPanel extends Panel {
    private static final Log logger = LogFactory.getLog(OpenIDSignInPanel.class);

    private static final long serialVersionUID = 1L;

    /**
     * El-cheapo model for form.
     */
    private final ValueMap properties = new ValueMap();

    /**
     * Field for user name.
     */
    private RequiredTextField<String> openid;
    private Button submit;

    private ClaimsRequest claimsRequest;
    private MessageQueueManager artifactQueueManager;
    private IdentityMediationUnitRegistry idsuRegistry;

    /**
     * @param id See Component constructor
     * @see org.apache.wicket.Component#Component(String)
     */
    public OpenIDSignInPanel(final String id, ClaimsRequest claimsRequest, MessageQueueManager artifactQueueManager,
                             final IdentityMediationUnitRegistry idsuRegistry
    ) {
        super(id);

        this.claimsRequest = claimsRequest;
        this.artifactQueueManager = artifactQueueManager;
        this.idsuRegistry = idsuRegistry;

        // Create feedback panel and add to page
        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        // Add sign-in form to page, passing feedback panel as
        // validation error handler
        final Form<Void> form = new Form<Void>("signInForm");

        // Attach textfield components that edit properties map
        // in lieu of a formal beans model
        form.add(openid = new RequiredTextField<String>("openid", new PropertyModel<String>(properties,
                "openid")) {

            @Override
            protected void onValid() {
                super.onValid();
                logger.info("onValid() called!");
                submit.setEnabled(true);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);    //To change body of overridden methods use File | Settings | File Templates.
                logger.info("OpenID TextField : onComponentTag");
            }

        });
        openid.setType(String.class);
        AjaxEventBehavior eb = new AjaxEventBehavior("onkeyup") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                logger.info("openid onEvent: " + target);
            }
        };
        eb.setThrottleDelay(Duration.ONE_SECOND);
        openid.add(eb);
        openid.setOutputMarkupId(true);

        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.ONE_SECOND);

        openid.add(new UrlValidator());

        form.setOutputMarkupId(true);

        submit = new IndicatingAjaxButton("apply", form)
		{
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                logger.info("onComponentTag()");
                //tag.put("style", "display:none;");
            }

            @Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{

                logger.info("submitting openid = " + openid.getDefaultModelObjectAsString());

				// repaint the feedback panel so that it is hidden
				target.addComponent(feedback);


                try {
                    String claimsConsumerUrl = signIn(getOpenid());
                    onSignInSucceeded(claimsConsumerUrl);
                } catch (Exception e) {
                    onSignInFailed();
                }

			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form)
			{
				// repaint the feedback panel so errors are shown
				target.addComponent(feedback);
			}
		};

        submit.setEnabled(false);
        form.add(submit);

//        add(submit);
        add(form);

    }


    /**
     * Convenience method to access the openid.
     *
     * @return The user name
     */
    public String getOpenid() {
        return openid.getDefaultModelObjectAsString();
    }

    /**
     * Convenience method set persistence for openid and password.
     *
     * @param enable Whether the fields should be persistent
     */
    public void setPersistent(final boolean enable) {
        openid.setPersistent(enable);
    }

    /**
     * Sign in user if possible.
     *
     * @param openid The openid
     * @return True if signin was successful
     */
    public String signIn(String openid) throws Exception {

        UUIDGenerator idGenerator = new UUIDGenerator();

        logger.info("Claims Request = " + claimsRequest);

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new ClaimImpl("openid", openid));
        //claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        ClaimsResponse response = new ClaimsResponseImpl(idGenerator.generateId(),
                null,
                claimsRequest.getId(),
                claims,
                claimsRequest.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(claimsRequest);
        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        // We want the binding factory to use a binding component to build this URL, if possible
        Channel claimsChannel = claimsRequest.getClaimsChannel();
        claimsChannel = getNonSerializedChannel(claimsChannel);

        String claimsEndpointUrl = null;
        if (claimsChannel != null) {

            MediationBindingFactory f = claimsChannel.getIdentityMediator().getBindingFactory();
            MediationBinding b = f.createBinding(SSOBinding.SSO_ARTIFACT.getValue(), claimsRequest.getClaimsChannel());

            claimsEndpointUrl = claimsEndpoint.getResponseLocation();
            if (claimsEndpointUrl == null)
                claimsEndpointUrl = claimsEndpoint.getLocation();

            if (b instanceof AbstractMediationHttpBinding) {
                AbstractMediationHttpBinding httpBinding = (AbstractMediationHttpBinding) b;
                claimsEndpointUrl = claimsEndpoint.getResponseLocation();

            } else {
                logger.warn("Cannot delegate URL construction to binding, non-http binding found " + b);
                claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                        claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
            }
        } else {

            logger.warn("Cannot delegate URL construction to binding, valid definition of channel " +
                    claimsRequest.getClaimsChannel().getName() + " not found ...");
            claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                    claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
        }

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL [" + claimsEndpointUrl + "]");

        Artifact a = artifactQueueManager.pushMessage(response);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returning claims to " + claimsEndpointUrl);

        return claimsEndpointUrl;
    }

    protected void onSignInFailed() {
        // Try the component based localizer first. If not found try the
        // application localizer. Else use the default
        error(getLocalizer().getString("signInFailed", this, "Sign in failed"));
    }

    protected void onSignInSucceeded(String claimsConsumerUrl) {
        getRequestCycle().setRequestTarget(new RedirectRequestTarget(claimsConsumerUrl));
    }

    protected EndpointDescriptor resolveClaimsEndpoint(ClaimsRequest request) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : request.getClaimsChannel().getEndpoints()) {
            // Look for unspecified claim endpoint using Artifacc binding
            if (AuthnCtxClass.OPENID_AUTHN_CTX.getValue().equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        request.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                request.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;
    }

    protected Channel getNonSerializedChannel(Channel serChannel) {

        for (IdentityMediationUnit idu : idsuRegistry.getIdentityMediationUnits()) {
            for (Channel c : idu.getChannels()) {
                if (c.getName().equals(serChannel.getName()))
                    return c;
            }
        }

        return null;
    }


}
