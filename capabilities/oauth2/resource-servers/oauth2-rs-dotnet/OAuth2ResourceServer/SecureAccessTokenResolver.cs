using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Configuration;
using OAuth2Common;

namespace OAuth2ResourceServer
{
    public class SecureAccessTokenResolver : AccessTokenResolver
    {

        private TokenSigner tokenSigner;

        private TokenEncrypter tokenEncrypter;

        private String sharedSecret;

        private String hmacAlgorithm;

        public void init()
        {
            // Configuration can be taken from app. settings, but also configured as properties.

            // Shared Secret
            string s = ConfigurationManager.AppSettings["oauth2SharedSecret"];
            if (s != null)
                sharedSecret = s;

            // Optional
            string ha = ConfigurationManager.AppSettings["oauth2HMACAlgorithm"];
            if (ha != null)
                hmacAlgorithm = ha;

            // Build necessary components
            HMACTokenSigner ts = new HMACTokenSigner();
            ts.Key = SharedSecret;
            if (HmacAlgorithm != null)
                ts.SignAlg = HmacAlgorithm;

            AESTokenEncrypter te = new AESTokenEncrypter();
            te.Key = SharedSecret;

            tokenSigner = ts;
            tokenEncrypter = te;
        }

        public OAuth2AccessToken resolve(String tokenString)
        {
            OAuth2AccessTokenEnvelope envelope = JasonUtils.UnmarshalAccessTokenEnvelope(tokenString);
            if (envelope == null)
            {
                throw new System.ApplicationException("Cannot create OAuth2 Envelope from token");
            }

            String accessToken = envelope.Token;
            if (!tokenSigner.IsValid(accessToken, envelope.SignatureValue))
            {
                // Invalid signature, throw exception.
                throw new System.ApplicationException("Invalid OAuth2 Token signature");
            }

            accessToken = tokenEncrypter.Decrypt(accessToken);
            if (envelope.Deflated)
                accessToken = JasonUtils.inflate(accessToken, true);

            OAuth2AccessToken at = JasonUtils.UnmarshalAccessToken(accessToken);
            // Check token expiration

            return at;
        }

        public String SharedSecret
        {
            get { return sharedSecret; }
            set { sharedSecret = value; }
        }

        public String HmacAlgorithm
        {
            get { return hmacAlgorithm; }
            set { hmacAlgorithm = value; }
        }

    }
}
