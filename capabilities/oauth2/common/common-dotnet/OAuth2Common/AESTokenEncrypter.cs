using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.IO;

namespace OAuth2Common
{
    public class AESTokenEncrypter : TokenEncrypter
    {

        private String key;

        private String encryptAlg = "AES";

        public String Key 
        {
            get { return key; }
            set { key = value; }
        }

        public String EncryptAlg
        {
            get { return encryptAlg; }
            set { encryptAlg = value; }
        }

        public String Decrypt(String encryptedTokenValue)
        {
            String tokenValue;
            // Create a new instance of the AesCryptoServiceProvider 
            // class.  This generates a new key and initialization vector (IV). 
 
            using (AesCryptoServiceProvider oauth2Aes = new AesCryptoServiceProvider())
            {
                oauth2Aes.Key = ToAESKey(key);
                // Decrypt the bytes to a string. 
                tokenValue = DecryptStringFromBytes(System.Convert.FromBase64String(encryptedTokenValue), oauth2Aes.Key, null);
            }

            return tokenValue;
        }


        protected string DecryptStringFromBytes(byte[] encryptedToken, byte[] aesKey, byte[] iv)
        {
            // Check arguments. 
            if (encryptedToken == null || encryptedToken.Length <= 0)
                throw new ArgumentNullException("encryptedToken");
            if (aesKey == null || aesKey.Length <= 0)
                throw new ArgumentNullException("Key");


            // Declare the string used to hold 
            // the decrypted text. 
            string token = null;

            // Create an AesCryptoServiceProvider object 
            // with the specified key and IV. 
            using (AesCryptoServiceProvider aes = new AesCryptoServiceProvider())
            {
                aes.Key = aesKey;
                aes.Mode = CipherMode.ECB;
                aes.Padding = PaddingMode.PKCS7;
                //aesAlg.IV = IV;

                // Create a decrytor to perform the stream transform.
                ICryptoTransform decryptor = aes.CreateDecryptor(aes.Key, null);

                int decryptedByteCount;
                byte[] decryptedBytes = new byte[encryptedToken.Length];

                // Create the streams used for decryption. 
                using (MemoryStream msDecrypt = new MemoryStream(encryptedToken))
                {
                    msDecrypt.Position = 0;
                    using (CryptoStream csDecrypt = new CryptoStream(msDecrypt, decryptor, CryptoStreamMode.Read))
                    {
                        decryptedByteCount = csDecrypt.Read(decryptedBytes, 0, decryptedBytes.Length);

                        token = System.Text.Encoding.UTF8.GetString(decryptedBytes, 0, decryptedByteCount);

                    }
                }

            }

            return token;

        }

        byte[] ToAESKey(String key)
        {
            byte[] binKey = Encoding.UTF8.GetBytes(key);
            SHA1 sha1 = SHA1Managed.Create();
            byte[] hashKey = sha1.ComputeHash(binKey);

            int padding = 16 - hashKey.Length;

            byte[] aesKey = new byte[16];
            if (padding <= 0)
            {
                Array.Copy(hashKey, aesKey, 16);
            }
            else
            {
                Array.Copy(hashKey, aesKey, hashKey.Length);
                for (int i = 0; i < padding; i++)
                {
                    aesKey[hashKey.Length + i] = 0;
                }
            }

            return aesKey;

            
        }


    }
}
