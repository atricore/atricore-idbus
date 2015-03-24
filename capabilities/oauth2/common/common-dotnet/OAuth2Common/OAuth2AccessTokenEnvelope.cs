using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public class OAuth2AccessTokenEnvelope
    {

        private String encryptionAlg;

        private String signatureAlg;

        private String signatureValue;

        private String token;

        private Boolean deflated;

        public String EncryptionAlg
        {
            get { return encryptionAlg; }
            set { encryptionAlg = value; }
        }

        public String SignatureAlg
        {
            get { return signatureAlg; }
            set { signatureAlg = value; }
        }

        public String SignatureValue
        {
            get { return signatureValue; }
            set { signatureValue = value; }
        }

        public String Token
        {
            get { return token; }
            set { token = value; }
        }

        public Boolean Deflated
        {
            get { return deflated; }
            set { deflated = value; }
        }

     





    }
}
