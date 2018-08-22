package org.atricore.idbus.capabilities.sso.main.test;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.zip.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DeflateInflateTest {

    private static final Log logger = LogFactory.getLog(DeflateInflateTest.class);

    private static String bufferLen = "4096";


    @BeforeClass
    public static void setupTestSuite() throws Exception {
    }

    @Test
    public void deflateInflate() throws Exception {

        String s = "Hello, World!";
        System.out.println("IF:" + s);

        s = deflateForRedirect(s, true);
        System.out.println("DF:" + s);

        s = inflateFromRedirect(s, true);
        System.out.println("IF:" + s);

    }

    //mnj@Test
    public void inflateXml() throws Exception {
        String s = "pZJNb+IwEIb/iuUeOBHbST+CRagoCVKk0kWEVtVeVm7sgqXERhlTuv9+HSCoXan00IMtefTMO+94Znj7XlfoTTWgrUl6LKA9pExppTarpPe4nPbj3u1oCKKuNnyhYGMNKORzDPB3kAleO7fhhOx2u2AXBbZZkZBSRp5n90W5VrXAR9iLfgnTS+IhT1x09L5egreN4VaABm5ErYC7khfj2T0PA8o3jXW2tFWXIuELfUrooNWXoFefCpzXFwCqcf5XMJq0TRt3ni8PELcvTmijJEapAqeNaDVO1ipbimptwfGYxozk6d1j4e8xI8XcHy8WkvGk+LPIivmvhyIjiyzNFxjlAFuVG3Ci9RFSxvr0qk+jZcg4ozwMA0YHvzF6Ok4Se0s+y3QjW9oEaxlN0uv4ckzpzdV1FjHmibSNT+8GLKRxlmZZPMjiCB8mzvdVGzS1TS2+ab+NaNl/3aN+2k67v3j0bdd5emp7lg7Jh6rd0hVOuC18fk2sVOhJVFt13hTs6ePeStXgH8k82LlfCv2mMBkdrH4U+i90enYTGP0D";

        System.out.println("DFXML:" + s);
        s = inflateFromRedirect(s, true);
        System.out.println("IFXML:" + s);
    }

    //@Test
    public void inflateServiceNowRequest() throws Exception {
        String s = "nZNPc9owEMW/ikd3/5EJSdFgZoyhU2bS1MVODr2p8jrR1JZcrQzk20c2JOHQ0Gmvu0/atz89zZG3TdyxtLdPagu/e0DrHdpGITt2EtIbxTRHiUzxFpBZwYr06y2Lg4h1RlstdEO8FBGMlVplWmHfginA7KSA  1tQp6s7ZCFIf567qCCXYDHpq/0PhC6DRXfdfwRgkoT77M2AkY/Cal5g0C8zSohxV1GV5 Wy3V6HaVRmkbxTRRFk mMrtN0TSdXs5kTYs4R5Q7ejyL2sFFoubIJiSNK/Wjqx5OSXjN6w hVEE8nP4iXnzZZSlVJ9Xh57Z9HEbIvZZn7 beiHC/YyQrMnVO706qCWiqoiPcABh0XNzyIyGI YmWjLXNO vJE/oqXLP4Gcx6ejzgN7Njga7PKdSPFs5c2jd5nBrh1Xq3pYcTecvuxCxrQsSIrvx6lrFfYgZC1HLYs8mHA9543Q8H825uHby5PEYRqDIDLkoWD9TLddtxIHCjCgQv7xvFcljWO0hbq/6J6USaYGO525SFee22qIS4gnM/ScIdBG/uK/U OFqfmB/u9t8 /4eIF";

        System.out.println("DFXML:" + s);

        s = inflateFromRedirect(s, true);

        System.out.println("Inflated : " + s);


    }

    public static String deflateForRedirect(String redirStr, boolean encode) {

        int n = redirStr.length();
        byte[] redirIs = null;
        try {
            redirIs = redirStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream deflated = new ByteArrayOutputStream(n);

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(redirIs);
        deflater.finish();

        byte[] buff = new byte[1024];
        int len = deflater.deflate(buff);

        while (len > 0) {
            deflated.write(buff, 0, len);
            len = deflater.deflate(buff);
        }

        deflater.end();

        byte[] exact = deflated.toByteArray();

        if (encode) {
            byte[] base64Str = new Base64().encode(exact);
            return new String(base64Str);
        }

        return new String(exact);
    }

    public static String inflateFromRedirect(String redirStr, boolean decode) throws Exception {

        if (redirStr == null || redirStr.length() == 0) {
            throw new RuntimeException("Redirect string cannot be null or empty");
        }

        byte[] redirBin = null;
        if (decode)
            redirBin = new Base64().decode(removeNewLineChars(redirStr).getBytes());
        else
            redirBin = redirStr.getBytes();

        if (redirBin == null || redirBin.length == 0)
            throw new RuntimeException("Redirect string cannot be null or empty");

        // Decompress the bytes
        Inflater inflater = new Inflater(true);
        inflater.setInput(redirBin);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        try {
            int resultLength = 0;
            byte[] buff = new byte[1024];

            resultLength = inflater.inflate(buff);

            while (resultLength > 0) {
                baos.write(buff, 0, resultLength);
                resultLength = inflater.inflate(buff);
            }

            inflater.end();

        } catch (DataFormatException e) {
            throw new RuntimeException("Cannot inflate SAML message : " + e.getMessage(), e);
        }

        inflater.end();

        // Decode the bytes into a String
        String outputString = null;
        try {
            outputString = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot convert byte array to string " + e.getMessage(), e);
        }
        return outputString;
    }

    public static String removeNewLineChars(String s) {
        String retString = null;
        if ((s != null) && (s.length() > 0) && (s.indexOf('\n') != -1)) {
            char[] chars = s.toCharArray();
            int len = chars.length;
            StringBuffer sb = new StringBuffer(len);
            for (int i = 0; i < len; i++) {
                char c = chars[i];
                if (c != '\n') {
                    sb.append(c);
                }
            }
            retString = sb.toString();
        } else {
            retString = s;
        }
        return retString;
    }

}
