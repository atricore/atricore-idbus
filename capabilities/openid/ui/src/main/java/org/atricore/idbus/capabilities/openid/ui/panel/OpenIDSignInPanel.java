package org.atricore.idbus.capabilities.openid.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimsRequest;


public class OpenIDSignInPanel extends Panel
{
    private static final Log logger = LogFactory.getLog(OpenIDSignInPanel.class);

	private static final long serialVersionUID = 1L;

	/** Field for user name. */
	private TextField<String> openid;

    private ClaimsRequest claimsRequest;

	/**
	 * Sign in form.
	 */
	public final class OpenIDSignInForm extends StatelessForm<Void>
	{
		private static final long serialVersionUID = 1L;

		/** El-cheapo model for form. */
		private final ValueMap properties = new ValueMap();

        /**
		 * Constructor.
		 * 
		 * @param id
		 *            id of the form component
		 */
		public OpenIDSignInForm(final String id)
		{
			super(id);

			// Attach textfield components that edit properties map
			// in lieu of a formal beans model
			add(openid = new TextField<String>("openid", new PropertyModel<String>(properties,
                    "openid")));

			openid.setType(String.class);

		}

		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		public final void onSubmit()
		{
			if (signIn(getOpenid()))
			{
				onSignInSucceeded();
			}
			else
			{
				onSignInFailed();
			}
		}
	}

	/**
	 * @param id
	 *            See Component constructor
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public OpenIDSignInPanel(final String id, ClaimsRequest claimsRequest)
	{
		super(id);

        this.claimsRequest = claimsRequest;

		// Add sign-in form to page, passing feedback panel as
		// validation error handler
		add(new OpenIDSignInForm("signInForm"));
	}

	/**
	 * Removes persisted form data for the signin panel (forget me)
	 */
	public final void forgetMe()
	{
		// Remove persisted user data. Search for child component
		// of type OpenIDSignInForm and remove its related persistence values.
		getPage().removePersistedFormData(OpenIDSignInForm.class, true);
	}

	/**
	 * Convenience method to access the openid.
	 * 
	 * @return The user name
	 */
	public String getOpenid()
	{
		return openid.getDefaultModelObjectAsString();
	}

	/**
	 * Convenience method set persistence for openid and password.
	 * 
	 * @param enable
	 *            Whether the fields should be persistent
	 */
	public void setPersistent(final boolean enable)
	{
		openid.setPersistent(enable);
	}

	/**
	 * Sign in user if possible.
	 * 
	 * @param openid
	 *            The openid
	 * @return True if signin was successful
	 */
	public boolean signIn(String openid)
	{

        logger.info("Claims Request = " + claimsRequest);

		//return AuthenticatedWebSession.get().signIn(username, password);
        return true;
	}

	protected void onSignInFailed()
	{
		// Try the component based localizer first. If not found try the
		// application localizer. Else use the default
		error(getLocalizer().getString("signInFailed", this, "Sign in failed"));
	}

	protected void onSignInSucceeded()
	{
		// If login has been called because the user was not yet
		// logged in, than continue to the original destination,
		// otherwise to the Home page
		if (!continueToOriginalDestination())
		{
			setResponsePage(getApplication().getHomePage());
		}
	}
}
