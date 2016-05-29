package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.handler.render.PageRenderer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration.RegistrationStartedPage;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.ResetPasswordRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.PrepareResetPasswordResponse;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class ReqPwdResetPanel extends Panel {

    private static final Log logger = LogFactory.getLog(ReqPwdResetPanel.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private Form form;

    private SubmitLink submit;

    private User user;

    public ReqPwdResetPanel(String id) {
        super(id);

        form = new StatelessForm<ReqPwdResetModel>("reqPwdResetForm", new CompoundPropertyModel<ReqPwdResetModel>(new ReqPwdResetModel()));

        final RequiredTextField<String> username = new RequiredTextField<String>("username");
        form.add(username);

        submit = new SubmitLink("doReqPwdReset")  {

            @Override
            public void onSubmit() {
                try {
                    reqPwdReset();

                } catch (UserNotFoundException e) {
                    // Hide the fact that the user does not exist
                    onReqPwdResetFailed();
                    return;
                } catch (Exception e) {
                    logger.error("Fatal error during password reset request : " + e.getMessage(), e);
                    onReqPwdResetFailed();
                    return;
                }
                onReqPwdResetSucceeded();
            }
        };

        form.add(submit);

        add(form);

        // Create feedback panel and add it to page
        final WebMarkupContainer feedbackBox = new WebMarkupContainer("feedbackBox");
        add(feedbackBox);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedbackBox.add(feedback);

    }

    protected ReqPwdResetModel getPwdResetModel() {
        return (ReqPwdResetModel) form.getDefaultModelObject();
    }

    protected void reqPwdReset() throws ProvisioningException {

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        // Lookup user
        ReqPwdResetModel pwdReset = getPwdResetModel();
        String username = pwdReset.getUsername();

        FindUserByUsernameRequest userReq = new FindUserByUsernameRequest();
        userReq.setUsername(username);

        FindUserByUsernameResponse userResp = app.getProvisioningTarget().findUserByUsername(userReq);
        user = userResp.getUser();


        // Start request process
        ResetPasswordRequest req = new ResetPasswordRequest(user);
        PrepareResetPasswordResponse resp = app.getProvisioningTarget().prepareResetPassword(req);

        // Create and send email using transaction ID
        String t = resp.getTransactionId();
        String c = resp.getCode();

        String from = getLocalizer().getString("email.sender", this, "josso@swirebev.com");
        app.getMailService().send(from,
                user.getEmail(),
                "Password Reset", buildEMailText(user, t).toString(),
                "text/html");

    }

    protected void onReqPwdResetSucceeded() {
        submit.setEnabled(false);
        error(getLocalizer().getString("reqPwdResetSucceeded", this, "Operation succeeded"));

        //PageParameters params = new PageParameters();
        //params.add("username", user.getUserName());
        //throw new RestartResponseAtInterceptPageException(((BaseWebApplication)getApplication()).resolvePage("SS/VFYPWDRESET"), params);

    }

    protected void onReqPwdResetFailed() {
        submit.setEnabled(false);
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }

    // TODO : Make it a utility ?!
    protected CharSequence renderPage(final Class<? extends Page> pageClass, PageParameters parameters)
    {

        final RenderPageRequestHandler handler = new RenderPageRequestHandler(new PageProvider(
                pageClass, parameters), RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT);

        final PageRenderer pageRenderer = getApplication().getPageRendererProvider().get(handler);

        RequestCycle originalRequestCycle = getRequestCycle();

        BufferedWebResponse tempResponse = new BufferedWebResponse(null);

        RequestCycleContext requestCycleContext = new RequestCycleContext(originalRequestCycle.getRequest(),
                tempResponse, getApplication().getRootRequestMapper(), getApplication().getExceptionMapperProvider().get());
        RequestCycle tempRequestCycle = new RequestCycle(requestCycleContext);

        final Response oldResponse = originalRequestCycle.getResponse();

        try
        {
            originalRequestCycle.setResponse(tempResponse);
            pageRenderer.respond(tempRequestCycle);
        }
        finally
        {
            originalRequestCycle.setResponse(oldResponse);
        }

        return tempResponse.getText();
    }


    protected CharSequence buildEMailText(User user, String transactionId) {

        PageParameters parameters = new PageParameters();
        parameters.set("name", user.getFirstName());
        parameters.set("username", user.getUserName());
        parameters.set("transactionId", transactionId);

        return renderPage(PwdResetEMailTemplate.class, parameters);
    }
}
