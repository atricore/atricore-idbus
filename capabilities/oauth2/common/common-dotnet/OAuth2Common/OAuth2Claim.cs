using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public class OAuth2Claim
    {
        private String type;

        private String value;

        private String attribute;

        public String Type
        {
            get { return type; }
            set { type = value; }
        }

        public String Value
        {
            get { return value; }
            set { this.value = value; }
        }       


        public String Attribute
        {
            get { return attribute; }
            set { attribute = value; }
        }       

        public OAuth2Claim()
        {
        }

        
    }
}
