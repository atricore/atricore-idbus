using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public interface TokenSigner
    {
        String SignToken(String tokenValue) ;

        Boolean IsValid(String tokenValue, String tokenSignature);

    }
}
