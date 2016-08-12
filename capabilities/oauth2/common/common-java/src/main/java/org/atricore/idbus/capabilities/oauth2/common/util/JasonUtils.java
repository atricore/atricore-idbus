package org.atricore.idbus.capabilities.oauth2.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessTokenEnvelope;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JasonUtils {

    private static final Log logger = LogFactory.getLog(JasonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.writerWithType(List.class).withType(ArrayList.class);
    }

    public static String marshalAccessToken(OAuth2AccessToken accessToken) throws IOException {
        return marshalAccessToken(accessToken, true);
    }

    public static String marshalAccessToken(OAuth2AccessToken accessToken, boolean encode) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        mapper.writeValue(baos, accessToken);
        String jsonToken = new String(baos.toByteArray());

        if (logger.isTraceEnabled())
            logger.trace("Marshaled AccessToken (JSON):\n" + jsonToken);

        if (encode)
            return new String(Base64.encodeBase64(jsonToken.getBytes("UTF-8")));
        else
            return jsonToken;
    }

    public static OAuth2AccessToken unmarshalAccessToken(String strAccessToken, boolean decode) throws IOException {
        if (decode)
            strAccessToken = new String(Base64.decodeBase64(strAccessToken.getBytes("UTF-8")));

        if (logger.isTraceEnabled())
            logger.trace("Unmarshal Access Token (JSON)\n" + strAccessToken);

        return mapper.readValue(new ByteArrayInputStream(strAccessToken.getBytes("UTF-8")), OAuth2AccessToken.class);
    }

    public static OAuth2AccessToken unmarshalAccessToken(String strAccessToken) throws IOException {
        return unmarshalAccessToken(strAccessToken, true);

    }

    public static String marshalAccessTokenEnvelope(OAuth2AccessTokenEnvelope envelope) throws IOException {
        return marshalAccessTokenEnvelope(envelope, true);
    }

    public static String marshalAccessTokenEnvelope(OAuth2AccessTokenEnvelope envelope, boolean encode) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        mapper.writeValue(baos, envelope);
        String jsonEnv = new String(baos.toByteArray());

        if (logger.isTraceEnabled())
            logger.trace("Marshaled AccessTokenEnvelope (JSON):\n" + jsonEnv);

        if (encode)
            return  deflate(jsonEnv, true);
        else
            return jsonEnv;
    }

    public static OAuth2AccessTokenEnvelope unmarshalAccessTokenEnvelope(String strAccessTokenEnvelope) throws IOException {
        return unmarshalAccessTokenEnvelope(strAccessTokenEnvelope, true);
    }

    public static OAuth2AccessTokenEnvelope unmarshalAccessTokenEnvelope(String strAccessTokenEnvelope, boolean decode) throws IOException {
        if (decode)
            strAccessTokenEnvelope = inflate(strAccessTokenEnvelope, true);

        if (logger.isTraceEnabled())
            logger.trace("Unmarshal AccessTokenEnvelope (JSON):\n" + strAccessTokenEnvelope);


        return mapper.readValue(new ByteArrayInputStream(strAccessTokenEnvelope.getBytes("UTF-8")), OAuth2AccessTokenEnvelope.class);
    }

    public static String marshalAccessTokenResponse(AccessTokenResponseType accessTokenResponse) throws IOException {
        return marshalAccessTokenResponse(accessTokenResponse, true);
    }

    public static String marshalAccessTokenResponse(AccessTokenResponseType accessTokenResponse, boolean encode) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        mapper.writeValue(baos, accessTokenResponse);
        String jsonTokenResp = new String(baos.toByteArray());

        if (logger.isTraceEnabled())
            logger.trace("Marshaled AccessTokenResponse (JSON):\n" + jsonTokenResp);

        if (encode)
            return new String(Base64.encodeBase64(jsonTokenResp.getBytes("UTF-8")));
        else
            return jsonTokenResp;
    }

    public static String deflate(String tokenStr, boolean encode) {

        int n = tokenStr.length();
        byte[] redirIs = null;
        try {
            redirIs = tokenStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        byte[] deflated = new byte[n];

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(redirIs);
        deflater.finish();
        int len = deflater.deflate(deflated);
        deflater.end();

        byte[] exact = new byte[len];

        System.arraycopy(deflated, 0, exact, 0, len);

        if (encode) {
            byte[] base64Str = new Base64().encode(exact);
            return new String(base64Str);
        }

        return new String(exact);
    }

    public static String inflate(String tokenStr, boolean decode) throws java.io.UnsupportedEncodingException {

        if (tokenStr == null || tokenStr.length() == 0) {
            throw new RuntimeException("Token string cannot be null or empty");
        }

        byte[] redirBin = null;
        if (decode)
            redirBin = new Base64().decode(removeNewLineChars(tokenStr).getBytes("UTF-8"));
        else
            redirBin = tokenStr.getBytes("UTF-8");

        // Decompress the bytes
        Inflater inflater = new Inflater(true);
        inflater.setInput(redirBin);
        int resultLen = 4096;

        byte[] result = new byte[resultLen];
        int resultLength = 0;
        try {
            resultLength = inflater.inflate(result);
        } catch (DataFormatException e) {
            throw new RuntimeException("Cannot inflate OAuth2 Token : " + e.getMessage(), e);
        }


        inflater.end();

        // Decode the bytes into a String
        String outputString = null;
        try {
            outputString = new String(result, 0, resultLength, "UTF-8");
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
