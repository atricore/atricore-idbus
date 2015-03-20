using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public class OAuth2Constants
    {

        static String SSO_SERVICE_BASE_URI = "urn:org:atricore:idbus:sso:metadata";

        static String OAUTH2_SERVICE_BASE_URI="urn:org:atricore:idbus:OAUTH:2.0:metadata";

        static String SERVICE_TYPE = "urn:org:atricore:idbus:OAUTH:2.0";

        static String TOKEN_SERVICE_TYPE = "{urn:org:atricore:idbus:OAUTH:2.0:metadata}TokenService";

        static String OAUTH2_PROTOCOL_PKG = "org.atricore.idbus.common.oauth._2_0.protocol";

        static String OAUTH2_PROTOCOL_NS = "urn:org:atricore:idbus:OAUTH:2.0:protocol";

        static String OAUTH2_IDPALIAS_VAR = "oauth2_idp_alias";

    }
}
