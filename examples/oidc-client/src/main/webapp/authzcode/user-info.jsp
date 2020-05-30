<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="com.nimbusds.oauth2.sdk.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.nimbusds.oauth2.sdk.token.BearerAccessToken" %>
<%@ page import="com.nimbusds.jwt.*" %>
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
        URI iFrameEndpoint = null;

        try {
            Properties props = new Properties();
            InputStream is = getClass().getResourceAsStream("/oidc.properties");
            props.load(is);

            // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
            Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
            OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
            URI userInfoEndpoint = op.getUserInfoEndpointURI();
            iFrameEndpoint = op.getCheckSessionIframeURI();

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
<body>
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
<% if (iFrameEndpoint != null) { %>
<iframe id="op-iframe" src="<%=iFrameEndpoint.toString()%>" style="display: none;" >OP CheckSession iFrame</iframe>
<% } %>
<iframe src="<%=request.getContextPath()%>/authzcode/chk-session.jsp" style="display: none;">RP CheckSession iFrame</iframe>
</body>
</html>


