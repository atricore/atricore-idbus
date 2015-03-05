using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Atricore.OAuth2Client;

namespace PreAuthnApp
{
    public partial class Login : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            // Store relay state for later
            String relayState = Request.QueryString["relay_state"];
            HttpContext.Current.Session["relay_state"] = relayState;
        }

        protected void SignIn_Click(object sender, EventArgs e)
        {

            // Create new OAuth2 client instance
            OAuth2Client client = new OAuth2Client();
            client.init();

            // Recover previously stored relay state
            String relayState = (String) HttpContext.Current.Session["relay_state"];

            // Perform pre-authentication, the created URL will have the proper authz token as request parameter
            String idpUrl = client.buildIdPPreAuthnResponseUrl(relayState, UserName.Text, UserPass.Text);

            Response.Redirect(idpUrl);

        }
    }


}