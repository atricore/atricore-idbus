/*
* OneTimePasswordAlgorithm.java
* OATH Initiative,
* HOTP one-time password algorithm
*
*/

/* Copyright (C) 2004, OATH.  All rights reserved.
*
* License to copy and use this software is granted provided that it
* is identified as the "OATH HOTP Algorithm" in all material
* mentioning or referencing this software or this function.
*
* License is also granted to make and use derivative works provided
* that such works are identified as
*  "derived from OATH HOTP algorithm"
* in all material mentioning or referencing the derived work.
*
* OATH (Open AuTHentication) and its members make no
* representations concerning either the merchantability of this
* software or the suitability of this software for any particular
* purpose.
*
* It is provided "as is" without express or implied warranty
* of any kind and OATH AND ITS MEMBERS EXPRESSaLY DISCLAIMS
* ANY WARRANTY OR LIABILITY OF ANY KIND relating to this software.
*
* These notices must be retained in any copies of any part of this
* documentation and/or software.
*/
package org.atricore.idbus.capabilities.oath;

import java.io.IOException;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.lang.reflect.UndeclaredThrowableException;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
* This class contains static methods that are used to calculate the
* One-Time Password (OTP) using
* JCE to provide the HMAC-SHA-1.
*
* @author Loren Hart
* @version 1.0
*/
public class HOTP {

   private HOTP() {
   }

   // These are used to calculate the check-sum digits.
   //                                0  1  2  3  4  5  6  7  8  9
   private static final int[] doubleDigits =
           {0, 2, 4, 6, 8, 1, 3, 5, 7, 9};

   /**
    * Calculates the checksum using the credit card algorithm.
    * This algorithm has the advantage that it detects any single
    * mistyped digit and any single transposition of
    * adjacent digits.
    *
    * @param num    the number to calculate the checksum for
    * @param digits number of significant places in the number
    * @return the checksum of num
    */
   public static int calcChecksum(long num, int digits) {
       boolean doubleDigit = true;
       int total = 0;
       while (0 < digits--) {
           int digit = (int) (num % 10);
           num /= 10;
           if (doubleDigit) {
               digit = doubleDigits[digit];
           }
           total += digit;
           doubleDigit = !doubleDigit;
       }
       int result = total % 10;
       if (result > 0) {
           result = 10 - result;
       }
       return result;
   }

   /**
    * This method uses the JCE to provide the HMAC-SHA-1
    * <p>
    * <p>
    * <p>
    * <p>
    * algorithm.
    * HMAC computes a Hashed Message Authentication Code and
    * in this case SHA1 is the hash algorithm used.
    *
    * @param keyBytes the bytes to use for the HMAC-SHA-1 key
    * @param text     the message or text to be authenticated.
    * @throws NoSuchAlgorithmException if no provider makes
    *                                  either HmacSHA1 or HMAC-SHA-1
    *                                  digest algorithms available.
    * @throws InvalidKeyException      The secret provided was not a valid HMAC-SHA-1 key.
    */

   public static byte[] hmac_sha1(byte[] keyBytes, byte[] text)
           throws NoSuchAlgorithmException, InvalidKeyException {
       //        try {
       Mac hmacSha1;
       try {
           hmacSha1 = Mac.getInstance("HmacSHA1");
       } catch (NoSuchAlgorithmException nsae) {
           hmacSha1 = Mac.getInstance("HMAC-SHA-1");
       }
       SecretKeySpec macKey =
               new SecretKeySpec(keyBytes, "RAW");
       hmacSha1.init(macKey);
       return hmacSha1.doFinal(text);
       //        } catch (GeneralSecurityException gse) {
       //            throw new UndeclaredThrowableException(gse);
       //        }
   }

   private static final int[] DIGITS_POWER
           // 0 1  2   3    4     5      6       7        8
           = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

