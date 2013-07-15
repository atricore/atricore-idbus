package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.handler.render.PageRenderer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOIdPApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.PrepareAddUserResponse;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/9/13
 */
public class ReqRegistrationPanel extends Panel {

    private static final Log logger = LogFactory.getLog(ReqRegistrationPanel.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private Form form;

    private SubmitLink submit;

    private String confirmUrl;

    public ReqRegistrationPanel(String id) {
        super(id);

        form = new StatelessForm<ReqRegistrationModel>("reqRegistrationForm", new CompoundPropertyModel<ReqRegistrationModel>(new ReqRegistrationModel()));

        final EmailTextField username = new EmailTextField("username");
        form.add(username);

        final RequiredTextField<String> firstName = new RequiredTextField<String>("firstName");
        form.add(firstName);

        final RequiredTextField<String> lastName = new RequiredTextField<String>("lastName");
        form.add(lastName);

        final RequiredTextField<String> company = new RequiredTextField<String>("company");
        form.add(company);

        final RequiredTextField<String> phone = new RequiredTextField<String>("phone");
        form.add(phone);

        // TODO : Internationalize
        submit = new SubmitLink("doReqRegistration")  {

            @Override
            public void onSubmit() {

                try {

                    reqRegistration();
                } catch (UserExistsException e) {
                    logger.debug("User already exists " + e.getUsername());
                    onUserExists(e.getUsername());
                    return;

                } catch (Exception e) {
                    logger.error("Fatal error during password reset request : " + e.getMessage(), e);
                    onReqRegistrationFailed();
                    return;
                }

                onReqRegistrationSucceeded();
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

        SSOWebSession s = (SSOWebSession) getSession();

        RegistrationState state = s.getRegistrationState();
        if (state == null) {
            state = new RegistrationState();
            s.setRegistrationState(state);
        }

    }

    protected ReqRegistrationModel getReqRegistrationModel() {
        return (ReqRegistrationModel) form.getDefaultModelObject();
    }

    protected void reqRegistration() throws UserExistsException, ProvisioningException {

        SSOIdPApplication app = (SSOIdPApplication) getApplication();

        // Lookup user
        ReqRegistrationModel registration = getReqRegistrationModel();
        String username = registration.getUsername();

        FindUserByUsernameRequest userReq = new FindUserByUsernameRequest();
        userReq.setUsername(username);

        try {
            FindUserByUsernameResponse userResp = app.getProvisioningTarget().findUserByUsername(userReq);
            User user = userResp.getUser();
            // This is a problem, we cannot registration this user again, should we notify the user ?
            throw new UserExistsException(user.getUserName());
        } catch (UserNotFoundException e) {
            // This is the expected outcome, user is new
        }

        // Start registration process
        AddUserRequest req = new AddUserRequest();

        req.setUserName(username);
        req.setEmail(username);
        req.setFirstName(registration.getFirstName());
        req.setSurename(registration.getLastName());
        req.setOrganizationName(registration.getCompany());
        req.setTelephoneNumber(registration.getPhone());

        PrepareAddUserResponse resp = app.getProvisioningTarget().prepareAddUser(req);

        // Create and send email using transaction ID
        String transactionId = resp.getTransactionId();


        // -----------------------------------------------------------

        String path = RequestCycle.get().getRequest().getFilterPath();
        String pagePath = urlFor(((BaseWebApplication)getApplication()).resolvePage("SS/CONFIRM"), new PageParameters().add("transactionId", transactionId)).toString();
        // This is a relative path !, now it's ../CONFIRM
        pagePath = pagePath.substring(2);

        path = path + "/SS" + pagePath;

        Url url  = RequestCycle.get().getRequest().getClientUrl();

        confirmUrl = url.getProtocol() + "://" +
                url.getHost() + (url.getPort() != 443 && url.getPort() != 80 ? ":" + url.getPort() + "" : "") +
                path;

        // TODO : Make from and subject also brandlable/internationlized
        app.getMailService().send("josso@atricore.com",
                username,
                "Registration", buildEMailText(registration, resp, transactionId, confirmUrl).toString(),
                "text/html");



    }

    protected void onUserExists(String username) {
        RegistrationState s = ((SSOWebSession)getSession()).getRegistrationState();
        s.setRetries(s.getRetries() + 1);
        error(getLocalizer().getString("userExists.info", this, "User already registered"));
    }

    protected void onReqRegistrationSucceeded() {
        submit.setEnabled(false);
        //error(getLocalizer().getString("reqRegistrationSucceeded", this, "Operation succeeded"));
        // error(confirmUrl);
        throw new RestartResponseAtInterceptPageException(((BaseWebApplication)getApplication()).resolvePage("SS/REGISTERED"));
    }

    protected void onReqRegistrationFailed() {
        // submit.setEnabled(false);
        RegistrationState s = ((SSOWebSession)getSession()).getRegistrationState();
        s.setRetries(s.getRetries() + 1);
        error(getLocalizer().getString("app.error", this, "Operation failed"));
    }

    // TODO : Make it a utility ?!
    protected CharSequence renderPage(final Class<? extends Page> pageClass, PageParameters parameters) {

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


    protected CharSequence buildEMailText(ReqRegistrationModel registration, PrepareAddUserResponse resp, String transactionId, String confirmUrl) {

        PageParameters parameters = new PageParameters();

        parameters.set("firstName", registration.getFirstName());
        parameters.set("lastName", registration.getFirstName());
        parameters.set("name", registration.getFirstName() + " " + registration.getLastName());
        parameters.set("transactionId", transactionId);
        parameters.set("confirmUrl", confirmUrl);
        parameters.set("tmpPassword", resp.getPassword());

        return renderPage(RegistrationEMailTemplate.class, parameters);
    }

    protected class UserExistsException extends Exception {
        private String username ;

        public UserExistsException(String username) {
            super("User " + username + " already exists");
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }
}
