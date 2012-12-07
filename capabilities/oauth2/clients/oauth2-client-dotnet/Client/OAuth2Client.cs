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
    class OAuth2Client
    {
        static void Main(string[] args)
        {
                string clientId = ConfigurationManager.AppSettings["clientId"];
                string clientSecret = ConfigurationManager.AppSettings["clientSecret"];
                string username = ConfigurationManager.AppSettings["username"];
                string password = ConfigurationManager.AppSettings["password"];
                string authorizationServerEndpoint = ConfigurationManager.AppSettings["authorizationServerEndpoint"];
                string serviceProviderAlias = ConfigurationManager.AppSettings["serviceProviderAlias"];
                string resourceServerEndpoint = ConfigurationManager.AppSettings["resourceServerEndpoint"];

                var messageElement = new TextMessageEncodingBindingElement();
                var transportElement = new HttpTransportBindingElement();
                messageElement.MessageVersion = MessageVersion.CreateVersion(EnvelopeVersion.Soap11, AddressingVersion.None);
                CustomBinding binding = new CustomBinding(messageElement, transportElement);
                EndpointAddress endpoint = new EndpointAddress(new Uri(authorizationServerEndpoint));

                OAuth2Protocol.AccessTokenRequestor.OAuthPortTypeClient client = new OAuth2Protocol.AccessTokenRequestor.OAuthPortTypeClient(binding, endpoint);

                OAuth2Protocol.AccessTokenRequestor.AccessTokenRequestType atreq = new OAuth2Protocol.AccessTokenRequestor.AccessTokenRequestType();
                atreq.clientId = clientId;
                atreq.clientSecret = clientSecret;
                atreq.username = username;
                atreq.password = password;
                OAuth2Protocol.AccessTokenRequestor.AccessTokenResponseType atrsp = new OAuth2Protocol.AccessTokenRequestor.AccessTokenResponseType();
                atrsp = client.AccessTokenRequest(atreq);
                String accessToken = atrsp.accessToken;

                string url = string.Format("{0}?atricore_sp_alias={1}&atricore_security_token={2}", resourceServerEndpoint, serviceProviderAlias, Uri.EscapeDataString(accessToken));
                
                Console.WriteLine(url);
        }
    }
}
