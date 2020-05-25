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
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%@ page import="com.nimbusds.openid.connect.sdk.claims.UserInfo" %>
<%@ page import="com.nimbusds.openid.connect.sdk.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

        ErrorObject error = null;
        Exception exception = null;

        UserInfo userInfo = null;
        JWT userInfoJWT = null;


        URI sloUrl = null;

        try {
            Properties props = new Properties();
            InputStream is = getClass().getResourceAsStream("/oidc.properties");
            props.load(is);

            // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
            Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
            OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
            URI userInfoEndpoint = op.getUserInfoEndpointURI();
            sloUrl = op.getEndSessionEndpointURI();

            // Get previously stored Bearer Access Token
            BearerAccessToken accessToken = (BearerAccessToken) request.getSession().getAttribute("bearer_access_token");

            // Issue User info request
            UserInfoRequest req = new UserInfoRequest(userInfoEndpoint, accessToken);
            UserInfoResponse res = UserInfoResponse.parse(req.toHTTPRequest().send());

            if (res.indicatesSuccess()) {
                UserInfoSuccessResponse successResponse = res.toSuccessResponse();
                userInfo = successResponse.getUserInfo();
                userInfoJWT = successResponse.getUserInfoJWT();

            } else {
                UserInfoErrorResponse errorResponse = res.toErrorResponse();
                error = errorResponse.getErrorObject();
            }

        } catch (Exception e) {
            exception = e;
        }

%>

<html>
<head>
    <title>ODIC Client Test - User Info</title>
</head>

<h2>Outcome</h2>

<% if (error == null && exception == null) {
    out.println("<p>UserInfo: " + userInfo.toJSONObject() + "</br></br></p>");

    out.println("<p>UserInfo JWT: " + userInfoJWT + "</br></p>");

    out.println("<br><br>");
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


