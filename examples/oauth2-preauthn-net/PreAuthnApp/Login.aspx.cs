using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Atricore.OAuth2Client;
using OAuth2ResourceServer;
using OAuth2Common;

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

            // Create a new OAuth2 secure access token resolver
            SecureAccessTokenResolver tr = new SecureAccessTokenResolver();
            tr.init();

            // Recover previously stored relay state
            String relayState = (String) HttpContext.Current.Session["relay_state"];

            // Request an access token
            String accessToken = client.requestToken(UserName.Text, UserPass.Text);

            // Resolve the token to get user information
            OAuth2AccessToken at = tr.resolve(accessToken);

            String userId = at.UserId;
            String email = at.getAttribute("email");

            // Perform pre-authentication, the created URL will have the proper authz token as request parameter
            String idpUrl = client.buildIdPPreAuthnResponseUrl(relayState, UserName.Text, UserPass.Text);

            Response.Redirect(idpUrl);

        }
    }


}