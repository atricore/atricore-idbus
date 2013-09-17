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

            string resourceServerEndpoint = ConfigurationManager.AppSettings["resourceServerEndpoint"];
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
