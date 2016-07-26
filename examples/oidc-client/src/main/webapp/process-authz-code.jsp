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
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.BearerAccessToken" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.TokenPair" %>
<%@ page import="com.nimbusds.openid.connect.sdk.OIDCAccessTokenResponse" %>
<%@ page import="com.nimbusds.jose.crypto.RSASSAVerifier" %>
<%@ page import="com.nimbusds.jwt.*" %>
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

        AuthorizationCode code = new AuthorizationCode(request.getParameter("code"));
        URI redirectUri = new URI(props.getProperty("oidc.authn.redirectUri"));

        // Authorization Grant
        AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);
        TokenResponse tokenRespose = OIDCAccessTokenResponse.parse(tokenRequest.toHTTPRequest().send());

        if (! tokenRespose.indicatesSuccess()) {
            // We got an error response...
            TokenErrorResponse errorResponse = (TokenErrorResponse) tokenRespose;
            error = errorResponse.getErrorObject();

        } else {

            OIDCAccessTokenResponse successResponse = (OIDCAccessTokenResponse) tokenRespose;

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

    } finally {
        //
    }


%>

<html>
<head>
    <title>ODIC Client Test - JWT Bearer with Authorization Code </title>
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

