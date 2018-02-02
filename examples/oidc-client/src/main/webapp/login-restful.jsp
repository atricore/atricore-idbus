<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientSecretJWT" %>
<%@ page import="com.nimbusds.oauth2.sdk.*" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientAuthentication" %>
<%@ page import="com.nimbusds.jose.crypto.MACSigner" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="com.nimbusds.jose.crypto.DirectEncrypter" %>
<%@ page import="com.nimbusds.jose.*" %>
<%@ page import="com.nimbusds.jose.crypto.AESEncrypter" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.AccessToken" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.RefreshToken" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.BearerAccessToken" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.TokenPair" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.nimbusds.openid.connect.sdk.OIDCAccessTokenResponse" %>
<%@ page import="com.nimbusds.jwt.*" %>
<%@ page import="com.nimbusds.oauth2.sdk.http.HTTPResponse" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    ErrorObject error = null;
    AccessToken accessToken = null;
    RefreshToken refreshToken = null;
    BearerAccessToken bearerAccessToken = null;
    TokenPair tokenPair = null;
    JWT idToken = null;
    ReadOnlyJWTClaimsSet claims = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        // use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
        byte[] key = props.getProperty("oidc.client.secret").getBytes("UTF-8");

        if (key.length != 32) {
            // We need a 32 byte length key, so  ...
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
        }
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        URI tokenEndpoint = new URI(props.getProperty("oidc.token.endpoint"));

        // Client Authentication
        ClientAuthentication clientAuth = null;
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet();
            claimsSet.setSubject(props.getProperty("oidc.client.id"));
            claimsSet.setIssuer(props.getProperty("oidc.client.id"));
            claimsSet.setIssueTime(new Date());
            claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + (5L * 60L * 1000L)));
            claimsSet.setJWTID(jid);
            claimsSet.setAudience(Arrays.asList(props.getProperty("oidc.client.audience")));

            SignedJWT clientAssertion = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            clientAssertion.sign(signer);

            // TODO : Set encrypted token as client authentication
            clientAuth = new ClientSecretJWT(clientAssertion);
        }

        JWT assertion = null;
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            // Create HMAC signer
            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet();

            // TODO : Take from a login form
            claimsSet.setSubject("admin");
            claimsSet.setClaim("cred", "atricore");

            claimsSet.setIssuer(props.getProperty("oidc.client.id"));
            claimsSet.setAudience(Arrays.asList(props.getProperty("oidc.client.audience")));
            claimsSet.setJWTID(jid);
            claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + (5L * 60L * 1000L)));
            claimsSet.setIssueTime(new Date());


            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            // Apply the HMAC
            signedJWT.sign(signer);

            // Create JWE object with signed JWT as payload
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));

            // Perform encryption
            jweObject.encrypt(new DirectEncrypter(secretKey.getEncoded()));

            // Serialise to JWE compact form
            String jweString = jweObject.serialize();

            // Create the encrypted JWT object
            assertion = EncryptedJWT.parse(jweString);
        }

        // Authorization Grant
        AuthorizationGrant authzGrant = new JWTBearerGrant(assertion);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);
        HTTPResponse httpTokenResponse = tokenRequest.toHTTPRequest().send();

        if (httpTokenResponse.getStatusCode() != 200) {
            throw new RuntimeException(httpTokenResponse.getContent());

        }

        TokenResponse tokenResponse = OIDCAccessTokenResponse.parse(httpTokenResponse);

        if (!tokenResponse.indicatesSuccess()) {
            // We got an error response...
            TokenErrorResponse errorResponse = (TokenErrorResponse) tokenResponse;
            error = errorResponse.getErrorObject();

        } else {

            OIDCAccessTokenResponse successResponse = (OIDCAccessTokenResponse) tokenResponse;

            // Get the access token, the server may also return a refresh token
            accessToken = successResponse.getAccessToken();
            refreshToken = successResponse.getRefreshToken();
            bearerAccessToken = successResponse.getBearerAccessToken();
            tokenPair = successResponse.getTokenPair();
            idToken = successResponse.getIDToken();

            SignedJWT signedIdToken = (SignedJWT) idToken;
            // TODO : JWSVerifier verifier = new RSASSAVerifier(publicKey);
            // TODO : signedIdToken.verify(verifier);
            claims = signedIdToken.getJWTClaimsSet();

        }
    } catch (ParseException e) {
        error = e.getErrorObject();

    } catch (GeneralException e) {
        error = e.getErrorObject();
        if (error == null) throw e;
    } finally {
        //
    }


%>
<html>
<head>
    <title>ODIC Client Test - JWT Bearer with Password </title>
</head>

<h2>Outcome</h2>

<% if (error == null) {
    out.println("Claims: " + claims + "</br></br>");

    out.println("IDToken: " + idToken.getParsedString() + "</br>");
    out.println("AccessToken: " + accessToken + "</br>");
    out.println("TokenPair: " + tokenPair + "</br>");
    out.println("RefreshToken: " + refreshToken + "</br>");
    out.println("BearerAccessToken: " + bearerAccessToken + "</br>");
} %>
<br><br>

<h3>Errors:</h3>
<% if (error != null) {
    out.println(error.getCode() + ":" + URLDecoder.decode(error.getDescription()));
} %>
<br>
</html>

