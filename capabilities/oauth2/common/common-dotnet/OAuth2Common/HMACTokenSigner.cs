using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;

namespace OAuth2Common
{
    public class HMACTokenSigner : TokenSigner
    {
        private String key;

        private String signAlg = "HMACSHA1";

        public String Key
        {
            get { return key; }
            set { key = value; }
        }

        public String SignAlg
        {
            get { return signAlg; }
            set { signAlg = value; }
        }


        public String SignToken(String tokenValue)
        {
            HMAC hmac = HMAC.Create(signAlg);
            byte[] binaryKey = Encoding.UTF8.GetBytes(key);
            hmac.Key = binaryKey;
            byte[] hash = hmac.ComputeHash(Encoding.UTF8.GetBytes(tokenValue));
            return System.Convert.ToBase64String(hash, 0, hash.Length);
        }

        public Boolean IsValid(String tokenValue, String tokenSignature)
        { 

            String expectedSignature = SignToken(tokenValue);
            return tokenSignature.Equals(expectedSignature);
        }

    }
        
}
