<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Properties" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    response.addHeader("Cache-Control", "no-cache, no-store");
    response.addHeader("Pragma", "no-cache");

    Properties props = new Properties();
    InputStream is = getClass().getResourceAsStream("/oidc.properties");
    props.load(is);

    // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
    Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
    OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);

    String targetOrigin = op.getCheckSessionIframeURI().toString();
    String clientId = props.getProperty("oidc.client.id");
    String sessionState = (String) request.getSession().getAttribute("session_state");


%>
<html>
<body>
<script>
let stat = "unchanged";
let mes = "<%=clientId%>" + " " + "<%=sessionState%>";
let timerID;
setTimer();

function check_session()
{
    let targetOrigin = "<%=targetOrigin%>";
    let win = window.parent.document.getElementById("op-iframe").
    contentWindow;
    win.postMessage( mes, targetOrigin);
}

function setTimer()
{
    check_session();
    timerID = setInterval(check_session, 3*1000);
}

window.addEventListener("message", receiveMessage, false);

function receiveMessage(e)
{
    let targetOrigin = "<%=targetOrigin%>";
    if (!targetOrigin.startsWith(e.origin) ) {return;}
    if (e.data == "changed") {
        // Go to main page
        window.parent.location.href = "<%=request.getContextPath()%>" + "/authzcode/index.jsp"
    }
}
</script>
</body>
</html>
