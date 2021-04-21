package org.atricore.idbus.capabilities.oath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;


public class OTPKeyGenerator {

    public static void main(String[] args) throws Exception {
        OTPKeyGenerator g = new OTPKeyGenerator();

        //String key = g.getRandomBase32Key(true);

        String key = "rseo rbms yuwp spio 46vs doba fqjm 3con";

        String url = g.getOTPURL(key, "Atricore, Inc.", "sgonzalez");

        String qrFile = "/tmp/qr.png";

        createQRCode(url, qrFile, 320, 320);

        System.out.println("Key: " + key);
        System.out.println("Url: " + url);
        System.out.println("QR : " + qrFile);

    }

    /**
     * Generate Base32 secret key (for now 20 bytes)
     *
     */
    public String getRandomBase32Key(boolean groupValues) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);

        if (groupValues) {
            // make the secret key more human-readable by lower-casing and
            // inserting spaces between each group of 4 characters
            secretKey = secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
        }

        return secretKey;
    }

    /**
     * Generate HEX secret key
     *
     */
    public String getRandomHexKey() {
        String normalizedBase32Key = getRandomBase32Key(false);
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(normalizedBase32Key);
        String hexKey = Hex.encodeHexString(bytes);
        return hexKey;
    }

    /**
     * URL format: otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}
     *
     * Sample key "quu6 ea2g horg md22 sn2y ku6v kisc kyag";
     */

    public String getOTPURL(String secretKey, String issuer, String account) {

        // Just in case our key is in 4 chars groups
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Utility to create an QR code for
     *
     * @param barCodeData
     * @param filePath
     * @param height
     * @param width
     * @throws WriterException
     * @throws IOException
     */
    public static void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        } finally {
            if (out != null) try {out.close();} catch (IOException e) {/**/}
        }
    }
}
