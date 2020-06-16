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
<%@ page import="com.nimbusds.jose.crypto.ECDSAVerifier" %>
<%@ page import="com.nimbusds.jose.crypto.MACVerifier" %>
<%@ page import="java.security.PublicKey" %>
<%@ page import="java.security.interfaces.RSAPublicKey" %>
<%@ page import="java.security.spec.X509EncodedKeySpec" %>
<%@ page import="java.security.KeyFactory" %>
<%@ page import="com.nimbusds.jose.crypto.RSASSAVerifier" %>
<%@ page import="com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.security.cert.Certificate" %>
<%@ page import="java.security.cert.CertificateFactory" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="sun.security.provider.X509Factory" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page import="com.nimbusds.openid.connect.sdk.Nonce" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.State" %>
<%@ page import="net.minidev.json.JSONObject" %>
<%@ page import="com.nimbusds.oauth2.sdk.http.HTTPResponse" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    ErrorObject error = null;
    Exception exception = null;
    AccessToken accessToken = null;
    RefreshToken refreshToken = null;
    BearerAccessToken bearerAccessToken = null;
    String sessionState = null;

    JWT idToken = null;
    JWTClaimsSet claims = null;
    URI sloUrl = null;

    CodeVerifier codeVerifier = (CodeVerifier) request.getSession().getAttribute("code_verifier");

    try {

        // Configuration properties:
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
        Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
        OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
        sloUrl = op.getEndSessionEndpointURI();

        // -------------------------------------------------
        // Load shared secret
        // Use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
        Secret secret = new Secret(props.getProperty("oidc.client.secret"));
        SecretKey secretKey = SecretKeyDerivation.deriveSecretKey(secret, 256);

        // -------------------------------------------------
        // Load IDP RSA Public key from a dert file
        String publicKeyContent = props.getProperty("oidc.idp.certificateRSA");
        byte[] publicKeyContentBytes = Base64.decodeBase64(publicKeyContent.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, ""));

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

        // -------------------------------------------------
        // Token endpoint
        URI tokenEndpoint = op.getTokenEndpointURI();

        // -------------------------------------------------
        // Process response.

        // Get authorization code
        AuthorizationCode code = new AuthorizationCode(request.getParameter("code"));
        URI redirectUri = new URI(props.getProperty("oidc.client.redirectUriBase"));

        // Build an authorization grant using CODE VERIFIER
        AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri, codeVerifier);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        // Client ID
        ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));

        // Build a token request
        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientId, authzGrant, scope);
        HTTPResponse httpTokenResponse = tokenRequest.toHTTPRequest().send();
        JSONObject jsonObject = httpTokenResponse.getContentAsJSONObject();

        if (httpTokenResponse.getStatusCode() != HTTPResponse.SC_OK) {
            // We got an error response...
            TokenErrorResponse errorResponse = TokenErrorResponse.parse(jsonObject);
            error = errorResponse.getErrorObject();

        } else {
            OIDCTokenResponse successResponse = OIDCTokenResponse.parse(jsonObject);
            Nonce nonce = (Nonce) session.getAttribute("nonce");
            State state = (State) session.getAttribute("state");

            // Get the access token, the server may also return a refresh token
            accessToken = successResponse.getOIDCTokens().getAccessToken();
            refreshToken = successResponse.getOIDCTokens().getRefreshToken();
            bearerAccessToken = successResponse.getOIDCTokens().getBearerAccessToken();
            idToken = successResponse.getOIDCTokens().getIDToken();
            sessionState = request.getParameter("session_state");

            String nonceStr = (String) idToken.getJWTClaimsSet().getClaim("nonce");
            if (nonce != null) {
                if (nonceStr == null || !nonce.getValue().equals(nonceStr)) {
                    throw new RuntimeException("Invalid NONCE : [" + nonceStr + "] != [" + nonce + "]");
                }
            }

            // TODO Validate State

            SignedJWT signedIdToken = (SignedJWT) idToken;

            // RSA Signature check
            // JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) pubKey);

            // EC (ES256,etc. ) Signature check
            // JWSVerifier verifier = new ECDSAVerifier(pubKey);

            // HMAC
            JWSVerifier verifier = new MACVerifier(secretKey);

            // Verify signature
            signedIdToken.verify(verifier);
            claims = signedIdToken.getJWTClaimsSet();

            request.getSession().setAttribute("username", claims.getSubject());
            request.getSession().setAttribute("session_state", sessionState);
            request.getSession().setAttribute("bearer_access_token", bearerAccessToken);
            request.getSession().setAttribute("refresh_token", refreshToken);
            request.getSession().setAttribute("id_token", idToken);

        }

    } catch (ParseException e) {
        error = e.getErrorObject();
        exception = e;
        request.getSession().removeAttribute("username");
    } catch (SerializeException e) {
        //error = e.getErrorObject();
        exception = e;
        request.getSession().removeAttribute("username");
    } catch (Exception e) {
        exception = e;
        request.getSession().removeAttribute("username");
    } finally {
        //
    }

%>

<html>
<jsp:include page="../inc/header.jsp"/>

<body class="gt-fixed">

<jsp:include page="../inc/top-bar.jsp"/>

<div id="idbus-error" class="gt-bd clearfix">
    <div class="gt-content">
        <div>
            <h2 class="gt-table-head">Received Tokens</h2>
        </div>

        <div>

            <% if
                (
                error
                ==
                null
                &&
                exception
                ==
                null
                )
                {

                out
                .
                println
                (
                "<ul>"
                )
                ;
                out
                .
                println
                (
                "<li><b>IDToken:</b> "
                +
                idToken
                .
                getParsedString
                (
                )
                +
                "</li>"
                )
                ;
                out
                .
                println
                (
                "<li><b>AccessToken:</b> "
                +
                accessToken
                +
                "</li>"
                )
                ;
                out
                .
                println
                (
                "<li><b>RefreshToken:</b> "
                +
                refreshToken
                +
                "</li>"
                )
                ;
                out
                .
                println
                (
                "<li><b>BearerAccessToken:</b> "
                +
                bearerAccessToken
                +
                "</li>"
                )
                ;
                out
                .
                println
                (
                "<li><b>SessionState:</b> "
                +
                sessionState
                +
                "</li>"
                )
                ;
                out
                .
                println
                (
                "</ul>"
                )
                ;

                }

                if
                (
                error
                !=
                null
                )
                {
                out
                .
                println
                (
                "<h3>Error:</h3><p>"
                +
                error
                .
                getCode
                (
                )
                +
                ":"
                +
                URLDecoder
                .
                decode
                (
                error
                .
                getDescription
                (
                )
                !=
                null
                ?
                error
                .
                getDescription
                (
                )
                :
                ""
                )
                +
                "</p>"
                )
                ;
                }

                if
                (
                exception
                !=
                null
                )
                {
                out
                .
                println
                (
                "<h3>Exception:</h3><p>"
                +
                exception
                .
                getMessage
                (
                )
                +
                "</p>"
                )
                ;
                }%>
        </div>
    </div>
</div>


<br>
</html>
