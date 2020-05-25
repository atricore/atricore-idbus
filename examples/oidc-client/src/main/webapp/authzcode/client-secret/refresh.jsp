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
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    Exception exception = null;
    ErrorObject error = null;
    AccessToken accessToken = null;
    RefreshToken refreshToken = null;
    BearerAccessToken bearerAccessToken = null;
    //TokenPair tokenPair = null;
    JWT idToken = null;
    JWTClaimsSet claims = null;
    URI sloUrl = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
        Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
        OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
        sloUrl = op.getEndSessionEndpointURI();

        // use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
        byte[] key = props.getProperty("oidc.client.secret").getBytes("UTF-8");

        if (key.length != 32) {
            // We need a 32 byte length key, so  ...
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
        }
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        URI tokenEndpoint = op.getTokenEndpointURI();

        // Client Authentication (client_secret_jwt)
        ClientAuthentication clientAuth = null;
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
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

        // Client Authentication (client_secret_basic)
        {

            byte[] n = new byte[64];

            ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));
            Secret secret = new Secret(props.getProperty("oidc.client.secret"));

            clientAuth = new ClientSecretBasic(clientId, secret);
        }


        // Authorization Grant
        RefreshToken currentRefreshToken = (RefreshToken) request.getSession().getAttribute("refresh_token");
        AuthorizationGrant authzGrant = new RefreshTokenGrant(currentRefreshToken);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);

        try {
            TokenResponse tokenRespose = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send());
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

                request.getSession().setAttribute("bearer_access_token", bearerAccessToken);
                request.getSession().setAttribute("refresh_token", refreshToken);

                SignedJWT signedIdToken = (SignedJWT) idToken;
                // TODO : JWSVerifier verifier = new RSASSAVerifier(publicKey);
                // TODO : signedIdToken.verify(verifier);
                claims = signedIdToken.getJWTClaimsSet();

            }
        } catch (ParseException e) {
            error = e.getErrorObject();
            exception = e;
        }


    } finally {
        //
    }


%>

<html>
<head>
    <title>ODIC Client Test - Refresh Token </title>
</head>

<h2>Outcome</h2>

<% if (error == null && exception == null) {
    out.println("<p>Claims: " + claims + "</br></br></p>");

    out.println("<p>IDToken: " + idToken.getParsedString() + "</br></p>");
    out.println("<p>AccessToken: " + accessToken + "</br></p>");

    out.println("<p>RefreshToken: " + refreshToken + "</br></p>");
    out.println("<p>BearerAccessToken: " + bearerAccessToken + "</br></p>");

    out.println("<br><br>");

    out.println("<p><a href=\"" + sloUrl + "?id_token_hint=" + idToken.getParsedString()  + "&post_logout_redirect_uri=http://localhost:8080/oidc-client/login-authz-code.jsp\">logout</a></p>");
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


