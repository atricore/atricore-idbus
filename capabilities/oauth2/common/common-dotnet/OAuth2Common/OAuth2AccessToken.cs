using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public class OAuth2AccessToken
    {
        private long timeStamp;

        private long rnd;

        private long expiresOn;

        private String userId;

        private List<OAuth2Claim> claims = new List<OAuth2Claim>();

        public long TimeStamp
        {
            get { return timeStamp; }
            set { timeStamp = value; }
        }

        public long Rnd
        {
            get { return rnd; }
            set { rnd = value; }
        }


        public long ExpiresOn
        {
            get { return expiresOn; }
            set { expiresOn = value; }
        }

        public List<OAuth2Claim> Claims
        {
            get { return claims; }
            set { claims = value; }
        }

        public String UserId
        {
            get {
                if (claims == null)
                    return null;

                if (userId != null)
                    return userId;

                for (int i = 0; i < claims.Count; i++)
                {
                    OAuth2Claim c = claims.ElementAt(i);
                    if (c.Type.Equals("USERID"))
                    {
                        userId = c.Value;
                        return c.Value;
                    }
                }
                return null;
            }
        }

        public String getAttribute(String name)
        {
            for (int i = 0; i < claims.Count; i++)
            {
                OAuth2Claim c = claims.ElementAt(i);
                if (c.Type.Equals("ATTRIBUTE") && c.Value.Equals(name))
                {
                    return c.Attribute;
                }
            }
            return null;
        }

        public Boolean isUserInRole(String name)
        {
            for (int i = 0; i < claims.Count; i++)
            {
                OAuth2Claim c = claims.ElementAt(i);
                if (c.Type.Equals("ROLE") && c.Value.Equals(name))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