   /**
    * This method generates an OTP value for the given
    * set of parameters.
    *
    * @param secret           the shared secret
    * @param movingFactor     the counter, time, or other value that
    *                         changes on a per use basis.
    * @param codeDigits       the number of digits in the OTP, not
    *                         including the checksum, if any.
    * @param addChecksum      a flag that indicates if a checksum digit
    *                         <p>
    *                         <p>
    *                         <p>
    *                         <p>
    *                         <p>
    *                         should be appended to the OTP.
    * @param truncationOffset the offset into the MAC result to
    *                         begin truncation.  If this value is out of
    *                         the range of 0 ... 15, then dynamic
    *                         truncation  will be used.
    *                         Dynamic truncation is when the last 4
    *                         bits of the last byte of the MAC are
    *                         used to determine the start offset.
    * @return A numeric String in base 10 that includes
    * {@link codeDigits} digits plus the optional checksum
    * digit if requested.
    * @throws NoSuchAlgorithmException if no provider makes
    *                                  either HmacSHA1 or HMAC-SHA-1
    *                                  digest algorithms available.
    * @throws InvalidKeyException      The secret provided was not
    *                                  a valid HMAC-SHA-1 key.
    */
   static public String generateOTP(byte[] secret,
                                    long movingFactor,
                                    int codeDigits,
                                    boolean addChecksum,
                                    int truncationOffset)
           throws NoSuchAlgorithmException, InvalidKeyException {
       // put movingFactor value into text byte array
       String result = null;
       int digits = addChecksum ? (codeDigits + 1) : codeDigits;
       byte[] text = new byte[8];
       for (int i = text.length - 1; i >= 0; i--) {
           text[i] = (byte) (movingFactor & 0xff);
           movingFactor >>= 8;
       }

       // compute hmac hash
       byte[] hash = hmac_sha1(secret, text);

       // put selected bytes into result int
       int offset = hash[hash.length - 1] & 0xf;
       if ((0 <= truncationOffset) &&
               (truncationOffset < (hash.length - 4))) {
           offset = truncationOffset;
       }
       int binary =
               ((hash[offset] & 0x7f) << 24)
                       | ((hash[offset + 1] & 0xff) << 16)
                       | ((hash[offset + 2] & 0xff) << 8)
                       | (hash[offset + 3] & 0xff);

       int otp = binary % DIGITS_POWER[codeDigits];
       if (addChecksum) {
           otp = (otp * 10) + calcChecksum(otp, codeDigits);
       }
       result = Integer.toString(otp);
       while (result.length() < digits) {
           result = "0" + result;
       }
       return result;
   }

   public static void main(String[] args) throws Exception {
       /*
       The following test data uses the ASCII string
   "12345678901234567890" for the secret:

   Secret = 0x3132333435363738393031323334353637383930

   Table 1 details for each count, the intermediate HMAC value.

   Count    Hexadecimal HMAC-SHA-1(secret, count)
   0        cc93cf18508d94934c64b65d8ba7667fb7cde4b0
   1        75a48a19d4cbe100644e8ac1397eea747a2d33ab
   2        0bacb7fa082fef30782211938bc1c5e70416ff44
   3        66c28227d03a2d5529262ff016a1e6ef76557ece
   4        a904c900a64b35909874b33e61c5938a8e15ed1c
   5        a37e783d7b7233c083d4f62926c7a25f238d0316
   6        bc9cd28561042c83f219324d3c607256c03272ae
   7        a4fb960c0bc06e1eabb804e5b397cdc4b45596fa
   8        1b3c89f65e6c9e883012052823443f048b4332db
   9        1637409809a679dc698207310c8c7fc07290d9e5

   Table 2 details for each count the truncated values (both in
   hexadecimal and decimal) and then the HOTP value.

                     Truncated
   Count    Hexadecimal    Decimal        HOTP
   0        4c93cf18       1284755224     755224
   1        41397eea       1094287082     287082
   2         82fef30        137359152     359152
   3        66ef7655       1726969429     969429
   4        61c5938a       1640338314     338314
   5        33c083d4        868254676     254676
   6        7256c032       1918287922     287922
   7         4e5b397         82162583     162583
   8        2823443f        673399871     399871
   9        2679dc69        645520489     520489
        */

       //long secret = 0x3132333435363738393031323334353637383930;

       String secret =  "12345678901234567890";

       for (int i = 0 ; i < 10 ; i++) {
           String hotpStr = generateOTP(secret.getBytes(), i, 6, false, -1);
           int hotp = Integer.parseInt(hotpStr);

           System.out.println(hotp);
       }
   }
}









