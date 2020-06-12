<%@ page import="com.nimbusds.jwt.JWT" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%

    JWT idToken = (JWT) request.getSession().getAttribute("id_token");

    Properties props = new Properties();
    InputStream is = getClass().getResourceAsStream("/oidc.properties");
    props.load(is);

    // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
    Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
    OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
    String sloUrl = null;

    String action = request.getParameter("action");

    if (action != null && action.equals("detected")) {
        sloUrl = request.getContextPath() + "/authzcode/login.jsp";
    } else {
        sloUrl = op.getEndSessionEndpointURI().toString();
        sloUrl = sloUrl + "?id_token_hint=" + idToken.getParsedString()  + "&post_logout_redirect_uri=http://localhost:8080"+request.getContextPath()+"/authzcode/login.jsp";
    }

    request.getSession().removeAttribute("username");
    request.getSession().removeAttribute("session_state");
    request.getSession().removeAttribute("bearer_access_token");
    request.getSession().removeAttribute("refresh_token");
    request.getSession().removeAttribute("id_token");

    response.sendRedirect(sloUrl);
%>
