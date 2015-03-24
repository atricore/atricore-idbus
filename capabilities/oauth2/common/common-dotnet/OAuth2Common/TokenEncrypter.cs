using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OAuth2Common
{
    public interface TokenEncrypter
    {
        String Decrypt(String encryptedTokenValue);
    }
}
