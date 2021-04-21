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
using Atricore.OAuth2Protocol.AccessTokenRequestor;


namespace Atricore.OAuth2Client
{
    public class OAuth2Client
    {

        private OAuthPortTypeClient client;

        private OAuthPortTypeClient pwdlessClient;

        private String clientId;

        private String clientSecret;

        public void init()
        {
            string authorizationServerEndpoint = ConfigurationManager.AppSettings["authorizationServerEndpoint"];

            string pwdlessAuthnServerEndpoint = ConfigurationManager.AppSettings["passworldessAuthnServerEndpoint"];

            clientId = ConfigurationManager.AppSettings["clientId"];
            clientSecret = ConfigurationManager.AppSettings["clientSecret"];
            string endpointTransportGuarantee = ConfigurationManager.AppSettings["endpointTransportGuarantee"];
            bool disableCertificateValidation = Convert.ToBoolean(ConfigurationManager.AppSettings["disableCertificateValidation"]);

            var messageElement = new TextMessageEncodingBindingElement();
            TransportBindingElement transportElement;
            if ("NONE".Equals(endpointTransportGuarantee)) {
                transportElement = new HttpTransportBindingElement();
            } else if ("CONFIDENTIAL".Equals(endpointTransportGuarantee)) {
                transportElement = new HttpsTransportBindingElement();
            } else {
                throw new OAuth2ClientException("Invalid endpointTransportGuarantee value: " + endpointTransportGuarantee);
            }
            messageElement.MessageVersion = MessageVersion.CreateVersion(EnvelopeVersion.Soap11, AddressingVersion.None);
            CustomBinding binding = new CustomBinding(messageElement, transportElement);
            EndpointAddress endpoint = new EndpointAddress(new Uri(authorizationServerEndpoint));
            EndpointAddress pwdlessEndpoint = new EndpointAddress(new Uri(pwdlessAuthnServerEndpoint));

            client = new OAuthPortTypeClient(binding, endpoint);
            pwdlessClient = new OAuthPortTypeClient(binding, pwdlessEndpoint);

            if (disableCertificateValidation)
                System.Net.ServicePointManager.ServerCertificateValidationCallback = ((sender, certificate, chain, sslPolicyErrors) => true);
        }

        public String requestToken(String usr, String pwd) 
        {
            AccessTokenRequestType atreq = new AccessTokenRequestType();
            atreq.clientId = clientId;
            atreq.clientSecret = clientSecret;
            atreq.username = usr;
            atreq.password = pwd;
            AccessTokenResponseType atrsp = new AccessTokenResponseType();
            atrsp = client.AccessTokenRequest(atreq);
            String accessToken = atrsp.accessToken;

            if (accessToken == null)
                throw new OAuth2ClientException(atrsp.error + " : " + atrsp.error_description);

            return accessToken;
        }

        /**
         *  Send a passwordless authentication message to the user.
         *  
         * @username user identifier
         * @targetSP target service provider alias
         */
        public void sendPasswordlessLink(String username, String targetSP)
        {

            sendPasswordlessLink(username, targetSP, null);
        }

        /**
         *  Send a passwordless authentication message to the user, specifying properties to be used to build the message.
         *  
         * @username user identifier
         * @targetSP target service provider alias
         * @properties additional properties
         */
        public void sendPasswordlessLink(String username, String targetSP, Dictionary<string, string> properties)
        {
            SendPasswordlessLinkRequestType pwdAuthnReq = new SendPasswordlessLinkRequestType();
            pwdAuthnReq.clientId = clientId;
            pwdAuthnReq.clientSecret = clientSecret;
            pwdAuthnReq.username = username;
            pwdAuthnReq.targetSP = targetSP;

            if (properties != null)
            {

                TemplatePropertyType[] tProps = new  TemplatePropertyType[properties.Count];
                int i = 0;
                foreach (KeyValuePair<string, string> property in properties) 
                {
                    TemplatePropertyType tProp = new TemplatePropertyType();
                    tProp.name = property.Key;
                    tProp.values = new string[] { property.Value };
                    tProps[i] = tProp;
                    i++;
                }

                pwdAuthnReq.properties = tProps;
            }

            SendPasswordlessLinkResponseType pwdAutrhnRes = pwdlessClient.SendPasswordlessLinkRequest(pwdAuthnReq);

            if (pwdAutrhnRes.statusCode == null || !pwdAutrhnRes.statusCode.Equals("200")) 
            {
                throw new OAuth2ClientException(pwdAutrhnRes.error + " : " + pwdAutrhnRes.error_description);
            }
        }

        /**
         * Builds a pre-authentication Url for the given username and password, and requesting
         * the default SP (as configured in the oauth2.spAlias property).
         *
         * This method calls the requestToken method.
         *
         * @param usr the username used to issue the access token
         * @param pwd the password used to issue the access token
         * @Deprecated
         */
        public String buildgetIdPInitPreAuthnUrlForDefaultSp(String usr, String pwd)  
        {
            String spAlias = ConfigurationManager.AppSettings["serviceProviderAlias"];
            return buildIdPInitPreAuthnUrl(spAlias, usr, pwd);
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
        public String buildIdPInitPreAuthnUrlForDefaultSp(String usr, String pwd)
        {
            String spAlias = ConfigurationManager.AppSettings["serviceProviderAlias"];
            return buildIdPInitPreAuthnUrl(spAlias, usr, pwd);
        }

        /**
         * Builds a pre-authentication Url for the given username and password, using the default SP
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
            return buildIdPInitPreAuthnUrlForToken(accessToken, spAlias);
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
        public String buildIdPInitPreAuthnUrlForToken(String accessToken, String spAlias)
        {

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
            return buildIdPPreAuthnResponseUrl(relayState, accessToken);
        }

        /**
         * Builds a pre-authentication Url for the given access token.
         *
         * This method calls the requestToken method.
         *
         * @oaran relayState as received with the pre-authn token request.
         * @param accessToken oauth2 access token
         */
        public String buildIdPPreAuthnResponseUrl(String relayState, String accessToken)
        {

            String idpPreAuthn = ConfigurationManager.AppSettings["idpPreAuthnResponse"];
            String preauthUrl = idpPreAuthn + "?atricore_security_token=" + Uri.EscapeDataString(accessToken) + "&scope=preauth-token";

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
