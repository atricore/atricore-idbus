using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.Configuration;
using Atricore.OAuth2Protocol;


namespace Atricore.OAuth2Client
{
    public class OAuth2Client
    {

        private OAuth2Protocol.AccessTokenRequestor.OAuthPortTypeClient client;

        private String clientId;

        private String clientSecret;

        static void Main(string[] args)
        {
            string username = ConfigurationManager.AppSettings["username"];
            string password = ConfigurationManager.AppSettings["password"];

            Atricore.OAuth2Client.OAuth2Client oauth2Client = new Atricore.OAuth2Client.OAuth2Client();
            oauth2Client.init();
            String accessToken = oauth2Client.requestToken(username, password);

            string resourceServerEndpoint = ConfigurationManager.AppSettings["idpInitPreAuthn"];
            string serviceProviderAlias = ConfigurationManager.AppSettings["serviceProviderAlias"];
            string url = string.Format("{0}?atricore_sp_alias={1}&atricore_security_token={2}", resourceServerEndpoint, serviceProviderAlias, Uri.EscapeDataString(accessToken));
            Console.WriteLine(url);
        }

        public void init()
        {
            string authorizationServerEndpoint = ConfigurationManager.AppSettings["authorizationServerEndpoint"];
            clientId = ConfigurationManager.AppSettings["clientId"];
            clientSecret = ConfigurationManager.AppSettings["clientSecret"];

            var messageElement = new TextMessageEncodingBindingElement();
            var transportElement = new HttpTransportBindingElement();
            messageElement.MessageVersion = MessageVersion.CreateVersion(EnvelopeVersion.Soap11, AddressingVersion.None);
            CustomBinding binding = new CustomBinding(messageElement, transportElement);
            EndpointAddress endpoint = new EndpointAddress(new Uri(authorizationServerEndpoint));

            client = new OAuth2Protocol.AccessTokenRequestor.OAuthPortTypeClient(binding, endpoint);
        }

        public String requestToken(String usr, String pwd) 
        {
            OAuth2Protocol.AccessTokenRequestor.AccessTokenRequestType atreq = new OAuth2Protocol.AccessTokenRequestor.AccessTokenRequestType();
            atreq.clientId = clientId;
            atreq.clientSecret = clientSecret;
            atreq.username = usr;
            atreq.password = pwd;
            OAuth2Protocol.AccessTokenRequestor.AccessTokenResponseType atrsp = new OAuth2Protocol.AccessTokenRequestor.AccessTokenResponseType();
            atrsp = client.AccessTokenRequest(atreq);
            String accessToken = atrsp.accessToken;

            if (accessToken == null)
                throw new OAuth2ClientException(atrsp.error + " : " + atrsp.error_description);

            return accessToken;
        }

        /**
         * Builds a pre-authentication Url for the given username and password, and requesting
         * the default SP (as configured in the oauth2.spAlias property).
         *
         * This method calls the requestToken method.
         *
         * @param usr the username used to issue the access token
         * @param pwd the password used to issue the access token
         */
        public String buildgetIdPInitPreAuthnUrlForDefaultSp(String usr, String pwd)  
        {
            String spAlias = ConfigurationManager.AppSettings["serviceProviderAlias"];
            return buildIdPInitPreAuthnUrl(spAlias, usr, pwd);
        }


        /**
         * Builds a pre-authentication Url for the given username and password.
         *
         * This method calls the requestToken method.
         *
         * @param usr the username used to issue the access token
         * @param pwd the password used to issue the access token
         */
        public String buildIdPInitPreAuthnUrl(String usr, String pwd) 
        {
            return buildIdPInitPreAuthnUrl(null, usr, pwd);
        }


        /**
         * Builds a pre-authentication Url for the given username and password.
         *
         * This method calls the requestToken method.
         *
         * @oaran relayState as received with the pre-authn token request.
         * @param spAlias SAML SP ALias, null if no specific SP is required or known.
         * @param usr the username used to issue the access token
         * @param pwd the password used to issue the access token
         */
        public String buildIdPInitPreAuthnUrl(String spAlias, String usr, String pwd) 
        {

            String accessToken = requestToken(usr, pwd);
            String idpPreAuthn = ConfigurationManager.AppSettings["idpInitPreAuthn"];
            String preauthUrl = idpPreAuthn + "?atricore_security_token=" + Uri.EscapeDataString(accessToken) + "&scope=preauth-token";

            if (spAlias != null)
                preauthUrl += "&atricore_sp_alias=" + Uri.EscapeDataString(spAlias);

            return preauthUrl;

        }


        /**
         * Builds a pre-authentication Url for the given username and password.
         *
         * This method calls the requestToken method.
         *
         * @oaran relayState as received with the pre-authn token request.
         * @param usr the username used to issue the access token
         * @param pwd the password used to issue the access token
         */
        public String buildIdPPreAuthnResponseUrl(String relayState, String usr, String pwd) {

            String accessToken = requestToken(usr, pwd);
            String idpPreAuthn = ConfigurationManager.AppSettings["idpPreAuthnResponse"];
            String preauthUrl = idpPreAuthn + "?atricore_security_token=" + Uri.EscapeDataString(accessToken) + " &scope=preauth-token";

            if (relayState != null)
                preauthUrl += "&relay_state=" + Uri.EscapeDataString(relayState);

            return preauthUrl;

        }
    }

    [Serializable()]
    public class OAuth2ClientException : System.Exception
    {
        public OAuth2ClientException () : base() { }
        public OAuth2ClientException (string message) : base(message) { }
        public OAuth2ClientException (string message, System.Exception inner) : base(message, inner) { }

        protected OAuth2ClientException(System.Runtime.Serialization.SerializationInfo info,
            System.Runtime.Serialization.StreamingContext context) { }
    }
}
