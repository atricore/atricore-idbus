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
<%@ page import="java.util.Map" %>
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

<jsp:include page="inc/header.jsp" />

<body class="gt-fixed">

<jsp:include page="inc/top-bar.jsp" />

<div id="idbus-error" class="gt-bd clearfix">
    <div class="gt-content">
        <div>
            <h2 class="gt-table-head">User Claims</h2>
        </div>

        <div>
            <% if (error == null && exception == null) {

                JWTClaimsSet cs = userInfo.toJWTClaimsSet();
                Map<String, Object> c = cs.getClaims();
                out.println("<ul>");
                for (String cName  : c.keySet()) {
                    out.println("<li>" + cName + ": " + c.get(cName).toString() + "</li>");
                }
                out.println("</ul>");
            }
            if (error != null) {
                out.println("<h3>Error:</h3><p>" + error.getCode() + ":" + URLDecoder.decode(error.getDescription()) + "</p>");
            }
            if (exception != null) {
                out.println("<h3>Exception:</h3><p>" + exception.getMessage() + "</p>");
            }%>
        </div>
    </div>
</div>

<!-- footer -->
<div class="gt-footer"><div class="gt-footer-inner">
    <p>Copyright &copy; 2007 - 2020 Atricore, Inc. - <a href="http://www.atricore.com" target="_blank">www.atricore.com</a></p>
</div>
<!-- /footer -->

<% if (iFrameEndpoint != null) { %>
<iframe id="op-iframe" src="<%=iFrameEndpoint.toString()%>" style="display: none;" >OP CheckSession iFrame</iframe>
<% } %>
<iframe src="<%=request.getContextPath()%>/authzcode/chk-session.jsp" style="display: none;">RP CheckSession iFrame</iframe>

</body>
</html>


