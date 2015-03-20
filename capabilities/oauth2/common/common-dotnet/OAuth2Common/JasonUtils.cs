using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.IO.Compression;
using Newtonsoft.Json;

namespace OAuth2Common
{
    public class JasonUtils
    {
        public static OAuth2AccessTokenEnvelope UnmarshalAccessTokenEnvelope(String tokenString)
        {
            return UnmarshalAccessTokenEnvelope(tokenString, true);
        }

        public static OAuth2AccessTokenEnvelope UnmarshalAccessTokenEnvelope(String tokenString, Boolean decode)
        {
            if (decode)
                tokenString = inflate(tokenString, decode);

            // JSon magic
            OAuth2AccessTokenEnvelope envelope = JsonConvert.DeserializeObject<OAuth2AccessTokenEnvelope>(tokenString);

            return envelope;
        }

        public static OAuth2AccessToken UnmarshalAccessToken(String tokenString)
        {
            return UnmarshalAccessToken(tokenString, true);
        }

        public static OAuth2AccessToken UnmarshalAccessToken(String tokenString, Boolean decode)
        {


            if (decode)
            {
                byte[] decoded = System.Convert.FromBase64String(tokenString);
                tokenString = System.Text.Encoding.UTF8.GetString(decoded); 
            }

            // JSon magic
            OAuth2AccessToken token = JsonConvert.DeserializeObject<OAuth2AccessToken>(tokenString);

            return token;

        }

        public static String inflate(String deflated, Boolean decode) 
        {

            String inflated = null;
            byte[] deflatedBin = null;

            if (decode)
                deflatedBin = System.Convert.FromBase64String(deflated);
            else
                deflatedBin = Encoding.UTF8.GetBytes(deflated);

            using (MemoryStream inflatedStream = new MemoryStream())
            {
                using (MemoryStream deflatedStream = new MemoryStream(deflatedBin))
                {

                    using (DeflateStream compressionStream = new DeflateStream(deflatedStream, CompressionMode.Decompress))
                    {
                        byte[] outBuffer = new byte[512];

                        int length = compressionStream.Read(outBuffer, 0, outBuffer.Length);
                        while (length > 0)
                        {
                            inflatedStream.Write(outBuffer, 0, length);
                            length = compressionStream.Read(outBuffer, 0, outBuffer.Length);
                        }
                        
                    }
                }
                inflatedStream.Flush();
                inflatedStream.Position = 0;
                StreamReader sr = new StreamReader(inflatedStream);
                inflated = sr.ReadToEnd();
            }

            return inflated;
        }
    }
}
