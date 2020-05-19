<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientSecretJWT" %>
<%@ page import="com.nimbusds.oauth2.sdk.*" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientAuthentication" %>
<%@ page import="com.nimbusds.jose.crypto.MACSigner" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="com.nimbusds.jose.*" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.AccessToken" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.RefreshToken" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.BearerAccessToken" %>
<%@ page import="com.nimbusds.jwt.*" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientSecretBasic" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.ClientID" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.Secret" %>
<%@ page import="com.nimbusds.openid.connect.sdk.OIDCTokenResponse" %>
<%@ page import="com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="com.nimbusds.jose.crypto.RSASSAVerifier" %>
<%@ page import="com.nimbusds.jose.crypto.ECDSAVerifier" %>
<%@ page import="com.nimbusds.jose.crypto.MACVerifier" %>
<%@ page import="java.security.spec.X509EncodedKeySpec" %>
<%@ page import="java.security.KeyFactory" %>
<%@ page import="java.security.interfaces.RSAPublicKey" %>
<%@ page import="java.security.PublicKey" %>
<%@ page import="java.security.cert.Certificate" %>
<%@ page import="java.security.cert.CertificateFactory" %>
<%@ page import="sun.security.provider.X509Factory" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    ErrorObject error = null;
    Exception exception = null;
    AccessToken accessToken = null;
    RefreshToken refreshToken = null;
    BearerAccessToken bearerAccessToken = null;

    JWT idToken = null;
    JWTClaimsSet claims = null;
    String sloUrl = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        sloUrl = props.getProperty("oidc.logout.endpoint");

        // -------------------------------------------------
        // Load shared secret
        // Use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
        Secret secret = new Secret(props.getProperty("oidc.client.secret"));
        SecretKey secretKey = SecretKeyDerivation.deriveSecretKey(secret, 256);

        // -------------------------------------------------
        // Load IDP RSA Public key from a dert file
        String publicKeyContent = props.getProperty("oidc.idp.certificateRSA");
        byte [] publicKeyContentBytes = Base64.decodeBase64(publicKeyContent.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, ""));

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new ByteArrayInputStream(publicKeyContentBytes));
        PublicKey pubKey = cert.getPublicKey();

        // Load IDP RSA Public key from a pub key file
        /*
        publicKeyContent = props.getProperty("oidc.idp.pubKeyRSA");
        byte [] publicKeyContentBytes = Base64.decodeBase64(publicKeyContent.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", ""));

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyContentBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubKey = kf.generatePublic(keySpecX509);
        */

        URI tokenEndpoint = new URI(props.getProperty("oidc.token.endpoint"));

        ClientAuthentication clientAuth = null;

        // -------------------------------------------------
        // Client Authentication (client_secret_jwt)

        /*
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            JWTClaimsSet claimsSet = builder.subject(props.getProperty("oidc.client.id")).
                    issuer(props.getProperty("oidc.client.id")).
                    issueTime(new Date()).
                    expirationTime(new Date(System.currentTimeMillis() + (5L * 60L * 1000L))).
                    jwtID(jid).audience(Arrays.asList(props.getProperty("oidc.client.audience"))).build();

            SignedJWT clientAssertion = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            clientAssertion.sign(signer);

            clientAuth = new ClientSecretJWT(clientAssertion);
        }
        */

        // -------------------------------------------------
        // Build  client authentication (client_secret_basic)
        {

            ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));
            clientAuth = new ClientSecretBasic(clientId, secret);
        }
        // -------------------------------------------------
        // Build Token request
        AuthorizationCode code = new AuthorizationCode(request.getParameter("code"));
        URI redirectUri = new URI(props.getProperty("oidc.authn.redirectUriBase"));

        // Authorization Grant
        AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);
        TokenResponse tokenRespose = null;
        try {
            tokenRespose = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send());

            if (! tokenRespose.indicatesSuccess()) {
                // We got an error response...
                TokenErrorResponse errorResponse = (TokenErrorResponse) tokenRespose;
                error = errorResponse.getErrorObject();

            } else {

                OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenRespose;

                // Get the access token, the server may also return a refresh token
                accessToken = successResponse.getOIDCTokens().getAccessToken();
                refreshToken = successResponse.getOIDCTokens().getRefreshToken();
                bearerAccessToken = successResponse.getOIDCTokens().getBearerAccessToken();
                idToken = successResponse.getOIDCTokens().getIDToken();

                SignedJWT signedIdToken = (SignedJWT) idToken;

                // RSA Signature check
                JWSVerifier verifier = new RSASSAVerifier(pubKeyRSA);

                // EC (ES256,etc. ) Signature check
                // JWSVerifier verifier = new ECDSAVerifier(publicKey);

                // HMAC
                //JWSVerifier verifier = new MACVerifier(secretKey);

                // Verify signature
                signedIdToken.verify(verifier);
                claims = signedIdToken.getJWTClaimsSet();

            }

        } catch (ParseException e) {
            error = e.getErrorObject();
            exception = e;
        } catch (SerializeException e) {
            //error = e.getErrorObject();
            exception = e;
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

<% if (error == null && exception == null) {
    out.println("Claims: " + claims + "</br></br>");

    out.println("IDToken: " + idToken.getParsedString() + "</br>");
    out.println("AccessToken: " + accessToken + "</br>");
    //out.println("TokenPair: " + tokenPair + "</br>");
    out.println("RefreshToken: " + refreshToken + "</br>");
    out.println("BearerAccessToken: " + bearerAccessToken + "</br>");

    out.println("<br><br>");

    out.println("<a href=\"" + sloUrl + "?id_token_hint=" + idToken.getParsedString() + "&post_logout_redirect_uri=http://localhost:8080/oidc-client/login-authz-code.jsp\">logout</a>");
}
%>


<h3>Errors:</h3>
<% if (error != null) {
    out.println(error.getCode() + ":" + URLDecoder.decode(error.getDescription()));
}

if (exception != null) {
    out.println(exception.getMessage());
}%>
<br>
</html>


