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
<%@ page import="com.nimbusds.oauth2.sdk.pkce.CodeVerifier" %>
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

    CodeVerifier codeVerifier = (CodeVerifier) request.getSession().getAttribute("code_verifier");;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        sloUrl = props.getProperty("oidc.logout.endpoint");

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

        AuthorizationCode code = new AuthorizationCode(request.getParameter("code"));
        URI redirectUri = new URI(props.getProperty("oidc.authn.redirectUriBase"));

        // Authorization Grant
        AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri, codeVerifier);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        // Client ID
        ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientId, authzGrant, scope);
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
                // TODO : JWSVerifier verifier = new RSASSAVerifier(publicKey);
                // TODO : signedIdToken.verify(verifier);
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

<%out.println("CodeVerifier: " + (codeVerifier != null ? codeVerifier.getValue() : "NA/"));%>

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
