using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OAuth2Common;

namespace OAuth2ResourceServer
{
    interface AccessTokenResolver
    {
        OAuth2AccessToken resolve(String token);
    }
}
